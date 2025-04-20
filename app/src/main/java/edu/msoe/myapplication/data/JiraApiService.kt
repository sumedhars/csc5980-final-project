package edu.msoe.myapplication.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JiraApiService {

    @GET("board/{boardId}/issue")
    suspend fun getIssuesForBoard(
        @Path("boardId") boardId: Int,
        @Query("state") state: String = "active"
    ): JiraIssuesResponse

    // fetch all worklogs for a given issue
    @GET("issue/{issueId}/worklog")
    suspend fun getWorklogsForIssue(
        @Path("issueId") issueId: String
    ): WorklogResponse

    // add a worklog entry for an issue
    @POST("issue/{issueId}/worklog")
    suspend fun addWorklog(
        @Path("issueId") issueId: String,
        @Body request: AddWorklogRequest
    ): AddWorklogResponse
}

// Data classes for the API response
data class JiraIssuesResponse(
    val issues: List<JiraIssue>
)

data class JiraIssue(
    val id: String,
    val key: String,
    val fields: JiraIssueFields
)

data class JiraIssueFields(
    val summary: String
)

data class WorklogResponse(
    val worklogs: List<WorklogItem>
)

data class WorklogItem(
    val timeSpentSeconds: Int,
    val started: String       // ISO 8601, e.g. "2025-04-20T14:12:00.000+0000"
)

data class AddWorklogRequest(
    val timeSpentSeconds: Int,
    val comment: String? = null
)

data class AddWorklogResponse(
    val id: String,
    val self: String
)