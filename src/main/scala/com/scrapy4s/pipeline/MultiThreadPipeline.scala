package com.scrapy4s.pipeline

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory


/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class MultiThreadPipeline(threadCount: Int) extends Pipeline {
  val logger = LoggerFactory.getLogger(this.getClass)
  lazy private val threadPool = new ThreadPoolExecutor(threadCount, threadCount,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue[Runnable](),
    new CallerRunsPolicy())

  override def pipe(response: Response): Unit = {
    threadPool.execute(() => {
      logger.debug(s"pipe -> exec ${response.url}")
      execute(response)
    })
  }

  def execute(response: Response): Unit

  override def close(): Unit = {
    threadPool.shutdown()
    while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
      logger.debug("wait for spider done ...")
    }
    shutdownHook()
    logger.debug("spider done !")
  }

  /**
    * 给子类用于资源收尾
    */
  def shutdownHook() = {
  }
}

object MultiThreadPipeline {
  def apply[T](threadCount: Int = Runtime.getRuntime.availableProcessors() * 2)(p: Response => Unit): MultiThreadPipeline = {
    new MultiThreadPipeline(threadCount) {
      override def execute(response: Response): Unit = p(response)
    }
  }
}
