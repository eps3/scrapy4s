package spider
import scalaj.http.HttpResponse

class SimpleSpider extends Spider[String]{
  override def paser(response: HttpResponse[String]) = {
    response.statusLine
  }
}
object SimpleSpider {
  def apply(): SimpleSpider = new SimpleSpider()
}
