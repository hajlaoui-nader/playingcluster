package xyz.funnycoding.events

sealed trait Event
case class AddEvent(message: String) extends Event
case class DeleteEvent(message: String) extends Event
