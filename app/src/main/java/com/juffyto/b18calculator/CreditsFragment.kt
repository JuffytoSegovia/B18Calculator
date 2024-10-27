package com.juffyto.b18calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment

class CreditsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSocialClicks(view)
        setupGroupClicks(view)
        setupAdvisoryClick(view)
        setupDonationClicks(view)
    }

    private fun setupSocialClicks(view: View) {
        view.findViewById<ImageView>(R.id.linkedinImage).setOnClickListener {
            openUrl("https://www.linkedin.com/in/juniorsegovia/")
        }

        view.findViewById<ImageView>(R.id.instagramImage).setOnClickListener {
            openUrl("https://www.instagram.com/juffyto/")
        }

        view.findViewById<ImageView>(R.id.facebookImage).setOnClickListener {
            openUrl("https://www.facebook.com/JuffytoSegovia")
        }

        view.findViewById<ImageView>(R.id.tiktokImage).setOnClickListener {
            openUrl("https://www.tiktok.com/@juffytosegovia")
        }

        view.findViewById<ImageView>(R.id.youtubeImage).setOnClickListener {
            openUrl("https://www.youtube.com/@Juffyto")
        }

        view.findViewById<ImageView>(R.id.websiteImage).setOnClickListener {
            openUrl("https://asesoriabeca18.web.app/")
        }
    }

    private fun setupGroupClicks(view: View) {
        view.findViewById<ImageView>(R.id.whatsappGroupImage).setOnClickListener {
            openUrl("https://chat.whatsapp.com/HM5v7U7DGBHJRZEtd30MXK")
        }

        view.findViewById<ImageView>(R.id.discordImage).setOnClickListener {
            openUrl("https://discord.gg/Kwg6pUwWtc")
        }

        view.findViewById<ImageView>(R.id.telegramImage).setOnClickListener {
            openUrl("https://t.me/+teoTNacA1xFmYjlh")
        }

        view.findViewById<ImageView>(R.id.facebookGroupImage).setOnClickListener {
            openUrl("https://bit.ly/https://www.facebook.com/groups/817847566603752")
        }
    }

    private fun setupAdvisoryClick(view: View) {
        view.findViewById<FrameLayout>(R.id.advisoryButton).setOnClickListener {
            openUrl("https://docs.google.com/forms/d/e/1FAIpQLSd2ATxiK7V4VJVolU_oOyodts_8yGo6WIDvTd3rrRoB8hPrbQ/viewform")
        }

        view.findViewById<FrameLayout>(R.id.whatsappAdvisoryButton).setOnClickListener {
            openUrl("https://wa.link/1u4mdo")
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDonationClicks(view: View) {
        val phoneNumber = "939824399"

        view.findViewById<ImageButton>(R.id.copyNumberButton).setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("phone number", phoneNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Número copiado", Toast.LENGTH_SHORT).show()
        }

        // Opcional: si quieres que al hacer clic en los íconos también se copie el número
        view.findViewById<LinearLayout>(R.id.yapeButton).setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("phone number", phoneNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Número de Yape copiado", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.plinButton).setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("phone number", phoneNumber)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Número de Plin copiado", Toast.LENGTH_SHORT).show()
        }
    }
}