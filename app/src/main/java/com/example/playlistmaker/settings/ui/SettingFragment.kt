package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : Fragment() {
    private  var _binding: FragmentSettingsBinding?=null
    private val binding get() = _binding!!
    private lateinit var shareButton: MaterialTextView
    private lateinit var supportButton: MaterialTextView
    private lateinit var agreementButton: MaterialTextView
    private lateinit var themeSwitcher: SwitchMaterial

    private val viewModel by viewModel<SettingsViewModel>()

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


        shareButton = binding.shareButton
        supportButton = binding.supportButton
        agreementButton = binding.agreementButton
        themeSwitcher = binding.themeSwitcher

        themeSwitcher.isChecked = viewModel.getThemeState()

        shareButton.setOnClickListener {
            val message = viewModel.getLinkToCourse()

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val message = viewModel.getEmailMessage()
            val subject = viewModel.getEmailSubject()
            val mailArray = viewModel.getArrayOfEmailAddresses()

            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, mailArray)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        agreementButton.setOnClickListener {
            val url = viewModel.getPracticumOffer()

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }


        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.saveAndChangeThemeState(checked)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}