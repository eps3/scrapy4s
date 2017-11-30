package com.scrapy4s.pipeline

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory


/**
  * 基于日志打印的Pipe
  */
class LoggerPipeline[T] extends SingleThreadPipeline[T] {
  override val logger = LoggerFactory.getLogger(classOf[LoggerPipeline[T]])
  override def execute(t: T, response: Response): Unit = {
    logger.info(s"get new data => $t")
  }
}
object LoggerPipeline {

  def apply[T](): LoggerPipeline[T] = new LoggerPipeline[T]
}