/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.planner.functions.casting.rules;

import org.apache.flink.annotation.Internal;
import org.apache.flink.table.planner.functions.casting.CastCodeBlock;
import org.apache.flink.table.planner.functions.casting.CastRulePredicate;
import org.apache.flink.table.planner.functions.casting.CodeGeneratorCastRule;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.LogicalTypeFamily;
import org.apache.flink.table.types.logical.utils.LogicalTypeCasts;

/**
 * Identity cast rule. For more details on when the rule is applied, check {@link
 * #isIdentityCast(LogicalType, LogicalType)}
 */
@Internal
public class IdentityCastRule extends AbstractCodeGeneratorCastRule<Object, Object> {

    public static final IdentityCastRule INSTANCE = new IdentityCastRule();

    private IdentityCastRule() {
        super(CastRulePredicate.builder().predicate(IdentityCastRule::isIdentityCast).build());
    }

    private static boolean isIdentityCast(
            LogicalType inputLogicalType, LogicalType targetLogicalType) {
        // TODO string to string casting now behaves like string casting.
        //  the discussion in FLINK-24413 will address it
        if (inputLogicalType.is(LogicalTypeFamily.CHARACTER_STRING)
                && targetLogicalType.is(LogicalTypeFamily.CHARACTER_STRING)) {
            return true;
        }
        return LogicalTypeCasts.supportsAvoidingCast(inputLogicalType, targetLogicalType);
    }

    @Override
    public CastCodeBlock generateCodeBlock(
            CodeGeneratorCastRule.Context context,
            String inputTerm,
            String inputIsNullTerm,
            LogicalType inputLogicalType,
            LogicalType targetLogicalType) {
        return new CastCodeBlock("", inputTerm, inputIsNullTerm);
    }
}
