package com.juffyto.b18calculator

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SelectionFragment : Fragment() {

    private lateinit var editTextNombre: TextInputEditText
    private lateinit var spinnerModalidad: AutoCompleteTextView
    private lateinit var editTextPuntajePreseleccion: TextInputEditText
    private lateinit var spinnerRegionIES: AutoCompleteTextView
    private lateinit var checkboxContainerTipoIES: LinearLayout
    private lateinit var spinnerIES: AutoCompleteTextView
    private lateinit var layoutIESDetails: LinearLayout
    private lateinit var textViewTipoIESHeader: TextView
    private lateinit var textViewGestionIESHeader: TextView
    private lateinit var textViewTipoIESDetail: TextView
    private lateinit var textViewGestionIESDetail: TextView
    private lateinit var textViewRegionIES: TextView
    private lateinit var textViewSiglasIES: TextView
    private lateinit var textViewTopIES: TextView
    private lateinit var textViewRankingIES: TextView
    private lateinit var textViewPuntajeRankingIES: TextView
    private lateinit var textViewPuntajeGestionIES: TextView
    private lateinit var textViewRatioSelectividad: TextView
    private lateinit var textViewPuntajeRatioSelectividad: TextView
    private lateinit var textViewPuntajeTotalIES: TextView
    private lateinit var checkboxContainerGestionIES: LinearLayout
    private lateinit var layoutIESSelection: LinearLayout
    private lateinit var layoutIESFilters: LinearLayout
    private lateinit var layoutFormularioSeleccion: LinearLayout
    private lateinit var layoutReporteSeleccion: LinearLayout
    private lateinit var buttonCalcularPuntaje: Button
    private lateinit var buttonLimpiarFormulario: Button
    private lateinit var buttonVolverFormulario: Button
    private lateinit var textViewReporteContenido: TextView

    private lateinit var buttonPuntajeSeleccion: Button
    private lateinit var textViewDesglosePuntaje: TextView
    private lateinit var textViewFormula: TextView
    private lateinit var textViewPuntajeMaximo: TextView
    private lateinit var textViewMensajeAnimo: TextView

    private lateinit var buttonReiniciarCalculadora: Button
    private var iesData: List<IES> = listOf()
    private var shouldShowErrors = false
    private var currentWindow = 1 // 1 para formulario, 2 para reporte
    private var selectedIESName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selection, container, false)

        initializeViews(view)
        loadIESData()
        setupSpinners()
        setupListeners()
        loadSavedState()

        return view
    }

    private fun initializeViews(view: View) {
        editTextNombre = view.findViewById(R.id.editTextNombre)
        spinnerModalidad = view.findViewById(R.id.spinnerModalidad)
        editTextPuntajePreseleccion = view.findViewById(R.id.editTextPuntajePreseleccion)
        spinnerRegionIES = view.findViewById(R.id.spinnerRegionIES)
        checkboxContainerTipoIES = view.findViewById(R.id.checkboxContainerTipoIES)
        spinnerIES = view.findViewById(R.id.spinnerIES)
        layoutIESDetails = view.findViewById(R.id.layoutIESDetails)
        textViewTipoIESHeader = view.findViewById(R.id.textViewTipoIESHeader)
        textViewGestionIESHeader = view.findViewById(R.id.textViewGestionIESHeader)
        textViewTipoIESDetail = view.findViewById(R.id.textViewTipoIESDetail)
        textViewGestionIESDetail = view.findViewById(R.id.textViewGestionIESDetail)
        textViewRegionIES = view.findViewById(R.id.textViewRegionIES)
        textViewSiglasIES = view.findViewById(R.id.textViewSiglasIES)
        textViewTopIES = view.findViewById(R.id.textViewTopIES)
        textViewRankingIES = view.findViewById(R.id.textViewRankingIES)
        textViewPuntajeRankingIES = view.findViewById(R.id.textViewPuntajeRankingIES)
        textViewPuntajeGestionIES = view.findViewById(R.id.textViewPuntajeGestionIES)
        textViewRatioSelectividad = view.findViewById(R.id.textViewRatioSelectividad)
        textViewPuntajeRatioSelectividad = view.findViewById(R.id.textViewPuntajeRatioSelectividad)
        textViewPuntajeTotalIES = view.findViewById(R.id.textViewPuntajeTotalIES)
        checkboxContainerGestionIES = view.findViewById(R.id.checkboxContainerGestionIES)
        layoutIESSelection = view.findViewById(R.id.layoutIESSelection)
        layoutIESFilters = view.findViewById(R.id.layoutIESFilters)
        layoutFormularioSeleccion = view.findViewById(R.id.layoutFormularioSeleccion)
        layoutReporteSeleccion = view.findViewById(R.id.layoutReporteSeleccion)
        buttonCalcularPuntaje = view.findViewById(R.id.buttonCalcularPuntaje)
        buttonLimpiarFormulario = view.findViewById(R.id.buttonLimpiarFormulario)
        buttonVolverFormulario = view.findViewById(R.id.buttonVolverFormulario)

        buttonPuntajeSeleccion = view.findViewById(R.id.buttonPuntajeSeleccion)
        textViewDesglosePuntaje = view.findViewById(R.id.textViewDesglosePuntaje)
        textViewFormula = view.findViewById(R.id.textViewFormula)
        textViewPuntajeMaximo = view.findViewById(R.id.textViewPuntajeMaximo)
        textViewMensajeAnimo = view.findViewById(R.id.textViewMensajeAnimo)
        buttonReiniciarCalculadora = view.findViewById(R.id.buttonReiniciarCalculadora)

        // Ocultar inicialmente
        layoutIESFilters.visibility = View.GONE
        layoutIESSelection.visibility = View.GONE
        layoutReporteSeleccion.visibility = View.GONE
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
        val regionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, regiones)
        spinnerRegionIES.setAdapter(regionAdapter)
    }

    private fun setupListeners() {
        editTextNombre.addTextChangedListener(createTextWatcher { validateField(editTextNombre) })

        spinnerModalidad.setOnItemClickListener { _, _, _, _ ->
            actualizarLimitePuntaje()
            validateField(spinnerModalidad)
            validateField(editTextPuntajePreseleccion)
        }

        editTextPuntajePreseleccion.addTextChangedListener(createTextWatcher { validateField(editTextPuntajePreseleccion) })

        spinnerRegionIES.setOnItemClickListener { _, _, _, _ ->
            layoutIESFilters.visibility = View.VISIBLE
            updateTipoIESCheckboxes()
            updateGestionIESCheckboxes()
            resetIESSelection()
            updateIESList()
            validateField(spinnerRegionIES)
        }

        spinnerIES.setOnItemClickListener { _, _, _, _ ->
            updateIESDetails()
        }

        buttonCalcularPuntaje.setOnClickListener {
            shouldShowErrors = true
            if (validarCampos()) {
                calcularYMostrarReporte()
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos requeridos", Toast.LENGTH_SHORT).show()
            }
        }

        buttonLimpiarFormulario.setOnClickListener { limpiarFormulario() }
        buttonVolverFormulario.setOnClickListener { mostrarFormulario() }

        buttonReiniciarCalculadora.setOnClickListener {
            reiniciarCalculadora()
        }

        buttonVolverFormulario.setOnClickListener {
            mostrarFormulario()
        }
    }

    private fun reiniciarCalculadora() {
        limpiarFormulario()
        mostrarFormulario()
    }

    private fun createTextWatcher(afterTextChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { afterTextChanged() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun validateField(view: View) {
        if (shouldShowErrors) {
            when (view) {
                editTextNombre -> {
                    if (editTextNombre.text.isNullOrBlank()) {
                        (editTextNombre.parent.parent as? TextInputLayout)?.error = "El nombre es requerido"
                    } else {
                        (editTextNombre.parent.parent as? TextInputLayout)?.error = null
                    }
                }
                spinnerModalidad -> {
                    if (spinnerModalidad.text.isNullOrBlank()) {
                        (spinnerModalidad.parent.parent as? TextInputLayout)?.error = "La modalidad es requerida"
                    } else {
                        (spinnerModalidad.parent.parent as? TextInputLayout)?.error = null
                    }
                }
                editTextPuntajePreseleccion -> {
                    val puntaje = editTextPuntajePreseleccion.text.toString().toIntOrNull()
                    val maxPuntaje = if (spinnerModalidad.text.toString() == "EIB") 180 else 170
                    if (puntaje == null || puntaje < 0 || puntaje > maxPuntaje) {
                        (editTextPuntajePreseleccion.parent.parent as? TextInputLayout)?.error = "El puntaje debe estar entre 0 y $maxPuntaje"
                    } else {
                        (editTextPuntajePreseleccion.parent.parent as? TextInputLayout)?.error = null
                    }
                }
                spinnerRegionIES -> {
                    if (spinnerRegionIES.text.isNullOrBlank()) {
                        (spinnerRegionIES.parent.parent as? TextInputLayout)?.error = "La región IES es requerida"
                    } else {
                        (spinnerRegionIES.parent.parent as? TextInputLayout)?.error = null
                    }
                }
                spinnerIES -> {
                    if (spinnerIES.text.isNullOrBlank()) {
                        (spinnerIES.parent.parent as? TextInputLayout)?.error = "La IES es requerida"
                    } else {
                        (spinnerIES.parent.parent as? TextInputLayout)?.error = null
                    }
                }
            }
        }
    }

    private fun updateTipoIESCheckboxes() {
        val regionSeleccionada = spinnerRegionIES.text.toString()
        val tiposIESEnRegion = iesData
            .filter { it.regionIES == regionSeleccionada }
            .map { it.tipoIES }
            .distinct()
            .sorted()

        checkboxContainerTipoIES.removeAllViews()
        tiposIESEnRegion.forEach { tipo ->
            val checkbox = CheckBox(context).apply {
                text = tipo
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnCheckedChangeListener { _, _ ->
                    updateGestionIESCheckboxes()
                    resetIESSelection()
                    updateIESList()
                    validarCampos()
                }
            }
            checkboxContainerTipoIES.addView(checkbox)
        }
    }

    private fun updateGestionIESCheckboxes() {
        val regionSeleccionada = spinnerRegionIES.text.toString()
        val tiposSeleccionados = getSelectedTiposIES()

        val gestionesIESEnRegion = iesData
            .filter { it.regionIES == regionSeleccionada && (tiposSeleccionados.isEmpty() || tiposSeleccionados.contains(it.tipoIES)) }
            .map { it.gestionIES }
            .distinct()
            .sorted()

        checkboxContainerGestionIES.removeAllViews()
        gestionesIESEnRegion.forEach { gestion ->
            val checkbox = CheckBox(context).apply {
                text = gestion
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setOnCheckedChangeListener { _, _ ->
                    resetIESSelection()
                    updateIESList()
                    validarCampos()
                }
            }
            checkboxContainerGestionIES.addView(checkbox)
        }
    }

    private fun resetIESSelection() {
        spinnerIES.setText("", false)
        layoutIESDetails.visibility = View.GONE
    }

    private fun updateIESList() {
        val regionSeleccionada = spinnerRegionIES.text.toString()
        val tiposSeleccionados = getSelectedTiposIES()
        val gestionesSeleccionadas = getSelectedGestionesIES()

        val iesFiltered = iesData.filter { ies ->
            (regionSeleccionada.isEmpty() || ies.regionIES == regionSeleccionada) &&
                    (tiposSeleccionados.isEmpty() || tiposSeleccionados.contains(ies.tipoIES)) &&
                    (gestionesSeleccionadas.isEmpty() || gestionesSeleccionadas.contains(ies.gestionIES))
        }

        val iesNames = iesFiltered.map { it.nombreIES }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, iesNames)
        spinnerIES.setAdapter(adapter)
        spinnerIES.setText("", false)  // Limpiar la selección actual

        layoutIESSelection.visibility = View.VISIBLE
    }

    private fun updateIESDetails() {
        val selectedIES = iesData.find { it.nombreIES == spinnerIES.text.toString() }
        if (selectedIES != null) {
            layoutIESDetails.visibility = View.VISIBLE
            selectedIESName = selectedIES.nombreIES
            textViewTipoIESDetail.text = "Tipo IES: ${selectedIES.tipoIES}"
            textViewGestionIESDetail.text = "Gestión IES: ${selectedIES.gestionIES}"
            textViewRegionIES.text = "Región IES: ${selectedIES.regionIES}"
            textViewSiglasIES.text = "Siglas IES: ${selectedIES.siglasIES}"
            textViewTopIES.text = "Top IES: ${selectedIES.topIES}"
            textViewRankingIES.text = "Ranking IES: ${selectedIES.rankingIES}"
            textViewPuntajeRankingIES.text = "Puntaje Ranking IES: ${selectedIES.puntajeRankingIES}"
            textViewPuntajeGestionIES.text = "Puntaje Gestión IES: ${selectedIES.puntajeGestionIES}"
            textViewRatioSelectividad.text = "Ratio Selectividad: ${selectedIES.ratioSelectividad}"
            textViewPuntajeRatioSelectividad.text = "Puntaje Ratio Selectividad: ${selectedIES.puntajeRatioSelectividad}"
            textViewPuntajeTotalIES.text = "Puntaje Total de la IES: ${selectedIES.puntosExtraPAO}"
            saveState()
        } else {
            layoutIESDetails.visibility = View.GONE
        }
    }

    private fun actualizarLimitePuntaje() {
        val modalidad = spinnerModalidad.text.toString()
        val maxPuntaje = if (modalidad == "EIB") 180 else 170
        (editTextPuntajePreseleccion.parent.parent as? TextInputLayout)?.hint = "Puntaje de Preselección (0-$maxPuntaje)"
    }

    private fun getSelectedTiposIES(): List<String> {
        return (0 until checkboxContainerTipoIES.childCount)
            .map { checkboxContainerTipoIES.getChildAt(it) as? CheckBox }
            .filterNotNull()
            .filter { it.isChecked }
            .map { it.text.toString() }
    }

    private fun getSelectedGestionesIES(): List<String> {
        return (0 until checkboxContainerGestionIES.childCount)
            .map { checkboxContainerGestionIES.getChildAt(it) as? CheckBox }
            .filterNotNull()
            .filter { it.isChecked }
            .map { it.text.toString() }
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        validateField(editTextNombre)
        validateField(spinnerModalidad)
        validateField(editTextPuntajePreseleccion)
        validateField(spinnerRegionIES)
        validateField(spinnerIES)

        isValid = isValid && (editTextNombre.parent.parent as? TextInputLayout)?.error == null
        isValid = isValid && (spinnerModalidad.parent.parent as? TextInputLayout)?.error == null
        isValid = isValid && (editTextPuntajePreseleccion.parent.parent as? TextInputLayout)?.error == null
        isValid = isValid && (spinnerRegionIES.parent.parent as? TextInputLayout)?.error == null
        isValid = isValid && (spinnerIES.parent.parent as? TextInputLayout)?.error == null

        return isValid
    }

    private fun calcularYMostrarReporte() {
        val nombre = editTextNombre.text.toString()
        val modalidad = spinnerModalidad.text.toString()
        val puntajePreseleccion = editTextPuntajePreseleccion.text.toString().toIntOrNull() ?: 0
        val iesSeleccionada = iesData.find { it.nombreIES == spinnerIES.text.toString() }

        if (iesSeleccionada != null) {
            // Calcular C (puntaje por posición en el ranking)
            val C = when (iesSeleccionada.topIES) {
                "Top 1 al 6" -> 10
                "Top 7 al 12" -> 7
                "Top 13 al 19" -> 5
                else -> 0
            }

            // Calcular G (puntaje por gestión)
            val G = when (iesSeleccionada.gestionIES) {
                "PRIVADA", "PRIVADA ASOCIATIVA" -> 5
                "PÚBLICA" -> 10
                else -> 0
            }

            // Calcular S (puntaje por ratio de selectividad)
            val S = when (iesSeleccionada.ratioSelectividad) {
                "QUINTIL 5" -> 10
                "QUINTIL 4" -> 7
                "QUINTIL 3" -> 5
                "QUINTIL 2" -> 2
                else -> 0
            }

            // Calcular el puntaje total
            val puntajeTotal = puntajePreseleccion + C + G + S

            // Actualizar el botón de puntaje
            buttonPuntajeSeleccion.text = "Tu puntaje de selección es: $puntajeTotal puntos"
            buttonPuntajeSeleccion.setBackgroundColor(obtenerColorPuntaje(puntajeTotal))

            // Actualizar el botón de puntaje
            buttonPuntajeSeleccion.text = "Tu puntaje de selección es: $puntajeTotal puntos"
            obtenerColorPuntaje(puntajeTotal)

            // Generar el desglose del puntaje
            val desglose = """
                ✅ PS (Puntaje de Preselección): $puntajePreseleccion
                ✅ C (Puntaje por Posición en Ranking): $C
                ✅ G (Puntaje por Gestión): $G
                ✅ S (Puntaje por Ratio de Selectividad): $S
            """.trimIndent()
            textViewDesglosePuntaje.text = desglose

            // Mostrar la fórmula
            textViewFormula.text = "Fórmula: Puntaje Total = PS + C + G + S"

            // Mostrar el puntaje máximo
            val puntajeMaximo = if (modalidad == "EIB") 210 else 200
            textViewPuntajeMaximo.text = "Puntaje máximo para esta modalidad: $puntajeMaximo puntos"

            // Generar el mensaje de ánimo
            textViewMensajeAnimo.text = generarMensajeAnimo(puntajeTotal)

            mostrarReporte()
        } else {
            Toast.makeText(context, "Por favor, seleccione una IES antes de calcular.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerColorPuntaje(puntaje: Int): Int {
        val color = when {
            puntaje >= 120 -> ContextCompat.getColor(requireContext(), R.color.green)
            puntaje >= 110 -> ContextCompat.getColor(requireContext(), R.color.blue)
            puntaje >= 100 -> ContextCompat.getColor(requireContext(), R.color.orange)
            else -> ContextCompat.getColor(requireContext(), R.color.red)
        }
        buttonPuntajeSeleccion.backgroundTintList = ColorStateList.valueOf(color)
        return color
    }

    private fun generarMensajeAnimo(puntaje: Int): String {
        return when {
            puntaje > 120 -> "¡Felicidades! Tienes una alta probabilidad de ganar la beca."
            puntaje > 110 -> "Tienes buenas posibilidades de ganar la beca."
            puntaje > 100 -> "Hay una probabilidad de que ganes la beca."
            else -> "Tu puntaje está por debajo del promedio para ganar la beca, pero sigue esforzándote."
        }
    }

    private fun limpiarFormulario() {
        shouldShowErrors = false

        editTextNombre.text?.clear()
        spinnerModalidad.setText("", false)
        editTextPuntajePreseleccion.text?.clear()
        spinnerRegionIES.setText("", false)
        checkboxContainerTipoIES.removeAllViews()
        checkboxContainerGestionIES.removeAllViews()
        spinnerIES.setText("", false)
        layoutIESDetails.visibility = View.GONE
        layoutIESFilters.visibility = View.GONE
        layoutIESSelection.visibility = View.GONE

        // Limpiar errores
        (editTextNombre.parent.parent as? TextInputLayout)?.error = null
        (spinnerModalidad.parent.parent as? TextInputLayout)?.error = null
        (editTextPuntajePreseleccion.parent.parent as? TextInputLayout)?.error = null
        (spinnerRegionIES.parent.parent as? TextInputLayout)?.error = null
        (spinnerIES.parent.parent as? TextInputLayout)?.error = null

        // Restablecer el hint del puntaje de preselección
        (editTextPuntajePreseleccion.parent.parent as? TextInputLayout)?.hint = "Puntaje de Preselección"

        // El botón de calcular puntaje permanece habilitado
        buttonCalcularPuntaje.isEnabled = true
    }

    private fun mostrarFormulario() {
        layoutFormularioSeleccion.visibility = View.VISIBLE
        layoutReporteSeleccion.visibility = View.GONE
        currentWindow = 1
        saveState()
    }

    private fun mostrarReporte() {
        layoutFormularioSeleccion.visibility = View.GONE
        layoutReporteSeleccion.visibility = View.VISIBLE
        currentWindow = 2
        saveState()
    }

    private fun saveState() {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putInt("currentWindow", currentWindow)
            putString("nombre", editTextNombre.text.toString())
            putString("modalidad", spinnerModalidad.text.toString())
            putString("puntajePreseleccion", editTextPuntajePreseleccion.text.toString())
            putString("regionIES", spinnerRegionIES.text.toString())
            putString("ies", spinnerIES.text.toString())

            // Guardar estado de los checkboxes de Tipo IES
            val selectedTiposIES = getSelectedTiposIES()
            putString("selectedTiposIES", selectedTiposIES.joinToString(","))

            // Guardar estado de los checkboxes de Gestión IES
            val selectedGestionesIES = getSelectedGestionesIES()
            putString("selectedGestionesIES", selectedGestionesIES.joinToString(","))

            // Guardar la selección de IES
            putString("selectedIES", spinnerIES.text.toString())

            // Guardar visibilidad de los layouts
            putBoolean("layoutIESFiltersVisible", layoutIESFilters.visibility == View.VISIBLE)
            putBoolean("layoutIESSelectionVisible", layoutIESSelection.visibility == View.VISIBLE)
            putBoolean("layoutIESDetailsVisible", layoutIESDetails.visibility == View.VISIBLE)

            // Guardar el estado del reporte si es necesario
            if (currentWindow == 2) {
                putString("desglosePuntaje", textViewDesglosePuntaje.text.toString())
                putString("formula", textViewFormula.text.toString())
                putString("puntajeMaximo", textViewPuntajeMaximo.text.toString())
                putString("mensajeAnimo", textViewMensajeAnimo.text.toString())
                putString("puntajeSeleccion", buttonPuntajeSeleccion.text.toString())
            }

            // Guardar el nombre de la IES seleccionada
            putString("selectedIESName", selectedIESName)

            // Guardar el estado de los detalles de la IES
            putString("tipoIESDetail", textViewTipoIESDetail.text.toString())
            putString("gestionIESDetail", textViewGestionIESDetail.text.toString())
            putString("regionIESDetail", textViewRegionIES.text.toString())
            putString("siglasIESDetail", textViewSiglasIES.text.toString())
            putString("topIESDetail", textViewTopIES.text.toString())
            putString("rankingIESDetail", textViewRankingIES.text.toString())
            putString("puntajeRankingIESDetail", textViewPuntajeRankingIES.text.toString())
            putString("puntajeGestionIESDetail", textViewPuntajeGestionIES.text.toString())
            putString("ratioSelectividadDetail", textViewRatioSelectividad.text.toString())
            putString("puntajeRatioSelectividadDetail", textViewPuntajeRatioSelectividad.text.toString())
            putString("puntajeTotalIESDetail", textViewPuntajeTotalIES.text.toString())

            // Guardar el desglose de la IES seleccionada
            putString("iesDesglose", textViewDesglosePuntaje.text.toString())
            putString("iesFormula", textViewFormula.text.toString())
            putString("iesPuntajeMaximo", textViewPuntajeMaximo.text.toString())
            putString("iesMensajeAnimo", textViewMensajeAnimo.text.toString())

            // Guardar el color del botón de puntaje
            putInt("buttonPuntajeColor", buttonPuntajeSeleccion.backgroundTintList?.defaultColor ?: Color.BLACK)

            apply()
        }
    }

    private fun loadSavedState() {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        currentWindow = sharedPrefs.getInt("currentWindow", 1)
        editTextNombre.setText(sharedPrefs.getString("nombre", ""))
        spinnerModalidad.setText(sharedPrefs.getString("modalidad", ""), false)
        editTextPuntajePreseleccion.setText(sharedPrefs.getString("puntajePreseleccion", ""))
        spinnerRegionIES.setText(sharedPrefs.getString("regionIES", ""), false)
        spinnerIES.setText(sharedPrefs.getString("ies", ""), false)

        // Restaurar visibilidad de los layouts
        layoutIESFilters.visibility = if (sharedPrefs.getBoolean("layoutIESFiltersVisible", false)) View.VISIBLE else View.GONE
        layoutIESSelection.visibility = if (sharedPrefs.getBoolean("layoutIESSelectionVisible", false)) View.VISIBLE else View.GONE
        layoutIESDetails.visibility = if (sharedPrefs.getBoolean("layoutIESDetailsVisible", false)) View.VISIBLE else View.GONE

        // Restaurar estado de los checkboxes de Tipo IES y Gestión IES
        val selectedTiposIES = sharedPrefs.getString("selectedTiposIES", "")?.split(",") ?: listOf()
        val selectedGestionesIES = sharedPrefs.getString("selectedGestionesIES", "")?.split(",") ?: listOf()

        if (layoutIESFilters.visibility == View.VISIBLE) {
            updateTipoIESCheckboxes()
            updateGestionIESCheckboxes()
            restoreCheckboxState(checkboxContainerTipoIES, selectedTiposIES)
            restoreCheckboxState(checkboxContainerGestionIES, selectedGestionesIES)
        }

        if (currentWindow == 2) {
            textViewDesglosePuntaje.text = sharedPrefs.getString("desglosePuntaje", "")
            textViewFormula.text = sharedPrefs.getString("formula", "")
            textViewPuntajeMaximo.text = sharedPrefs.getString("puntajeMaximo", "")
            textViewMensajeAnimo.text = sharedPrefs.getString("mensajeAnimo", "")
            buttonPuntajeSeleccion.text = sharedPrefs.getString("puntajeSeleccion", "")
            mostrarReporte()
        } else {
            mostrarFormulario()
        }

        // Actualizar la lista de IES basada en los filtros restaurados
        if (layoutIESSelection.visibility == View.VISIBLE) {
            updateIESList()
        }

        selectedIESName = sharedPrefs.getString("selectedIESName", null)
        spinnerIES.setText(selectedIESName, false)

        if (layoutIESDetails.visibility == View.VISIBLE) {
            textViewTipoIESDetail.text = sharedPrefs.getString("tipoIESDetail", "")
            textViewGestionIESDetail.text = sharedPrefs.getString("gestionIESDetail", "")
            textViewRegionIES.text = sharedPrefs.getString("regionIESDetail", "")
            textViewSiglasIES.text = sharedPrefs.getString("siglasIESDetail", "")
            textViewTopIES.text = sharedPrefs.getString("topIESDetail", "")
            textViewRankingIES.text = sharedPrefs.getString("rankingIESDetail", "")
            textViewPuntajeRankingIES.text = sharedPrefs.getString("puntajeRankingIESDetail", "")
            textViewPuntajeGestionIES.text = sharedPrefs.getString("puntajeGestionIESDetail", "")
            textViewRatioSelectividad.text = sharedPrefs.getString("ratioSelectividadDetail", "")
            textViewPuntajeRatioSelectividad.text = sharedPrefs.getString("puntajeRatioSelectividadDetail", "")
            textViewPuntajeTotalIES.text = sharedPrefs.getString("puntajeTotalIESDetail", "")
        }

        // Restaurar detalles de IES si estaban visibles
        if (layoutIESDetails.visibility == View.VISIBLE) {
            updateIESDetails()
        }

        // Restaurar el color del botón de puntaje
        val buttonColor = sharedPrefs.getInt("buttonPuntajeColor", ContextCompat.getColor(requireContext(), R.color.blue_primary))
        buttonPuntajeSeleccion.backgroundTintList = ColorStateList.valueOf(buttonColor)

        // Restaurar la selección de IES
        val selectedIES = sharedPrefs.getString("selectedIES", "")
        spinnerIES.setText(selectedIES, false)

        if (!selectedIES.isNullOrEmpty()) {
            // Si hay una IES seleccionada, actualizar los detalles
            updateIESDetails()

            // Restaurar el desglose
            textViewDesglosePuntaje.text = sharedPrefs.getString("iesDesglose", "")
            textViewFormula.text = sharedPrefs.getString("iesFormula", "")
            textViewPuntajeMaximo.text = sharedPrefs.getString("iesPuntajeMaximo", "")
            textViewMensajeAnimo.text = sharedPrefs.getString("iesMensajeAnimo", "")

            // Mostrar la ventana de resultado si estaba visible
            if (currentWindow == 2) {
                mostrarReporte()
            }
        }
    }

    private fun restoreCheckboxState(container: LinearLayout, selectedItems: List<String>) {
        for (i in 0 until container.childCount) {
            val checkbox = container.getChildAt(i) as? CheckBox
            checkbox?.isChecked = selectedItems.contains(checkbox?.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        saveState()
    }

    override fun onResume() {
        super.onResume()
        loadSavedState()
    }
}