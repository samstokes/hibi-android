package uk.co.samstokes.hibi

import java.util.Date
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import uk.co.samstokes.hibi.model.Task
import android.os.AsyncTask
import uk.co.samstokes.hibi.model.HibiFetcher
import android.widget.Toast

class TaskListFragment extends ListFragment {
  
  trait Callbacks {}
  
  private var mListener: Option[Callbacks] = None
  private var mTodo: Array[Task] = Array()
  
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    
    updateTasks()
  }
  
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
    val v = inflater.inflate(R.layout.fragment_task_list, container, false)

    getActivity().setTitle(R.string.title_todo)
    
    v
  }
    
  override def onAttach(activity: Activity) {
    super.onAttach(activity)
    try {
      mListener = Some(activity.asInstanceOf[Callbacks])
    } catch {
      case e: ClassCastException => throw new ClassCastException(
          activity.toString() + " must implement " + classOf[Callbacks].getName())
    }
  }
  
  override def onDetach() {
    super.onDetach()
    mListener = None
  }
  
  private def updateTasks() {
    new FetchTasksTask().execute()
  }
  
  private def setupAdapter() {
	setListAdapter(new TaskAdapter(this, mTodo))
  }

  private class FetchTasksTask extends AsyncTaskAdapter[Void, Void, Either[String, Array[Task]]] {
    private val fetcher = new HibiFetcher()
    
    override def pleaseDoInBackground(params: Void*) = {
      fetcher.getTasks().right.map(_.toArray)
    }
    
    override def onPostExecute(tasksOpt: Either[String, Array[Task]]) = tasksOpt match {
      case Right(tasks) =>
      	mTodo = tasks
      		.filterNot(_.isDone)
      		.filter(_.isActive)
      		.filter(_.isTodoToday).sorted
      	setupAdapter()
      case Left(error) =>
        Toast.makeText(
            getActivity(),
            getString(R.string.fetch_failed, error),
            Toast.LENGTH_LONG
            ).show()
    }
  }
  
}

object TaskListFragment {
  def newInstance(): TaskListFragment = new TaskListFragment()
}