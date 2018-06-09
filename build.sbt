name := "spark-kafka"

version := "1.0"

scalaVersion := "2.11.9"

val sparkVersion = "2.3.0"
val kafkaVersion = "0.10.2.1"

resolvers += Resolver.mavenLocal

libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion exclude("log4j", "log4j") exclude("org.spark-project.spark", "unused")
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % Test
libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"


docker exec -it spark-kafka spark-submit \
  --master spark://172.17.0.2:7077 \
  --class com.vishnuviswanath.spark.streaming.KafkaSourceStreaming \
  /spark_23-assembly-1.0.jar --master spark://172.17.0.2:7077 --kafka-broker 172.17.0.2:9092