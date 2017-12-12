name := "scrapy4s"

organization := "com.scrapy4s"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.4"


libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.asynchttpclient" % "async-http-client" % "2.0.37" exclude("io.netty", "netty-handler")
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test
libraryDependencies += "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.21"
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.35"
libraryDependencies += "redis.clients" % "jedis" % "2.9.0"
libraryDependencies += "io.netty" % "netty-handler" % "4.0.52.Final"
