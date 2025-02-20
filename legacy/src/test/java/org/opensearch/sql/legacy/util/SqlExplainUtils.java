/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.legacy.util;

import com.alibaba.druid.sql.parser.ParserException;
import java.sql.SQLFeatureNotSupportedException;
import org.mockito.Mockito;
import org.opensearch.client.Client;
import org.opensearch.sql.legacy.exception.SQLFeatureDisabledException;
import org.opensearch.sql.legacy.exception.SqlParseException;
import org.opensearch.sql.legacy.query.OpenSearchActionFactory;
import org.opensearch.sql.legacy.query.QueryAction;

/**
 * Test utils class that explains a query
 */
public class SqlExplainUtils {

    public static String explain(String query) {
        try {
            Client mockClient = Mockito.mock(Client.class);
            CheckScriptContents.stubMockClient(mockClient);
            QueryAction queryAction = OpenSearchActionFactory.create(mockClient, query);

            return queryAction.explain().explain();
        } catch (SqlParseException | SQLFeatureNotSupportedException | SQLFeatureDisabledException e) {
            throw new ParserException("Illegal sql expr in: " + query);
        }
    }

    private SqlExplainUtils() {}
}
