package uk.co.samstokes.hibi.model

sealed trait TaskAction extends Serializable {
  val actionPath: String
}

case class Complete(task: Task) extends TaskAction {
  val actionPath = "%d/complete" format task.id
}
case class Postpone(task: Task) extends TaskAction {
  val actionPath = "%d/postpone" format task.id
}