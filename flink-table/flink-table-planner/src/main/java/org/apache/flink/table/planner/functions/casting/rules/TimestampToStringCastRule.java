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
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.planner.functions.casting.CastRulePredicate;
import org.apache.flink.table.planner.functions.casting.CodeGeneratorCastRule;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.LogicalTypeFamily;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.table.types.logical.utils.LogicalTypeChecks;

import org.apache.calcite.avatica.util.DateTimeUtils;

import static org.apache.flink.table.planner.codegen.calls.BuiltInMethods.TIMESTAMP_TO_STRING_TIME_ZONE;
import static org.apache.flink.table.planner.functions.casting.rules.CastRuleUtils.accessStaticField;
import static org.apache.flink.table.planner.functions.casting.rules.CastRuleUtils.staticCall;

/** {@link LogicalTypeFamily#TIMESTAMP} to {@link LogicalTypeFamily#CHARACTER_STRING} cast rule. */
@Internal
public class TimestampToStringCastRule extends AbstractCharacterFamilyTargetRule<TimestampData> {

    public static final TimestampToStringCastRule INSTANCE = new TimestampToStringCastRule();

    private TimestampToStringCastRule() {
        super(
                CastRulePredicate.builder()
                        .input(LogicalTypeFamily.TIMESTAMP)
                        .target(LogicalTypeFamily.CHARACTER_STRING)
                        .build());
    }

    @Override
    public String generateStringExpression(
            CodeGeneratorCastRule.Context context,
            String inputTerm,
            LogicalType inputLogicalType,
            LogicalType targetLogicalType) {
        final String zoneId =
                (inputLogicalType.is(LogicalTypeRoot.TIMESTAMP_WITH_LOCAL_TIME_ZONE))
                        ? context.getSessionTimeZoneTerm()
                        : accessStaticField(DateTimeUtils.class, "UTC_ZONE");
        final int precision = LogicalTypeChecks.getPrecision(inputLogicalType);

        return staticCall(TIMESTAMP_TO_STRING_TIME_ZONE(), inputTerm, zoneId, precision);
    }
}
