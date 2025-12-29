package com.udvash.student.data.repository

import com.udvash.student.data.remote.ApiClient
import org.jsoup.Jsoup
import java.io.IOException

object AuthRepository {
    private val api = ApiClient.api

    suspend fun performLogin(regNo: String, password: String): Result<Boolean> {
        try {
            // Step 1: Get Initial Page for Token
            val initialResponse = api.getLoginPage()
            if (!initialResponse.isSuccessful) return Result.failure(Exception("Failed to load login page"))
            
            var html = initialResponse.body() ?: ""
            var token = extractToken(html) ?: return Result.failure(Exception("Token not found in initial page"))

            // Step 2: Post Registration Number
            val step1Params = mapOf(
                "RegistrationNumber" to regNo,
                "__RequestVerificationToken" to token
            )
            val step1Response = api.postRegistrationNumber(step1Params)
            if (!step1Response.isSuccessful) return Result.failure(Exception("Step 1 Failed"))
            
            html = step1Response.body() ?: ""
            // The response might be the Password page (200 OK) or a redirect
            // If we are at the password page, we need the NEW token
            token = extractToken(html) ?: return Result.failure(Exception("Token not found in password page"))

            // Step 3: Post Password
            val step2Params = mapOf(
                "RegistrationNumber" to regNo,
                "Password" to password,
                "RememberMe" to "true",
                "__RequestVerificationToken" to token,
                "returnUrl" to ""
            )
            
            val loginResponse = api.postPassword(step2Params)
            // Success is usually determined by a redirect to Dashboard or getting the Dashboard HTML
            // Check if we are redirected to Dashboard or if the body contains "Dashboard"
            val loginBody = loginResponse.body() ?: ""
            
            if (loginResponse.code() == 302 || loginBody.contains("Dashboard", ignoreCase = true) || loginBody.contains("Welcome")) {
                 return Result.success(true)
            }

            return Result.failure(Exception("Login failed, incorrect credentials or flow."))

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun extractToken(html: String): String? {
        val doc = Jsoup.parse(html)
        val element = doc.select("input[name=__RequestVerificationToken]").first()
        return element?.attr("value")
    }
}
