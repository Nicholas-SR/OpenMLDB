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

%module sql_router_sdk
%include "std_unique_ptr.i"
%include std_string.i
%include std_shared_ptr.i
%include stl.i
%include stdint.i
%include std_vector.i
#ifdef SWIGJAVA
%include various.i
%apply char *BYTE { char *string_buffer_var_name };
#endif

%shared_ptr(hybridse::sdk::ResultSet);
%shared_ptr(hybridse::sdk::Schema);
%shared_ptr(hybridse::sdk::ColumnTypes);
%shared_ptr(openmldb::sdk::SQLRouter);
%shared_ptr(openmldb::sdk::SQLRequestRow);
%shared_ptr(openmldb::sdk::SQLRequestRowBatch);
%shared_ptr(openmldb::sdk::ColumnIndicesSet);
%shared_ptr(openmldb::sdk::SQLInsertRow);
%shared_ptr(openmldb::sdk::SQLInsertRows);
%shared_ptr(openmldb::sdk::ExplainInfo);
%shared_ptr(hybridse::sdk::ProcedureInfo);
%shared_ptr(openmldb::sdk::QueryFuture);
%shared_ptr(openmldb::sdk::TableReader);
%template(VectorUint32) std::vector<uint32_t>;
%template(VectorString) std::vector<std::string>;

%{
#include "sdk/sql_router.h"
#include "sdk/result_set.h"
#include "sdk/base.h"
#include "sdk/sql_request_row.h"
#include "sdk/sql_insert_row.h"
#include "sdk/table_reader.h"

using hybridse::sdk::Schema;
using hybridse::sdk::ColumnTypes;
using hybridse::sdk::ResultSet;
using openmldb::sdk::SQLRouter;
using openmldb::sdk::SQLRouterOptions;
using openmldb::sdk::SQLRequestRow;
using openmldb::sdk::SQLRequestRowBatch;
using openmldb::sdk::ColumnIndicesSet;
using openmldb::sdk::SQLInsertRow;
using openmldb::sdk::SQLInsertRows;
using openmldb::sdk::ExplainInfo;
using hybridse::sdk::ProcedureInfo;
using openmldb::sdk::QueryFuture;
using openmldb::sdk::TableReader;
%}

%include "sdk/sql_router.h"
%include "sdk/base.h"
%include "sdk/result_set.h"
%include "sdk/sql_request_row.h"
%include "sdk/sql_insert_row.h"
%include "sdk/table_reader.h"

%template(ColumnDescPair) std::pair<std::string, hybridse::sdk::DataType>;
%template(ColumnDescVector) std::vector<std::pair<std::string, hybridse::sdk::DataType>>;
%template(TableColumnDescPair) std::pair<std::string, std::vector<std::pair<std::string, hybridse::sdk::DataType>>>;
%template(TableColumnDescPairVector) std::vector<std::pair<std::string, std::vector<std::pair<std::string, hybridse::sdk::DataType>>>>;
