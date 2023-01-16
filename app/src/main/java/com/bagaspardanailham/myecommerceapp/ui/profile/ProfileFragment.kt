package com.bagaspardanailham.myecommerceapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.FragmentProfileBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel>()

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

        val items = listOf("EN", "IDN")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_row_language, items)
        (binding.edtLang as? AutoCompleteTextView)?.setAdapter(adapter)

        setProfile()
        setAction()
    }

    private fun setProfile() {
        lifecycleScope.launch {
            viewModel.getUserPref().collect { pref ->
                binding.apply {
                    tvUserName.text = pref?.name.toString()
                    tvUserEmail.text = pref?.email.toString()
                    Glide.with(requireActivity())
                        .load(pref?.imgPath.toString())
                        .into(tvUserImg)
                }
            }
        }
    }

    private fun setAction() {
        binding.btnToChangePassword.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteToken()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}