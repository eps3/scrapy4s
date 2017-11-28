package example

import java.io.File

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
      .withPipeline(HtmlSavePipeline[String](saveFolder()))
      .start()
  }

  def saveFolder() ={
    Seq(System.getProperty("user.home"), "data", "spider", "example").mkString(File.separator)
  }
}
