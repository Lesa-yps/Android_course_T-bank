package com.example.android_course_t_bank

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import domain.LibraryObj

class MainActivity : AppCompatActivity(),
ListFragment.OnLibraryItemClickListener,
DetailFragment.OnSaveListener {

    private val isLandscape: Boolean
    get() = findViewById<View?>(R.id.detailFragmentContainer) != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (isLandscape) {
                supportFragmentManager.commit {
                    replace(R.id.listFragmentContainer, ListFragment())
                }
            } else {
                supportFragmentManager.commit {
                    replace(R.id.fragmentContainer, ListFragment())
                }
            }
        }
    }

    override fun onLibraryItemClick(item: LibraryObj) {
        val fragment = DetailFragment.newInstance(item, isReadOnly = true)

        if (isLandscape) {
            supportFragmentManager.commit {
                replace(R.id.detailFragmentContainer, fragment)
            }
        } else {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, fragment)
                addToBackStack(null)
            }
        }
    }

    override fun onAddItemRequested() {
        val fragment = DetailFragment.newInstance(null, isReadOnly = false)

        if (isLandscape) {
            supportFragmentManager.commit {
                replace(R.id.detailFragmentContainer, fragment)
            }
        } else {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, fragment)
                addToBackStack(null)
            }
        }
    }

    // При сохранении объекта из DetailFragment
    override fun onObjectSaved(obj: LibraryObj) {
        val listFragment = supportFragmentManager
            .findFragmentById(if (isLandscape) R.id.listFragmentContainer else R.id.fragmentContainer)

        if (listFragment is ListFragment) {
            listFragment.addItem(obj)
        }

        if (!isLandscape) {
            supportFragmentManager.popBackStack()
        }
    }
}
