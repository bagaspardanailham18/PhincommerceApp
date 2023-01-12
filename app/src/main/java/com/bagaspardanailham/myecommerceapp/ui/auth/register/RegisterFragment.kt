package com.bagaspardanailham.myecommerceapp.ui.auth.register

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bagaspardanailham.myecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint
import com.bagaspardanailham.myecommerceapp.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private val registerViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.imgPickerBtn?.setOnClickListener {
            showImgPickerDialog()
        }

        binding?.btnToLogin?.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding?.btnSignup?.setOnClickListener {
            handleRegister()
        }

    }

    private fun showImgPickerDialog() {
        val items = arrayOf("Camera", "Gallery")
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Select Image")
            .setItems(items) { dialog, which ->
                when {
                    items[which] == "Camera" -> {
                        getImgFromCamera()
                    }
                    items[which] == "Gallery" -> {
                        getImgFromGallery()
                    }
                }
            }
            .show()

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            when {
                permission[Manifest.permission.CAMERA] ?: false -> {
                    getImgFromCamera()
                }
            }
        }

    private fun checkSelfPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getImgFromCamera() {
        if (checkSelfPermission(CAMARA_PERMISSION)) {

        } else {

        }
    }

    private fun getImgFromGallery() {

    }

    private fun handleRegister() {
        val email = binding?.edtEmail?.text.toString().trim()
        val password = binding?.edtPassword?.text.toString().trim()
        val confirmPassword = binding?.edtConfirmPassword?.text.toString().trim()
        val name = binding?.edtName?.text.toString().trim()
        val phone = binding?.edtPhone?.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding?.layoutEdtEmail?.error = "Email is required!"
                return
            }
            password.isEmpty() -> {
                binding?.layoutEdtPassword?.error = "Password is required!"
                return
            }
            password.isEmpty() -> {
                binding?.layoutEdtConfirmPassword?.error = "Confirm password is required!"
                return
            }
            name.isEmpty() -> {
                binding?.layoutEdtName?.error = "Name is required!"
                return
            }
            phone.isEmpty() -> {
                binding?.layoutEdtPhone?.error = "Phone is required!"
                return
            }
            else -> {
                if (password != confirmPassword) {
                    binding?.layoutEdtEmail?.error = "Password doesn't match!"
                    return
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(requireActivity(), "Email is not valid", Toast.LENGTH_SHORT).show()
                    return
                }
                else if (!binding?.rgMale!!.isChecked && !binding?.rgFemale!!.isChecked) {
                    Toast.makeText(requireActivity(), "Gender is required!", Toast.LENGTH_SHORT).show()
                    return
                }
                else {
                    val genderId = if (binding?.rgMale!!.isChecked) "0" else "1"
                    Log.d("data", "Name : $name, email: $email, gender: $genderId")
                    lifecycleScope.launch {
                        binding?.progressBar?.visibility = View.VISIBLE
                        registerViewModel.registerUser(
                            email, password, name, genderId.toInt(), phone, ""
                        ).observe(viewLifecycleOwner) { result ->
                            when(result) {
                                is Result.Success -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    Toast.makeText(requireActivity(), result.data.success?.message, Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                }
                                is Result.Error -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                                }
                                is Result.Loading -> {
                                    binding?.progressBar?.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val CAMARA_PERMISSION = Manifest.permission.CAMERA
    }
}