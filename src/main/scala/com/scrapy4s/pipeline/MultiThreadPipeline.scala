package com.scrapy4s.pipeline

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory


/**
  * 多线程处理器
  * Created by sheep3 on 2017/11/28.
  */
class MultiThreadPipeline(threadCount: Int,
                                  pipe: Pipeline
                                 ) extends Pipeline {

  val logger = LoggerFactory.getLogger(classOf[MultiThreadPipeline])

  lazy private val threadPool = new ThreadPoolExecutor(threadCount, threadCount,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue[Runnable](),
    new CallerRunsPolicy())

  override def pipe(response: Response): Unit = {
    threadPool.execute(() => {
      logger.debug(s"pipe -> exec ${response.url}")
      pipe.pipe(response)
    })
  }

  override def close(): Unit = {
    threadPool.shutdown()
    while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
      logger.debug("wait for spider done ...")
    }
    pipe.close()
    logger.debug("spider done !")
  }

}

object MultiThreadPipeline {
  def apply[T](pipe: Pipeline)
              (implicit threadCount: Int = Runtime.getRuntime.availableProcessors() * 2): MultiThreadPipeline = new MultiThreadPipeline(threadCount, pipe)
}
