package com.app.todo.data.remote

import com.app.core.Api
import com.app.todo.data.Post
import retrofit2.http.*

object PostApi {
    interface Service {
        @GET("/posts")
        suspend fun find(): List<Post>

        @GET("/posts/{id}")
        suspend fun read(@Path("id") postId: Long): Post;

        @Headers("Content-Type: application/json")
        @POST("/posts")
        suspend fun create(@Body item: Post): Post

        @Headers("Content-Type: application/json")
        @PUT("/posts")
        suspend fun update(@Body post: Post): Post
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}