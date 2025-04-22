package edu.msoe.myapplication.data

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JiraApiServiceImpl : JiraApiService {

    private val email: String = "bassoe@msoe.edu" // Replace with your email
    private val apiToken: String = "api-token-here" // Updated API token

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

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pongstars.atlassian.net/rest/agile/1.0/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(JiraApiService::class.java)

    override suspend fun getIssuesForBoard(boardId: Int, state: String): JiraIssuesResponse {
        return api.getIssuesForBoard(boardId, state)
    }

    override suspend fun getWorklogsForIssue(issueId: String): WorklogResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addWorklog(
        issueId: String,
        request: AddWorklogRequest
    ): AddWorklogResponse {
        TODO("Not yet implemented")
    }
}