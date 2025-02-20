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
 *   Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.opensearch.sql.ast.tree.RareTopN.CommandType;
import org.opensearch.sql.expression.Expression;

/**
 * Logical Rare and TopN Plan.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class LogicalRareTopN extends LogicalPlan {

  private final CommandType commandType;
  private final Integer noOfResults;
  private final List<Expression> fieldList;
  private final List<Expression> groupByList;

  /**
   * Constructor of LogicalRareTopN.
   */
  public LogicalRareTopN(
      LogicalPlan child,
      CommandType commandType, Integer noOfResults,
      List<Expression> fieldList,
      List<Expression> groupByList) {
    super(Collections.singletonList(child));
    this.commandType = commandType;
    this.noOfResults = noOfResults;
    this.fieldList = fieldList;
    this.groupByList = groupByList;
  }

  @Override
  public <R, C> R accept(LogicalPlanNodeVisitor<R, C> visitor, C context) {
    return visitor.visitRareTopN(this, context);
  }
}
