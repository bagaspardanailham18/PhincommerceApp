package com.bagaspardanailham.myecommerceapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bagaspardanailham.myecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.databinding.FragmentLoginBinding
import com.bagaspardanailham.myecommerceapp.ui.MainActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnToSignup?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding?.btnLogin?.setOnClickListener {
            handleLogin()
        }

    }

    private fun handleLogin() {
        val email = binding?.edtEmail?.text.toString().trim()
        val password = binding?.edtPassword?.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding?.layoutEdtEmail?.error = "Email is required"
                return
            }
            password.isEmpty() -> {
                binding?.layoutEdtPassword?.error = "Password is required"
                return
            }
            else -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding?.layoutEdtEmail?.error = "Wrong email format"
                    return
                } else {
                    lifecycleScope.launch {
                        binding?.progressBar?.visibility = View.VISIBLE
                        viewModel.loginUser(email, password).observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    binding?.progressBar?.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    result.data.success?.apply {
                                        val authToken = accessToken.toString()
                                        val refreshToken = refreshToken.toString()
                                        val id = dataUser?.id
                                        val name = dataUser?.name.toString()
                                        val email = dataUser?.email.toString()
                                        val phone = dataUser?.phone.toString()
                                        val gender = dataUser?.gender.toString()
                                        val imgPath = dataUser?.path.toString()

                                        lifecycleScope.launch {
                                            viewModel.saveToken(authToken, refreshToken, id, name, email, phone, gender, imgPath)
                                        }

                                        Log.d("AuthToken", "Auth Token : $authToken")
                                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                                        Log.d("pref", """
                                            authToken : $authToken,
                                            refreshToken : $refreshToken,
                                            id : $id,
                                            name : $name,
                                            email : $email,
                                            phone : $phone,
                                            gender : $gender,
                                            imgPath : $imgPath
                                        """.trimIndent())
                                    }

                                    val intent = Intent(requireActivity(), MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                                is Result.Error -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    val errorres = JSONObject(result.errorBody?.string()).toString()
                                    val gson = Gson()
                                    val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                    val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                    Toast.makeText(requireActivity(), errorResponse.error?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}