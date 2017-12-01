package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.{FileUtil, HashUtil}

/**
  * Created by sheep3 on 2017/11/30.
  */
object MeiziSpider {
  def main(args: Array[String]): Unit = {
    /**
      * 下载器
      */
    val downloader = Spider().pipe(
      FileDumpPipeline(FileUtil.pathWithHome(Seq("data", "spider", "meizi")))(r => {
        HashUtil.getHash(r.url) + ".jpg"
      })
    )

    /**
      * 详情页
      */
    val detail = Spider().pipe(_.regex("""<img alt=".*?src="(.*?)" /><br />""").map(_ (0)).foreach(url => downloader.execute(Request(url))))

    /**
      * 列表页
      */
    Spider().pipe(
      _.regex("""<a target='_blank' href="(.*?)">""")
        .map(_ (0))
        .foreach(url => detail.execute(Request(url)))
    ).setStartUrl((1 to 72).map(i => Request(s"http://www.meizitu.com/a/more_$i.html")))
      .start()

    /**
      * 按任务顺序挨个等待关闭
      */
    detail.waitForShop()
    downloader.waitForShop()
  }
}
