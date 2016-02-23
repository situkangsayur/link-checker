package com.gdn.link.checker.actors

import akka.actor.{Props, ActorRef, Actor}

/**
 * Created by hendri_k on 2/23/16.
 */
object Receptionist {

  case class Job(client: ActorRef, url: String)
  case class Failed(url: String)
  case class Get(url: String)
  case class Result(url: String, links: Set[String])

  def props = Props[Receptionist]

}

class Receptionist extends Actor {

  import Receptionist._

  var reqNo = 0

  def receive = waiting

  def controllerProps: Props = Controller.props

  val waiting: Receive = {
    case Get(url) =>
      context.become(runNext(Vector(Job(sender, url))))
  }

  def running(queue: Vector[Job]): Receive = {
    case Controller.Result(links) =>
      val job = queue.head
      job.client ! Result(job.url, links)
      context.stop(sender)
      context.become(runNext(queue.tail))
    case Get(url) =>
      context.become(enqueue(queue, Job(sender, url)))
  }

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue isEmpty) waiting
    else {
      val controller = context.actorOf(controllerProps, s"c$reqNo")
      controller ! Controller.Check(queue.head.url, 2)
      running(queue)
    }
  }

  def enqueue(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > 3) {
      sender ! Failed(job.url)
      running(queue)
    } else
      running(queue :+ job)
  }

}
