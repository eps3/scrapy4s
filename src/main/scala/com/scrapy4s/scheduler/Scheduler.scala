package com.scrapy4s.scheduler

import com.scrapy4s.http.Request
import com.scrapy4s.spider.Spider


trait Scheduler {

  def load(spider: Spider)

  def save(spider: Spider)

  def check(request: Request): Boolean

  def ok(request: Request)
}
