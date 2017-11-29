package example

import java.io.File

import http.Request
import org.slf4j.LoggerFactory
import pipeline.{HtmlSavePipeline, LineFilePipeline, SingleThreadPipeline}
import spider.{SimpleSpider, Spider}


/**
  * Created by sheep3 on 2017/11/28.
  */
object FangSpider {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    /**
      * 详情页爬虫
      */
    val houseDetailSpider = new Spider[Detail](r => {
      val price: String = if (r.request.url.startsWith("http://shop.fang.com/zu/3_")) {
        r.regex("""id="diyprice">(\d+)<""".r).headOption.getOrElse(Seq("")).head
      } else {
        r.regex("""class="red20b">(\d+)<""".r).headOption.getOrElse(Seq("")).head
      }
      val name = r.regex("""var content= '(.*?)';""".r).headOption.getOrElse(Seq("")).head
      val map = r.regex("""id="iframeBaiduMap.*?src="(.*?)">""".r).head.head
      val px_py = Request(map).execute().regex("""px:"(.*?)",py:"(.*?)"""".r).head
      Detail(name, price, px_py(0),px_py(1))
    }).withPipeline(
      // 保存整个html
      HtmlSavePipeline[Detail](saveFolder("detail"))((t, r) => {
        val urlSplit = r.request.url.split("/")
        urlSplit(urlSplit.length - 1)
      })).withPipeline(
      // 保存行数据
      LineFilePipeline(saveFolder("detail.txt"))((t, r) => {
        s"${r.request.url}\t${t.line}"
      }))

    /**
      * 列表爬虫
      */
    val houseListSpider = SimpleSpider().withPipeline(
      // 保存整个网页
      HtmlSavePipeline[String](saveFolder("list"))((t, r) => {
        r.request.url.substring(32, r.request.url.length - 1) + ".txt"
      })).withPipeline(
      // 生成新的任务 -> houseDetailSpider
      SingleThreadPipeline[String]((t, r) => {
        r.regex("""<dd class="info rel floatr">[\s\S]*?<a href='(.*?)'""".r)
          .map(u => s"http://shop.fang.com${u(0)}")
          .foreach(url => houseDetailSpider.execute(Request(url)))
      }))
      // 添加启动url
      .withStartUrl({
      (1 to 1).map("http://shop.fang.com/zu/house/i3%d/".format(_)).map(Request(_))
    })

    houseDetailSpider.run()
    houseListSpider.start()
    houseDetailSpider.waitForShop()
  }

  /**
    * 包容平台的路径拼接
    * 在linux中-> ~/data/spider/fang/subFolder
    * 在windows中 -> C:\\Users\\user_name\\data\\spider\\fang\\subFolder
    *
    * @param subFolder 子路径
    * @return
    */
  def saveFolder(subFolder: String) = {
    Seq(System.getProperty("user.home"), "data", "spider", "fang", subFolder).mkString(File.separator)
  }
}

case class Detail(name: String, price: String, px: String, py: String) {
  def line = s"$name\t$price\t$px\t$py"
}