package com.gdn.link.checker

import akka.actor.ActorSystem
import com.gdn.link.checker.actors.{MainActor}

/**
 * Created by hendri_k on 2/23/16.
 */
object LinkCheckerApp extends App {

  val system = ActorSystem("linkCheckerSystem")

  val master = system.actorOf(MainActor.props, "mainactor")

  master ! MainActor.Start

}
