package hrhera.ali.backgroundsync.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hrhera.ali.backgroundsync.data.inmemory.InMemoryUploadStateController
import hrhera.ali.backgroundsync.data.repo.UploadRepositoryImpl
import hrhera.ali.backgroundsync.domain.controller.UploadStateController
import hrhera.ali.backgroundsync.domain.repo.UploadRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UploadModule {
    @Binds
    @Singleton
    abstract fun bindUploadStateController(
        impl: InMemoryUploadStateController
    ): UploadStateController


    @Binds
    @Singleton
    abstract fun bindUploadRepo(
        impl: UploadRepositoryImpl
    ): UploadRepository
}
