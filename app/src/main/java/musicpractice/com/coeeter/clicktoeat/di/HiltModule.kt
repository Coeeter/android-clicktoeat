package musicpractice.com.coeeter.clicktoeat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.api.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Singleton
    @Provides
    fun providesRetrofit(
        @ApplicationContext context: Context
    ): Retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.base_url))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun providesCommentService(
        retrofit: Retrofit
    ): CommentService = retrofit.create(CommentService::class.java)

    @Singleton
    @Provides
    fun providesFavoriteService(
        retrofit: Retrofit
    ): FavoriteService = retrofit.create(FavoriteService::class.java)

    @Singleton
    @Provides
    fun providesLikeService(
        retrofit: Retrofit
    ): LikeService = retrofit.create(LikeService::class.java)

    @Singleton
    @Provides
    fun providesRestaurantService(
        retrofit: Retrofit
    ): RestaurantService = retrofit.create(RestaurantService::class.java)

    @Singleton
    @Provides
    fun providesUserService(
        retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

}