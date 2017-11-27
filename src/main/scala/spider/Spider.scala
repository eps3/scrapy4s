package spider

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import http.Request
import org.slf4j.LoggerFactory
import pipeline.Pipeline
import scheduler.{HashSetScheduler, Scheduler}

import scala.collection.mutable.ListBuffer
import scalaj.http.{Http, HttpResponse}

/**
  * 爬虫核心类，用于组装爬虫
  * @tparam T 封装数据的bean
  */
trait Spider[T] {
  val logger = LoggerFactory.getLogger(this.getClass)
  private val startUrl = ListBuffer[Request]()
  private val pipelines = ListBuffer[Pipeline[T]]()
  private var threadCount: Int = 10
  private var scheduler: Option[Scheduler] = None

  def paser(response: HttpResponse[String]): T

  lazy private val threadPool = new ThreadPoolExecutor(threadCount, threadCount,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue[Runnable](),
    new CallerRunsPolicy())

  def withStartUrl(urls: Seq[Request]) = {
    startUrl.appendAll(urls)
    this
  }

  def withThreadCount(count: Int) = {
    threadCount = count
    this
  }

  def withPipeline(pipeline: Pipeline[T]) = {
    pipelines.append(pipeline)
    this
  }

  def withScheduler(scheduler: Scheduler) = {
    this.scheduler = Some(scheduler)
    this
  }

  def start(): Unit ={
    init()
    waitForShop()
  }

  /**
    * 初始化爬虫设置，并将初始url倒入任务池中
    */
  def init(): Unit ={
    if (scheduler.isEmpty){
      scheduler = Some(new HashSetScheduler())
    }
    startUrl.foreach(request => {
      threadPool.execute(() => {
        /**
          * 判断是否已经爬取过
          */
        if(scheduler.get.check(request)) {
          logger.info(s"crawler -> $request")
          val response: HttpResponse[String] = Http(request.url).method(request.method).asString
          val model = paser(response)
          pipelines.foreach(p => {
            p.pipe(model)
          })
        } else {
          logger.debug(s"$request has bean spider !")
        }
      })
    })
  }

  def waitForShop() = {
    threadPool.shutdown()
    while (threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
      logger.debug("wait for spider done ...")
    }
    logger.debug("spider done !")
  }
}
