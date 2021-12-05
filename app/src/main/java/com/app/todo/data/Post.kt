package com.app.todo.data

data class Post (
    var id: Long,
    var imageUrl: String,
    var title: String,
    var authorName: String,
    var description: String,
    var latitude: Double,
    var longitude: Double
) { }