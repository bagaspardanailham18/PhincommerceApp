package com.bagaspardanailham.myecommerceapp.data.repository

import android.os.Bundle
import com.bagaspardanailham.myecommerceapp.utils.Constant
import com.bagaspardanailham.myecommerceapp.utils.Constant.BANK
import com.bagaspardanailham.myecommerceapp.utils.Constant.BUTTON_CLICK
import com.bagaspardanailham.myecommerceapp.utils.Constant.BUTTON_NAME
import com.bagaspardanailham.myecommerceapp.utils.Constant.CHANGE_PASSWORD
import com.bagaspardanailham.myecommerceapp.utils.Constant.CHOOSE_PAYMENT_METHOD
import com.bagaspardanailham.myecommerceapp.utils.Constant.DETAIL_PRODUCT
import com.bagaspardanailham.myecommerceapp.utils.Constant.EMAIL
import com.bagaspardanailham.myecommerceapp.utils.Constant.FAVORITE
import com.bagaspardanailham.myecommerceapp.utils.Constant.GENDER
import com.bagaspardanailham.myecommerceapp.utils.Constant.HOME
import com.bagaspardanailham.myecommerceapp.utils.Constant.IMAGE
import com.bagaspardanailham.myecommerceapp.utils.Constant.JENIS_PEMBAYARAN
import com.bagaspardanailham.myecommerceapp.utils.Constant.LANGUAGE
import com.bagaspardanailham.myecommerceapp.utils.Constant.LOGIN
import com.bagaspardanailham.myecommerceapp.utils.Constant.MULTIPLE_SELECT
import com.bagaspardanailham.myecommerceapp.utils.Constant.NAME
import com.bagaspardanailham.myecommerceapp.utils.Constant.NOTIFICATION
import com.bagaspardanailham.myecommerceapp.utils.Constant.ON_SCROLL
import com.bagaspardanailham.myecommerceapp.utils.Constant.ON_SEARCH
import com.bagaspardanailham.myecommerceapp.utils.Constant.PAGE
import com.bagaspardanailham.myecommerceapp.utils.Constant.PAYMENT_METHOD
import com.bagaspardanailham.myecommerceapp.utils.Constant.PHONE
import com.bagaspardanailham.myecommerceapp.utils.Constant.POPUP_DETAIL
import com.bagaspardanailham.myecommerceapp.utils.Constant.POPUP_SORT
import com.bagaspardanailham.myecommerceapp.utils.Constant.PRODUCT_ID
import com.bagaspardanailham.myecommerceapp.utils.Constant.PRODUCT_NAME
import com.bagaspardanailham.myecommerceapp.utils.Constant.PRODUCT_PRICE
import com.bagaspardanailham.myecommerceapp.utils.Constant.PRODUCT_RATE
import com.bagaspardanailham.myecommerceapp.utils.Constant.PRODUCT_TOTAL
import com.bagaspardanailham.myecommerceapp.utils.Constant.PRODUCT_TOTALPRICE
import com.bagaspardanailham.myecommerceapp.utils.Constant.PROFILE
import com.bagaspardanailham.myecommerceapp.utils.Constant.RATE
import com.bagaspardanailham.myecommerceapp.utils.Constant.SIGNUP
import com.bagaspardanailham.myecommerceapp.utils.Constant.SORT_BY
import com.bagaspardanailham.myecommerceapp.utils.Constant.SPLASH
import com.bagaspardanailham.myecommerceapp.utils.Constant.TROLLEY
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.*
import com.google.firebase.analytics.FirebaseAnalytics.Param.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsRepository @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    // SPLASH
    fun onLoadSplash(screenClass: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, SPLASH)
        params.putString(SCREEN_CLASS, screenClass)
        firebaseAnalytics.logEvent(SCREEN_VIEW, params)
    }

    // LOGIN PAGE
    fun onLoadLogin(screenClass: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, LOGIN)
        params.putString(SCREEN_CLASS, screenClass)
        firebaseAnalytics.logEvent(SCREEN_VIEW, params)
    }
    fun onLoginButtonClicked(email: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, LOGIN)
        params.putString(EMAIL, email)
        params.putString(BUTTON_NAME, LOGIN)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickButtonRegister() {
        val params = Bundle()
        params.putString(SCREEN_NAME, LOGIN)
        params.putString(BUTTON_NAME, SIGNUP)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }

    // REGISTER PAGE
    fun onLoadRegister(screenClass: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, SIGNUP)
        params.putString(SCREEN_CLASS, screenClass)
        firebaseAnalytics.logEvent(SCREEN_VIEW, params)
    }
    fun onClickButtonLogin() {
        val params = Bundle()
        params.putString(SCREEN_NAME, SIGNUP)
        params.putString(BUTTON_NAME, LOGIN)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onSignupButtonClicked(imageSource: String?, email: String, name: String, phone: String, gender: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, SIGNUP)
        params.putString(IMAGE, imageSource)
        params.putString(EMAIL, email)
        params.putString(NAME, name)
        params.putString(PHONE, phone)
        params.putString(GENDER, gender)
        params.putString(BUTTON_NAME, SIGNUP)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }


    // HOME PAGE
    fun onPagingScroll(offset: Int) {
        val params = Bundle()
        params.putString(SCREEN_NAME, HOME)
        params.putInt(PAGE, offset)
        firebaseAnalytics.logEvent(ON_SCROLL, params)
    }
    fun onSearch(query: String?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, HOME)
        params.putString(SEARCH, query)
        firebaseAnalytics.logEvent(ON_SEARCH, params)
    }
    fun onClickProduct(productId: Int?, productName: String?, price: Double, rate: Int?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, HOME)
        params.putString(PRODUCT_NAME, productName)
        params.putDouble(PRODUCT_PRICE, price)
        rate?.let { params.putInt(PRODUCT_RATE, it) }
        productId?.let { params.putInt(PRODUCT_ID, it) }
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onClickTrolleyIcon() {
        val params = Bundle()
        params.putString(SCREEN_NAME, HOME)
        params.putString(BUTTON_NAME, "Trolley Icon")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickNotificationIcon() {
        val params = Bundle()
        params.putString(SCREEN_NAME, HOME)
        params.putString(BUTTON_NAME, "Notif Icon")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }


    // FAVORITE PAGE
    fun onSearchFavorite(query: String?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, FAVORITE)
        params.putString(SEARCH, query)
        firebaseAnalytics.logEvent(ON_SEARCH, params)
    }
    fun onClickSortBy(sortType: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, FAVORITE)
        params.putString(SORT_BY, sortType)
        firebaseAnalytics.logEvent(POPUP_SORT, params)
    }
    fun onClickProductFavorite(productId: Int?, productName: String?, price: Double, rate: Int?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, FAVORITE)
        params.putString(PRODUCT_NAME, productName)
        params.putDouble(PRODUCT_PRICE, price)
        rate?.let { params.putInt(PRODUCT_RATE, it) }
        productId?.let { params.putInt(PRODUCT_ID, it) }
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onClickTrolleyIconFromFavorite() {
        val params = Bundle()
        params.putString(SCREEN_NAME, FAVORITE)
        params.putString(BUTTON_NAME, "Trolley Icon")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickNotificationIconFromFavorite() {
        val params = Bundle()
        params.putString(SCREEN_NAME, FAVORITE)
        params.putString(BUTTON_NAME, "Notif Icon")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }


    // NOTIFICATION PAGE
    fun onLoadNotif(screenClass: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, NOTIFICATION)
        params.putString(SCREEN_CLASS, screenClass)
        firebaseAnalytics.logEvent(SCREEN_VIEW, params)
    }
    fun onClickMultipleSelectIcon() {
        val params = Bundle()
        params.putString(SCREEN_NAME, NOTIFICATION)
        params.putString(BUTTON_NAME, "Multiple Select Icon")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickItemNotification(title: String, message: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, NOTIFICATION)
        params.putString("title", title)
        params.putString("message", message)
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onSelectCheckBox(title: String, message: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, MULTIPLE_SELECT)
        params.putString("title", title)
        params.putString("message", message)
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onClickDeleteIcon(totalSelectItem: Int) {
        val params = Bundle()
        params.putString(SCREEN_NAME, MULTIPLE_SELECT)
        params.putString(BUTTON_NAME, "Delete Icon")
        params.putInt("total_select_item", totalSelectItem)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickReadIcon(totalSelectItem: Int) {
        val params = Bundle()
        params.putString(SCREEN_NAME, MULTIPLE_SELECT)
        params.putString(BUTTON_NAME, "Read Icon")
        params.putInt("total_select_item", totalSelectItem)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }

    // DETAIL PRODUCT PAGE
    fun onClickButtonAddToTrolley() {
        val params = Bundle()
        params.putString(SCREEN_NAME, DETAIL_PRODUCT)
        params.putString(BUTTON_NAME, "+ Trolley")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickShareProduct(productName: String, price: Double, productId: Int?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, DETAIL_PRODUCT)
        params.putString(PRODUCT_NAME, productName)
        params.putDouble(PRODUCT_PRICE, price)
        productId?.let { params.putInt(PRODUCT_ID, it) }
        params.putString(BUTTON_NAME, "Share Product")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickLoveIcon(productId: Int?, productName: String, status: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, DETAIL_PRODUCT)
        params.putString(BUTTON_NAME, "Love Icon")
        productId?.let { params.putInt(PRODUCT_ID, it) }
        params.putString(PRODUCT_NAME, productName)
        params.putString("status", status)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onShowPopup(productId: Int?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, DETAIL_PRODUCT)
        params.putString("popup", "show")
        productId?.let { params.putInt(PRODUCT_ID, it) }
        firebaseAnalytics.logEvent(POPUP_DETAIL, params)
    }
    fun onClickButtonBuyNowForPaymentMethod(totalPrice: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, DETAIL_PRODUCT)
        params.putString(BUTTON_NAME, "Buy Now - $totalPrice")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickButtonBuyNow(totalPrice: Double, productId: Int, productName: String, price: Int, quantity: Int?, paymentMethod: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, DETAIL_PRODUCT)
        params.putString(BUTTON_NAME, "Buy Now - $totalPrice")
        params.putInt(PRODUCT_ID, productId)
        params.putString(PRODUCT_NAME, productName)
        params.putInt(PRODUCT_PRICE, price)
        quantity?.let { params.putInt(PRODUCT_TOTAL, it) }
        params.putDouble(PRODUCT_TOTALPRICE, totalPrice)
        params.putString(PAYMENT_METHOD, paymentMethod)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }


    // CHOOSE PAYMENT METHOD PAGE
    fun onClickBank(paymentMethodType: String, paymentMethod: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, CHOOSE_PAYMENT_METHOD)
        params.putString(JENIS_PEMBAYARAN, paymentMethodType)
        params.putString(BANK, paymentMethod)
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }


    // TROLLEY PAGE
    fun onClickDelete(productId: Int?, productName: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, TROLLEY)
        params.putString(BUTTON_NAME, "Delete Icon")
        productId?.let { params.putInt(PRODUCT_ID, it) }
        params.putString(PRODUCT_NAME, productName)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onSelectCheckbox(productId: Int?, productName: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, TROLLEY)
        productId?.let { params.putInt(PRODUCT_ID, it) }
        params.putString(PRODUCT_NAME, productName)
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }


    // SUCCESS PAGE
    fun onClickButtonSubmit(rate: Int) {
        val params = Bundle()
        params.putString(SCREEN_NAME, SUCCESS)
        params.putString(BUTTON_NAME, "Submit")
        params.putInt(RATE, rate)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }


    // PROFILE PAGE
    fun onChangeLanguage(lang: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, PROFILE)
        params.putString(ITEM_NAME, "Change Language")
        params.putString(LANGUAGE, lang)
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onClickChangePassword() {
        val params = Bundle()
        params.putString(SCREEN_NAME, PROFILE)
        params.putString(ITEM_NAME, "Change Password")
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onClickLogout() {
        val params = Bundle()
        params.putString(SCREEN_NAME, PROFILE)
        params.putString(ITEM_NAME, "Logout")
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }


    // CHANGE PASSWORD PAGE
    fun onClickButtonSave() {
        val params = Bundle()
        params.putString(SCREEN_NAME, CHANGE_PASSWORD)
        params.putString(BUTTON_NAME, "Save")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }


    // SHARED PAGE
    fun onLoadScreen(screenName: String, screenClass: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screenName)
        params.putString(SCREEN_CLASS, screenClass)
        firebaseAnalytics.logEvent(SCREEN_VIEW, params)
    }

    fun onClickBackIcon(screen: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screen)
        params.putString(BUTTON_NAME, "Back Icon")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickButtonQuantity(screenName: String, iconType: String, total: Int?, productId: Int?, productName: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screenName)
        params.putString(BUTTON_NAME, iconType)
        total?.let { params.putInt("total", it) }
        productId?.let { params.putInt(PRODUCT_ID, it) }
        params.putString(PRODUCT_NAME, productName)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickIconBank(screenName: String, paymentMethod: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screenName)
        params.putString(BUTTON_NAME, paymentMethod)
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickButtonBuy(screenName: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screenName)
        params.putString(BUTTON_NAME, "Buy")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onClickBuyButton(totalPrice: Double?, paymentMethod: String?) {
        val params = Bundle()
        params.putString(SCREEN_NAME, TROLLEY)
        params.putString(BUTTON_NAME, "Buy")
        if (totalPrice != null && paymentMethod != null) {
            params.putDouble(PRODUCT_TOTALPRICE, totalPrice)
            params.putString(PAYMENT_METHOD, paymentMethod)
        }
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }
    fun onChangeImage(screenName: String, imageSource: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screenName)
        params.putString(IMAGE, imageSource)
        firebaseAnalytics.logEvent(SELECT_ITEM, params)
    }
    fun onClickCameraIcon(screenName: String) {
        val params = Bundle()
        params.putString(SCREEN_NAME, screenName)
        params.putString(BUTTON_NAME, "Icon Photo")
        firebaseAnalytics.logEvent(BUTTON_CLICK, params)
    }

}
















