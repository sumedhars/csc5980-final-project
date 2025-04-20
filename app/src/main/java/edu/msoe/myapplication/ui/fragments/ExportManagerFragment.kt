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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import edu.msoe.myapplication.R
import edu.msoe.myapplication.data.ExportSummary
import edu.msoe.myapplication.data.TaskRepository
import edu.msoe.myapplication.data.TimeLog
import edu.msoe.myapplication.data.JiraApiServiceImpl
import edu.msoe.myapplication.ui.viewmodels.ExportViewModel
import edu.msoe.myapplication.ui.viewmodels.ExportViewModelFactory
import java.io.File
import java.time.LocalDate

class ExportManagerFragment : Fragment() {

    private val exportViewModel: ExportViewModel by viewModels {
        ExportViewModelFactory(TaskRepository(JiraApiServiceImpl()))
    }

    private lateinit var tvExportSummary: TextView
    private lateinit var btnSelectDateRange: Button
    private lateinit var btnExportEmail: Button

    private var currentSummary: ExportSummary? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_export_manager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvExportSummary      = view.findViewById(R.id.tvExportSummary)
        btnSelectDateRange   = view.findViewById(R.id.btnSelectDateRange)
        btnExportEmail       = view.findViewById(R.id.btnExportEmail)

        // Initial message
        tvExportSummary.text = "No date range selected. Please select a date range."

        // Navigate to date picker
        btnSelectDateRange.setOnClickListener {
            findNavController().navigate(R.id.action_exportManager_to_exportDatePicker)
        }

        // Observe LiveData from the ViewModel
        exportViewModel.summary.observe(viewLifecycleOwner) { summary ->
            currentSummary = summary
            tvExportSummary.text = buildString {
                append("Time Summary from ${summary.startDate} to ${summary.endDate}:\n")
                append("• Total Hours: ${"%.2f".format(summary.totalHours)}\n")
                append("• Number of Tasks: ${summary.totalTasks}")
            }
        }

        // When we get the date range back from the DatePicker
        setFragmentResultListener("exportDatePickerRequestKey") { _, bundle ->
            val startDate = LocalDate.parse(bundle.getString("startDate")!!)
            val endDate   = LocalDate.parse(bundle.getString("endDate")!!)
            exportViewModel.loadSummary(startDate, endDate)
        }

        // Export CSV into Downloads
        btnExportEmail.setOnClickListener {
            val summary = currentSummary
            if (summary == null) {
                Toast.makeText(requireContext(), "Please select a date range first.", Toast.LENGTH_SHORT).show()
            } else {
                val csv = buildReportCsv(summary)
                saveCsvToDownloads(csv, summary)
            }
        }
    }

    private fun buildReportCsv(summary: ExportSummary): String {
        return buildString {
            append("Report: Time Summary\n")
            append("Date Range,${summary.startDate},${summary.endDate}\n")
            append("Total Hours,${"%.2f".format(summary.totalHours)}\n")
            append("Number of Tasks,${summary.totalTasks}\n")
        }
    }

    private fun saveCsvToDownloads(csvContent: String, summary: ExportSummary) {
        val filename = "TimeSummary_${summary.startDate}_to_${summary.endDate}.csv"
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outFile = File(downloads, filename)
        try {
            outFile.writeText(csvContent)
            Toast.makeText(requireContext(),
                "Saved report to Downloads/$filename",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "Error saving report: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
