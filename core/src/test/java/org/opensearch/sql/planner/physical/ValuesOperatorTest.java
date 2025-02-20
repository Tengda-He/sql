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
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.sql.planner.physical;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.opensearch.sql.data.model.ExprValueUtils.collectionValue;
import static org.opensearch.sql.expression.DSL.literal;
import static org.opensearch.sql.planner.physical.PhysicalPlanDSL.values;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensearch.sql.data.model.ExprValue;

class ValuesOperatorTest {

  @Test
  public void shouldHaveNoChild() {
    ValuesOperator values = values(ImmutableList.of(literal(1)));
    assertThat(
        values.getChild(),
        is(empty())
    );
  }

  @Test
  public void iterateSingleRow() {
    ValuesOperator values = values(ImmutableList.of(literal(1), literal("abc")));
    List<ExprValue> results = new ArrayList<>();
    while (values.hasNext()) {
      results.add(values.next());
    }

    assertThat(
        results,
        contains(collectionValue(Arrays.asList(1, "abc")))
    );
  }

}
