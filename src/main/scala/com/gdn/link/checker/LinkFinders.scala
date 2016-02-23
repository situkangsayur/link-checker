package com.gdn.link.checker

import org.jsoup.Jsoup
import scala.collection.JavaConverters._

/**
 * Created by hendri_k on 2/23/16.
 */
object LinksFinder {

  def find(body: String): Iterator[String] = {
    val document = Jsoup.parse(body)
    val links = document.select("a[href]")
    for {
      link <- links.iterator().asScala
    } yield link.absUrl("href")
  }

}