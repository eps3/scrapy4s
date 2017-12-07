### scrapy4s

>  易用的scala爬虫框架



#### 1. 功能

- 多线程抓取，Url去重
- 支持代理设置
- 方便简单的xpath、正则API
- 方便简单的重试机制
- 进度保存，断点续爬



#### 2. 使用

```scala
import com.scrapy4s.pipeline.HtmlSavePipeline
import com.scrapy4s.scheduler.HashSetScheduler
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.FileUtil


object ExampleSpider {
  def main(args: Array[String]): Unit = {
    val spider = Spider("example")
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
      // 设置保存进度
      .setHistory(true)
      .pipe(r => {
        r.xpath("""//span[@class="item_title"]/a/text()""").foreach(println)
      })
      // 设置数据处理器
      .pipe(HtmlSavePipeline(FileUtil.pathWithHome(Seq("data", "spider", "example"))))
      .run()
  }
}
```

##### 2.1 例子

 - [com.scrapy4s.example.MeiziSpider](./src/main/scala/com/scrapy4s/example/MeiziSpider.scala) 妹子图爬虫



#### 3. API说明

##### 3.1 Spider

| 方法                                       | 说明                                       | 默认值      |
| ---------------------------------------- | ---------------------------------------- | -------- |
| setTimeOut(timeOut: Int)                 | 超时时间                                     | 50000ms  |
| setTryCount(tryCount: Int)               | 重试次数                                     | 10       |
| setProxyResource(proxyResource: ProxyResource) | 代理资源                                     | 无        |
| setThreadCount(count: Int)               | 线程数                                      | core * 2 |
| setHistory(history: Boolean)             | 保存历史进度，如果需要保存进度，必须给Spider传入name属性```val spider = Spider("名称")``` | false    |
| setTestFunc(test_func: Response => Boolean) | 请求成功测试方法，错误则重试                           |          |
| pipe(pipeline: Pipeline)                 | 传入数据的处理方法                                |          |
| pipeForRequest(request: Response => Seq[Request]) | 传入数据处理并包含后续请求的方案                         |          |
| fork(pipeline: Pipeline)(implicit threadCount: Int) | 传入数据处理方法，不过该方法将会在一个新的线程池中执行              |          |
| setStartUrl(urls: Seq[Request])          | 起始url                                    |          |
| setStartUrl(url: Request)                | 起始url                                    |          |
| setStartUrl(url: String)                 | 起始url                                    |          |



##### 3.2 pipeline

> 是爬虫数据处理核心，对于多个pipeline，每个请求的数据都会在每个pipeline里执行

###### 3.2.1 LineFilePipeline 行数据Pipeline

```scala
import com.scrapy4s.pipeline.LineFilePipeline
import com.scrapy4s.http.Response
// 第一个参数是目标文件
// 第二个参数是需要存的行数据解析函数
val lineFilePipeline = LineFilePipeline("~/data/line.txt")(response => {
  Some(s"${response.url} ${response.statusCode}")
})
```

###### 3.2.2 FileDumpPipeline 文件下载Pipeline

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

###### 3.2.3 Other Pipeline

- LoggerPipeline 打印日志的Pipeline
- MultiThreadPipeline 多线程Pipeline
- SingleThreadPipeline 单线程Pipeline




##### 3.3 Manage

> 对于不同的Spider，将会在不同的线程池中执行数据抓取任务，这也会导致多个线程池难以管理，使用Manage，可以使所有的Spider公用同一个线程池，并可以设置一些Spider的通用配置。

e.g.

```scala
val downloader = Spider("downloader") ...
val detail = Spider("detail") ...
val list = Spider("list") ...
CmdManage()
      .setThreadCount(ThreadUtil.core * 3)
      .register(downloader)
      .register(detail)
      .register(list)
      .start()
```



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
  - ~~添加进度保存，重启恢复~~
  - 添加redis调度器
  - 添加监控组件
- v2: 添加异步io以及Akka调度机制
- v3: 添加web监控管理器