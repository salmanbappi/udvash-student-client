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

        // NOTE: Selectors need to be adjusted based on actual HTML structure of /Routine/PastClasses.
        // Assuming a standard table or list structure based on common Udvash themes.
        // Since I don't have the PastClasses HTML, I will use generic robust selectors 
        // that likely match their card/list layout.
        
        val cards = doc.select(".card, .class-item, .uu-card") // Fallback selectors
        
        // If specific structure is unknown, we look for containers with dates and 'View' buttons
        // For now, I'll assume a structure similar to what is typical for Bootstrap/their theme
        
        // Placeholder parsing logic - intended to be refined with real HTML
        // Looking for rows in a table or divs
        val rows = doc.select("tr")
        if (rows.isNotEmpty()) {
             for (row in rows) {
                 val cols = row.select("td")
                 if (cols.size >= 3) {
                     val date = cols[0].text()
                     val title = cols[1].text()
                     // Extract links
                     val videoLink = row.select("a[href*='video'], a[href*='player']").attr("href")
                     val pdfLink = row.select("a[href*='.pdf']").attr("href")
                     
                     if (title.isNotEmpty()) {
                         items.add(ClassItem(
                             title = title,
                             date = date,
                             subject = "", 
                             videoUrl = if (videoLink.isNotEmpty()) videoLink else null,
                             lectureNoteUrl = if (pdfLink.isNotEmpty()) pdfLink else null,
                             practiceNoteUrl = null
                         ))
                     }
                 }
             }
        }
        
        // If table parsing yielded nothing, try card based (common in mobile views)
        if (items.isEmpty()) {
            val divs = doc.select("div[class*=card]")
            for (div in divs) {
                val title = div.select("h4, h5, .title").text()
                val date = div.select(".date, span:matches(\\d)").text()
                val links = div.select("a")
                var videoUrl: String? = null
                var noteUrl: String? = null
                
                for (link in links) {
                    val href = link.attr("href")
                    if (href.contains("video") || href.contains("play")) videoUrl = href
                    if (href.endsWith(".pdf")) noteUrl = href
                }
                
                if (title.isNotEmpty()) {
                    items.add(ClassItem(title, date, "", videoUrl, noteUrl, null))
                }
            }
        }

        return items
    }
}
