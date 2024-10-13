package com.juffyto.b18calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import android.content.Intent
import android.net.Uri
import android.widget.Button

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<CardView>(R.id.preselectionCard).setOnClickListener {
            (activity as? MainActivity)?.navigateToPreselection()
        }

        view.findViewById<CardView>(R.id.selectionCard).setOnClickListener {
            (activity as? MainActivity)?.navigateToSelection()
        }

        view.findViewById<CardView>(R.id.creditsCard).setOnClickListener {
            (activity as? MainActivity)?.navigateToCredits()
        }

        view.findViewById<Button>(R.id.buttonPostulaBeca18).setOnClickListener {
            openBeca18Website()
        }

        return view
    }

    private fun openBeca18Website() {
        val url = "https://www.pronabec.gob.pe/beca-18/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}