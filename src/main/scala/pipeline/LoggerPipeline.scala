package pipeline

import org.slf4j.LoggerFactory

/**
  * 基于日志打印的Pipe
  * @tparam T 处理后的类型
  */
class LoggerPipeline[T] extends Pipeline[T]{
  val logger = LoggerFactory.getLogger(classOf[LoggerPipeline[T]])
  override def pipe(t: T): Unit = {
    logger.info(s"get new data => $t")
  }
}
