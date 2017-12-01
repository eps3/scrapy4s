package com.scrapy4s.pipeline
import com.scrapy4s.http.Response

/**
  * Created by sheep3 on 2017/12/1.
  */
class SimplePipeline(p: Response => Unit) extends Pipeline{

  override def pipe(response: Response): Unit = p(response)
}

object SimplePipeline {
  def apply(p: Response => Unit): SimplePipeline = new SimplePipeline(p)
}
