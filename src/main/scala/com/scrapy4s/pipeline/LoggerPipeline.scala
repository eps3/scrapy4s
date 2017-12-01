package com.scrapy4s.pipeline

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory


/**
  * 基于日志打印的Pipe
  */
class LoggerPipeline extends Pipeline {
  val logger = LoggerFactory.getLogger(classOf[LoggerPipeline])

  override def pipe(response: Response): Unit = {
    logger.info(s"get new data => ${response.body}")
  }

}
object LoggerPipeline {

  val default = new LoggerPipeline

  def apply(): LoggerPipeline = default
}