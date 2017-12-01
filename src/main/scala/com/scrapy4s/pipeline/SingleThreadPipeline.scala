package com.scrapy4s.pipeline


/**
  * Created by sheep3 on 2017/11/28.
  */
class SingleThreadPipeline(pipe: Pipeline) extends MultiThreadPipeline(1, pipe) {
}

object SingleThreadPipeline {
  def apply(pipe: Pipeline): SingleThreadPipeline = new SingleThreadPipeline(pipe)
}
