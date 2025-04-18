package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import edu.msoe.myapplication.R
import java.util.Locale

class ExportDatePickerFragment : Fragment() {

    private lateinit var datePickerStart: DatePicker
    private lateinit var datePickerEnd: DatePicker
    private lateinit var btnConfirm: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_export_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // UI elements
        datePickerStart = view.findViewById(R.id.datePickerStart)
        datePickerEnd = view.findViewById(R.id.datePickerEnd)
        btnConfirm = view.findViewById(R.id.btnConfirmDateRange)

        btnConfirm.setOnClickListener {
            // Read out the selected year/month/day for each picker
            val startYear = datePickerStart.year
            val startMonth = datePickerStart.month + 1   // month is zero-based
            val startDay = datePickerStart.dayOfMonth

            val endYear = datePickerEnd.year
            val endMonth = datePickerEnd.month + 1
            val endDay = datePickerEnd.dayOfMonth

            // Format as ISO dates: YYYY-MM-DD
            val startDate = String.format(Locale.US, "%04d-%02d-%02d", startYear, startMonth, startDay)
            val endDate   = String.format(Locale.US, "%04d-%02d-%02d", endYear,   endMonth,   endDay)

            // Send the result back to ExportManagerFragment
            setFragmentResult(
                "exportDatePickerRequestKey",
                bundleOf(
                    "startDate" to startDate,
                    "endDate"   to endDate
                )
            )

            // Navigate back to the ExportManagerFragment
            findNavController().navigate(R.id.action_exportDatePicker_to_exportManager)
        }
    }
}
