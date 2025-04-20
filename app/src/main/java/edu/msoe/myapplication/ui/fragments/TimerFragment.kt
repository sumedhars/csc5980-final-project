package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import edu.msoe.myapplication.R
import edu.msoe.myapplication.data.TaskRepository
import edu.msoe.myapplication.data.JiraApiServiceImpl
import edu.msoe.myapplication.ui.viewmodels.TimerViewModel
import edu.msoe.myapplication.ui.viewmodels.TimerViewModelFactory

class TimerFragment : Fragment() {

    private val timerViewModel: TimerViewModel by viewModels {
        TimerViewModelFactory(TaskRepository(JiraApiServiceImpl()))
    }

    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnResume: Button
    private lateinit var btnStop: Button

    private var issueId: String? = null
    private var issueTitle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvTimer   = view.findViewById(R.id.tvTimer)
        btnStart  = view.findViewById(R.id.btnStart)
        btnPause  = view.findViewById(R.id.btnPause)
        btnResume = view.findViewById(R.id.btnResume)
        btnStop   = view.findViewById(R.id.btnStop)

        // Retrieve the issue ID/title from arguments
        arguments?.let {
            issueId    = it.getString("issueId")
            issueTitle = it.getString("issueTitle")
            requireActivity().title = issueTitle
        }

        btnStart.setOnClickListener {
            timerViewModel.startTimer()
        }
        btnPause.setOnClickListener {
            issueId?.let { id -> timerViewModel.pauseTimer(id) }
        }
        btnResume.setOnClickListener {
            timerViewModel.resumeTimer()
        }
        btnStop.setOnClickListener {
            issueId?.let { id ->
                timerViewModel.stopTimer(id)
                // Return to task list
                findNavController().popBackStack(R.id.taskListFragment, false)
            }
        }

        // Observe the elapsed time and update UI
        timerViewModel.elapsedMillis.observe(viewLifecycleOwner) { ms ->
            tvTimer.text = formatMillis(ms)
        }
    }

    private fun formatMillis(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours        = totalSeconds / 3600
        val minutes      = (totalSeconds % 3600) / 60
        val seconds      = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
