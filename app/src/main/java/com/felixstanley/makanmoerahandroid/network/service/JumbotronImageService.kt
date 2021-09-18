package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.jumbotron.JumbotronImage
import retrofit2.http.GET

private const val BASE_URL = "jumbotronImage"

interface JumbotronImageService {

    @GET(BASE_URL)
    suspend fun getJumbotronImages(): List<JumbotronImage>
}