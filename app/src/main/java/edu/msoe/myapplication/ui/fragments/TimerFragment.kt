package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.msoe.myapplication.R

class TimerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        val issueId = arguments?.getString("issueId")
        val issueTitle = arguments?.getString("issueTitle")

        val issueTitleTextView: TextView = view.findViewById(R.id.tvTimerDisplay)
        issueTitleTextView.text = issueTitle

        return view
    }
}
