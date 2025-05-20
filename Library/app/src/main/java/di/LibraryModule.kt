package di

import api.retrofit.BookApi
import api.retrofit.RetrofitHelper
import dagger.Module
import dagger.Provides
import repository.LibraryRepository
import repository.LibraryRepositoryImpl
import room.LibraryDao
import room.LibraryDatabase
import utils.SortType


@Module
class LibraryModule {
    @Provides
    @LibraryScope
    fun provideLibraryDao(database: LibraryDatabase): LibraryDao = database.libraryDao()

    @Provides
    @LibraryScope
    fun provideBookApi(): BookApi = RetrofitHelper.createBookApi()

    @Provides
    @LibraryScope
    fun provideLibraryRepository(
        dao: LibraryDao,
        bookApi: BookApi
    ): LibraryRepository = LibraryRepositoryImpl(dao, bookApi)

    @Provides
    @LibraryScope
    fun provideSortType(sortType: SortType): SortType = sortType
}
