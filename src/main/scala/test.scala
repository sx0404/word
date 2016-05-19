/**
  * Created by SX_H on 2016/5/19.
  */
import java.text.SimpleDateFormat

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer


object test {

  def main(args:Array[String])={
    println("11111111")
    val conf = new SparkConf()
    val sc = new SparkContext(conf)
    val hadoopConf = sc.hadoopConfiguration
    val awsAccessKeyId = args(0)
    val awsSecretAccessKey = args(1)

    hadoopConf.set("fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")

    hadoopConf.set("fs.s3n.awsAccessKeyId", awsAccessKeyId)

    hadoopConf.set("fs.s3n.awsSecretAccessKey", awsSecretAccessKey)

    val  word_path = "s3n://emojikeyboardlite/word/20160516/language=en_US/21_172.31.28.24_1463461307337_GMT_minus_0800"

    val data = sc.textFile(word_path)
        .filter{x =>
          x.split("\t").length >= 4

        }
      .map { x =>
        val item = x.split("\t")
        val application = item(0)
        val word = item(1)
        val deviceuid = item(2)
        val ts = item(3)
        ((application,word,deviceuid),ts)
      }
        .groupByKey()
      .sortBy(x =>
        x._2
      ).collect()
  for(elem <- data){
    println(elem._1._1+"..." +elem._1._2+"..." +elem._1._3+"..." +elem._2+"..." +" this is word")
  }
  }
}
