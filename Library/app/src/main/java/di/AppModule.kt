package di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import room.LibraryDatabase
import room.MIGRATION_1_2
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideLibraryDatabase(application: Application): LibraryDatabase {
        return Room.databaseBuilder(
            application,
            LibraryDatabase::class.java, "library.db"
        ).addMigrations(MIGRATION_1_2).build()
    }
}