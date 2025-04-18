package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.os.Environment
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
import java.io.File

class ExportManagerFragment : Fragment() {

    private lateinit var tvExportSummary: TextView
    private lateinit var btnSelectDateRange: Button
    private lateinit var btnExportEmail: Button

    // Variables to hold the user-selected date range
    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_export_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initialize UI components
        tvExportSummary = view.findViewById(R.id.tvExportSummary)
        btnSelectDateRange = view.findViewById(R.id.btnSelectDateRange)
        btnExportEmail = view.findViewById(R.id.btnExportEmail)

        // initial message for the summary area
        tvExportSummary.text = "No date range selected. Please select a date range."

        // when user clicks the "Select Date Range" button,
        // navigate to the ExportDatePickerFragment
        btnSelectDateRange.setOnClickListener {
            findNavController().navigate(R.id.action_exportManager_to_exportDatePicker)
        }

        //when user clicks "Export via Email", generate the report and save it locally
        btnExportEmail.setOnClickListener {
            if (selectedStartDate != null && selectedEndDate != null) {
                // Calculate the summary and generate the CSV report.
                val summaryText = calculateSummary(selectedStartDate!!, selectedEndDate!!)
                val csvContent = generateCSVReport(selectedStartDate!!, selectedEndDate!!, summaryText)
                // Save the generated CSV into the Downloads folder.
                saveReportToDownloads(csvContent, selectedStartDate!!, selectedEndDate!!)
            } else {
                Toast.makeText(requireContext(), "Please select a date range first.", Toast.LENGTH_SHORT).show()
            }
        }

        // Listen for results from the ExportDatePickerFragment.
        // When the user selects a date range,
        // update the stored values and recalculate the summary.
        setFragmentResultListener("exportDatePickerRequestKey") { _, bundle ->
            val startDate = bundle.getString("startDate")
            val endDate = bundle.getString("endDate")
            if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
                selectedStartDate = startDate
                selectedEndDate = endDate
                val summary = calculateSummary(startDate, endDate)
                tvExportSummary.text = summary
            }
        }
    }

    /**
     * Calculates the export summary based on the provided start and end dates.
     *
     * Replace the dummy implementation with a real calculation (e.g., summing logged hours from tasks).
     */
    private fun calculateSummary(startDate: String, endDate: String): String {
        // TODO: Replace these with the actual summary calculation logic.
        val totalHours = 8   // e.g., Sum logged hours over tasks between the dates
        val numberOfTasks = 3  // e.g., Count of tasks with logged time in the date range

        return "Time Summary from $startDate to $endDate:\n" +
                "Total Hours: $totalHours\n" +
                "Number of Tasks: $numberOfTasks"
    }

    /**
     * Generates a CSV report using the provided start
     * and end dates as well as the calculated summary.
     */
    private fun generateCSVReport(startDate: String, endDate: String, summary: String): String {
        val sb = StringBuilder()
        sb.append("Time Summary Report\n")
        sb.append("Date Range:, $startDate to $endDate\n")
        sb.append("\n")
        // In the CSV, we replace line breaks in the summary with commas.
        sb.append(summary.replace("\n", ", ") + "\n")
        return sb.toString()
    }

    /**
     * Saves the provided report data as a CSV file in the public Downloads folder.
     *
     * TODO: Ensure proper permissions (WRITE_EXTERNAL_STORAGE) in AndroidManifest
     * TODO: and have requested runtime permissions for API levels that require them.
     */
    private fun saveReportToDownloads(reportData: String, startDate: String, endDate: String) {
        // Create a file name
        val fileName = "TimeSummary_${startDate}_to_${endDate}.csv"
        // Get the Downloads directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        try {
            file.writeText(reportData)
            Toast.makeText(requireContext(), "Report saved to Downloads/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save report: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
