package edu.msoe.myapplication.data

class TaskRepository(private val apiService: JiraApiService) {

    suspend fun fetchTasks(boardId: Int): List<Issue> {
        val response = apiService.getIssuesForBoard(boardId)
        return response.issues.map { jiraIssue ->
            Issue(
                id = jiraIssue.id,
                title = jiraIssue.fields.summary
            )
        }
    }

    fun pushTimeRecord(issueId: String, timeSpent: Long) {
        // TODO: Implement the API call to update JIRA with the time record.
    }
}

