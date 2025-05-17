package com.example.frontend_happygreen.models

class ScannedObject {
    var id: Int = 0
    var user: Int = 0
    var object_type: String? = null
    var description: String? = null
    var image: String? = null // URL
    var recognized_at: String? = null
    var location_lat: Double? = null
    var location_lng: Double? = null
}