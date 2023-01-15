package com.bagaspardanailham.myecommerceapp.ui.auth.register

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bagaspardanailham.myecommerceapp.R
import dagger.hilt.android.AndroidEntryPoint
import com.bagaspardanailham.myecommerceapp.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.ui.CameraActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.utils.createCustomTempFile
import com.bagaspardanailham.myecommerceapp.utils.reduceFileImage
import com.bagaspardanailham.myecommerceapp.utils.rotateBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private var getFile: File? = null

    private val registerViewModel by viewModels<AuthViewModel>()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    requireActivity(),
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

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

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

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

//    private val launcherIntentCamera = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) {
//        if (it.resultCode == RESULT_OK) {
//            val imageBitmap = it.data?.extras?.get("data") as Bitmap
//            binding?.tvUserImg?.setImageBitmap(imageBitmap)
//        }
//    }

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permission ->
//            when {
//                permission[Manifest.permission.CAMERA] ?: false -> {
//                    getImgFromCamera()
//                }
//            }
//        }
//
//    private fun checkSelfPermission(permission: String): Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireActivity(),
//            permission
//        ) == PackageManager.PERMISSION_GRANTED
//    }

    private fun getImgFromCamera() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding?.tvUserImgPrev?.setImageBitmap(result)
        }
    }

    private fun getImgFromGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding?.tvUserImgPrev?.setImageURI(selectedImg)
        }

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
                    binding?.layoutEdtEmail?.error = "Password must be same"
                    binding?.layoutEdtConfirmPassword?.error = "Password must be same"
                    return
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding?.layoutEdtEmail?.error = "Wrong email format"
                    return
                }
                else if (!binding?.rgMale!!.isChecked && !binding?.rgFemale!!.isChecked) {
                    Toast.makeText(requireActivity(), "Gender is required!", Toast.LENGTH_SHORT).show()
                    return
                }
                else {
                    val genderId = if (binding?.rgMale!!.isChecked) "0" else "1"

                    val file = reduceFileImage(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        getFile?.name.toString(),
                        requestImageFile
                    )
                    Log.d("data", "Name : $name, email: $email, gender: $genderId")
                    lifecycleScope.launch {
                        binding?.progressBar?.visibility = View.VISIBLE
                        registerViewModel.registerUser(
                            email, password, name, genderId.toInt(), phone, imageMultipart
                        ).observe(viewLifecycleOwner) { result ->
                            when(result) {
                                is Result.Success -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    Toast.makeText(requireActivity(), result.data.success?.message, Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                }
                                is Result.Error -> {
                                    binding?.progressBar?.visibility = View.GONE
                                    val errorres = JSONObject(result.errorBody?.string()).toString()
                                    Log.d("error", result.toString())
                                    val gson = Gson()
                                    val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                    val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                    Toast.makeText(requireActivity(), errorResponse.error?.message, Toast.LENGTH_SHORT).show()
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
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}