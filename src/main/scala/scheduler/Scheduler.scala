package scheduler

import http.Request

trait Scheduler {
  def check(request: Request): Boolean
}
