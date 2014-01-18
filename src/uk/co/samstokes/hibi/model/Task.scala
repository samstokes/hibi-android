package uk.co.samstokes.hibi.model

import java.util.Date

case class Task(
    val title: String,
    val doneAt: Option[Date]) {
	
	def isDone = doneAt.isDefined
}
