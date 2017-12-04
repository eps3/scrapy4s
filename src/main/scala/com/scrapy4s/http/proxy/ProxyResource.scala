package com.scrapy4s.http.proxy

/**
  * Created by sheep3 on 2017/12/4.
  */
trait ProxyResource {

  def get: ProxyModel

  def returnProxy(proxy: ProxyModel)

}
