package com.scrapy4s.http

import com.scrapy4s.spider.Spider
import org.asynchttpclient

/**
  * Created by sheep3 on 2017/12/13.
  */
class RequestWithData[T](url: String, val message: T) extends Request(url) {
  override def packageResponse(response: asynchttpclient.Response) = new ResponseWithData(this, response, message)
}

object RequestWithData {
  def apply[T](url: String, data: T): RequestWithData[T] = new RequestWithData(url, data)

  def main(args: Array[String]): Unit = {
    val testSpider = Spider("test").pipe{
      case r: ResponseWithData[_] =>
        println("get message hello")
        assert(r.message.equals("hello"))
      case _ =>
        println("奇怪的路由")
    }.setStartUrl(RequestWithData("http://tool.oschina.net/codeformat/json","hello"))
    testSpider.start()
  }
}
