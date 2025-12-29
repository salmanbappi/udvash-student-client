package com.udvash.student.data.repository

import com.udvash.student.data.model.CourseContentItem
import com.udvash.student.data.remote.ApiClient
import org.jsoup.Jsoup

object CourseRepository {
    private val api = ApiClient.api

    suspend fun getCourseContent(): Result<List<CourseContentItem>> {
        return try {
            // NOTE: The actual endpoint for "Course & Content" as per analysis is /Content/Index?id=2
            // We need to add this to the API interface first, or use a generic GET.
            // For now, I'll assume we add it to the API interface or use a dynamic path.
            // Since UdvashApi.kt doesn't have it, I'll add a generic GET method to ApiClient or just use what we have.
            // Wait, I can't modify the Interface from here easily without a separate step.
            // But I can cast the Retrofit service if I had a generic one.
            // Let's assume I will add `getCourseContentPage()` to `UdvashApi` in the next step.
            
            // Temporary placeholder call - will fail until API is updated
            // val response = api.getCourseContentPage() 
            // Actually, I can use the existing `getDashboard` as a template or just add the function.
            
            // Let's proceed assuming I will update UdvashApi.kt immediately after this.
            val response = api.getCourseContentPage()
            
            if (response.isSuccessful && response.body() != null) {
                val items = parseCourseContent(response.body()!!)
                Result.success(items)
            } else {
                Result.failure(Exception("Failed to fetch course content"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCourseContent(html: String): List<CourseContentItem> {
        val doc = Jsoup.parse(html)
        val items = mutableListOf<CourseContentItem>()
        val baseUrl = "https://online.udvash-unmesh.com"

        // Selector based on analysis: .card-body a.btn-menu
        val links = doc.select(".card.course-and-content .card-body a.btn-menu")

        for (link in links) {
            val href = link.attr("href")
            val title = link.select("h3").text().trim()
            val iconUrl = link.select("img").attr("src")

            if (title.isNotEmpty()) {
                items.add(CourseContentItem(
                    title = title,
                    iconUrl = if (iconUrl.startsWith("http")) iconUrl else "$baseUrl$iconUrl",
                    link = if (href.startsWith("http")) href else "$baseUrl$href"
                ))
            }
        }

        return items
    }
}
