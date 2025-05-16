package com.example.minikeep.data.remote.api

import com.google.api.client.auth.oauth2.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoogleTokenApi {
    @FormUrlEncoded
    @POST("token")
    suspend fun getAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): TokenResponse
}
