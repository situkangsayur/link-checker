package com.gdn.link.checker

import java.util.concurrent.Executor
import com.ning.http.client.AsyncHttpClient
import scala.concurrent.{Promise, Future}

/**
 * Created by hendri_k on 2/23/16.
 */
trait WebClient {
  def get(url: String)(implicit exec: Executor): Future[String]
}

case class BadStatus(code: Int) extends RuntimeException

object AsyncWebClient extends WebClient {

  private val client = new AsyncHttpClient()

  def get(url: String)(implicit exec: Executor): Future[String] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[String]()
    f.addListener(new Runnable {
      def run() = {
        val response = f.get
        if (response.getStatusCode < 400)
          p.success(response.getResponseBodyExcerpt(131072))
        else p.failure(BadStatus(response.getStatusCode))
      }
    }, exec)
    p.future
  }

  def shutdown(): Unit = {
    client.closeAsynchronously()
  }

}