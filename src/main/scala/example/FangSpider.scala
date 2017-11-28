package example

import http.{Request, Response}
import pipeline.HtmlSavePipeline
import spider.Spider


/**
  * Created by sheep3 on 2017/11/28.
  */
object FangSpider {
  def main(args: Array[String]): Unit = {
    val houseListSpider = Spider[String](res => {
      res.response.statusLine
    }).withPipeline(HtmlSavePipeline[String]("/Users/admin/data/spider/fang/")((t, r) => {
      r.request.url.substring(32, r.request.url.length - 1)
    })).withStartUrl({
      (1 to 51).map("http://shop.fang.com/zu/house/i3%d/".format(_)).map(Request(_))
    })
    houseListSpider.start()
  }
}
