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

package org.opensearch.sql.legacy.query.planner.physical.node.join;

import static com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import static org.opensearch.index.query.QueryBuilders.boolQuery;
import static org.opensearch.index.query.QueryBuilders.termsQuery;
import static org.opensearch.sql.legacy.query.planner.logical.node.Join.JoinCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.opensearch.common.Strings;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.sql.legacy.query.planner.core.ExecuteParams;
import org.opensearch.sql.legacy.query.planner.physical.PhysicalOperator;
import org.opensearch.sql.legacy.query.planner.physical.Row;
import org.opensearch.sql.legacy.query.planner.physical.estimation.Cost;
import org.opensearch.sql.legacy.query.planner.resource.blocksize.BlockSize;

/**
 * Block-based Hash Join implementation
 */
public class BlockHashJoin<T> extends JoinAlgorithm<T> {

    /**
     * Use terms filter optimization or not
     */
    private final boolean isUseTermsFilterOptimization;

    public BlockHashJoin(PhysicalOperator<T> left,
                         PhysicalOperator<T> right,
                         JoinType type,
                         JoinCondition condition,
                         BlockSize blockSize,
                         boolean isUseTermsFilterOptimization) {
        super(left, right, type, condition, blockSize);

        this.isUseTermsFilterOptimization = isUseTermsFilterOptimization;
    }

    @Override
    public Cost estimate() {
        return new Cost();
    }

    @Override
    protected void reopenRight() throws Exception {
        Objects.requireNonNull(params, "Execute params is not set so unable to add extra filter");

        if (isUseTermsFilterOptimization) {
            params.add(ExecuteParams.ExecuteParamType.EXTRA_QUERY_FILTER, queryForPushedDownOnConds());
        }
        right.open(params);
    }

    @Override
    protected List<CombinedRow<T>> probe() {
        List<CombinedRow<T>> combinedRows = new ArrayList<>();
        int totalSize = 0;

        /* Return if already found enough matched rows to give ResourceMgr a chance to check resource usage */
        while (right.hasNext() && totalSize < hashTable.size()) {
            Row<T> rightRow = right.next();
            Collection<Row<T>> matchedLeftRows = hashTable.match(rightRow);

            if (!matchedLeftRows.isEmpty()) {
                combinedRows.add(new CombinedRow<>(rightRow, matchedLeftRows));
                totalSize += matchedLeftRows.size();
            }
        }
        return combinedRows;
    }

    /**
     * Build query for pushed down conditions in ON
     */
    private BoolQueryBuilder queryForPushedDownOnConds() {
        BoolQueryBuilder orQuery = boolQuery();
        Map<String, Collection<Object>>[] rightNameToLeftValuesGroup = hashTable.rightFieldWithLeftValues();

        for (Map<String, Collection<Object>> rightNameToLeftValues : rightNameToLeftValuesGroup) {
            if (LOG.isTraceEnabled()) {
                rightNameToLeftValues.forEach((rightName, leftValues) ->
                        LOG.trace("Right name to left values mapping: {} => {}", rightName, leftValues));
            }

            BoolQueryBuilder andQuery = boolQuery();
            rightNameToLeftValues.forEach(
                    (rightName, leftValues) -> andQuery.must(termsQuery(rightName, leftValues))
            );

            if (LOG.isTraceEnabled()) {
                LOG.trace("Terms filter optimization: {}", Strings.toString(andQuery));
            }
            orQuery.should(andQuery);
        }
        return orQuery;
    }

    /*********************************************
     *          Getters for Explain
     *********************************************/

    public boolean isUseTermsFilterOptimization() {
        return isUseTermsFilterOptimization;
    }
}
