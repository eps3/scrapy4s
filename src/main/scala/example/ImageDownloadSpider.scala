package example

import http.{Request, Response}
import pipeline.{FileDumpPipeline, HtmlSavePipeline}
import spider.SimpleSpider
import util.FileUtil

/**
  * Created by sheep3 on 2017/11/29.
  */
object ImageDownloadSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
        "https://www.retail-week.com/pictures/1180xany/7/3/9/1357739_Asda.jpg"
      ).map(Request(_)))
      .withPipeline(
        FileDumpPipeline[String](FileUtil.pathWithHome(Seq("data", "spider", "image")))((_: String, r: Response) => {
          val splitArr = r.request.url.split("/")
          splitArr(splitArr.length - 1)
        })
      )
      .start()
  }
}
