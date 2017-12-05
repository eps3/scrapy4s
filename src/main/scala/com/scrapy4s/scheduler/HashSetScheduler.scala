package com.scrapy4s.scheduler

import java.io.{File, FileWriter}
import java.{lang, util}
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

import com.alibaba.fastjson.{JSON, JSONObject}
import com.scrapy4s.http.Request
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.FileUtil
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.io.Source

class HashSetScheduler(path: String) extends Scheduler {
  val logger = LoggerFactory.getLogger(classOf[HashSetScheduler])
  private val hashSet: ConcurrentHashMap.KeySetView[Request, lang.Boolean] =ConcurrentHashMap.newKeySet[Request]()

  private val okList = Collections.synchronizedList(new util.ArrayList[Request]())
  private val allList = Collections.synchronizedList(new util.ArrayList[Request]())

  override def check(request: Request): Boolean = {
    logger.debug(s"check request => $request")
    val result = hashSet.add(request)
    if (result) {
      allList.add(request)
    }
    result
  }

  override def ok(request: Request): Unit = {
    okList.add(request)
  }

  /**
    * 加载配置，并提交任务
    * @param spider 目标的spider
    */
  override def load(spider: Spider): Unit = {
    val okFileName =  new File(path, spider.name + "_ok.spider")
    val allFileName =  new File(path, spider.name + "_all.spider")
    if (okFileName.exists() && allFileName.exists()) {
      Source.fromFile(okFileName).getLines().foreach(line => {
        if (line.nonEmpty) {
          val request = RequestJson.load(line).request
          ok(request)
          check(request)
        }
      })
      logger.info(s"success load file: ok -> ${okFileName.getAbsolutePath}")
      Source.fromFile(allFileName).getLines().foreach(line => {
        if (line.nonEmpty) {
          spider.execute(RequestJson.load(line).request)
        }
      })
      logger.info(s"success load file: all -> ${allFileName.getAbsolutePath}")
    }
  }

  override def save(spider: Spider): Unit = {
    val okFileName =  new File(path, spider.name + "_ok.spider")
    val allFileName =  new File(path, spider.name + "_all.spider")
    val okWriter = new FileWriter(okFileName)
    val AllWriter = new FileWriter(allFileName)

    okList.asScala.foreach(r => {
      okWriter.write(s"${RequestJson.load(r).toJson}\n")
    })
    okWriter.close()
    logger.info(s"success save file: ok -> ${okFileName.getAbsolutePath}")

    allList.asScala.foreach(r => {
      AllWriter.write(s"${RequestJson.load(r).toJson}\n")
    })
    AllWriter.close()
    logger.info(s"success save file: all -> ${allFileName.getAbsolutePath}")
  }
}
object HashSetScheduler {
  def apply(path: String = FileUtil.tempDir): HashSetScheduler = new HashSetScheduler(path)
}
class RequestJson {
  @BeanProperty
  var url: String = _

  @BeanProperty
  var method: String = _

  @BeanProperty
  var data: util.Map[String, util.List[String]] = _

  @BeanProperty
  var header: util.Map[String, util.List[String]] = _

  def request = {
    Request(
      url = this.url,
      method = this.method,
      data = this.data.asScala.map(d => d._1 -> d._2.asScala.toList).toMap,
      header = this.header.asScala.map(d => d._1 -> d._2.asScala.toList).toMap
    )
  }

  def toJson: String = {
    val jsonObj = new JSONObject()
    jsonObj.put("url", url)
    jsonObj.put("method", method)
    jsonObj.put("header", header)
    jsonObj.put("data", data)
    jsonObj.toJSONString
  }
}

object RequestJson {
  def load(request: Request): RequestJson = {
    val json = new RequestJson()
    json.url = request.url
    json.method = request.method
    json.data = request.data.map(d => d._1 -> d._2.asJava).asJava
    json.header = request.header.map(d => d._1 -> d._2.asJava).asJava
    json
  }

  def load(json: String): RequestJson = {
    JSON.parseObject(json, classOf[RequestJson])
  }
}