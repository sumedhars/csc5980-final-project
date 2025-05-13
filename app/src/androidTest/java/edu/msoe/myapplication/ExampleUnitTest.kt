package edu.msoe.myapplication

import android.util.Base64
import edu.msoe.myapplication.data.JiraApiService
import org.junit.Test
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class JiraIntegrationTest {
    private val baseUrl: String = "https://pongstars.atlassian.net/rest/api/3/"

    private val email: String = "bassoe@msoe.edu" // Replace with your email
    private val apiToken: String = "api-key-here"



    // Service instances


    private var testIssueId: String? = "10221"

    @Before
    fun setupWorkLogTest() {
        testIssueId = "10221" // Replace with an actual issue ID
    }

    @Test
    fun testWorkLogIntegration() {
        val authHeader: String = Base64.encodeToString(
            "$email:$apiToken".toByteArray(), Base64.NO_WRAP
        )

        // Log work for the existing issue
        val workLogJson = JSONObject().apply {
            put("timeSpentSeconds", 3600) // 1 hour
            put("started", "2025-05-08T12:00:00.000+0000")
        }

        val logRequest = Request.Builder()
            .url("$baseUrl/issue/$testIssueId/worklog")
            .post(workLogJson.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", authHeader)
            .build()

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Basic $authHeader")
                    .url("$baseUrl/issue/$testIssueId/worklog")
                    .post(workLogJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                chain.proceed(request)
            }
            .build()

        val logResponse = client.newCall(logRequest).execute()
        if (!logResponse.isSuccessful) {
            println("Failed to log work. Code: ${logResponse.code}, Body: ${logResponse.body?.string()}")
        }
        assertTrue(logResponse.isSuccessful)

        // Verify the work log exists
        val verifyRequest = Request.Builder()
            .url("$baseUrl/issue/$testIssueId/worklog")
            .get()
            .addHeader("Authorization", authHeader)
            .build()

        val verifyResponse = client.newCall(verifyRequest).execute()
        assertTrue(verifyResponse.isSuccessful)
        val responseBody = verifyResponse.body?.string()
        val jsonResponse = JSONObject(responseBody ?: "")
        val workLogs = jsonResponse.getJSONArray("worklogs")
        assertTrue(workLogs.length() > 0)

        // Delete the work log
        val workLogId = workLogs.getJSONObject(0).getString("id")
        val deleteRequest = Request.Builder()
            .url("$baseUrl/issue/$testIssueId/worklog/$workLogId")
            .delete()
            .addHeader("Authorization", authHeader)
            .build()

        val deleteResponse = client.newCall(deleteRequest).execute()
        assertTrue(deleteResponse.isSuccessful)
    }

    @After
    fun deleteTestIssue() {
        val authHeader: String = Base64.encodeToString(
            "$email:$apiToken".toByteArray(), Base64.NO_WRAP
        )

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Basic $authHeader")
                    .build()
                chain.proceed(request)
            }
            .build()
        testIssueId?.let {
            val deleteRequest = Request.Builder()
                .url("$baseUrl/issue/$it")
                .delete()
                .addHeader("Authorization", authHeader)
                .build()

            client.newCall(deleteRequest).execute().use { response ->
                assertTrue(response.isSuccessful)
            }
        }
    }
}

interface ApiService {
    @POST("issue")
    fun createIssue(@Body issueRequest: CreateIssueRequest): Call<IssueResponse>
}

data class IssueResponse(
    val id: String,
    val key: String
)

data class CreateIssueRequest(
    val fields: IssueFields
)

data class IssueFields(
    val project: ProjectKey,
    val summary: String,
    val description: AtlassianDocument,
    val issuetype: IssueType,
    val id: String
)

data class ProjectKey(
    val key: String
)

data class IssueType(
    val name: String
)

data class AtlassianDocument(
    val type: String = "doc",
    val version: Int = 1,
    val content: List<Content>
)

data class Content(
    val type: String,
    val content: List<ContentText>
)

data class ContentText(
    val type: String,
    val text: String
)

