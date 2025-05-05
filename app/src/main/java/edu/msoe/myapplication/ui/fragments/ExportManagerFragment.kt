package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.msoe.myapplication.R
import edu.msoe.myapplication.data.ExportSummary
import edu.msoe.myapplication.data.TaskRepository
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
    private lateinit var progressBar: ProgressBar
    private var loadingSnackbar: Snackbar? = null

    private var currentSummary: ExportSummary? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_export_manager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind views
        tvExportSummary = view.findViewById(R.id.tvExportSummary)
        btnSelectDateRange = view.findViewById(R.id.btnSelectDateRange)
        btnExportEmail = view.findViewById(R.id.btnExportEmail)
        progressBar = view.findViewById(R.id.progressLoading)

        // Initial UI state
        tvExportSummary.text = "No date range selected. Please select a date range."
        btnExportEmail.isEnabled = false

        // Observe loading state
        exportViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSelectDateRange.isEnabled = !isLoading
            btnExportEmail.isEnabled = !isLoading && currentSummary != null

            if (isLoading) {
                // If loading persists, show Snackbar after 2 seconds
                progressBar.postDelayed({
                    if (exportViewModel.loading.value == true) {
                        loadingSnackbar = Snackbar.make(
                            view,
                            "Working on it... this may take a moment.",
                            Snackbar.LENGTH_INDEFINITE
                        )
                        loadingSnackbar?.show()
                    }
                }, 2000)
            } else {
                loadingSnackbar?.dismiss()
            }
        }

        // Observe summary updates
        exportViewModel.summary.observe(viewLifecycleOwner) { summary ->
            currentSummary = summary
            tvExportSummary.text = buildString {
                append("Time Summary from ${summary.startDate} to ${summary.endDate}:\n")
                append("• Total Hours: ${"%.2f".format(summary.totalHours)}\n")
                append("• Number of Tasks: ${summary.totalTasks}")
            }
            // Enable export once summary is ready
            btnExportEmail.isEnabled = exportViewModel.loading.value == false
        }

        // Handle date range return from picker
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("startDate")
            ?.observe(viewLifecycleOwner) { start ->
                val end = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("endDate") ?: return@observe

                exportViewModel.loadSummary(
                    LocalDate.parse(start),
                    LocalDate.parse(end)
                )
            }

        // Navigation to date picker
        btnSelectDateRange.setOnClickListener {
            findNavController().navigate(R.id.action_exportManager_to_exportDatePicker)
        }

        // Export CSV to Downloads
        btnExportEmail.setOnClickListener {
            currentSummary?.let { summary ->
                val csv = buildReportCsv(summary)
                saveCsvToDownloads(csv, summary)
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Please select a date range first.",
                    Toast.LENGTH_SHORT
                ).show()
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
        val downloadDir = File("/sdcard/Download")
        if (!downloadDir.exists()) downloadDir.mkdirs()

        val filename = "TimeSummary_${summary.startDate}_to_${summary.endDate}.csv"
        val downloads = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outFile = File(downloads, filename)

        try {
            outFile.writeText(csvContent)
            Toast.makeText(
                requireContext(),
                "Saved report to Downloads/$filename",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error saving report: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

