package uk.co.samstokes.hibi

import uk.co.samstokes.hibi.model.Task

class TaskListActivity extends SingleFragmentActivity
    with TaskListFragment.Callbacks {

  override def createFragment() = TaskListFragment.newInstance()
  
}
