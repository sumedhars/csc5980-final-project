package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.msoe.myapplication.R

// fragment to handle the stopwatch functionality for tracking time
class TimerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    // TODO: Implement stopwatch logic and pause/resume behavior.
    // If paused and exiting, show a dialog to push time to JIRA
}
