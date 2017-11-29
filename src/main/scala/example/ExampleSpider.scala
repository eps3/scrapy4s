package example

import http.Request
import pipeline.HtmlSavePipeline
import spider.SimpleSpider
import util.FileUtil

object ExampleSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
          "https://segmentfault.com",
          "https://segmentfault.com",
          "https://segmentfault.com/q/1010000012185894"
        ).map(Request(_)))
      .withPipeline(HtmlSavePipeline[String](FileUtil.pathWithHome(Seq("data", "spider", "example"))))
      .start()
  }

}
