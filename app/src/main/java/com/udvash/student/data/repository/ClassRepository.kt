package com.udvash.student.data.repository

import com.udvash.student.data.model.ClassItem
import com.udvash.student.data.remote.ApiClient
import org.jsoup.Jsoup

object ClassRepository {
    private val api = ApiClient.api

    suspend fun getPastClasses(): Result<List<ClassItem>> {
        return try {
            val response = api.getPastClasses()
            if (response.isSuccessful && response.body() != null) {
                val items = parseClasses(response.body()!!)
                Result.success(items)
            } else {
                Result.failure(Exception("Failed to fetch classes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseClasses(html: String): List<ClassItem> {
        val doc = Jsoup.parse(html)
        val items = mutableListOf<ClassItem>()
        val baseUrl = "https://online.udvash-unmesh.com"

        val cards = doc.select(".uu-routine-item")
        
        for (card in cards) {
            val title = card.select(".uu-routine-title").text().trim()
            
            // Extract Date: Find h4 that contains "Date & Time"
            var date = ""
            val h4s = card.select("h4")
            for (h4 in h4s) {
                if (h4.text().contains("Date & Time", ignoreCase = true)) {
                    // Remove the label "Date & Time" to get just the value
                    date = h4.ownText().trim() 
                    // If ownText is empty (sometimes it's in a span or text node next to span), try replacing
                    if (date.isEmpty()) {
                        date = h4.text().replace("Date & Time", "").trim()
                    }
                    break
                }
            }

            // Links
            val links = card.select(".uu-routine-item-footer a")
            var videoUrl: String? = null
            var noteUrl: String? = null

            for (link in links) {
                val href = link.attr("href")
                val text = link.text().trim()
                val fullUrl = if (href.startsWith("http")) href else "$baseUrl$href"

                if (text.equals("Video", ignoreCase = true) || href.contains("videoId")) {
                    videoUrl = fullUrl
                } else if (text.equals("Notes", ignoreCase = true) || href.contains("isNotes=true")) {
                    noteUrl = fullUrl
                }
            }

            if (title.isNotEmpty()) {
                items.add(ClassItem(
                    title = title,
                    date = date,
                    subject = "", // Subject is mixed in the body, complex to parse simply, skipping for now
                    videoUrl = videoUrl,
                    lectureNoteUrl = noteUrl,
                    practiceNoteUrl = null // No separate practice note link found in footer
                ))
            }
        }

        return items
    }
}
