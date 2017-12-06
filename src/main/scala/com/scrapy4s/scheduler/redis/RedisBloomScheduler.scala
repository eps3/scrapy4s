package com.scrapy4s.scheduler.redis

import java.math.BigInteger
import java.security.MessageDigest

import com.scrapy4s.http.Request
import com.scrapy4s.scheduler.Scheduler
import com.scrapy4s.spider.Spider
import redis.clients.jedis.JedisPool

/**
  * Created by sheep3 on 2017/12/6.
  */
class RedisBloomScheduler(prefix: String, capacity: Long, errorRate: Double, pool: JedisPool)
  extends RedisHandler(pool)
    with Scheduler {


  val salt: Array[Int] = Array[Int](543, 460, 171, 876, 796, 607, 650, 81, 837, 545, 591, 946, 846, 521, 913, 636, 878, 735, 414, 372,
    344, 324, 223, 180, 327, 891, 798, 933, 493, 293, 836, 10, 6, 544, 924, 849, 438, 41, 862, 648, 338,
    465, 562, 693, 979, 52, 763, 103, 387, 374, 349, 94, 384, 680, 574, 480, 307, 580, 71, 535, 300, 53,
    481, 519, 644, 219, 686, 236, 424, 326, 244, 212, 909, 202, 951, 56, 812, 901, 926, 250, 507, 739, 371,
    63, 584, 154, 7, 284, 617, 332, 472, 140, 605, 262, 355, 526, 647, 923, 199, 518)

  val bitSetSize: Double = Math.ceil(capacity * (Math.log(Math.E) / Math.log(2)) * (Math.log(1 / errorRate) / Math.log(2)))
  val k: Int = Math.ceil(Math.log(2) * bitSetSize / capacity).toInt
  val mem: Double = Math.ceil(bitSetSize / 8 / 1024 / 1024)
  val blockNum: Int = Math.ceil(mem / 512).toInt
  val blockSize: Long = Math.pow(2, 32).toLong
  val realBitSetSize: Long = blockNum * blockSize

  // 申请内存
  handler(jedis => {
    for (i <- 0 until k) {
      val keyName = s"$prefix$i"
      val offset = blockSize-1
      if (!jedis.getbit(keyName, offset)) {
        jedis.setbit(keyName, offset, false)
      }
    }
    true
  })

  def getHash(str: String): BigInteger = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    new BigInteger(1, md5Digest.digest(str.getBytes("UTF-8")))
  }

  override def load(spider: Spider): Unit = {
    // TODO: 进度恢复
  }

  override def save(spider: Spider): Unit = {
    // TODO: 进度存储
  }

  override def check(request: Request): Boolean = {
    handler(jedis => {
      var notExistFlag = false
      for (i <- 0 until k) {
        val hash = getHash(request.toString + salt(i).toString)
          .remainder(BigInteger.valueOf(realBitSetSize))
          .longValueExact()
        val keyName = s"$prefix${hash / blockSize}"
        val offset = hash % blockSize
        if (!jedis.getbit(keyName, offset)) {
          notExistFlag = true
          jedis.setbit(keyName, offset, true)
        }
      }
      !notExistFlag
    })
  }

  override def ok(request: Request): Unit = {
    // TODO: 记录数据流
  }
}
