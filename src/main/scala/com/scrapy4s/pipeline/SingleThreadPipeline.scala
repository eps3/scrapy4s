package com.scrapy4s.pipeline

import com.scrapy4s.http.Response


/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class SingleThreadPipeline[T] extends MultiThreadPipeline[T](1) {
}

object SingleThreadPipeline {
  def apply[T](p: (T, Response) => Unit): SingleThreadPipeline[T] = {
    (t: T, response: Response) => p(t, response)
  }
}
