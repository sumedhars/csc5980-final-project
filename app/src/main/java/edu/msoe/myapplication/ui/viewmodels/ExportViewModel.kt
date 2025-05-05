package edu.msoe.myapplication.ui.viewmodels

import androidx.lifecycle.*
import edu.msoe.myapplication.data.ExportSummary
import edu.msoe.myapplication.data.TaskRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExportViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _summary = MutableLiveData<ExportSummary>()
    val summary: LiveData<ExportSummary> = _summary

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * Load all time logs between [startDate] and [endDate], then
     * compute and emit an ExportSummary, signaling loading state.
     */
    fun loadSummary(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _loading.postValue(true)
            try {
                val logs = repository.getTimeLogsByDateRange(startDate, endDate)
                // Sum up total minutes
                val totalMinutes = logs.sumOf { it.durationMinutes }
                // Count distinct issues
                val totalTasks = logs.map { it.issueId }.toSet().size
                val hours = totalMinutes / 60.0

                _summary.postValue(
                    ExportSummary(
                        startDate = startDate,
                        endDate = endDate,
                        totalHours = hours,
                        totalTasks = totalTasks
                    )
                )
            } finally {
                _loading.postValue(false)
            }
        }
    }
}

class ExportViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
