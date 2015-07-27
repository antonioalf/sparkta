/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.sparkta.sdk

import java.io.{Serializable => JSerializable}
import java.sql.Timestamp
import java.util.Date

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

object DateOperations {

  val ParquetPathPattern = "/'year='yyyy/'month='MM/'day='dd/'hour='HH/'minute='mm/'second='ss"

  def getTimeFromGranularity(timeDimension: Option[String], granularity: Option[String]): Long =
    (timeDimension, granularity) match {
      case (Some(time), Some(granularity)) => dateFromGranularity(DateTime.now, granularity)
      case _ => 0L
    }

  def dateFromGranularity(value: DateTime, granularity: String): Long = {
    val secondsDate = value.withMillisOfSecond(0)
    val minutesDate = secondsDate.withSecondOfMinute(0)
    val hourDate = minutesDate.withMinuteOfHour(0)
    val dayDate = hourDate.withHourOfDay(0)
    val monthDate = dayDate.withDayOfMonth(1)
    val yearDate = monthDate.withMonthOfYear(1)
    val s15 = roundDateTime(value, Duration.standardSeconds(15))

    granularity.toLowerCase match {
      case "minute" => minutesDate.getMillis
      case "hour" => hourDate.getMillis
      case "day" => dayDate.getMillis
      case "month" => monthDate.getMillis
      case "year" => yearDate.getMillis
      case "second" => secondsDate.getMillis
      case "s15" => s15.getMillis
      case _ => 0L
    }
  }

  def millisToTimeStamp(date: Long): Timestamp = new Timestamp(date)

  def getMillisFromSerializable(date: JSerializable): Long = date match {
    case value if value.isInstanceOf[Timestamp] || value.isInstanceOf[Date]
      || value.isInstanceOf[DateTime] => getMillisFromDateTime(date)
    case value if value.isInstanceOf[Long] => value.asInstanceOf[Long]
    case value if value.isInstanceOf[String] => value.asInstanceOf[String].toLong
    case _ => new DateTime().getMillis
  }

  def getMillisFromDateTime(value: JSerializable): Long = value match {
    case value if value.isInstanceOf[Timestamp] => value.asInstanceOf[Timestamp].getTime
    case value if value.isInstanceOf[Date] => value.asInstanceOf[Date].getTime
    case value if value.isInstanceOf[DateTime] => value.asInstanceOf[DateTime].getMillis
  }

  def subPath(granularity: String, datePattern: Option[String]): String = {
    val suffix = dateFromGranularity(DateTime.now, granularity)
    if (!datePattern.isDefined || suffix.equals(0L)) s"/$suffix"
    else s"/${DateTimeFormat.forPattern(datePattern.get).print(new DateTime(suffix))}/$suffix"
  }

  /**
   * Generates a parquet path with the format contained in ParquetPathPattern.
   * @return the object described above.
   */
  def generateParquetPath(dateTime: Option[DateTime] = Option(DateTime.now()),
                          parquetPattern: Option[String] = Some(ParquetPathPattern)): String = {
    val pattern = parquetPattern.get match {
      case "year" => "/'year='yyyy/'"
      case "month" => "/'year='yyyy/'month='MM/'"
      case "day" => "/'year='yyyy/'month='MM/'day='dd/'"
      case "hour" => "/'year='yyyy/'month='MM/'day='dd/'hour='HH/'"
      case "minute" => "/'year='yyyy/'month='MM/'day='dd/'hour='HH/'minute='mm/'"
      case _ => ParquetPathPattern
    }
    DateTimeFormat.forPattern(pattern).print(dateTime.get)
  }

  def roundDateTime(t: DateTime, d: Duration) = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }
}
