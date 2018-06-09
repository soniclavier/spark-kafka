package com.vishnuviswanath.spark

import java.sql.Timestamp
import javax.swing.event.CaretEvent

import com.vishnuviswanath.util.ParameterParser
import org.apache.spark.sql.{DataFrame, SparkSession}


case class CarEvent(carId: String, speed: Option[Int], acceleration: Option[Double], timestamp: Timestamp)
object CarEvent {
  def apply(rawStr: String): CarEvent = {
    val parts = rawStr.split(",")
    CarEvent(parts(0), Some(Integer.parseInt(parts(1))), Some(java.lang.Double.parseDouble(parts(2))), new Timestamp(parts(3).toLong))
  }
}

/**
  * Created by vviswanath on 3/15/18.
  */
object SparkKafkaExample {

  def main(args: Array[String]): Unit = {
    val params = ParameterParser.parse(args)
    val sparkMaster = params.getOrElse("master", "local[*]")
    val kafkaBroker = params.getOrElse("kafka-broker", "localhost:9092")
    //create a spark session, and run it on local mode
    val spark = SparkSession.builder()
      .appName("KafkaSourceStreaming")
      .master(sparkMaster)
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    //read the source
    val df: DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("subscribe", params.getOrElse("kafka-source-topics", "cars"))
      //.schema(schema)  : we cannot set a schema for kafka source. Kafka source has a fixed schema of (key, value)
      .load()

    val fastCars = df
      .select("CAST(value as STRING")
      .map(s ⇒ CarEvent(s.getString(0)))
      .filter(car ⇒ car.speed.get > 100)

    val query = fastCars
      .writeStream.format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("topic", params.getOrElse("kafka-sink-topics", "fastcars"))
      .start()

    query.awaitTermination()


  }

}
