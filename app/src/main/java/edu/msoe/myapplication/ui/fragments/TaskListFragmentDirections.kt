package edu.msoe.myapplication.ui.fragments

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import edu.msoe.myapplication.R

class TaskListFragmentDirections private constructor() {
    companion object {
        fun actionTaskListToTimer(issueId: String, issueTitle: String): NavDirections {
            return ActionOnlyNavDirections(R.id.action_taskList_to_timer).apply {
                arguments.putString("issueId", issueId)
                arguments.putString("issueTitle", issueTitle)
            }
        }
    }
}