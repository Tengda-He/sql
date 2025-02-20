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
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.ast.tree;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.opensearch.sql.ast.AbstractNodeVisitor;

/**
 * Logical plan node of RelationSubquery.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ToString
public class RelationSubquery extends UnresolvedPlan {
  private UnresolvedPlan query;
  private String alias;

  /**
   * Take subquery alias as table name.
   */
  public String getAliasAsTableName() {
    return alias;
  }

  @Override
  public List<UnresolvedPlan> getChild() {
    return ImmutableList.of(query);
  }

  @Override
  public <T, C> T accept(AbstractNodeVisitor<T, C> nodeVisitor, C context) {
    return nodeVisitor.visitRelationSubquery(this, context);
  }

  @Override
  public UnresolvedPlan attach(UnresolvedPlan child) {
    return this;
  }
}
