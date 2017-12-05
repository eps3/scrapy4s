package com.scrapy4s.scheduler

import com.scrapy4s.http.Request
import org.scalatest.FunSuite

class HashSetSchedulerSpec extends FunSuite{

  test("Scheduler mast check to false") {
    val scheduler = HashSetScheduler()
    scheduler.check(Request("http://www.scalatest.org/user_guide/selecting_a_style"))
    assert(!scheduler.check(Request("http://www.scalatest.org/user_guide/selecting_a_style")))
  }



}
