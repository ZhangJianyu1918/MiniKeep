package com.example.minikeep.data.repository

import android.content.Context
import com.example.minikeep.data.remote.api.GoogleTokenApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable
import com.google.android.gms.common.api.Scope
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.services.calendar.CalendarScopes
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleAuthenticationRepository(context: Context) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken("823384730210-7jj7nh5b3ia7t3f258g6csii69s08lij.apps.googleusercontent.com")
        .requestIdToken("3797093735-8pgvsu8b3hk99ju4i88i7vf0gu6ogi2i.apps.googleusercontent.com")
        .requestEmail()
        .requestScopes(Scope(CalendarScopes.CALENDAR))
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://oauth2.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleTokenApi::class.java)

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
//    googleAccount?.let {
//        val accessToken = it.idToken // 或者 requestServerAuthCode 时换 token
//    }

    fun logout() {
        googleSignInClient.signOut().addOnCompleteListener {
            // 已成功退出
            println("GoogleSignOut User signed out")
        }
    }
}

