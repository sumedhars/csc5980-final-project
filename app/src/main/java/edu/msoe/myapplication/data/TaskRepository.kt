package edu.msoe.myapplication.data

import java.time.LocalDate

// Auxiliary data class
data class TimeLog(
    val issueId: String,
    val durationMinutes: Int,
    val date: LocalDate
)


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

    /**
     * Fetch all worklogs for each issue, filter to the [start]–[end] range,
     * and return a flat list of TimeLog entries.
     */
    suspend fun getTimeLogsByDateRange(start: LocalDate, end: LocalDate): List<TimeLog> {
        // You may want to pass boardId in or cache inside the repo
        val issues = fetchTasks(boardId = 1)
        val entries = mutableListOf<TimeLog>()

        for (issue in issues) {
            val resp = apiService.getWorklogsForIssue(issue.id)
            for (wl in resp.worklogs) {
                // Parse the ISO date (first 10 chars = YYYY‑MM‑DD)
                val date = LocalDate.parse(wl.started.substring(0, 10))
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    val minutes = wl.timeSpentSeconds / 60
                    entries += TimeLog(
                        issueId = issue.id,
                        durationMinutes = minutes,
                        date = date
                    )
                }
            }
        }
        return entries
    }

    /**
     * Log time to Jira, optionally with a start time.
     */
    suspend fun logWork(issueId: String, minutes: Int, started: String? = null) {
        val request = AddWorklogRequest(
            timeSpentSeconds = minutes * 60,
            started = started
        )
        apiService.addWorklog(issueId, request)
    }
}
