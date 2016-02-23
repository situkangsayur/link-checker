package com.gdn.link.checker.actors

import akka.actor.{ActorRef, Actor}
import akka.pattern.pipe
import com.gdn.link.checker.{AsyncWebClient, WebClient}

/**
 * Created by hendri_k on 2/23/16.
 */
object Cache {

  case class Get(url: String)
  case class Result(client: ActorRef, url: String, body: String)

}

class Cache extends Actor {

  import Cache._

  implicit val exec = context.dispatcher

  var cache = Map.empty[String, String]

  def receive = {
    case Get(url) =>
      if (cache contains url) sender ! cache(url)
      else {
        val client = sender()
        val webClient: WebClient = AsyncWebClient
        webClient get url map (Result(client, url, _)) pipeTo self
      }
    case Result(client, url, body) =>
      cache += url -> body
      client ! body
  }

}