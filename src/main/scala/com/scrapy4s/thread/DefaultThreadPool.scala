package com.scrapy4s.thread

import java.util.concurrent.{BlockingQueue, CountDownLatch, Executor, TimeUnit}
import java.util.concurrent.atomic.AtomicBoolean

import com.scrapy4s.exception.QueueTimeOutException
import org.slf4j.LoggerFactory

/**
  * Created by sheep3 on 2017/12/6.
  */
class DefaultThreadPool(
                name: String,
                threadCount: Int,
                queue: BlockingQueue[Runnable]
                ) extends ThreadPool {

  val logger = LoggerFactory.getLogger(classOf[DefaultThreadPool])
  val startFlag = new AtomicBoolean(false)
  val countDownLatch = new CountDownLatch(threadCount)
  init()

  private def init(): Unit = {
    if (startFlag.compareAndSet(false, true)) {
      (1 to threadCount).foreach(i => {
        val thread = new Thread(() => {task()})
        thread.setName(s"pool-$name-$i")
        thread.start()
      })
    } else {
      throw new Exception("线程池已经启动")
    }
  }



  def task() = {
    try {
      while (startFlag.get()) {
        try {
          val runnable = queue.poll(1, TimeUnit.SECONDS)
          if (runnable == null) {
            throw new QueueTimeOutException()
          }
          runnable.run()
        } catch {
          case _: QueueTimeOutException =>
          case e: Exception =>
            logger.error("thread pool exception", e)
        }
      }
    } finally {
      countDownLatch.countDown()
    }
  }

  override def shutdown() = {
    startFlag.compareAndSet(true, false)
  }

  override def waitForStop() = {
    countDownLatch.await()
  }

  override def waitForStop(timeout: Long, unit: TimeUnit): Boolean = {
    countDownLatch.await(timeout, unit)
  }

  override def execute(command: Runnable) = {
    if (command == null) throw new NullPointerException()
    queue.put(command)
  }
}
