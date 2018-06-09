package com.vishnuviswanath.spark

import org.apache.spark.sql.SparkSession

/**
  * Created by vviswanath on 3/15/18.
  */
object HelloWorld {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val names = spark.readStream.text("/Users/vviswanath/spark_meetup/names")

    val helloNames = names.as[String].map(name ⇒ s"hello $name")

    val query = helloNames.writeStream.format("console").start()

    query.awaitTermination()
  }

}
