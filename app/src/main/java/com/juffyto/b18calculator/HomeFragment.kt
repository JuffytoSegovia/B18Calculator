package com.juffyto.b18calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView

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

        return view
    }
}