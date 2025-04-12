package edu.msoe.myapplication.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JiraApiServiceImpl : JiraApiService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://your-domain.atlassian.net/rest/agile/1.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(JiraApiService::class.java)

    override suspend fun getIssuesForBoard(boardId: Int, state: String): JiraIssuesResponse {
        return api.getIssuesForBoard(boardId, state)
    }
}