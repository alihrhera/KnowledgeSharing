package hrhera.ali.backgroundsync.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hrhera.ali.backgroundsync.data.inmemory.FakeApi
import hrhera.ali.backgroundsync.data.network.UploadService
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiProvider {
    @Provides
    @Singleton
    fun provideUploadService(): UploadService = FakeApi()
}