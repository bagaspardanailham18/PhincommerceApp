package com.bagaspardanailham.myecommerceapp.ui.auth.login

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bagaspardanailham.myecommerceapp.EspressoIdlingResource
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.bagaspardanailham.myecommerceapp.R
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {


    @get:Rule
    val activityRule = ActivityScenarioRule(AuthActivity::class.java)

    @Before
    fun setUp() {
        ActivityScenario.launch(AuthActivity::class.java)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getEspressoIdlingResource())
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getEspressoIdlingResource())
    }

    @Test
    fun loadLoginFragment() {
        // Verify that all component in UI are displayed
        onView(withId(R.id.layout_edt_email)).check(matches(isDisplayed()))
        onView(withId(R.id.layout_edt_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_to_signup)).check(matches(isDisplayed()))

        // Perform Login
        onView(withId(R.id.edt_email)).perform(typeText("bagass@gmail.com"))
        onView(withId(R.id.edt_email)).perform(pressImeActionButton())
        onView(withId(R.id.edt_password)).perform(typeText("123456"))
        onView(withId(R.id.edt_password)).perform(pressImeActionButton())
        onView(withId(R.id.btn_login)).perform(click())
    }

    @Test
    fun loadRegisterFragment() {
        // Verify Btn To Register in LoginFragment is Displayed
        //onView(withId(R.id.btn_to_signup)).check(matches(isDisplayed()))

        // Perform CLick btn to Register
        onView(withId(R.id.btn_to_signup)).perform(click())

        // Perform Register
        onView(withId(R.id.edt_email)).perform(typeText("bagasssss@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText("123455"), closeSoftKeyboard())
        onView(withId(R.id.scrollView)).perform(swipeUp())

        onView(withId(R.id.edt_confirm_password)).perform(typeText("123455"), closeSoftKeyboard())
        onView(withId(R.id.edt_name)).perform(typeText("Bagas Pardana Ilham"), closeSoftKeyboard())
        onView(withId(R.id.edt_phone)).perform(typeText("0892114141"), closeSoftKeyboard())

        onView(withId(R.id.rg_male)).perform(click())
        onView(withId(R.id.btn_signup)).perform(click())
    }
}