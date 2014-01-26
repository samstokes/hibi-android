package uk.co.samstokes.hibi

import Implicits.dialogOnClick
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import uk.co.samstokes.hibi.model.Task
import android.content.Intent
import uk.co.samstokes.hibi.model.Complete
import uk.co.samstokes.hibi.model.Postpone
import uk.co.samstokes.hibi.model.TaskAction

class TaskActionsFragment extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle) = {
    // TODO pass task ids around instead
    val task = getArguments().getSerializable(TaskActionsFragment.ARG_TASK).asInstanceOf[Task]
    
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.task_actions)
        .setMessage(getString(R.string.finished_task_question, task.title))
        .setPositiveButton(R.string.complete_task, {
           sendResult(Complete(task))
         }).setNeutralButton(R.string.postpone_task, {
           sendResult(Postpone(task))
         })
        .create()
  }
  
  private def sendResult(action: TaskAction) {
    val fragment = getTargetFragment()
    if (fragment == null) return
    
    val intent = new Intent()
    intent.putExtra(TaskActionsFragment.EXTRA_ACTION, action)
    
    fragment.onActivityResult(TaskListActivity.REQUEST_ACTIONS, 0, intent)
  }
  
}

object TaskActionsFragment {
  val ARG_TASK = getClass().getPackage().getName() + ".TASK"
  val EXTRA_ACTION = getClass().getPackage().getName() + ".ACTION"
  
  def newInstance(task: Task) = {
    val args = new Bundle()
    args.putSerializable(ARG_TASK, task)
    val fragment = new TaskActionsFragment()
    fragment.setArguments(args)
    fragment
  }
}