package edu.msoe.myapplication.data

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JiraApiServiceImpl : JiraApiService {

    private val email = "bassoe@msoe.edu" // Replace with your email
    private val apiToken = "ATATT3xFfGF0l33rpmjeFMLXburt5t7c8AMPn__OMw4NafgABk8U2xsv-lgdYi_gYFaEXc4OeQ66Rx2MCmZXBAY_K2GT-cY1qndK-qS3V7yem_XdQHJ54avptCxwJzx3iyr_uOz5hgY82eVxKwLBPvq4m7zHyKyYGbHCH-0IKIHXtUkgFckJjeM=D51610E0" // Updated API token

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
        .baseUrl("https://pongstars.atlassian.net/rest/agile/1.0/") // Updated base URL
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(JiraApiService::class.java)

    override suspend fun getIssuesForBoard(boardId: Int, state: String): JiraIssuesResponse {
        return api.getIssuesForBoard(boardId, state)
    }
}