package com.scrapy4s.manage

/**
  * 管理器，用于启动爬虫
  */
trait Manage {
  def run()

  def start()
}
