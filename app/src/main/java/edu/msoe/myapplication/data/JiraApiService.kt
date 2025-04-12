package edu.msoe.myapplication.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JiraApiService {

    @GET("board/{boardId}/issue")
    suspend fun getIssuesForBoard(
        @Path("boardId") boardId: Int,
        @Query("state") state: String = "active"
    ): JiraIssuesResponse
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
