package com.bagaspardanailham.myecommerceapp

import com.bagaspardanailham.core.data.remote.response.SuccessResponse
import com.bagaspardanailham.core.data.remote.response.auth.DataUser
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.LoginSuccess
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse

object DataDummy {

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginSuccess(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                dataUser = generateDummyDataUser(),
                message = "message",
                status = null,
            )
        )
    }

    private fun generateDummyDataUser(): DataUser {
        return DataUser(
            image = "image",
            path = "path",
            gender = 1,
            phone = "phone",
            name = "name",
            id = 1,
            email = "email"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            SuccessResponse("message", 200)
        )
    }

    fun generateDummyChangePasswordResponse(): ChangePasswordResponse {
        return ChangePasswordResponse(
            SuccessResponse("message", 200)
        )
    }

}