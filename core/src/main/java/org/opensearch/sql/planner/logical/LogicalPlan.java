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

package org.opensearch.sql.planner.logical;

import java.util.List;
import lombok.EqualsAndHashCode;
import org.opensearch.sql.planner.PlanNode;

/**
 * The abstract base class for all the Logical Plan node.
 */
@EqualsAndHashCode(callSuper = false)
public abstract class LogicalPlan implements PlanNode<LogicalPlan> {

  private List<LogicalPlan> childPlans;

  public LogicalPlan(List<LogicalPlan> childPlans) {
    this.childPlans = childPlans;
  }

  /**
   * Accept the {@link LogicalPlanNodeVisitor}.
   *
   * @param visitor visitor.
   * @param context visitor context.
   * @param <R>     returned object type.
   * @param <C>     context type.
   * @return returned object.
   */
  public abstract <R, C> R accept(LogicalPlanNodeVisitor<R, C> visitor, C context);

  public LogicalPlan replaceChildPlans(List<LogicalPlan> childPlans) {
    this.childPlans = childPlans;
    return this;
  }


  @Override
  public List<LogicalPlan> getChild() {
    return childPlans;
  }
}
