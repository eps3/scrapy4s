package example

import http.Request
import pipeline.{HtmlSavePipeline, LoggerPipeline}
import spider.SimpleSpider

object ExampleSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
          "https://segmentfault.com",
          "https://segmentfault.com",
          "https://segmentfault.com/q/1010000012185894"
        ).map(Request(_)))
      .withPipeline(LoggerPipeline[String]())
      .withPipeline(HtmlSavePipeline[String]("/Users/admin/data/spider/"))
      .start()
  }
}
