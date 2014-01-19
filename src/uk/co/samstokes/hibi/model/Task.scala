package uk.co.samstokes.hibi.model

import java.util.Date
import org.json.JSONObject
import java.text.SimpleDateFormat

case class Task(
    val title: String,
    val doneAt: Option[Date]) {
	
	def isDone = doneAt.isDefined
}

object Task {
	
	val DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private val RE_UNSUPPORTED_TIMEZONE = "Z$".r

	
	def fromJson(json: JSONObject) = {
	    val taskObj = json.getJSONObject("task")
	    val dateString = if (taskObj.isNull("done_at")) None else Some(taskObj.getString("done_at"))
	    Task(
	        taskObj.getString("title"),
	        dateString.map(parseDate)
	    )
	}
	
	private def parseDate(dateString: String) = {
		val nicened = RE_UNSUPPORTED_TIMEZONE.replaceFirstIn(dateString, "+0000")
		DATE_FORMAT.parse(nicened)
	}
}