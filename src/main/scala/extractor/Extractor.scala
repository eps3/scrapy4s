package extractor

import scala.util.matching.Regex

/**
  * Created by sheep3 on 2017/11/28.
  */
object Extractor {
  def regex(r: Regex, content: String) =
    r.findAllMatchIn(content).map(m => for (i <- 1 to m.groupCount) yield m.group(i)).toList
}
