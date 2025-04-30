package edu.msoe.myapplication.data

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JiraApiServiceImpl : JiraApiService {

    private val email: String = "bassoe@msoe.edu" // Replace with your email
    private val apiToken: String = "insert-key-here"

    private val encodedCredentials: String = Base64.encodeToString(
        "$email:$apiToken".toByteArray(), Base64.NO_WRAP
    )

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Basic $encodedCredentials")
                .build()
            chain.proceed(request)
        }
        .build()

    // Retrofit for Agile endpoints (board/{boardId}/issue)
    private val agileRetrofit = Retrofit.Builder()
        .baseUrl("https://pongstars.atlassian.net/rest/agile/1.0/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit for core Jira endpoints (issue/{issueId}/worklog)
    private val coreRetrofit = Retrofit.Builder()
        .baseUrl("https://pongstars.atlassian.net/rest/api/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Service instances
    private val agileApi: JiraApiService = agileRetrofit.create(JiraApiService::class.java)
    private val coreApi:  JiraApiService = coreRetrofit.create(JiraApiService::class.java)

    override suspend fun getIssuesForBoard(boardId: Int, state: String): JiraIssuesResponse {
        return agileApi.getIssuesForBoard(boardId, state)
    }

    override suspend fun getWorklogsForIssue(issueId: String): WorklogResponse {
        return coreApi.getWorklogsForIssue(issueId)
    }

    override suspend fun addWorklog(
        issueId: String,
        request: AddWorklogRequest
    ): AddWorklogResponse {
        return coreApi.addWorklog(issueId, request)
    }
}