package com.juffyto.b18calculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SelectionFragment : Fragment() {

    private lateinit var editTextNombre: TextInputEditText
    private lateinit var spinnerModalidad: AutoCompleteTextView
    private lateinit var editTextPuntajePreseleccion: TextInputEditText
    private lateinit var textViewErrorPuntaje: TextView
    private lateinit var spinnerRegionIES: AutoCompleteTextView
    private lateinit var checkboxContainerTipoIES: LinearLayout
    private lateinit var spinnerIES: AutoCompleteTextView
    private lateinit var layoutIESDetails: LinearLayout
    private lateinit var textViewTipoIES: TextView
    private lateinit var textViewRegionIES: TextView
    private lateinit var textViewSiglasIES: TextView
    private lateinit var textViewTopIES: TextView
    private lateinit var textViewRankingIES: TextView
    private lateinit var textViewPuntajeRankingIES: TextView
    private lateinit var textViewGestionIES: TextView
    private lateinit var textViewPuntajeGestionIES: TextView
    private lateinit var textViewRatioSelectividad: TextView
    private lateinit var textViewPuntajeRatioSelectividad: TextView
    private lateinit var textViewPuntajeTotalIES: TextView

    private var iesData: List<IES> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selection, container, false)

        editTextNombre = view.findViewById(R.id.editTextNombre)
        spinnerModalidad = view.findViewById(R.id.spinnerModalidad)
        editTextPuntajePreseleccion = view.findViewById(R.id.editTextPuntajePreseleccion)
        textViewErrorPuntaje = view.findViewById(R.id.textViewErrorPuntaje)
        spinnerRegionIES = view.findViewById(R.id.spinnerRegionIES)
        checkboxContainerTipoIES = view.findViewById(R.id.checkboxContainerTipoIES)
        spinnerIES = view.findViewById(R.id.spinnerIES)
        layoutIESDetails = view.findViewById(R.id.layoutIESDetails)
        textViewTipoIES = view.findViewById(R.id.textViewTipoIES)
        textViewRegionIES = view.findViewById(R.id.textViewRegionIES)
        textViewSiglasIES = view.findViewById(R.id.textViewSiglasIES)
        textViewTopIES = view.findViewById(R.id.textViewTopIES)
        textViewRankingIES = view.findViewById(R.id.textViewRankingIES)
        textViewPuntajeRankingIES = view.findViewById(R.id.textViewPuntajeRankingIES)
        textViewGestionIES = view.findViewById(R.id.textViewGestionIES)
        textViewPuntajeGestionIES = view.findViewById(R.id.textViewPuntajeGestionIES)
        textViewRatioSelectividad = view.findViewById(R.id.textViewRatioSelectividad)
        textViewPuntajeRatioSelectividad = view.findViewById(R.id.textViewPuntajeRatioSelectividad)
        textViewPuntajeTotalIES = view.findViewById(R.id.textViewPuntajeTotalIES)

        loadIESData()
        setupSpinners()
        setupTipoIESCheckboxes()
        setupListeners()

        return view
    }

    private fun loadIESData() {
        try {
            val jsonString = context?.assets?.open("ies-data.json")?.bufferedReader().use { it?.readText() }
            val type = object : TypeToken<List<IES>>() {}.type
            iesData = Gson().fromJson(jsonString, type)
            Log.d("SelectionFragment", "Loaded ${iesData.size} IES entries")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SelectionFragment", "Error loading IES data: ${e.message}")
            Toast.makeText(context, "Error al cargar datos de IES", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSpinners() {
        val modalidades = arrayOf("Ordinaria", "CNA y PA", "EIB", "Protección", "FF. AA.", "VRAEM", "Huallaga", "REPARED")
        val modalidadAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modalidades)
        spinnerModalidad.setAdapter(modalidadAdapter)

        val regiones = iesData.map { it.regionIES }.distinct().sorted()
        Log.d("SelectionFragment", "Unique regions: ${regiones.joinToString()}")
        val regionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, regiones)
        spinnerRegionIES.setAdapter(regionAdapter)
    }

    private fun setupListeners() {
        spinnerModalidad.setOnItemClickListener { _, _, _, _ -> actualizarLimitePuntaje() }
        editTextPuntajePreseleccion.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validarPuntajePreseleccion() }
        spinnerRegionIES.setOnItemClickListener { _, _, _, _ -> updateIESList() }
        spinnerIES.setOnItemClickListener { _, _, _, _ -> updateIESDetails() }
    }

    private fun updateIESList() {
        val regionSeleccionada = spinnerRegionIES.text.toString()
        val tiposSeleccionados = getSelectedTiposIES()

        val iesFiltered = iesData.filter { ies ->
            (regionSeleccionada.isEmpty() || ies.regionIES == regionSeleccionada) &&
                    (tiposSeleccionados.isEmpty() || tiposSeleccionados.contains(ies.tipoIES))
        }

        val iesNames = iesFiltered.map { it.nombreIES }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, iesNames)
        spinnerIES.setAdapter(adapter)

        Log.d("SelectionFragment", "IES filtradas: ${iesFiltered.size}")
    }

    private fun updateIESDetails() {
        val selectedIES = iesData.find { it.nombreIES == spinnerIES.text.toString() }
        if (selectedIES != null) {
            layoutIESDetails.visibility = View.VISIBLE
            textViewTipoIES.text = "Tipo IES: ${selectedIES.tipoIES}"
            textViewRegionIES.text = "Región IES: ${selectedIES.regionIES}"
            textViewSiglasIES.text = "Siglas IES: ${selectedIES.siglasIES}"
            textViewTopIES.text = "Top IES: ${selectedIES.topIES}"
            textViewRankingIES.text = "Ranking IES: ${selectedIES.rankingIES}"
            textViewPuntajeRankingIES.text = "Puntaje Ranking IES: ${selectedIES.puntajeRankingIES}"
            textViewGestionIES.text = "Gestión IES: ${selectedIES.gestionIES}"
            textViewPuntajeGestionIES.text = "Puntaje Gestión IES: ${selectedIES.puntajeGestionIES}"
            textViewRatioSelectividad.text = "Ratio Selectividad: ${selectedIES.ratioSelectividad}"
            textViewPuntajeRatioSelectividad.text = "Puntaje Ratio Selectividad: ${selectedIES.puntajeRatioSelectividad}"
            textViewPuntajeTotalIES.text = "Puntaje Total de la IES: ${selectedIES.puntosExtraPAO}"
        } else {
            layoutIESDetails.visibility = View.GONE
        }
    }

    private fun actualizarLimitePuntaje() {
        val modalidad = spinnerModalidad.text.toString()
        val maxPuntaje = if (modalidad == "EIB") 180 else 170
        editTextPuntajePreseleccion.hint = "Puntaje de Preselección (0-$maxPuntaje)"
    }

    private fun validarPuntajePreseleccion() {
        val puntaje = editTextPuntajePreseleccion.text.toString().toIntOrNull()
        val modalidad = spinnerModalidad.text.toString()
        val maxPuntaje = if (modalidad == "EIB") 180 else 170

        if (puntaje == null || puntaje < 0 || puntaje > maxPuntaje) {
            textViewErrorPuntaje.text = "El puntaje debe estar entre 0 y $maxPuntaje"
            textViewErrorPuntaje.visibility = View.VISIBLE
        } else {
            textViewErrorPuntaje.visibility = View.GONE
        }
    }

    private fun setupTipoIESCheckboxes() {
        val tiposIES = iesData.map { it.tipoIES }.distinct().sorted()
        tiposIES.forEach { tipo ->
            val checkbox = CheckBox(context).apply {
                text = tipo
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnCheckedChangeListener { _, _ -> updateIESList() }
            }
            checkboxContainerTipoIES.addView(checkbox)
        }
    }

    private fun getSelectedTiposIES(): List<String> {
        return (0 until checkboxContainerTipoIES.childCount)
            .map { checkboxContainerTipoIES.getChildAt(it) }
            .filterIsInstance<CheckBox>()
            .filter { it.isChecked }
            .map { it.text.toString() }
    }
}