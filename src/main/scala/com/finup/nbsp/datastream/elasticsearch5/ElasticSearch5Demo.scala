package com.finup.nbsp.datastream.elasticsearch5

import java.net.InetSocketAddress
import java.util.Properties

import com.finup.nbsp.datastream.kafka.KafkaStringSchema
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.elasticsearch2.{ElasticsearchSink, ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer08, FlinkKafkaProducer08}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
  * Created by wq on 16/9/26.
  *
  *
  *
PUT http://honest:9200/flink-test2
{
  "settings": {
    "index": {
      "number_of_shards": 1,
      "number_of_replicas": 0
    }
  }
}

PUT /flink-test2/_mapping/fink_test_data2
{
    "properties": {
        "data": {
            "type": "string",
            "index": "not_analyzed"
        },
        "log": {
            "type": "string"
        }
    }
}
  */
object ElasticSearch5Demo {

  private val ZOOKEEPER_HOST = "localhost:2181"
  private val KAFKA_BROKER = "localhost:9092"
  private val KAFKA_GROUP = "input2"

  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // configure Kafka consumer
    val kafkaProps = new Properties
    kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER_HOST)
    kafkaProps.setProperty("bootstrap.servers", KAFKA_BROKER)
    kafkaProps.setProperty("group.id", KAFKA_GROUP)


    val kafkaConsumer = new FlinkKafkaConsumer08[String](
      "input2", KafkaStringSchema, kafkaProps)

    val kafkaproducer = new FlinkKafkaProducer08[String](
      "localhost:9092",
      "output",
      KafkaStringSchema
    )

    val ds = env.addSource(kafkaConsumer)

    // configure Elasticsearch
    val config: java.util.Map[String,String] = new java.util.HashMap[String, String]
    config.put("bulk.flush.max.actions", "1")
    config.put("cluster.name", "elasticsearch")

    val transports  = new java.util.ArrayList[InetSocketAddress]()
    transports.add(new InetSocketAddress("honest", 9300))

    val elasticsearchSink = new ElasticsearchSinkFunction[String] {
      def createIndexRequest(element: String): IndexRequest = {
        val map = new java.util.HashMap[String, String]
        if(element.contains(",")){
          val k = element.split(",")
          map.put("log1",k(1))
          map.put("data1", k(0))
        }
        println("SENDING: " + element)
        Requests.indexRequest.index("flink-test2").`type`("fink_test_data2").source(map)
      }

      override def process(element: String, ctx: RuntimeContext, indexer: RequestIndexer) {
        indexer.add(createIndexRequest(element))
      }
    }

    val essink  = new ElasticsearchSink(config,transports,elasticsearchSink)

    ds.addSink(essink)

    env.execute("Flink ElasticSearch2 Example")

  }

}
