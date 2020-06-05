package cz.chytilek.sportactivities

import android.os.Bundle
import android.view.Menu
import android.support.design.widget.NavigationView
import androidx.navigation.findNavController
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import androidx.navigation.ui.*
import cz.chytilek.sportactivities.model.SportActivity
import cz.chytilek.sportactivities.provider.SportActivityMeta

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_list, R.id.nav_add), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
            R.id.nav_add ->
            {
                if(findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.nav_list) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_ListFragment_to_AddFragment)
                }
            }
            R.id.nav_list ->
            {
                if(findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.nav_add) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_AddFragment_to_ListFragment)
                }
            }
        }

        menuItem.isChecked = true
        drawerLayout.closeDrawers()
        return true
    }

    fun openAddFragmentWithBundle(sportActivity: SportActivity){
        val bundle = Bundle()
        bundle.putSerializable(SportActivityMeta.BUNDLE_KEY, sportActivity)
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_ListFragment_to_AddFragment, bundle)
    }
}
