package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var btnCancel: Button
    private lateinit var btnLogTime: Button

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
        btnCancel = view.findViewById(R.id.btnCancel)
        btnLogTime = view.findViewById(R.id.btnLogTime)

        // Retrieve the issue ID/title from arguments
        arguments?.let {
            issueId    = it.getString("issueId")
            issueTitle = it.getString("issueTitle")
            requireActivity().title = issueTitle
        }

        btnStart.setOnClickListener {
            timerViewModel.startTimer()
            btnLogTime.isEnabled = false // Disable log time until conditions are met
        }

        btnPause.setOnClickListener {
            issueId?.let { id ->
                timerViewModel.pauseTimer(id)
                if (timerViewModel.elapsedMillis.value ?: 0L > 0) {
                    btnLogTime.isEnabled = true // Enable log time if time is logged
                }
            }
        }

        btnResume.setOnClickListener {
            timerViewModel.resumeTimer()
            btnLogTime.isEnabled = false // Disable log time while timer is running
        }

        btnCancel.setOnClickListener {
            timerViewModel.resetTimer()
            tvTimer.text = "00:00:00" // Reset timer display
            btnLogTime.isEnabled = false // Disable log time
        }

        btnLogTime.setOnClickListener {
            issueId?.let { id ->
                val elapsedMillis = timerViewModel.elapsedMillis.value ?: 0L
                if (elapsedMillis > 0) {
                    timerViewModel.logTime(id, elapsedMillis / 1000) // Pass duration in seconds
                    btnLogTime.isEnabled = false // Disable log time after logging
                    Toast.makeText(requireContext(), "Time logged successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No time to log!", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(requireContext(), "Issue ID is missing!", Toast.LENGTH_SHORT).show()
        }

        // Navigate to DatePickerFragment to manually log time
        val btnAddTime: Button = view.findViewById(R.id.btnAddTime)
        btnAddTime.setOnClickListener {
            findNavController().navigate(R.id.action_timerFragment_to_datePickerFragment)
        }

        // Handle data returned from DatePickerFragment
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<String>("selectedDateTime").observe(viewLifecycleOwner) { dateTime ->
                val durationInSeconds = get<Long>("durationInSeconds") ?: return@observe

                issueId?.let { id ->
                    timerViewModel.logManualTime(id, dateTime, durationInSeconds)
                    Toast.makeText(requireContext(), "Time logged successfully!", Toast.LENGTH_SHORT).show()
                } ?: Toast.makeText(requireContext(), "Issue ID is missing!", Toast.LENGTH_SHORT).show()
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
