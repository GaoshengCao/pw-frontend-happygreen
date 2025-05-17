package com.example.frontend_happygreen.models

class Post {
    var id: Int = 0
    var group: Int = 0 // Group ID
    var author: Int = 0 // User ID
    var text: String? = null
    var image: String? = null // URL
    var location_lat: Double? = null
    var location_lng: Double? = null
    var created_at: String? = null
}

