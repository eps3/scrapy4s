package com.scrapy4s.pipeline

import com.scrapy4s.http.Response


/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class SingleThreadPipeline extends MultiThreadPipeline(1) {
}

object SingleThreadPipeline {
  def apply(p: (Response) => Unit): SingleThreadPipeline = {
    (response: Response) => p(response)
  }
}
