package com.scrapy4s.pipeline

import com.scrapy4s.http.{Request, Response}


/**
  * 数据管道
  */
trait Pipeline {

  /**
    * 处理数据
    *
    * @param response 待处理的请求
    */
  def pipe(response: Response): Unit

  /**
    * 处理数据，返回接下来的爬取任务
    *
    * @param response 待处理的请求
    * @return 接下来的爬取任务
    */
  def pipeForRequest(response: Response): Seq[Request] = {
    pipe(response)
    Seq.empty[Request]
  }

  /**
    * 资源回收
    */
  def close() = {

  }
}

object Pipeline {

}