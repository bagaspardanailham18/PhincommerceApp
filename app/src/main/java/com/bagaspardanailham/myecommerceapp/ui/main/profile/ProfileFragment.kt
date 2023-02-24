package com.bagaspardanailham.myecommerceapp.ui.main.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bagaspardanailham.myecommerceapp.data.Result
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.myecommerceapp.databinding.FragmentProfileBinding
import com.bagaspardanailham.myecommerceapp.ui.CameraActivity
import com.bagaspardanailham.myecommerceapp.ui.LoadingDialog
import com.bagaspardanailham.myecommerceapp.ui.splash.SplashScreenActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bagaspardanailham.myecommerceapp.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private lateinit var loading: LoadingDialog

    private var getFile: File? = null

    private lateinit var langNames: List<String>
    private lateinit var langImgs: List<Int>

    private var isUserAction = false

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
        ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = LoadingDialog(requireActivity())

        langNames = listOf("Default", "EN", "IDN")
        langImgs = listOf(0, R.drawable.us_flag, R.drawable.idn_flag)

        setProfile()
        setAction()
    }

    private fun setProfile() {
        lifecycleScope.launch {
            viewModel.getUserPref().collect { pref ->
                binding.apply {
                    println("lyfecycle preference")
                    tvUserName.text = pref?.name.toString()
                    tvUserEmail.text = pref?.email.toString()
                    Log.d("imgpref", "My Image : ${pref?.imgPath}")
                    if (pref?.imgPath.toString().isEmpty()) {
                        tvUserImgPrev.setImageResource(R.drawable.user)
                    } else {
                        Glide.with(requireActivity())
                            .load(pref?.imgPath.toString())
                            .into(tvUserImgPrev)
                    }
                }
            }
        }
    }

    private fun setLocale() {
        lifecycleScope.launch {
            profileViewModel.getSettingPref().collect { data ->
                Toast.makeText(requireActivity(), data?.langName.toString(), Toast.LENGTH_SHORT).show()
                if (data?.langName != null) {
                    if (data.langName == "en") {
                        binding.langSpinner.setSelection(langNames.indexOf("en"))
                        val locale = Locale("en")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
                    } else {
                        binding.langSpinner.setSelection(langNames.indexOf("in"))
                        val locale = Locale("in")
                        Locale.setDefault(locale)
                        val config = Configuration()
                        config.locale = locale
                        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
                    }
                } else {
                    binding.langSpinner.setSelection(0)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setAction() {
        binding.imgPickerBtn.setOnClickListener {
            val items = arrayOf("Camera", "Gallery")
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Select Image")
                .setItems(items) { dialog, which ->
                    when {
                        items[which] == "Camera" -> {
                            if (!allPermissionGranted()) {
                                ActivityCompat.requestPermissions(
                                    requireActivity(),
                                    REQUIRED_PERMISSIONS,
                                    REQUEST_CODE_PERMISSIONS
                                )
                            } else {
                                getImgFromCamera()
                            }
                        }
                        items[which] == "Gallery" -> {
                            getImgFromGallery()
                        }
                    }
                }
                .show()

            // Analytics
            firebaseAnalyticsRepository.onClickCameraIcon(Constant.PROFILE)
        }

        binding.apply {
            val adapter = ChangeLangAdapter(requireContext(), langNames, langImgs)
            langSpinner.adapter = adapter

            lifecycleScope.launch {
                profileViewModel.getSettingPref().collect { pref ->
                    if (pref?.langName != null) {
                        if (pref.langName == "en") {
                            langSpinner.setSelection(1)
                        } else if (pref.langName == "in") {
                            langSpinner.setSelection(2)
                        } else {
                            langSpinner.setSelection(0)
                        }
                    }
                }
            }

            langSpinner.setOnTouchListener { view, motionEvent ->
                isUserAction = true
                false
            }
            langSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (isUserAction) {
                        when (position) {
                            1 -> {
                                setLanguage("en")
                                startActivity(Intent(requireActivity(), SplashScreenActivity::class.java))
                                // Analytics
                                firebaseAnalyticsRepository.onChangeLanguage("EN")
                            }
                            2 -> {
                                setLanguage("in")
                                startActivity(Intent(requireActivity(), SplashScreenActivity::class.java))
                                // Analytics
                                firebaseAnalyticsRepository.onChangeLanguage("ID")
                            }
                            else -> {
                                setLanguage("en")
                                startActivity(Intent(requireActivity(), SplashScreenActivity::class.java))
                                // Analytics
                                firebaseAnalyticsRepository.onChangeLanguage("EN")
                            }
                        }
                    } else {
                        isUserAction = false
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }

        binding.btnToChangePassword.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
            // Analytics
            firebaseAnalyticsRepository.onClickChangePassword()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutValidationDialog()
            // Analytics
            firebaseAnalyticsRepository.onClickLogout()
        }
    }

    private fun getImgFromCamera() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val myFile = reduceFileImage(it.data?.getSerializableExtra("picture") as File, isBackCamera)

//            val result = rotateBitmap(
//                BitmapFactory.decodeFile(myFile.path),
//                isBackCamera
//            )

            getFile = myFile
            Glide.with(requireActivity())
                .load(myFile)
                .into(binding.tvUserImgPrev)
            uploadImg()

            // Analytics
            firebaseAnalyticsRepository.onChangeImage(
                Constant.PROFILE, "Camera"
            )
        }
    }

    private fun uploadImg() {
        if (getFile != null) {
            val file = getFile as File
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestImageFile
            )
            lifecycleScope.launch {
                val id = viewModel.getUserPref().first()?.id
                val authToken = viewModel.getUserPref().first()?.authTokenKey.toString()
                profileViewModel.changeImage(
                    authToken,
                    id?.toRequestBody("text/plain".toMediaType())!!,
                    imageMultipart
                ).observe(viewLifecycleOwner) { response ->
                    Log.d("repeat", "repeat")
                    when (response) {
                        is Result.Loading -> {
                            loading.startLoading()
                        }
                        is Result.Success -> {
                            loading.isDismiss()
                            response.data.success.apply {
                                println("result success")
                                lifecycleScope.launch {
                                    viewModel.updateImgPath(response.data.success.path)
                                }
                                Toast.makeText(requireActivity(), response.data.success.message, Toast.LENGTH_SHORT).show()
                            }

                        }
                        is Result.Error -> {
                            try {
                                loading.isDismiss()
                                val errorres = JSONObject(response.errorBody?.string()).toString()
                                val gson = Gson()
                                val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                if (response.errorCode == 401) {
                                    Toast.makeText(requireActivity(), "Token is expired", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireActivity(), errorResponse.error?.message, Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: Exception) {
                                loading.isDismiss()
                                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getImgFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
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

        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = reduceGalleryFileImage(uriToFile(selectedImg, requireActivity()))
            getFile = myFile
            //binding.tvUserImgPrev.setImageURI(selectedImg)
            Glide.with(requireActivity())
                .load(selectedImg)
                .into(binding.tvUserImgPrev)

            uploadImg()

            // Analytics
            firebaseAnalyticsRepository.onChangeImage(
                Constant.PROFILE, "Gallery"
            )
        }

    }

    private fun setLanguage(localeName: String) {
        val locale = Locale(localeName)
        Locale.setDefault(locale)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)

        lifecycleScope.launch {
            profileViewModel.saveSettingPref(localeName)
        }
    }

    private fun showLogoutValidationDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(String.format(resources.getString(R.string.validation), resources.getString(
                R.string.menu_logout)))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                lifecycleScope.launch {
                    viewModel.deleteToken()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                }
                requireActivity().finish()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Analytics
        firebaseAnalyticsRepository.onLoadScreen(
            Constant.PROFILE, this.javaClass.simpleName
        )
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}










