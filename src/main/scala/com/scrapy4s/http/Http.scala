package com.scrapy4s.http

trait Http {
  def execute(request: Request): Response
}
