package util

import java.security.MessageDigest

/**
  * Created by sheep3 on 2017/11/30.
  */
object HashUtil {
  def getHash(str: String): String = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val hash = md5Digest.digest(str.getBytes("UTF-8"))
    hash.map("%02x".format(_)).mkString.toUpperCase
  }
}
