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

package org.opensearch.sql.legacy.esdomain.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.opensearch.sql.legacy.util.CheckScriptContents.mockLocalClusterState;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.sql.legacy.esdomain.LocalClusterState;

/**
 * Test for FieldMappings class
 */
public class FieldMappingsTest {

    private static final String TEST_MAPPING_FILE = "mappings/field_mappings.json";

    @Before
    public void setUp() throws IOException {
        URL url = Resources.getResource(TEST_MAPPING_FILE);
        String mappings = Resources.toString(url, Charsets.UTF_8);
        mockLocalClusterState(mappings);
    }

    @After
    public void cleanUp() {
        LocalClusterState.state(null);
    }

    @Test
    public void flatFieldMappingsShouldIncludeFieldsOnAllLevels() {
        IndexMappings indexMappings = LocalClusterState.state().getFieldMappings(new String[]{"field_mappings"});
        FieldMappings fieldMappings = indexMappings.firstMapping().firstMapping();

        Map<String, String> typeByFieldName = new HashMap<>();
        fieldMappings.flat(typeByFieldName::put);
        assertThat(
            typeByFieldName,
            allOf(
                aMapWithSize(13),
                hasEntry("address", "text"),
                hasEntry("age", "integer"),
                hasEntry("employer", "text"),
                hasEntry("employer.raw", "text"),
                hasEntry("employer.keyword", "keyword"),
                hasEntry("projects", "nested"),
                hasEntry("projects.active", "boolean"),
                hasEntry("projects.members", "nested"),
                hasEntry("projects.members.name", "text"),
                hasEntry("manager", "object"),
                hasEntry("manager.name", "text"),
                hasEntry("manager.name.keyword", "keyword"),
                hasEntry("manager.address", "keyword")
            )
        );
    }

}
