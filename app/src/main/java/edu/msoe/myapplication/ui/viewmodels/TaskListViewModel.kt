package edu.msoe.myapplication.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.msoe.myapplication.data.Issue
import edu.msoe.myapplication.data.TaskRepository
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _issues = MutableLiveData<List<Issue>>()
    val issues: LiveData<List<Issue>> get() = _issues

    fun fetchIssues(boardId: Int) {
        viewModelScope.launch {
            val fetchedIssues = repository.fetchTasks(boardId)
            _issues.postValue(fetchedIssues)
        }
    }
}
