package spider

import scalaj.http.HttpResponse

object SimpleSpider {
  def apply(): Spider[String] = Spider[String]{res: HttpResponse[String] => res.statusLine}
}
