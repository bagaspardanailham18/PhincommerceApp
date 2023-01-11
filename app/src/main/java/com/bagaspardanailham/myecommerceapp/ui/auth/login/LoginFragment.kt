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
import com.bagaspardanailham.myecommerceapp.databinding.FragmentLoginBinding
import com.bagaspardanailham.myecommerceapp.ui.MainActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel

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
                    Toast.makeText(requireActivity(), "Email is not valid", Toast.LENGTH_SHORT).show()
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
                                    if (result.data.success?.status == 200) {
                                        binding?.progressBar?.visibility = View.GONE
                                        val authToken = result.data.success.accessToken.toString()
                                        val refreshToken = result.data.success.refreshToken.toString()
                                        lifecycleScope.launch {
                                            viewModel.saveToken(authToken, refreshToken)
                                        }

                                        Log.d("AuthToken", "Auth Token : $authToken")
                                        Toast.makeText(requireActivity(), result.data.success.message, Toast.LENGTH_SHORT).show()

                                        val intent = Intent(requireActivity(), MainActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    } else if (result.data.success?.status == 400) {
                                        Toast.makeText(requireActivity(), result.data.success.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                is Result.Error -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
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