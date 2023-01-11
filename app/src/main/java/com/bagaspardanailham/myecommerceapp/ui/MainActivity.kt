package com.bagaspardanailham.myecommerceapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.ActivityMainBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.text.Typography.dagger


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                lifecycleScope.launch {
                    viewModel.deleteToken()
                    startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupWindow() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}