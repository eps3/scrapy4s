package example

import http.{Request, Response}
import pipeline.HtmlSavePipeline
import spider.Spider


/**
  * Created by sheep3 on 2017/11/28.
  */
object FangSpider {
  def main(args: Array[String]): Unit = {
    Spider[String]{res: Response => res.response.statusLine}
      .withPipeline(HtmlSavePipeline[String]("/Users/admin/data/spider/fang/"))
      .withStartUrl({
        (1 to 51).map("http://shop.fang.com/zu/house/i3%d/".format(_)).map(Request(_))
      }).start()
  }
}
