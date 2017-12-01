package com.scrapy4s.pipeline

import com.scrapy4s.http.{Request, Response}


/**
  * 数据管道
  */
trait Pipeline {

  def pipe(response: Response): Unit

  def pipeForRequest(response: Response): Seq[Request] = {
    pipe(response)
    Seq.empty[Request]
  }

  def close() = {

  }
}

object Pipeline {

}