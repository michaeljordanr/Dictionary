package com.michaeljordanr.dictionary.feature_dictionary.di

import android.app.Application
import androidx.room.PrimaryKey
import androidx.room.Room
import com.google.gson.Gson
import com.michaeljordanr.dictionary.feature_dictionary.data.local.Converters
import com.michaeljordanr.dictionary.feature_dictionary.data.local.WordInfoDao
import com.michaeljordanr.dictionary.feature_dictionary.data.local.WordInfoDatabase
import com.michaeljordanr.dictionary.feature_dictionary.data.remote.DictionaryApi
import com.michaeljordanr.dictionary.feature_dictionary.data.repository.WordInfoRepositoryImpl
import com.michaeljordanr.dictionary.feature_dictionary.data.util.GsonParser
import com.michaeljordanr.dictionary.feature_dictionary.domain.repository.WordInfoRepository
import com.michaeljordanr.dictionary.feature_dictionary.domain.usecase.GetWordInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WordInfoModule {

    @Provides
    @Singleton
    fun provideGetWordInfoUseCase(repository: WordInfoRepository): GetWordInfo {
        return GetWordInfo(repository)
    }

    @Provides
    @Singleton
    fun provideWordInfoRepository(
        db: WordInfoDatabase,
        api: DictionaryApi
    ): WordInfoRepository {
        return WordInfoRepositoryImpl(api, db.dao)
    }

    @Provides
    @Singleton
    fun provideWordInfoDatabase(app: Application): WordInfoDatabase {
        return Room.databaseBuilder(
            app, WordInfoDatabase::class.java, "word_db"
        )
            .fallbackToDestructiveMigration()
            .addTypeConverter(Converters(GsonParser(Gson())))
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionary(): DictionaryApi {
        return Retrofit.Builder()
            .baseUrl(DictionaryApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}