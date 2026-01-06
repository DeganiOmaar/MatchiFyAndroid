package com.example.matchify.data.remote

import android.util.Log
import com.example.matchify.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiService(private val authPreferences: AuthPreferences) {

    private val baseUrl = "http://10.0.2.2:3000/"

    private val authInterceptor = Interceptor { chain ->
        val token = authPreferences.currentAccessToken.value

        val originalRequest = chain.request()
        val newRequest: Request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        Log.d("ApiService", "Sending request: ${newRequest.url}")
        Log.d("ApiService", "Authorization: ${newRequest.header("Authorization")}")

        val response = chain.proceed(newRequest)
        
        if (!response.isSuccessful) {
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            Log.e("ApiService", "HTTP ${response.code} Error: $errorBody")
        }
        
        response
    }




    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = com.google.gson.GsonBuilder()
        .serializeNulls()
        .create()
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(
                com.google.gson.GsonBuilder()
                    .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.IDENTITY)
                    .create()
            ))
            .build()
    }


    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val recruiterApi: RecruiterApi by lazy {
        retrofit.create(RecruiterApi::class.java)
    }


    val talentApi: TalentApi by lazy {
        retrofit.create(TalentApi::class.java)
    }


    val missionApi: MissionApi by lazy {
        retrofit.create(MissionApi::class.java)
    }


    val proposalApi: ProposalApi by lazy {
        retrofit.create(ProposalApi::class.java)
    }


    val conversationApi: ConversationApi by lazy {
        retrofit.create(ConversationApi::class.java)
    }

    val favoriteApi: FavoriteApi by lazy {
        retrofit.create(FavoriteApi::class.java)
    }


    val portfolioApi: PortfolioApi by lazy {
        retrofit.create(PortfolioApi::class.java)
    }


    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }


    val skillApi: SkillApi by lazy {
        retrofit.create(SkillApi::class.java)
    }


    val alertApi: AlertApi by lazy {
        retrofit.create(AlertApi::class.java)
    }


    val contractApi: ContractApi by lazy {
        retrofit.create(ContractApi::class.java)
    }


    val aiApi: AiApi by lazy {
        retrofit.create(AiApi::class.java)
    }

    val offerApi: OfferApi by lazy {
        retrofit.create(OfferApi::class.java)
    }

    val interviewApi: com.example.matchify.data.remote.api.InterviewApi by lazy {
        retrofit.create(com.example.matchify.data.remote.api.InterviewApi::class.java)
    }

    val ratingApi: com.example.matchify.data.remote.api.RatingApi by lazy {
        retrofit.create(com.example.matchify.data.remote.api.RatingApi::class.java)
    }


    companion object {
        @Volatile
        private var INSTANCE: ApiService? = null

        fun initialize(authPreferences: AuthPreferences) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = ApiService(authPreferences)
                    }
                }
            }
        }

        fun getInstance(): ApiService {
            return INSTANCE ?: throw IllegalStateException("ApiService has not been initialized")
        }
    }
}