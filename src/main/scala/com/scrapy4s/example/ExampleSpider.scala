package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.HtmlSavePipeline
import com.scrapy4s.spider.SimpleSpider
import com.scrapy4s.util.FileUtil


object ExampleSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
          "https://segmentfault.com",
          "https://segmentfault.com",
          "https://segmentfault.com/q/1010000012185894"
        ).map(Request(_)))
      .withPipeline(HtmlSavePipeline(FileUtil.pathWithHome(Seq("data", "spider", "example"))))
      .start()
  }
}
