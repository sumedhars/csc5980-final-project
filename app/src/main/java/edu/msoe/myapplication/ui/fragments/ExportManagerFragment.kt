package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import edu.msoe.myapplication.R

// Fragment that allows the user to export time summary reports
class ExportManagerFragment : Fragment() {

    private lateinit var tvExportSummary: TextView
    private lateinit var btnSelectDateRange: Button
    private lateinit var btnExportEmail: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Initialize UI components
        tvExportSummary = view.findViewById(R.id.tvExportSummary)
        btnSelectDateRange = view.findViewById(R.id.btnSelectDateRange)
        btnExportEmail = view.findViewById(R.id.btnExportEmail)

        // Set an initial message for the summary text view
        tvExportSummary.text = "No date range selected. Please select a date range."

        // Set up the Continue button to navigate to the ExportDatePickerFragment
        btnSelectDateRange.setOnClickListener {
            findNavController().navigate(R.id.action_exportManager_to_exportDatePicker)
        }

        // Handle the export action (this is a stub; implement actual CSV/PDF generation as needed)
        btnExportEmail.setOnClickListener {
            Toast.makeText(requireContext(), "Exporting summary via email...", Toast.LENGTH_SHORT).show()
            // TODO: Implement the logic to generate the report and share via email.
        }

        // Listen for the result from ExportDatePickerFragment (using a predefined request key)
        setFragmentResultListener("exportDatePickerRequestKey") { _, bundle ->
            val startDate = bundle.getString("startDate")
            val endDate = bundle.getString("endDate")
            if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
                // For demonstration purposes, we simulate a summary.
                // In a real implementation, calculate total hours and build an appropriate summary.
                val totalHours = 8  // Replace with actual logic to compute hours
                val summaryText = "Time Summary from $startDate to $endDate:\nTotal Hours: $totalHours"
                tvExportSummary.text = summaryText
            }
        }
    }
}
