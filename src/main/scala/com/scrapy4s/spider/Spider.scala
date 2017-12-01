package com.scrapy4s.spider

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.scrapy4s.http.{Request, RequestConfig, Response}
import com.scrapy4s.pipeline.{Pipeline, SimplePipeline}
import com.scrapy4s.scheduler.{HashSetScheduler, Scheduler}
import org.slf4j.LoggerFactory


/**
  * 爬虫核心类，用于组装爬虫
  */
case class Spider(
                 threadCount: Int = Runtime.getRuntime.availableProcessors() * 2,
                 requestConfig: RequestConfig = RequestConfig.default,
                 startUrl: Seq[Request] = Seq.empty[Request],
                 pipelines: Seq[Pipeline] = Seq.empty[Pipeline],
                 scheduler: Scheduler = new HashSetScheduler()
               ) {
  val logger = LoggerFactory.getLogger(this.getClass)

  lazy private val threadPool = new ThreadPoolExecutor(threadCount, threadCount,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue[Runnable](),
    new CallerRunsPolicy())

  def withRequestConfig(rc: RequestConfig) = {
    new Spider(
      threadCount = threadCount,
      requestConfig = rc,
      startUrl = startUrl,
      pipelines = pipelines,
      scheduler = scheduler
    )
  }

  def withTestFunc(test_func: Response => Boolean) = {
    withRequestConfig(requestConfig.withTestFunc(test_func))
  }

  def withTimeOut(timeOut: Int) = {
    withRequestConfig(requestConfig.withTimeOut(timeOut))
  }

  def withTryCount(tryCount: Int) = {
    withRequestConfig(requestConfig.withTryCount(tryCount))
  }

  def withStartUrl(urls: Seq[Request]) = {
    new Spider(
      threadCount = threadCount,
      requestConfig = requestConfig,
      startUrl = startUrl ++ urls,
      pipelines = pipelines,
      scheduler = scheduler
    )
  }

  def withThreadCount(count: Int) = {
    new Spider(
      threadCount = count,
      requestConfig = requestConfig,
      startUrl = startUrl,
      pipelines = pipelines,
      scheduler = scheduler
    )
  }

  def withPipeline(pipeline: Pipeline): Spider = {
    new Spider(
      threadCount = threadCount,
      requestConfig = requestConfig,
      startUrl = startUrl,
      pipelines = pipelines :+ pipeline,
      scheduler = scheduler
    )
  }

  def withPipeline(p: Response => Unit): Spider = {
    withPipeline(SimplePipeline(p))
  }

  def withScheduler(s: Scheduler) = {
    new Spider(
      threadCount = threadCount,
      requestConfig = requestConfig,
      startUrl = startUrl,
      pipelines = pipelines,
      scheduler = s
    )
  }


  def start() ={
    run()
    waitForShop()
  }

  /**
    * 初始化爬虫设置，并将初始url倒入任务池中
    */
  def run() ={
    startUrl.foreach(request => {
      execute(request)
    })
    this
  }

  def waitForShop() = {
    threadPool.shutdown()
    while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
      logger.debug("wait for spider done ...")
    }
    pipelines.foreach(p => {
      p.close()
    })
    logger.info("spider done !")
  }

  /**
    * 提交请求任务到线程池
    * @param request 等待执行的请求
    */
  def execute(request: Request) = {
    threadPool.execute(() => {
      try {
        /**
          * 判断是否已经爬取过
          */
        if(scheduler.check(request)) {
          val response = request.execute(this)

          /**
            * 执行数据操作
            */
          pipelines.foreach(p => {
            try {
              p.pipe(response)
            } catch {
              case e: Exception =>
                logger.error(s"pipe error, pipe: $p, request: ${request.url}", e)
            }
          })
        } else {
          logger.debug(s"$request has bean spider !")
        }
      } catch {
        case e: Exception =>
          logger.error(s"request: ${request.url} error", e)
      }
    })
    this
  }
}
object Spider{
  def apply(): Spider = new Spider()
}