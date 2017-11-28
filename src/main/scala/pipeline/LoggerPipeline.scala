package pipeline

import org.slf4j.LoggerFactory

import scalaj.http.HttpResponse

/**
  * 基于日志打印的Pipe
  */
class LoggerPipeline[T] extends SingleThreadPipeline[T] {
  override val logger = LoggerFactory.getLogger(classOf[LoggerPipeline[T]])
  override def execute(t: T, response: HttpResponse[String]): Unit = {
    logger.info(s"get new data => $t")
  }
}
object LoggerPipeline {

  def apply[T](): LoggerPipeline[T] = new LoggerPipeline[T]
}