package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.manage.CmdManage
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.{FileUtil, HashUtil, ThreadUtil}

/**
  * Created by sheep3 on 2017/11/30.
  */
object MeiziSpider {
  def main(args: Array[String]): Unit = {
    /**
      * 下载器
      */
    val downloader = Spider("downloader")
      .setTimeOut(10 * 1000)
      .pipe(
        FileDumpPipeline(FileUtil.pathWithHome(Seq("data", "spider", "meizi")))(r => {
          HashUtil.getHash(r.url) + ".jpg"
        })
      )

    /**
      * 详情页
      */
    val detail = Spider("detail")
      .pipe(_.regex("""<img alt=".*?src="(.*?)" /><br />""")
        .map(_ (0))
        .foreach(url => downloader.execute(Request(url))))

    /**
      * 列表页
      */
    val list = Spider("list").pipeForRequest(r => {
      /**
        * 获取详情页
        */
      r.regex("""<a target='_blank' href="(.*?)">""")
        .map(_ (0))
        .foreach(url => detail.execute(Request(url)))

      /**
        * 获取新的列表页
        */
      r.regex("""<li><a href='(.*?)'>""")
        .map(_ (0))
        .map(i => Request(s"http://www.meizitu.com/a/$i"))
    })
      .setStartUrl(Request(s"http://www.meizitu.com/a/more_1.html"))

    /**
      * 注册爬虫，并启动
      */
    CmdManage()
      .setThreadCount(ThreadUtil.core * 3)
      .register(downloader)
      .register(detail)
      .register(list)
      .start()

  }
}
