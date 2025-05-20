package di

import android.app.Application
import utils.getSavedSortType

class LibraryApplication : Application(), LibraryComponentProvider {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun getLibraryComponent(): LibraryComponent {
        val sortType = getSavedSortType(this)
        return appComponent.libraryComponent().create(sortType)
    }
}