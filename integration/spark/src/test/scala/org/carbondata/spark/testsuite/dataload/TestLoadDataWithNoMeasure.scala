/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.carbondata.spark.testsuite.dataload

import java.io.File

import org.apache.spark.sql.Row
import org.apache.spark.sql.common.util.CarbonHiveContext._
import org.apache.spark.sql.common.util.QueryTest
import org.scalatest.BeforeAndAfterAll

/**
  * Test Class for data loading with hive syntax and old syntax
  *
  */
class TestLoadDataWithNoMeasure extends QueryTest with BeforeAndAfterAll {

  override def beforeAll {
    sql("DROP TABLE IF EXISTS nomeasureTest_sd")
    sql(
      "CREATE TABLE nomeasureTest (empno String, doj String) STORED BY 'org.apache.carbondata" +
        ".format'"
    )
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "/../../")
      .getCanonicalPath
    val testData = currentDirectory + "/src/test/resources/datasample.csv"
    sql("LOAD DATA LOCAL INPATH '" + testData + "' into table nomeasureTest")
  }

  test("test data loading and validate query output") {

    checkAnswer(
      sql("select empno from nomeasureTest"),
      Seq(Row("11"), Row("12"))
    )
  }

  test("test data loading with single dictionary column") {
    sql("DROP TABLE IF EXISTS nomeasureTest_sd")
    sql("CREATE TABLE nomeasureTest_sd (city String) STORED BY 'org.apache.carbondata.format'")
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "/../../")
      .getCanonicalPath
    val testData = currentDirectory + "/src/test/resources/datasingleCol.csv"
    sql("LOAD DATA LOCAL INPATH '" + testData + "' into table nomeasureTest_sd options " +
      "('FILEHEADER'='city')"
    )

    checkAnswer(
      sql("select city from nomeasureTest_sd"),
      Seq(Row("CA"), Row("LA"), Row("AD"))
    )
  }

  test("test data loading with single no dictionary column") {
    sql("DROP TABLE IF EXISTS nomeasureTest_sd")
    sql(
      "CREATE TABLE nomeasureTest_sd (city String) STORED BY 'org.apache.carbondata.format' " +
        "TBLPROPERTIES ('DICTIONARY_EXCLUDE'='city')"
    )
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "/../../")
      .getCanonicalPath
    val testData = currentDirectory + "/src/test/resources/datasingleCol.csv"
    sql("LOAD DATA LOCAL INPATH '" + testData + "' into table nomeasureTest_sd options " +
      "('FILEHEADER'='city')"
    )

    checkAnswer(
      sql("select city from nomeasureTest_sd"),
      Seq(Row("CA"), Row("LA"), Row("AD"))
    )
  }

  test("test data loading with single complex struct type column") {
    //only data load check
    sql("DROP TABLE IF EXISTS nomeasureTest_scd")
    sql(
      "CREATE TABLE nomeasureTest_scd (cityDetail struct<cityName:string,cityCode:string>) STORED" +
        " " +
        "BY 'org.apache.carbondata.format'"
    )
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "/../../")
      .getCanonicalPath
    val testData = currentDirectory + "/src/test/resources/datasingleComplexCol.csv"
    sql("LOAD DATA LOCAL INPATH '" + testData + "' into table nomeasureTest_scd options " +
      "('DELIMITER'=',','QUOTECHAR'='\"','FILEHEADER'='cityDetail','COMPLEX_DELIMITER_LEVEL_1'=':')"
    )
  }

  test("test data loading with single complex array type column") {
    //only data load check
    sql("DROP TABLE IF EXISTS nomeasureTest_scd")
    sql(
      "CREATE TABLE nomeasureTest_scd (cityDetail array<string>) STORED" +
        " " +
        "BY 'org.apache.carbondata.format'"
    )
    val currentDirectory = new File(this.getClass.getResource("/").getPath + "/../../")
      .getCanonicalPath
    val testData = currentDirectory + "/src/test/resources/datasingleComplexCol.csv"
    sql("LOAD DATA LOCAL INPATH '" + testData + "' into table nomeasureTest_scd options " +
      "('DELIMITER'=',','QUOTECHAR'='\"','FILEHEADER'='cityDetail'," +
      "'COMPLEX_DELIMITER_LEVEL_1'=':')"
    )
  }

  override def afterAll {
    sql("drop table nomeasureTest")
    sql("drop table nomeasureTest_sd")
    sql("drop table nomeasureTest_scd")
  }
}
