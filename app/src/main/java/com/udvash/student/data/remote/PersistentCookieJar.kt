package com.udvash.student.data.remote

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieJar : CookieJar {
    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val currentCookies = cookieStore[host]?.toMutableList() ?: mutableListOf()
        
        for (cookie in cookies) {
            currentCookies.removeIf { it.name == cookie.name }
            currentCookies.add(cookie)
        }
        cookieStore[host] = currentCookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: Collections.emptyList()
    }
    
    fun clear() {
        cookieStore.clear()
    }
}
