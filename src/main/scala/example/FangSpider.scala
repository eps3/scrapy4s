package example

import java.io.File

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
    }).withPipeline(HtmlSavePipeline[String](saveFolder("list"))((t, r) => {
      r.request.url.substring(32, r.request.url.length - 1) + ".txt"
    })).withStartUrl({
      (1 to 51).map("http://shop.fang.com/zu/house/i3%d/".format(_)).map(Request(_))
    })
    houseListSpider.start()

    val detailSpider = Spider[String](res => {
      res.response.statusLine
    }).withPipeline(HtmlSavePipeline[String](saveFolder("detail"))((t, r) => {
      r.request.url.substring(32, r.request.url.length - 1) + ".txt"
    }))

  }

  def saveFolder(subFolder: String) ={
    Seq(System.getProperty("user.home"), "data", "spider", "fang", subFolder).mkString(File.separator)
  }
}
