package com.scrapy4s.http.proxy

import scala.beans.BeanProperty

/**
  * Created by sheep3 on 2017/12/4.
  */
case class ProxyModel(
                  @BeanProperty ip: String,
                  @BeanProperty port: Int
                )
