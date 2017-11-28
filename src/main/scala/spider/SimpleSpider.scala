package spider

import http.Response


object SimpleSpider {
  def apply(): Spider[String] = Spider[String]{res: Response => res.response.statusLine}
}
