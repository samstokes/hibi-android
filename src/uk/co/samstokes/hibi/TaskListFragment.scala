package uk.co.samstokes.hibi

import java.util.ArrayList
import java.util.Date

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import Implicits._
import uk.co.samstokes.hibi.model.HibiFetcher
import uk.co.samstokes.hibi.model.Task
import android.widget.ListView

class TaskListFragment extends ListFragment {
        
  private val TAG = classOf[TaskListFragment].getSimpleName()
  private val HACKY_HARDCODED_USERNAME = "TODO_EDIT_ME"
  private val HACKY_HARDCODED_PASSWORD = "TODO_EDIT_ME"
  private val fetcher = new HibiFetcher(HACKY_HARDCODED_USERNAME, HACKY_HARDCODED_PASSWORD)
  
  private var mListener: Option[TaskListFragment.Callbacks] = None
  private var mTodo: java.util.List[Task] = new ArrayList()
  
  private var mNewTaskTitle: Option[EditText] = None
  private var mAddTaskButton: Option[Button] = None
  
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    
    updateTasks()
    
    setRetainInstance(true)
  }
  
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
    val v = inflater.inflate(R.layout.fragment_task_list, container, false)

    getActivity().setTitle(R.string.title_todo)
    
    mNewTaskTitle = Option(v.findViewById(R.id.new_task_editText).asInstanceOf[EditText])
    mNewTaskTitle.foreach(_.setOnEditorActionListener {(view: TextView, actionId: Int, event: KeyEvent) =>
        mAddTaskButton.map(_.callOnClick()).getOrElse(true)
    })
    mAddTaskButton = Option(v.findViewById(R.id.new_task_button).asInstanceOf[Button])
    mAddTaskButton.foreach(_.setOnClickListener {_: View =>
      mAddTaskButton.foreach(_.setEnabled(false))

      val task = Task(
          mNewTaskTitle.map(_.getText().toString()).getOrElse(""),
          Integer.MAX_VALUE,
          true,
          new Date(),
          None
          )
      new AddTaskTask().execute(task)
      ()
    })
    
    v
  }
    
  override def onAttach(activity: Activity) {
    super.onAttach(activity)
    try {
      mListener = Some(activity.asInstanceOf[TaskListFragment.Callbacks])
    } catch {
      case e: ClassCastException => throw new ClassCastException(
          activity.toString() + " must implement " + classOf[TaskListFragment.Callbacks].getName())
    }
  }
  
  override def onDetach() {
    super.onDetach()
    mListener = None
  }
  
  override def onListItemClick(listView: ListView, v: View, position: Int, id: Long) {
    val task = (getListAdapter().asInstanceOf[TaskAdapter]).getItem(position)
    mListener.foreach(_.onTaskSelected(task))
  }
  
  private def updateTasks() {
    new FetchTasksTask().execute()
  }
  
  private def setupAdapter() {
	setListAdapter(new TaskAdapter(this, mTodo))
  }
  
  private def updateTaskList() {
    // TODO re-sort list
    Option(getListAdapter()).map(_.asInstanceOf[TaskAdapter].notifyDataSetChanged())
  }
  
  private def signalMessage(length: Int, messageResId: Int, messageParams: AnyRef*) {
    Toast.makeText(
                getActivity(),
                getString(messageResId, messageParams: _*),
                length
                ).show()
  }

  private class FetchTasksTask extends AsyncTaskAdapter[Void, Void, Either[String, Array[Task]]] {

    override def pleaseDoInBackground(params: Void*) = {
      fetcher.getTasks().right.map(_.toArray)
    }
    
    override def onPostExecute(tasksOpt: Either[String, Array[Task]]) = tasksOpt match {
      case Right(tasks) =>
        mTodo = new ArrayList()
      	tasks
      		.filterNot(_.isDone)
      		.filter(_.isActive)
      		.filter(_.isTodoToday).sorted
      		.foreach(mTodo.add)

      	setupAdapter()
      case Left(error) =>
        Toast.makeText(
            getActivity(),
            getString(R.string.fetch_failed, error),
            Toast.LENGTH_LONG
            ).show()
    }
  }
  
  private class AddTaskTask extends AsyncTaskAdapter[Task, Void, Either[String, Option[Task]]] {
    override def pleaseDoInBackground(tasks: Task*) = try {
      fetcher.postTask(tasks.head).right.map(Some(_))
    } catch {
      case iSuck: HibiFetcher.ISuckException =>
        /* ugh, because our HTTP library is terrible, we can't actually get a
         * result from POSTs; instead we get an EOFException when we try.
         * However, that actually indicates success... so we'll convey that to
         * the UI thread by returning success, but with an empty result.
         */
        
        Right(None)
    }
    
    override def onPostExecute(taskOpt: Either[String, Option[Task]]) = {
      taskOpt match {
          case Right(taskOpt) =>
            mNewTaskTitle.foreach(_.setText(null))
            taskOpt match {
              case Some(task) =>
                mTodo.add(task)
                updateTaskList()
              case None =>
                // succeeded but couldn't get the new task; refresh everything
                signalMessage(Toast.LENGTH_SHORT, R.string.refreshing_tasks)
                updateTasks()
            }
          case Left(error) =>
            signalMessage(Toast.LENGTH_LONG, R.string.add_task_failed, error)
        }
      mAddTaskButton.foreach(_.setEnabled(true))
    }
  }
  
}

object TaskListFragment {
  def newInstance(): TaskListFragment = new TaskListFragment()
    
  trait Callbacks {
    def onTaskSelected(task: Task)
  }
}