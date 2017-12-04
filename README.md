### scrapy4s

>  易用的scala爬虫框架



#### 1. 组件

- scheduler -> 调度器，用于url调度与去重
- pipeline -> 管道，用于数据的后续处理
- http -> http组件，用于http相关封装
- spider -> 爬虫核心类，用于构建爬虫



#### 2. 使用

```scala
import com.scrapy4s.pipeline.HtmlSavePipeline
import com.scrapy4s.scheduler.HashSetScheduler
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.FileUtil


object ExampleSpider {
  def main(args: Array[String]): Unit = {
    val spider = Spider()
      // 设置超时时间
      .setTimeOut(1000 * 5)
      // 设置线程数
      .setThreadCount(1)
      // 设置调度器
      .setScheduler(HashSetScheduler())
      // 设置请求成功的测试方法
      .setTestFunc(_.statusCode == 200)
      // 设置请求重试次数
      .setTryCount(3)
      // 设置起始Url
      .setStartUrl("https://www.v2ex.com")
      .pipe(r => {
        r.xpath("""//span[@class="item_title"]/a/text()""").foreach(println)
      })
      // 设置数据处理器
      .pipe(HtmlSavePipeline(FileUtil.pathWithHome(Seq("data", "spider", "example"))))
      .run()
    spider.waitForShop()
  }
}
```

##### 2.1 例子

 - [com.scrapy4s.example.MeiziSpider](./src/main/scala/com/scrapy4s/example/MeiziSpider.scala) 妹子图爬虫

#### 3 pipeline 

> 是爬虫数据处理核心，对于多个pipeline，每个请求的数据都会在每个pipeline里执行

##### 2.1.1 LineFilePipeline 行数据Pipeline

```scala
import com.scrapy4s.pipeline.LineFilePipeline
import com.scrapy4s.http.Response
// 第一个参数是目标文件
// 第二个参数是需要存的行数据解析函数
val lineFilePipeline = LineFilePipeline("~/data/line.txt")(response => {
  s"${response.url} ${response.statusCode}"
})
```



##### 2.1.2 FileDumpPipeline 文件下载Pipeline

```scala
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.http.Response
// 第一个参数是存放的文件夹
// 第二个参数是文件名生成函数
val fileDumpPipeline = FileDumpPipeline("~/data/")(response => {
  val splitArr = response.url.split("/")
  splitArr(splitArr.length - 1)
})
```



##### 2.1.3 Other Pipeline

- LoggerPipeline 打印日志的Pipeline
- MultiThreadPipeline 多线程Pipeline
- SingleThreadPipeline 单线程Pipeline



#### Update Log

- 2017-12-01
  - 简化了pipeline模型，为多线程pipe提供了fork方法
  - 添加了pipeForRequest方法，为爬虫的继续添加驱动
  - 修复了设置超时时间失败的bug
- 2017-11-27
  - 完成了简单初版 例子代码
- 2017-11-30
  - 完成了简单爬虫的大部分功能


#### TODO

- v1: 完成基于传统Java并发API版本的Scrapy
  - ~~为Request添加xpath的封装~~
  - ~~添加代理支持~~
  - ~~添加更多的Pipeline支持~~
  - 添加进度保存，重启恢复
- v2: 添加异步io以及Akka调度机制
- v3: 添加web监控管理器