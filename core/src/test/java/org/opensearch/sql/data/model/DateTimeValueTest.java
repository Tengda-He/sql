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

package org.opensearch.sql.data.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.opensearch.sql.data.model.ExprValueUtils.integerValue;
import static org.opensearch.sql.data.type.ExprCoreType.TIME;
import static org.opensearch.sql.data.type.ExprCoreType.TIMESTAMP;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.opensearch.sql.exception.ExpressionEvaluationException;
import org.opensearch.sql.exception.SemanticCheckException;

public class DateTimeValueTest {

  private static final int MICROS_PRECISION_MAX = 6;

  @Test
  public void timeValueInterfaceTest() {
    ExprValue timeValue = new ExprTimeValue("01:01:01");

    assertEquals(TIME, timeValue.type());
    assertEquals(LocalTime.parse("01:01:01"), timeValue.timeValue());
    assertEquals("01:01:01", timeValue.value());
    assertEquals("TIME '01:01:01'", timeValue.toString());
    assertThrows(ExpressionEvaluationException.class, () -> integerValue(1).timeValue(),
        "invalid to get timeValue from value of type INTEGER");
  }

  @Test
  public void timestampValueInterfaceTest() {
    ExprValue timestampValue = new ExprTimestampValue("2020-07-07 01:01:01");

    assertEquals(TIMESTAMP, timestampValue.type());
    assertEquals(ZonedDateTime.of(LocalDateTime.parse("2020-07-07T01:01:01"),
        ZoneId.of("UTC")).toInstant(), timestampValue.timestampValue());
    assertEquals("2020-07-07 01:01:01", timestampValue.value());
    assertEquals("TIMESTAMP '2020-07-07 01:01:01'", timestampValue.toString());
    assertEquals(LocalDate.parse("2020-07-07"), timestampValue.dateValue());
    assertEquals(LocalTime.parse("01:01:01"), timestampValue.timeValue());
    assertEquals(LocalDateTime.parse("2020-07-07T01:01:01"), timestampValue.datetimeValue());
    assertThrows(ExpressionEvaluationException.class, () -> integerValue(1).timestampValue(),
        "invalid to get timestampValue from value of type INTEGER");
  }

  @Test
  public void dateValueInterfaceTest() {
    ExprValue dateValue = new ExprDateValue("2012-07-07");

    assertEquals(LocalDate.parse("2012-07-07"), dateValue.dateValue());
    assertEquals(LocalTime.parse("00:00:00"), dateValue.timeValue());
    assertEquals(LocalDateTime.parse("2012-07-07T00:00:00"), dateValue.datetimeValue());
    assertEquals(ZonedDateTime.of(LocalDateTime.parse("2012-07-07T00:00:00"),
        ZoneId.systemDefault()).toInstant(), dateValue.timestampValue());
    ExpressionEvaluationException exception =
        assertThrows(ExpressionEvaluationException.class, () -> integerValue(1).dateValue());
    assertEquals("invalid to get dateValue from value of type INTEGER",
        exception.getMessage());
  }

  @Test
  public void datetimeValueInterfaceTest() {
    ExprValue datetimeValue = new ExprDatetimeValue("2020-08-17 19:44:00");

    assertEquals(LocalDateTime.parse("2020-08-17T19:44:00"), datetimeValue.datetimeValue());
    assertEquals(LocalDate.parse("2020-08-17"), datetimeValue.dateValue());
    assertEquals(LocalTime.parse("19:44:00"), datetimeValue.timeValue());
    assertEquals(ZonedDateTime.of(LocalDateTime.parse("2020-08-17T19:44:00"),
        ZoneId.of("UTC")).toInstant(), datetimeValue.timestampValue());
    assertEquals("DATETIME '2020-08-17 19:44:00'", datetimeValue.toString());
    assertThrows(ExpressionEvaluationException.class, () -> integerValue(1).datetimeValue(),
        "invalid to get datetimeValue from value of type INTEGER");
  }

  @Test
  public void dateInUnsupportedFormat() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class, () -> new ExprDateValue("2020-07-07Z"));
    assertEquals("date:2020-07-07Z in unsupported format, please use yyyy-MM-dd",
        exception.getMessage());
  }

  @Test
  public void timeInUnsupportedFormat() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class, () -> new ExprTimeValue("01:01:0"));
    assertEquals("time:01:01:0 in unsupported format, please use HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void timestampInUnsupportedFormat() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprTimestampValue("2020-07-07T01:01:01Z"));
    assertEquals(
        "timestamp:2020-07-07T01:01:01Z in unsupported format, "
            + "please use yyyy-MM-dd HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void datetimeInUnsupportedFormat() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprDatetimeValue("2020-07-07T01:01:01Z"));
    assertEquals(
        "datetime:2020-07-07T01:01:01Z in unsupported format, "
            + "please use yyyy-MM-dd HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void stringDateTimeValue() {
    ExprValue stringValue = new ExprStringValue("2020-08-17 19:44:00");

    assertEquals(LocalDateTime.parse("2020-08-17T19:44:00"), stringValue.datetimeValue());
    assertEquals(LocalDate.parse("2020-08-17"), stringValue.dateValue());
    assertEquals(LocalTime.parse("19:44:00"), stringValue.timeValue());
    assertEquals("\"2020-08-17 19:44:00\"", stringValue.toString());

    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprStringValue("2020-07-07T01:01:01Z").datetimeValue());
    assertEquals(
        "datetime:2020-07-07T01:01:01Z in unsupported format, "
            + "please use yyyy-MM-dd HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void stringDateValue() {
    ExprValue stringValue = new ExprStringValue("2020-08-17");

    assertEquals(LocalDateTime.parse("2020-08-17T00:00:00"), stringValue.datetimeValue());
    assertEquals(LocalDate.parse("2020-08-17"), stringValue.dateValue());
    assertEquals("\"2020-08-17\"", stringValue.toString());

    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprStringValue("2020-07-07Z").dateValue());
    assertEquals("date:2020-07-07Z in unsupported format, please use yyyy-MM-dd",
        exception.getMessage());
  }

  @Test
  public void stringTimeValue() {
    ExprValue stringValue = new ExprStringValue("19:44:00");

    assertEquals(LocalTime.parse("19:44:00"), stringValue.timeValue());
    assertEquals("\"19:44:00\"", stringValue.toString());

    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprStringValue("01:01:0").timeValue());
    assertEquals("time:01:01:0 in unsupported format, please use HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void timeWithVariableMicroPrecision() {
    String timeWithMicrosFormat = "10:11:12.%s";

    // Check all lengths of microsecond precision, up to max precision accepted
    StringBuilder micros = new StringBuilder();
    for (int microPrecision = 1; microPrecision <= MICROS_PRECISION_MAX; microPrecision++) {
      micros.append(microPrecision);
      String timeWithMicros = String.format(timeWithMicrosFormat, micros);

      ExprValue timeValue = new ExprTimeValue(timeWithMicros);
      assertEquals(LocalTime.parse(timeWithMicros), timeValue.timeValue());
    }
  }

  @Test
  public void timestampWithVariableMicroPrecision() {
    String dateValue = "2020-08-17";
    String timeWithMicrosFormat = "10:11:12.%s";

    // Check all lengths of microsecond precision, up to max precision accepted
    StringBuilder micros = new StringBuilder();
    for (int microPrecision = 1; microPrecision <= MICROS_PRECISION_MAX; microPrecision++) {
      micros.append(microPrecision);
      String timeWithMicros = String.format(timeWithMicrosFormat, micros);

      String timestampString = String.format("%s %s", dateValue, timeWithMicros);
      ExprValue timestampValue = new ExprTimestampValue(timestampString);

      assertEquals(LocalDate.parse(dateValue), timestampValue.dateValue());
      assertEquals(LocalTime.parse(timeWithMicros), timestampValue.timeValue());
      String localDateTime = String.format("%sT%s", dateValue, timeWithMicros);
      assertEquals(LocalDateTime.parse(localDateTime), timestampValue.datetimeValue());
    }
  }

  @Test
  public void datetimeWithVariableMicroPrecision() {
    String dateValue = "2020-08-17";
    String timeWithMicrosFormat = "10:11:12.%s";

    // Check all lengths of microsecond precision, up to max precision accepted
    StringBuilder micros = new StringBuilder();
    for (int microPrecision = 1; microPrecision <= MICROS_PRECISION_MAX; microPrecision++) {
      micros.append(microPrecision);
      String timeWithMicros = String.format(timeWithMicrosFormat, micros);

      String datetimeString = String.format("%s %s", dateValue, timeWithMicros);
      ExprValue datetimeValue = new ExprDatetimeValue(datetimeString);

      assertEquals(LocalDate.parse(dateValue), datetimeValue.dateValue());
      assertEquals(LocalTime.parse(timeWithMicros), datetimeValue.timeValue());
      String localDateTime = String.format("%sT%s", dateValue, timeWithMicros);
      assertEquals(LocalDateTime.parse(localDateTime), datetimeValue.datetimeValue());
    }
  }

  @Test
  public void timestampOverMaxMicroPrecision() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprTimestampValue("2020-07-07 01:01:01.1234567"));
    assertEquals(
        "timestamp:2020-07-07 01:01:01.1234567 in unsupported format, "
                + "please use yyyy-MM-dd HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void datetimeOverMaxMicroPrecision() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprDatetimeValue("2020-07-07 01:01:01.1234567"));
    assertEquals(
        "datetime:2020-07-07 01:01:01.1234567 in unsupported format, "
                + "please use yyyy-MM-dd HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }

  @Test
  public void timeOverMaxMicroPrecision() {
    SemanticCheckException exception =
        assertThrows(SemanticCheckException.class,
            () -> new ExprTimeValue("01:01:01.1234567"));
    assertEquals(
        "time:01:01:01.1234567 in unsupported format, please use HH:mm:ss[.SSSSSS]",
        exception.getMessage());
  }
}
