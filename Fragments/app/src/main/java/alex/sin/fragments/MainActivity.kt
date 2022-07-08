package alex.sin.fragments

import alex.sin.fragments.fragments.FirstFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private fun <T> illegalState(): T = throw IllegalStateException("Invalid fragment id")

    private var currentTabId: Int? = null
    private var tabs = mutableListOf<Int>()

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().hide(supportFragmentManager.fragments[0]).commit()

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        if (tabs.isEmpty()) {
            navigateToFragment(R.id.first_tab_navigation)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.first_tab_navigation,
                R.id.second_tab_navigation,
                R.id.third_tab_navigation -> {
                    navigateToFragment(it.itemId)
                    true
                }
                else -> false
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        currentTabId = savedInstanceState.getInt("currentTabId")
        val tabsCount = savedInstanceState.getInt("tabsCount")
        for (i in 0 until tabsCount) {
            val tab = savedInstanceState.getInt("tab_" + i)
            tabs.add(tab)
        }

        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { transaction.hide(it) }
        transaction.commit()

        supportFragmentManager.beginTransaction().show(
            supportFragmentManager.findFragmentByTag(currentTabId.toString()) ?: illegalState())
            .commit()

        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("currentTabId", currentTabId ?: illegalState())
        outState.putInt("tabsCount", tabs.size)
        for (i in 0 until tabs.size) {
            outState.putInt("tab_" + i, tabs[i])
        }

        super.onSaveInstanceState(outState)
    }

    private fun navigateToFragment(id: Int) {
        val destination = supportFragmentManager.findFragmentByTag(id.toString()) ?: FirstFragment()

        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.findFragmentByTag(currentTabId.toString())?.let {
            transaction.hide(it)
        }

        if (destination.isAdded) {
            transaction.show(destination)
        } else {
            transaction.add(R.id.nav_host_fragment, destination, id.toString())
            tabs.add(id)
        }

        this.currentTabId = destination.tag?.toInt()
        transaction.commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(currentTabId.toString()) ?: illegalState()
        
        if (fragment.childFragmentManager.backStackEntryCount > 0) {
            fragment.childFragmentManager.popBackStack()
        } else {
            if (tabs.size > 1) {
                tabs.removeLast()
                bottomNavigationView.selectedItemId = tabs.last()
                navigateToFragment(tabs.last())
            } else {
                finish()
            }
        }
    }
}
