package xyz.funnycoding.events

sealed trait Event
case class GetEvent(index: String, eventId: String) extends Event


