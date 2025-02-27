/*
 * Copyright 2021 4Paradigm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com._4paradigm.openmldb.taskmanager.spark

import com._4paradigm.openmldb.taskmanager.JobInfoManager
import com._4paradigm.openmldb.taskmanager.config.TaskManagerConfig
import com._4paradigm.openmldb.taskmanager.dao.JobInfo
import com._4paradigm.openmldb.taskmanager.yarn.YarnClientUtil
import org.apache.spark.launcher.SparkLauncher

object SparkJobManager {

  /**
   * Create the SparkLauncher object with pre-set parameters like yarn-cluster.
   *
   * @param mainClass the full-qualified Java class name
   * @return the SparkLauncher object
   */
  def createSparkLauncher(mainClass: String): SparkLauncher = {

    val launcher = new SparkLauncher()
      .setAppResource(TaskManagerConfig.BATCHJOB_JAR_PATH)
      .setMainClass(mainClass)

    if (TaskManagerConfig.SPARK_HOME != null && TaskManagerConfig.SPARK_HOME.nonEmpty) {
      launcher.setSparkHome(TaskManagerConfig.SPARK_HOME)
    }

    TaskManagerConfig.SPARK_MASTER.toLowerCase match {
      case "local" => {
        launcher.setMaster("local")
      }
      case "yarn" => {
        launcher.setMaster("yarn")
          .setDeployMode("cluster")
          .setConf("spark.yarn.maxAppAttempts", "1")
      }
      case _ => throw new Exception(s"Unsupported Spark master ${TaskManagerConfig.SPARK_MASTER}")
    }

    if (TaskManagerConfig.SPARK_YARN_JARS != null && TaskManagerConfig.SPARK_YARN_JARS.nonEmpty) {
      launcher.setConf("spark.yarn.jars", TaskManagerConfig.SPARK_YARN_JARS)
    }

    launcher
  }

  def submitSparkJob(jobType: String, mainClass: String, args: List[String] = List(),
                     sparkConf: Map[String, String] = Map()): JobInfo = {

    val jobInfo = JobInfoManager.createJobInfo(jobType, args, sparkConf)

    // Submit Spark application with SparkLauncher
    val launcher = createSparkLauncher(mainClass)
    if (args != null) {
      launcher.addAppArgs(args:_*)
    }

    // Set ad-hoc Spark configuration
    for ((k, v) <- sparkConf) {
      launcher.setConf(k, v)
    }

    // Submit Spark application and watch state with custom listener
    launcher.startApplication(new SparkJobListener(jobInfo))

    jobInfo
  }

  def stopSparkYarnJob(jobInfo: JobInfo): Unit = {
    if (jobInfo.isFinished) {
      // TODO: return error message
    } else if (jobInfo.getApplicationId == null) {

    } else {
      YarnClientUtil.killYarnJob(jobInfo.getApplicationId)
    }
  }

}
