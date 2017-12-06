package com.scrapy4s.scheduler.redis

import redis.clients.jedis.{Jedis, JedisPool}

/**
  * Created by sheep3 on 2017/12/6.
  */
class RedisHandler(pool: JedisPool) {
  def handler(function: (Jedis) => Boolean): Boolean = {
    var jedis: Jedis = null
    try {
      jedis = pool.getResource
      val result = function(jedis)
      jedis.close()
      result
    } finally {
      if (jedis != null) {
        jedis.close()
      }
    }
  }
}
