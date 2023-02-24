package com.bagaspardanailham.myecommerceapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.databinding.ActivityMainBinding
import com.bagaspardanailham.myecommerceapp.ui.main.favorite.FavoriteFragment
import com.bagaspardanailham.myecommerceapp.ui.main.home.HomeFragment
import com.bagaspardanailham.myecommerceapp.ui.main.profile.ProfileViewModel
import com.bagaspardanailham.myecommerceapp.ui.notification.NotificationActivity
import com.bagaspardanailham.myecommerceapp.ui.notification.NotificationViewModel
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyActivity
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyViewModel
import com.bagaspardanailham.myecommerceapp.utils.setVisibility
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<ProfileViewModel>()
    private val trollyViewModel by viewModels<TrollyViewModel>()
    private val notificationViewModel by viewModels<NotificationViewModel>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()

        firebaseAnalytics = Firebase.analytics

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        setLocale()
        setAction()
        setBadgeCounter()
    }

    private fun setAction() {
        binding.menuNotification.setOnClickListener {
            firebaseAnalyticsRepository.onClickNotificationIcon()
            startActivity(Intent(this, NotificationActivity::class.java))
        }
        binding.menuCart.setOnClickListener {
            firebaseAnalyticsRepository.onClickTrolleyIcon()
            startActivity(Intent(this, TrollyActivity::class.java))
        }
    }

    private fun setBadgeCounter() {
        lifecycleScope.launch {
            trollyViewModel.getAllProductFromTrolly().observe(this@MainActivity) { result ->
                with(binding) {
                    if (result.isNotEmpty()) {
                        badgeCounter.setVisibility(true)
                        tvCartSize.text = result.size.toString()
                    } else {
                        badgeCounter.setVisibility(false)
                    }
                }
            }
            notificationViewModel.getAllNotification().collect() { result ->
                val unreadNotification = result.filter { !it.isRead }
                with(binding) {
                    if (unreadNotification.isNotEmpty()) {
                        notifBadgeCounter.setVisibility(true)
                        tvNotifSize.text = unreadNotification.size.toString()
                    } else {
                        notifBadgeCounter.setVisibility(false)
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

    private fun setupWindow() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}