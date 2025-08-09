package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.ncorp.hesapp.R
import com.ncorp.hesapp.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Fragment giriş animasyonu
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        view.startAnimation(fadeInAnimation)
        
        setupThemeSection()
        setupBehaviorSection()
        setupAboutSection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupThemeSection() {
        try {
            val prefs = requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
            val currentTheme = try {
                prefs.getString("theme_mode", "system") ?: "system"
            } catch (e: Exception) {
                "system" // Varsayılan tema
            }
            
            when (currentTheme) {
                "light" -> binding.rbThemeLight.isChecked = true
                "dark" -> binding.rbThemeDark.isChecked = true
                else -> binding.rbThemeSystem.isChecked = true
            }

            binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
                try {
                    val mode = when (checkedId) {
                        R.id.rbThemeLight -> "light"
                        R.id.rbThemeDark -> "dark"
                        else -> "system"
                    }
                    
                    try {
                        prefs.edit { putString("theme_mode", mode) }
                    } catch (e: Exception) {
                        com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(
                            binding.root, 
                            "Tema tercihi kaydedilirken hata oluştu"
                        )
                        return@setOnCheckedChangeListener
                    }
                    
                    val appMode = when (mode) {
                        "light" -> AppCompatDelegate.MODE_NIGHT_NO
                        "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    AppCompatDelegate.setDefaultNightMode(appMode)
                } catch (e: Exception) {
                    com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(
                        binding.root, 
                        "Tema değiştirilirken hata oluştu"
                    )
                }
            }
        } catch (e: Exception) {
            com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(
                binding.root, 
                "Tema ayarları yüklenirken hata oluştu"
            )
        }
    }

    private fun setupBehaviorSection() {
        try {
            val prefs = requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
            
            val enabled = try {
                prefs.getBoolean("sound_enabled", false)
            } catch (e: Exception) {
                false // Varsayılan olarak ses kapalı
            }
            binding.switchSounds.isChecked = enabled
            binding.switchSounds.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefs.edit { putBoolean("sound_enabled", isChecked) }
                } catch (e: Exception) {
                    com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(
                        binding.root, 
                        "Ses ayarı kaydedilirken hata oluştu"
                    )
                }
            }

            val lite = try {
                prefs.getBoolean("lite_mode", false)
            } catch (e: Exception) {
                false // Varsayılan olarak lite mod kapalı
            }
            binding.switchLiteMode.isChecked = lite
            binding.switchLiteMode.setOnCheckedChangeListener { _, isChecked ->
                try {
                    prefs.edit { putBoolean("lite_mode", isChecked) }
                } catch (e: Exception) {
                    com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(
                        binding.root, 
                        "Lite mod ayarı kaydedilirken hata oluştu"
                    )
                }
            }
        } catch (e: Exception) {
            com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(
                binding.root, 
                "Davranış ayarları yüklenirken hata oluştu"
            )
        }
    }

    private fun setupAboutSection() {
        val pm = requireContext().packageManager
        val pkg = requireContext().packageName
        val versionName = try {
            pm.getPackageInfo(pkg, 0).versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
        binding.tvVersion.text = getString(R.string.app_name) + " v" + versionName
    }
} 