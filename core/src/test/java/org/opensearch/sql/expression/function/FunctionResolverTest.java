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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.expression.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.sql.data.type.WideningTypeRule;
import org.opensearch.sql.exception.ExpressionEvaluationException;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class FunctionResolverTest {
  @Mock
  private FunctionSignature exactlyMatchFS;
  @Mock
  private FunctionSignature bestMatchFS;
  @Mock
  private FunctionSignature leastMatchFS;
  @Mock
  private FunctionSignature notMatchFS;
  @Mock
  private FunctionSignature functionSignature;
  @Mock
  private FunctionBuilder exactlyMatchBuilder;
  @Mock
  private FunctionBuilder bestMatchBuilder;
  @Mock
  private FunctionBuilder leastMatchBuilder;
  @Mock
  private FunctionBuilder notMatchBuilder;

  private FunctionName functionName = FunctionName.of("add");

  @Test
  void resolve_function_signature_exactly_match() {
    when(functionSignature.match(exactlyMatchFS)).thenReturn(WideningTypeRule.TYPE_EQUAL);
    FunctionResolver resolver = new FunctionResolver(functionName,
        ImmutableMap.of(exactlyMatchFS, exactlyMatchBuilder));

    assertEquals(exactlyMatchBuilder, resolver.resolve(functionSignature).getValue());
  }

  @Test
  void resolve_function_signature_best_match() {
    when(functionSignature.match(bestMatchFS)).thenReturn(1);
    when(functionSignature.match(leastMatchFS)).thenReturn(2);
    FunctionResolver resolver = new FunctionResolver(functionName,
        ImmutableMap.of(bestMatchFS, bestMatchBuilder, leastMatchFS, leastMatchBuilder));

    assertEquals(bestMatchBuilder, resolver.resolve(functionSignature).getValue());
  }

  @Test
  void resolve_function_not_match() {
    when(functionSignature.match(notMatchFS)).thenReturn(WideningTypeRule.IMPOSSIBLE_WIDENING);
    when(notMatchFS.formatTypes()).thenReturn("[INTEGER,INTEGER]");
    when(functionSignature.formatTypes()).thenReturn("[BOOLEAN,BOOLEAN]");
    FunctionResolver resolver = new FunctionResolver(functionName,
        ImmutableMap.of(notMatchFS, notMatchBuilder));

    ExpressionEvaluationException exception = assertThrows(ExpressionEvaluationException.class,
        () -> resolver.resolve(functionSignature));
    assertEquals("add function expected {[INTEGER,INTEGER]}, but get [BOOLEAN,BOOLEAN]",
        exception.getMessage());
  }
}
