package pipeline

import java.io.{File, FileWriter}
import java.nio.file.Paths
import java.security.MessageDigest

import scalaj.http.HttpResponse

/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class HtmlSavePipeline[T](fileDir: String) extends SingleThreadPipeline[T] {

//  val dir = new File(fileDir).getCanonicalPath

  def fileName(t: T, response: HttpResponse[String]): String

  override def execute(t: T, response: HttpResponse[String]): Unit = {
    val file = new File(fileDir , fileName(t, response))
    val writer = new FileWriter(file)
    writer.write(response.body)
    writer.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }
}

object HtmlSavePipeline {
  def apply[T](fileDir: String)(implicit p: (T, HttpResponse[String]) => String = (_: T, r: HttpResponse[String]) => getHash(r.body)): HtmlSavePipeline[T] = {
    new HtmlSavePipeline[T](fileDir) {
      override def fileName(t: T, response: HttpResponse[String]) = {
        p(t, response)
      }
    }
  }

  def getHash(str: String): String = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val hash = md5Digest.digest(str.getBytes("UTF-8"))
    hash.map("%02x".format(_)).mkString.toUpperCase
  }

}