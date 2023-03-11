package com.bagaspardanailham.myecommerceapp.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.ActivityMainBinding
import com.bagaspardanailham.myecommerceapp.ui.main.profile.ProfileViewModel
import com.bagaspardanailham.myecommerceapp.ui.notification.NotificationActivity
import com.bagaspardanailham.myecommerceapp.ui.notification.NotificationViewModel
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyActivity
import com.bagaspardanailham.myecommerceapp.ui.trolly.TrollyViewModel
import com.bagaspardanailham.core.utils.setVisibility
import com.bagaspardanailham.myecommerceapp.ui.main.profile.ProfileFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
    private lateinit var idLocale: String

    override fun onStart() {
        super.onStart()
        setLocale()
    }

    override fun onResume() {
        super.onResume()
        setLocale()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()

        firebaseAnalytics = Firebase.analytics

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.navigation_profile) {
                binding.menuNotification.setVisibility(false)
                binding.menuCart.setVisibility(false)
            } else {
                binding.menuNotification.setVisibility(true)
                binding.menuCart.setVisibility(true)
            }
        }

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
            viewModel.getSettingPref().collectLatest { data ->
                if (data?.localeId != null) {
                    if (data.localeId == "0") {
                        idLocale = "en"
                    } else {
                        idLocale = "in"
                    }
                } else {
                    idLocale = "en"
                }
            }
        }
        val locale = Locale(idLocale)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, this@MainActivity.resources.displayMetrics)
    }

    private fun setupWindow() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}