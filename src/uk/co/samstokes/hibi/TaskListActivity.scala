package uk.co.samstokes.hibi

import uk.co.samstokes.hibi.model.Task

class TaskListActivity extends SingleFragmentActivity
    with TaskListFragment.Callbacks {
  
  val DIALOG_ACTIONS = "TASK_ACTIONS"

  override def createFragment() = TaskListFragment.newInstance()
  
  override def onTaskSelected(task: Task) {
    val dialog = TaskActionsFragment.newInstance(task)
    dialog.setTargetFragment(getFragment(), TaskListActivity.REQUEST_ACTIONS)
    dialog.show(getSupportFragmentManager(), DIALOG_ACTIONS)
  }
  
}

object TaskListActivity {
    val REQUEST_ACTIONS = 0
}