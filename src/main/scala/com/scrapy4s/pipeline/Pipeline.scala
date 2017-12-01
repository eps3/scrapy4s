package com.scrapy4s.pipeline

import com.scrapy4s.http.Response


/**
  * 数据管道
  */
trait Pipeline {
  def pipe(response: Response)

  def close()
}

object Pipeline {

}