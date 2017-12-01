package com.scrapy4s.pipeline
import com.scrapy4s.http.{Request, Response}

class RequestPipeline(request: Response => Seq[Request]) extends Pipeline {
  override def pipe(response: Response): Unit = {}

  override def pipeForRequest(response: Response) = request(response)
}

object RequestPipeline {
  def apply(request: Response => Seq[Request]): RequestPipeline = new RequestPipeline(request)
}
