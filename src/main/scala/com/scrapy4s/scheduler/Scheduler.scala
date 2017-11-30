package com.scrapy4s.scheduler

import com.scrapy4s.http.Request


trait Scheduler {
  def check(request: Request): Boolean
}
