package com.gdn.link.checker.actors

import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import scala.concurrent.duration._

/**
 * Created by hendri_k on 2/23/16.
 */
object Controller {

  case class Check(url: String, depth: Int)
  case class Result(links: Set[String])
  case object Timeout

  def props = Props[Controller]

}

class Controller extends Actor with ActorLogging {
  import Controller._

  implicit val exec = context.dispatcher

  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  context.system.scheduler.scheduleOnce(10 seconds, self, Timeout)

  def receive = {
    case Check(url, depth) =>
      log.debug("{} checking {}", depth, url)
      if (!cache(url) && depth > 0)
        children += context.actorOf(Getter.props(url, depth - 1))
      cache += url
    case Getter.Done =>
      children -= sender
      if (children.isEmpty) context.parent ! Result(cache)
    case Timeout =>
      children foreach (_ ! Getter.Abort)
  }

}