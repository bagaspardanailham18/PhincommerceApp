package com.bagaspardanailham.myecommerceapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.ActivityMainBinding
import com.bagaspardanailham.myecommerceapp.ui.profile.ProfileViewModel
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyActivity
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyViewModel
import com.google.android.material.badge.BadgeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<ProfileViewModel>()
    private val trollyViewModel by viewModels<TrollyViewModel>()

    private var pendingCart: Int? = 12
    private lateinit var badgeCounter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        setLocale()
        setAction()
        setBadgeCounter()
    }

    private fun setAction() {
        binding.menuCart.setOnClickListener {
            startActivity(Intent(this, TrollyActivity::class.java))
        }
    }

    private fun setBadgeCounter() {
        lifecycleScope.launch {
            trollyViewModel.getAllProductFromTrolly().observe(this@MainActivity) { result ->
                with(binding) {
                    if (result.isNotEmpty()) {
                        badgeCounter.visibility = View.VISIBLE
                        tvCartSize.text = result.size.toString()
                    } else {
                        badgeCounter.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setLocale() {
        lifecycleScope.launch {
            viewModel.getSettingPref().collect { data ->
                if (data?.langName != null) {
                    if (data.langName == "en") {
                        val locale = Locale("en")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        this@MainActivity.resources.updateConfiguration(config, this@MainActivity.resources.displayMetrics)
                    } else {
                        val locale = Locale("in")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        this@MainActivity.resources.updateConfiguration(config, this@MainActivity.resources.displayMetrics)
                    }
                } else {
                    val locale = Locale("en")
                    Locale.setDefault(locale)
                    val config = Configuration()
                    config.locale = locale
                    this@MainActivity.resources.updateConfiguration(config, this@MainActivity.resources.displayMetrics)
                }
            }
        }
    }

//    @SuppressLint("UnsafeOptInUsageError")
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//
//        val menuItem = menu?.findItem(R.id.menu_cart)
//
//        if (pendingCart == 0) {
//            menuItem?.actionView = null
//        } else {
//            menuItem?.setActionView(R.layout.cart_badge)
//            val view: View = menuItem!!.actionView
//            badgeCounter = view.findViewById(R.id.badge_counter)
//            badgeCounter.text = pendingCart.toString()
//        }
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_cart -> {
//                startActivity(Intent(this, TrollyActivity::class.java))
//
//                val badgeDrawable = binding.navView.getBadge(R.id.menu_chart)
//                if (badgeDrawable != null) {
//                    badgeDrawable.isVisible = false
//                    badgeDrawable.clearNumber()
//                }
//
//                binding.navView.removeBadge(R.id.menu_chart)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun setupWindow() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}