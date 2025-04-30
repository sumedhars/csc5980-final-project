package edu.msoe.myapplication.ui.viewmodels

import androidx.lifecycle.*
import edu.msoe.myapplication.data.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TaskRepository) : ViewModel() {

    private var startTimestamp: Long = 0L
    private var accumulatedMillis: Long = 0L

    private val _elapsedMillis = MutableLiveData<Long>(0L)
    val elapsedMillis: LiveData<Long> = _elapsedMillis

    private var tickerJob: Job? = null

    /** Start timing a new session. */
    fun startTimer() {
        // reset any previous state
        startTimestamp = System.currentTimeMillis()
        accumulatedMillis = 0L
        _elapsedMillis.value = 0L

        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                _elapsedMillis.postValue(accumulatedMillis + (now - startTimestamp))
                delay(1000L)
            }
        }
    }

    /** Pause the timer, logging the elapsed time so far. */
    fun pauseTimer(issueId: String) {
        tickerJob?.cancel()
        val now = System.currentTimeMillis()
        val sessionMillis = accumulatedMillis + (now - startTimestamp)
        accumulatedMillis = sessionMillis
        _elapsedMillis.value = sessionMillis

        // send to JIRA as minutes (rounding)
//        val minutes = (sessionMillis / 1000 / 60).toInt()
//        viewModelScope.launch {
//            repository.logWork(issueId, minutes)
//        }
    }

    /** Resume timing the same session. */
    fun resumeTimer() {
        startTimestamp = System.currentTimeMillis()
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                _elapsedMillis.postValue(accumulatedMillis + (now - startTimestamp))
                delay(1000L)
            }
        }
    }

    /** Stop the timer completely and log the final time. */
    fun stopTimer(issueId: String) {
        tickerJob?.cancel()
        val now = System.currentTimeMillis()
        val totalMillis = accumulatedMillis + (now - startTimestamp)
        _elapsedMillis.value = totalMillis

        val minutes = (totalMillis / 1000 / 60).toInt()
        viewModelScope.launch {
            repository.logWork(issueId, minutes)
        }
    }

    /** Reset the timer completely without logging the time. */
    fun resetTimer() {
        tickerJob?.cancel()
        startTimestamp = 0L
        accumulatedMillis = 0L
        _elapsedMillis.value = 0L
    }

    /** Log time to Jira, optionally with a start time. */
    fun logTime(issueId: String, durationInSeconds: Long, started: String? = null) {
        if (durationInSeconds < 60) {
            viewModelScope.launch {
                repository.logWork(issueId, 1, started)
            }
        }
        else {
            val minutes = (durationInSeconds / 60).toInt()
            viewModelScope.launch {
                repository.logWork(issueId, minutes, started)
            }
        }
    }

    /** Log manual time to Jira. */
    fun logManualTime(issueId: String, dateTime: String, durationInSeconds: Long) {
        val minutes = (durationInSeconds / 60).toInt()
        viewModelScope.launch {
            repository.logWork(issueId, minutes, dateTime)
        }
    }

    override fun onCleared() {
        tickerJob?.cancel()
        super.onCleared()
    }
}

class TimerViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
