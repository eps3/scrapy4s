package example

import java.io.File

import http.{Request, Response}
import org.slf4j.LoggerFactory
import pipeline.{HtmlSavePipeline, LineFilePipeline}
import spider.Spider


/**
  * Created by sheep3 on 2017/11/28.
  */
object FangSpider {
  val logger = LoggerFactory.getLogger(this.getClass)
  def main(args: Array[String]): Unit = {
    /**
      * 详情页爬虫
      */
    val houseDetailSpider = Spider[String](res => {
      val price = res.regex("""id="diyprice">(\d+)<""".r).headOption.getOrElse(Seq("")).head
      val name = res.regex("""var content= '(.*?)';""".r).headOption.getOrElse(Seq("")).head
      s"${res.request.url}\t$name\t$price"
    }).withPipeline(HtmlSavePipeline[String](saveFolder("detail"))((t, r) => {
      val urlSplit = r.request.url.split("/")
      urlSplit(urlSplit.length-1)
    })).withPipeline(LineFilePipeline(saveFolder("detail.txt")))

    /**
      * 列表爬虫
      */
    val houseListSpider = Spider[String](res => {
      /**
        * 解析详情页
        */
      res.regex("""<dd class="info rel floatr">[\s\S]*?<a href='(.*?)'""".r)
        .map(u => s"http://shop.fang.com${u(0)}")
        .foreach(url => houseDetailSpider.execute(Request(url)))
      res.response.statusLine
    }).withPipeline(HtmlSavePipeline[String](saveFolder("list"))((t, r) => {
      r.request.url.substring(32, r.request.url.length - 1) + ".txt"
    })).withStartUrl({
      (1 to 51).map("http://shop.fang.com/zu/house/i3%d/".format(_)).map(Request(_))
    })

    houseDetailSpider.run()
    houseListSpider.start()
    houseDetailSpider.waitForShop()
  }

  /**
    * 包容平台的路径拼接
    *   在linux中-> ~/data/spider/fang/subFolder
    *   在windows中 -> C:\\Users\\user_name\\data\\spider\\fang\\subFolder
    * @param subFolder 子路径
    * @return
    */
  def saveFolder(subFolder: String) ={
    Seq(System.getProperty("user.home"), "data", "spider", "fang", subFolder).mkString(File.separator)
  }
}
