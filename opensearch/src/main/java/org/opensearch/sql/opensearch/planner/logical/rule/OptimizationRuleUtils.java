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
 *
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.sql.opensearch.planner.logical.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.opensearch.sql.ast.tree.Sort;
import org.opensearch.sql.expression.ExpressionNodeVisitor;
import org.opensearch.sql.expression.NamedExpression;
import org.opensearch.sql.expression.ReferenceExpression;
import org.opensearch.sql.planner.logical.LogicalSort;

@UtilityClass
public class OptimizationRuleUtils {

  /**
   * Does the sort list only contain {@link ReferenceExpression}.
   *
   * @param logicalSort LogicalSort.
   * @return true only contain ReferenceExpression, otherwise false.
   */
  public static boolean sortByFieldsOnly(LogicalSort logicalSort) {
    return logicalSort.getSortList().stream()
        .map(sort -> sort.getRight() instanceof ReferenceExpression)
        .reduce(true, Boolean::logicalAnd);
  }

  /**
   * Does the sort list has option other than default options.
   * The default sort options are (ASC NULL_FIRST) or (DESC NULL LAST)
   *
   * @param logicalSort LogicalSort.
   * @return true sort list only option default options, otherwise false.
   */
  public static boolean sortByDefaultOptionOnly(LogicalSort logicalSort) {
    return logicalSort.getSortList().stream()
        .map(sort -> Sort.SortOption.DEFAULT_ASC.equals(sort.getLeft())
            || Sort.SortOption.DEFAULT_DESC.equals(sort.getLeft()))
        .reduce(true, Boolean::logicalAnd);
  }

  /**
   * Find reference expression from expression.
   * @param expressions a list of expression.
   *
   * @return a list of ReferenceExpression
   */
  public static Set<ReferenceExpression> findReferenceExpressions(
      List<NamedExpression> expressions) {
    Set<ReferenceExpression> projectList = new HashSet<>();
    for (NamedExpression namedExpression : expressions) {
      projectList.addAll(findReferenceExpression(namedExpression));
    }
    return projectList;
  }

  /**
   * Find reference expression from expression.
   * @param expression expression.
   *
   * @return a list of ReferenceExpression
   */
  public static List<ReferenceExpression> findReferenceExpression(
      NamedExpression expression) {
    List<ReferenceExpression> results = new ArrayList<>();
    expression.accept(new ExpressionNodeVisitor<Object, Object>() {
      @Override
      public Object visitReference(ReferenceExpression node, Object context) {
        return results.add(node);
      }
    }, null);
    return results;
  }
}
