package uk.co.samstokes.hibi.model

import java.util.Date
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

case class Task(
    val title: String,
    val order: Int,
    val isActive: Boolean,
    val scheduledFor: Date,
    val doneAt: Option[Date]) {
	
	def isTodoToday = {
	  val tomorrow = Calendar.getInstance()
	  tomorrow.roll(Calendar.DATE, 1)
	  tomorrow.set(Calendar.HOUR_OF_DAY, 0)
	  tomorrow.set(Calendar.MINUTE, 0)
	  tomorrow.set(Calendar.SECOND, 0)
	  
	  val todoAt = Calendar.getInstance()
	  todoAt.setTime(scheduledFor)
	  
	  todoAt before tomorrow
	}
	def isDone = doneAt.isDefined

	def toJson = {
	  val json = new JSONObject
	  json
	      .put("title", title)
      json
	}
}

object Task {
	
	val DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private val RE_UNSUPPORTED_TIMEZONE = "Z$".r

	implicit val ORDERING: Ordering[Task] = Ordering.by(_.order)
	
	def fromJson(json: JSONObject) = {
	    val taskObj = json.getJSONObject("task")
	    val doneAtString = if (taskObj.isNull("done_at")) None else Some(taskObj.getString("done_at"))
	    Task(
	        taskObj.getString("title"),
	        taskObj.getInt("order"),
	        taskObj.getBoolean("active"),
	        parseDate(taskObj.getString("scheduled_for")),
	        doneAtString.map(parseDate)
	    )
	}
	
	private def parseDate(dateString: String) = {
		val nicened = RE_UNSUPPORTED_TIMEZONE.replaceFirstIn(dateString, "+0000")
		DATE_FORMAT.parse(nicened)
	}
}