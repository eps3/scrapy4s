package example

import http.{Request, Response}
import pipeline.HtmlSavePipeline
import spider.SimpleSpider
import util.FileUtil

/**
  * Created by sheep3 on 2017/11/29.
  */
object ImageDownloadSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
        "http://cdnsfb.soufunimg.com/7/2017_11/24/M11/21/e6df6a967c034a99a64fe3016a309f22.jpg"
      ).map(Request(_)))
      .withPipeline(
        HtmlSavePipeline[String](FileUtil.pathWithHome(Seq("data", "spider", "image")))((_: String, r: Response) => {
          val splitArr = r.request.url.split("/")
          splitArr(splitArr.length - 1)
        })
      )
      .start()
  }

}
