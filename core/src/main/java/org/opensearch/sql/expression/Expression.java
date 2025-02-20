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

package org.opensearch.sql.expression;

import java.io.Serializable;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.data.type.ExprType;
import org.opensearch.sql.expression.env.Environment;

/**
 * The definition of the resolved expression.
 */
public interface Expression extends Serializable {

  /**
   * Evaluate the value of expression in the value environment.
   */
  ExprValue valueOf(Environment<Expression, ExprValue> valueEnv);

  /**
   * The type of the expression.
   */
  ExprType type();

  /**
   * Accept a visitor to visit current expression node.
   * @param visitor   visitor
   * @param context   context
   * @param <T>       result type
   * @param <C>       context type
   * @return          result accumulated by visitor when visiting
   */
  <T, C> T accept(ExpressionNodeVisitor<T, C> visitor, C context);

}
