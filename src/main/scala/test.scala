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

    val  word_path = "s3n://emojikeyboardlite/meta/20160517/metatab-r-00029"

    val data = sc.textFile(word_path)
      .filter { x =>
        x.split("\t").length >= 16

      }
      .map { x =>
        val item = x.split("\t")
        val deviceuid = item(0)
        val extra = item(15)
        (deviceuid, extra)
      }
      .map { case  (deviceuid, extra) =>
        val item = extra.split("&")
        var ip = ""
        for(elem <- item)
        {
          val k = elem.split("=")
          if (k(0) == "ip") {
            ip = k(1)
          }

        }
        (deviceuid,ip)
      }.filter{case (deviceuid,ip)=>
    if (ip == "") false else true
    }


    val path = "hdfs:///sx/word/"
    HDFS.removeFile(path)
    data.repartition(1).saveAsTextFile(path)
  }
}
