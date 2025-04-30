package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.msoe.myapplication.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatePickerFragment : Fragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var etDuration: EditText
    private lateinit var btnSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        datePicker = view.findViewById(R.id.datePicker)
        timePicker = view.findViewById(R.id.timePicker)
        etDuration = view.findViewById(R.id.etDuration)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month + 1 // Month is zero-based
            val day = datePicker.dayOfMonth

            val hour = timePicker.hour
            val minute = timePicker.minute

            val durationText = etDuration.text.toString()
            if (durationText.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a duration.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val durationInSeconds = parseDuration(durationText)
            if (durationInSeconds == null) {
                Toast.makeText(requireContext(), "Invalid duration format.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateTime = LocalDateTime.of(year, month, day, hour, minute)
            val formattedDateTime = dateTime.format(DateTimeFormatter.ISO_DATE_TIME)

            // Pass the data back to the TimerFragment
            findNavController().previousBackStackEntry?.savedStateHandle?.set("selectedDateTime", formattedDateTime)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("durationInSeconds", durationInSeconds)

            findNavController().popBackStack()
        }
    }

    private fun parseDuration(duration: String): Long? {
        val regex = "(\\d+)h|(\\d+)m".toRegex()
        var totalSeconds = 0L

        regex.findAll(duration).forEach { matchResult ->
            val (hours, minutes) = matchResult.destructured
            totalSeconds += hours.toLongOrNull()?.times(3600) ?: 0
            totalSeconds += minutes.toLongOrNull()?.times(60) ?: 0
        }

        return if (totalSeconds > 0) totalSeconds else null
    }
}