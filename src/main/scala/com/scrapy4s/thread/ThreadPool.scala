package com.scrapy4s.thread

import java.util.concurrent.{Executor, TimeUnit}

/**
  * Created by sheep3 on 2017/12/7.
  */
trait ThreadPool extends Executor{

  /**
    * 停止所有线程
    */
  def shutdown()

  /**
    * 阻塞等待爬取完成
    */
  def waitForStop()

  /**
    * 等待爬取完成
    *
    * @param timeout 等待时间
    * @param unit 时间的单位
    * @return 爬取完成则返回true
    */
  def waitForStop(timeout: Long, unit: TimeUnit): Boolean

}
