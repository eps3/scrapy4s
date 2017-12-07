package com.scrapy4s.scheduler

import com.scrapy4s.http.Request
import com.scrapy4s.spider.Spider

/**
  * 调度器接口
  *
  *   用于爬虫的url去重
  */
trait Scheduler {

  /**
    * 加载历史记录
    *
    * @param spider 爬虫配置
    */
  def load(spider: Spider)

  /**
    * 保存爬取历史
    *
    * @param spider 爬虫配置
    */
  def save(spider: Spider)

  /**
    * 验证是否爬取过该请求
    *   若从未爬取过，则添加到调度器，并返回true
    *
    * @param request 待验证的目标请求
    * @return
    */
  def check(request: Request): Boolean

  /**
    * 记录已经完成的请求
    *
    * @param request 待记录请求
    */
  def ok(request: Request)
}
