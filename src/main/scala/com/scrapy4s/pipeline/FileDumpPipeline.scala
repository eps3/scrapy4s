package com.scrapy4s.pipeline

import java.io.{File, FileOutputStream}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil
import org.slf4j.LoggerFactory


/**
  * 文件下载Pipeline
  *
  * @param fileDir 目标文件夹
  * @param fileNamePaser 文件名解析方法，建议不同页面的返回值不一致，否则会出现文件覆盖的情况
  */
class FileDumpPipeline(fileDir: String, fileNamePaser: Response => String) extends Pipeline {
  val logger = LoggerFactory.getLogger(classOf[FileDumpPipeline])

  override def pipe(response: Response): Unit = {
    val file = new File(fileDir, fileNamePaser(response))
    val outStream = new FileOutputStream(file)
    val buffer = new Array[Byte](response.inputStream.available())
    response.inputStream.read(buffer)
    outStream.write(buffer)
    outStream.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }

}

object FileDumpPipeline {
  /**
    * 文件下载Pipeline
    *
    * @param fileDir 目标文件夹
    * @param fileNamePaser 文件名解析方法，建议不同页面的返回值不一致，否则会出现文件覆盖的情况
    * @return
    */
  def apply(fileDir: String)
           (implicit fileNamePaser: Response => String = r => HashUtil.getHash(r.request.toString)): FileDumpPipeline = {
    new FileDumpPipeline(fileDir, fileNamePaser)
  }
}