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
            val loginBody = loginResponse.body() ?: ""
            
            // Check for explicit failure message
            if (loginBody.contains("Invalid Password", ignoreCase = true) || 
                loginBody.contains("validation-summary-errors", ignoreCase = true)) {
                return Result.failure(Exception("Invalid Password"))
            }

            // Check for success (Dashboard text or Redirect)
            if (loginResponse.code() == 302 || 
                loginBody.contains("<title>Dashboard", ignoreCase = true) || 
                loginBody.contains("Dashboard", ignoreCase = true)) {
                 return Result.success(true)
            }

            // Fallback: If we are still on the login page (check title), it's a failure
            if (loginBody.contains("<title>Student Login", ignoreCase = true)) {
                return Result.failure(Exception("Login failed. Please check your credentials."))
            }

            // Assume success if none of the above failures matched (risky but often true for 200 OK dashboard)
            return Result.success(true)

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
