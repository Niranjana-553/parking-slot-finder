package com.example.parkingslotfinder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private ApiClient() {}

    private static Retrofit springRetrofit;
    private static Retrofit supabaseRetrofit;

    public static Retrofit spring() {
        if (springRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String token = new SessionManager(
                                ParkingSlotFinderApp.getContext()).getAccessToken();

                        Request.Builder b = original.newBuilder()
                                .header("Accept", "application/json");

                        if (token != null && !token.isEmpty()) {
                            b.header("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(b.build());
                    })
                    .addInterceptor(logging)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            springRetrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return springRetrofit;
    }

    public static Retrofit supabase() {
        if (supabaseRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request r = chain.request().newBuilder()
                                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                                .header("Content-Type", "application/json")
                                .build();
                        return chain.proceed(r);
                    })
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            supabaseRetrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.SUPABASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return supabaseRetrofit;
    }
}
