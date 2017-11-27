### Scala 版本Scrapy

#### 1. 组件

- scheduler -> 调度器，用于url调度与去重
- pipeline -> 管道，用于数据的后续处理
- http -> http组件，用于http相关封装
- manage -> 管理器，用于启动爬虫

##### 1.1 Scheduler接口

- check(req: Request) 判断是否已经爬取


#### Update Log

- 2017-11-27
  - 完成了简单初版 例子代码 -> example.ExampleSpider


#### TODO

- v1: 完成基于传统Java并发API版本的Scrapy
- v2: 添加异步io以及Akka调度机制
- v3: 添加web监控管理器