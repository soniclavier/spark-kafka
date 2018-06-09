package com.vishnuviswanath.spark

import com.vishnuviswanath.util.ParameterParser
import org.apache.spark.sql.SparkSession

/**
  * Created by vviswanath on 3/6/18.
  * Before running this app, run "nc -lk 9999"
  */
object SocketStreamingApp {

  def main(args: Array[String]): Unit = {

    val parameters = ParameterParser.parse(args)

    val spark = SparkSession
      .builder
      .master("local[*]")
      .getOrCreate()

    val input = spark
      .readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    val query = input
      .writeStream
      .format("console")
      .start()

    query.awaitTermination()

  }

}
