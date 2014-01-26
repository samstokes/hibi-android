package uk.co.samstokes.hibi

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.app.AlertDialog
import android.util.Log

import uk.co.samstokes.hibi.model.Task
import Implicits._

class TaskActionsFragment extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle) = {
    val task = getArguments().getSerializable(TaskActionsFragment.ARG_TASK).asInstanceOf[Task]
    
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.task_actions)
        .setMessage(getString(R.string.finished_task_question, task.title))
        .setPositiveButton(R.string.complete_task, {
           Log.d("foo", "completing")
         }).setNeutralButton(R.string.postpone_task, {
           Log.d("foo", "postponing")
         })
        .create()
  }
  
}

object TaskActionsFragment {
  val ARG_TASK = getClass().getPackage().getName() + ".TASK"
  
  def newInstance(task: Task) = {
    val args = new Bundle()
    args.putSerializable(ARG_TASK, task)
    val fragment = new TaskActionsFragment()
    fragment.setArguments(args)
    fragment
  }
}