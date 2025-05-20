package di

import com.example.android_course_t_bank.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent
import utils.SortType
import viewmodel.LibraryViewModel
import javax.inject.Scope


@LibraryScope
@Subcomponent(modules = [LibraryModule::class])
interface LibraryComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance sortType: SortType): LibraryComponent
    }

    fun inject(activity: MainActivity)

    fun libraryViewModelFactory(): LibraryViewModel.Factory
}

// аннотация для области видимости
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LibraryScope