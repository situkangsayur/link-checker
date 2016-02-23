package com.gdn.link.checker.actors

import akka.actor.{ActorLogging, Props, ReceiveTimeout, Actor}
import scala.concurrent.duration._

/**
 * Created by hendri_k on 2/23/16.
 */
object MainActor {

  case object Start

  def props = Props[MainActor]

}

class MainActor extends Actor with ActorLogging{

  import MainActor._

  context.setReceiveTimeout(10 seconds)

  def receive = idle

  def idle: Receive ={
    case Start =>
      val receptionist = context.actorOf(Receptionist.props, "receptionist")

      receptionist ! Receptionist.Get("http://www.google.com/3")
      receptionist ! Receptionist.Get("https://www.facebook.com/")
      receptionist ! Receptionist.Get("http://www.google.com")

      context.become(running)
  }

  def running: Receive = {
    case Receptionist.Result(url, links) =>
      log.info(links.toVector.sorted.mkString(s"Results for '$url':\n", "\n", "\n"))
    case Receptionist.Failed(url) =>
      log.info(s"Failed to fetch '$url'\n")
    case ReceiveTimeout =>
      context.stop(self)
  }

  override def postStop(): Unit = {
    context.system.shutdown()
  }

}