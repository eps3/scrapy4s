package example

import http.Request
import pipeline.HtmlSavePipeline
import spider.Spider

import scalaj.http.HttpResponse

/**
  * Created by sheep3 on 2017/11/28.
  */
object FangSpider {
  def main(args: Array[String]): Unit = {
    Spider[String]{res: HttpResponse[String] => res.statusLine}
      .withPipeline(HtmlSavePipeline[String]("/Users/admin/data/spider/fang/"))
      .withStartUrl({
        (1 to 51).map("http://shop.fang.com/zu/house/i3%d/".format(_)).map(Request(_))
      }).start()
  }
}
