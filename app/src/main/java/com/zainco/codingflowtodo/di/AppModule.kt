package com.zainco.codingflowtodo.di

import android.app.Application
import androidx.room.Room
import com.zainco.codingflowtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class/*means we want this module to be used inside this application component*/)//we want to use the same db throughout our whole app
object AppModule {
    //way of providing dependencies
    @Provides
    @Singleton
    fun provideDataBase(
        app: Application,
        callback: TaskDatabase.Callback,
    ) =
        Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)// this could be responsible for creating dummy database records for user first time displaying
            .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton/*lives as long as the application is*/
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope