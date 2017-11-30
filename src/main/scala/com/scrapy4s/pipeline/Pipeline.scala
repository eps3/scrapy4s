package com.scrapy4s.pipeline

import com.scrapy4s.http.Response


/**
  * 数据管道
  */
trait Pipeline[T] {
  def pipe(t: T, response: Response)

  def close()
}
