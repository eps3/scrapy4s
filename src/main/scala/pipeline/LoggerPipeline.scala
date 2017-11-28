package pipeline

import org.slf4j.LoggerFactory

/**
  * 基于日志打印的Pipe
  */
object LoggerPipeline{
  val logger = LoggerFactory.getLogger(LoggerPipeline.getClass)
  def apply[T](): MultiThreadPipeline[T] = SingleThreadPipeline[T](t => logger.info(s"get new data => $t"))
}