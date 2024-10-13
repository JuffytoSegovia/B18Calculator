package com.juffyto.b18calculator

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.min
import android.content.res.ColorStateList
import android.graphics.Color

class PreselectionFragment : Fragment() {

    private var currentWindow = 1 // 1: Inicio, 2: Continuación, 3: Resultado
    private lateinit var layoutInicio: LinearLayout
    private lateinit var layoutContinuacion: LinearLayout
    private lateinit var layoutResultado: LinearLayout
    private lateinit var layoutLenguaOriginaria: LinearLayout

    private lateinit var editTextNombre: TextInputEditText
    private lateinit var spinnerModalidad: AutoCompleteTextView
    private lateinit var editTextENP: TextInputEditText
    private lateinit var textViewENPError: TextView
    private lateinit var spinnerSisfoh: AutoCompleteTextView
    private lateinit var spinnerDepartamento: AutoCompleteTextView
    private lateinit var spinnerLenguaOriginaria: AutoCompleteTextView

    private lateinit var layoutModalidad: TextInputLayout
    private lateinit var layoutSisfoh: TextInputLayout
    private lateinit var layoutDepartamento: TextInputLayout

    private lateinit var buttonContinuar: Button
    private lateinit var buttonCalcular: Button
    private lateinit var buttonReiniciar: Button
    private lateinit var buttonLimpiar: Button
    private lateinit var buttonInfoQuintil: ImageButton
    private lateinit var buttonInfoLengua: ImageButton

    private lateinit var checkboxConcursoNacional: CheckBox
    private lateinit var checkboxConcursoParticipacion: CheckBox
    private lateinit var checkboxJuegosNacionales: CheckBox
    private lateinit var checkboxJuegosParticipacion: CheckBox

    private lateinit var checkboxDiscapacidad: CheckBox
    private lateinit var checkboxBomberos: CheckBox
    private lateinit var checkboxVoluntarios: CheckBox
    private lateinit var checkboxComunidadNativa: CheckBox
    private lateinit var checkboxMetalesPesados: CheckBox
    private lateinit var checkboxPoblacionBeneficiaria: CheckBox
    private lateinit var checkboxOrfandad: CheckBox
    private lateinit var checkboxDesproteccion: CheckBox
    private lateinit var checkboxAgenteSalud: CheckBox

    private lateinit var textViewNombreResultado: TextView
    private lateinit var buttonPuntajeResultado: Button
    private lateinit var textViewDesglosePuntaje: TextView
    private lateinit var textViewFormula: TextView
    private lateinit var textViewPuntajeMaximo: TextView
    private lateinit var textViewMensajeAnimo: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preselection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupListeners()
        setupSpinners()
        setupENPValidation()

        // Restaurar el estado si existe
        savedInstanceState?.let {
            currentWindow = it.getInt("currentWindow", 1)
        }

        cargarDatosGuardados()
        restoreCurrentWindow()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentWindow", currentWindow)
    }

    private fun initializeViews(view: View) {
        layoutInicio = view.findViewById(R.id.layoutInicio)
        layoutContinuacion = view.findViewById(R.id.layoutContinuacion)
        layoutResultado = view.findViewById(R.id.layoutResultado)
        layoutLenguaOriginaria = view.findViewById(R.id.layoutLenguaOriginaria)

        editTextNombre = view.findViewById(R.id.editTextNombre)
        spinnerModalidad = view.findViewById(R.id.spinnerModalidad)
        editTextENP = view.findViewById(R.id.editTextENP)
        textViewENPError = view.findViewById(R.id.textViewENPError)
        spinnerSisfoh = view.findViewById(R.id.spinnerSisfoh)
        spinnerDepartamento = view.findViewById(R.id.spinnerDepartamento)
        spinnerLenguaOriginaria = view.findViewById(R.id.spinnerLenguaOriginaria)

        layoutModalidad = view.findViewById(R.id.layoutModalidad)
        layoutSisfoh = view.findViewById(R.id.layoutSisfoh)
        layoutDepartamento = view.findViewById(R.id.layoutDepartamento)

        buttonContinuar = view.findViewById(R.id.buttonContinuar)
        buttonCalcular = view.findViewById(R.id.buttonCalcular)
        buttonReiniciar = view.findViewById(R.id.buttonReiniciar)
        buttonLimpiar = view.findViewById(R.id.buttonLimpiar)
        buttonInfoQuintil = view.findViewById(R.id.buttonInfoQuintil)
        buttonInfoLengua = view.findViewById(R.id.buttonInfoLengua)

        checkboxConcursoNacional = view.findViewById(R.id.checkboxConcursoNacional)
        checkboxConcursoParticipacion = view.findViewById(R.id.checkboxConcursoParticipacion)
        checkboxJuegosNacionales = view.findViewById(R.id.checkboxJuegosNacionales)
        checkboxJuegosParticipacion = view.findViewById(R.id.checkboxJuegosParticipacion)

        checkboxDiscapacidad = view.findViewById(R.id.checkboxDiscapacidad)
        checkboxBomberos = view.findViewById(R.id.checkboxBomberos)
        checkboxVoluntarios = view.findViewById(R.id.checkboxVoluntarios)
        checkboxComunidadNativa = view.findViewById(R.id.checkboxComunidadNativa)
        checkboxMetalesPesados = view.findViewById(R.id.checkboxMetalesPesados)
        checkboxPoblacionBeneficiaria = view.findViewById(R.id.checkboxPoblacionBeneficiaria)
        checkboxOrfandad = view.findViewById(R.id.checkboxOrfandad)
        checkboxDesproteccion = view.findViewById(R.id.checkboxDesproteccion)
        checkboxAgenteSalud = view.findViewById(R.id.checkboxAgenteSalud)

        textViewNombreResultado = view.findViewById(R.id.textViewNombreResultado)
        buttonPuntajeResultado = view.findViewById(R.id.buttonPuntajeResultado)
        textViewDesglosePuntaje = view.findViewById(R.id.textViewDesglosePuntaje)
        textViewFormula = view.findViewById(R.id.textViewFormula)
        textViewPuntajeMaximo = view.findViewById(R.id.textViewPuntajeMaximo)
        textViewMensajeAnimo = view.findViewById(R.id.textViewMensajeAnimo)
    }

    private fun setupListeners() {
        buttonContinuar.setOnClickListener {
            if (validateInitialInputs()) {
                showContinuationLayout()
            }
        }

        buttonCalcular.setOnClickListener {
            calculateAndShowResult()
        }

        buttonReiniciar.setOnClickListener {
            resetCalculator()
        }

        buttonLimpiar.setOnClickListener {
            limpiarFormulario()
        }

        buttonInfoQuintil.setOnClickListener {
            showQuintilInfo()
        }

        buttonInfoLengua.setOnClickListener {
            showLenguaInfo()
        }

        spinnerModalidad.setOnItemClickListener { _, _, _, _ ->
            updateSisfohOptions()
            updateLenguaOriginariaVisibility()
            updateCheckboxes()
            layoutModalidad.error = null
        }
    }

    private fun setupSpinners() {
        val modalidades = resources.getStringArray(R.array.modalidades)
        spinnerModalidad.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, modalidades))

        updateSisfohOptions()

        val departamentosConPuntaje = resources.getStringArray(R.array.departamentos).map { departamento ->
            val puntaje = calcularPuntajeQuintil(departamento)
            "$departamento - $puntaje puntos"
        }
        spinnerDepartamento.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, departamentosConPuntaje))

        spinnerSisfoh.setOnItemClickListener { _, _, _, _ -> layoutSisfoh.error = null }
        spinnerDepartamento.setOnItemClickListener { _, _, _, _ -> layoutDepartamento.error = null }

        setupLenguaOriginariaSpinner()
    }

    private fun setupLenguaOriginariaSpinner() {
        val opcionesLengua = arrayOf(
            "Hablante de lengua de primera prioridad - 10 puntos",
            "Hablante de lengua de segunda prioridad - 5 puntos"
        )
        spinnerLenguaOriginaria.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, opcionesLengua))
    }

    private fun updateSisfohOptions() {
        val sisfohOptions = if (spinnerModalidad.text.toString() == "Ordinaria") {
            resources.getStringArray(R.array.sisfoh_options_ordinaria)
        } else {
            resources.getStringArray(R.array.sisfoh_options)
        }
        spinnerSisfoh.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, sisfohOptions))
        layoutSisfoh.error = null
    }

    private fun updateLenguaOriginariaVisibility() {
        layoutLenguaOriginaria.visibility = if (spinnerModalidad.text.toString() == "EIB") View.VISIBLE else View.GONE
    }

    private fun updateCheckboxes() {
        val modalidad = spinnerModalidad.text.toString()
        checkboxDesproteccion.isEnabled = modalidad == "Protección"
        checkboxComunidadNativa.isEnabled = modalidad != "CNA y PA"
        checkboxOrfandad.isEnabled = modalidad != "Protección"
    }

    private fun setupENPValidation() {
        editTextENP.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true) {
                    validateENP(s.toString())
                } else {
                    hideENPError()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateENP(enp: String): Boolean {
        val enpValue = enp.toIntOrNull()
        return when {
            enpValue == null -> {
                showENPError("Ingrese un número válido")
                false
            }
            enpValue < 0 || enpValue > 120 -> {
                showENPError("El puntaje debe estar entre 0 y 120")
                false
            }
            enpValue % 2 != 0 -> {
                showENPError("El puntaje debe ser un número par")
                false
            }
            else -> {
                hideENPError()
                true
            }
        }
    }

    private fun showENPError(message: String) {
        textViewENPError.text = message
        textViewENPError.visibility = View.VISIBLE
    }

    private fun hideENPError() {
        textViewENPError.visibility = View.GONE
    }

    private fun validateInitialInputs(): Boolean {
        var isValid = true

        if (editTextNombre.text.isNullOrBlank()) {
            editTextNombre.error = "Ingrese su nombre"
            isValid = false
        }

        if (spinnerModalidad.text.isNullOrBlank()) {
            layoutModalidad.error = "Seleccione una modalidad"
            isValid = false
        }

        if (editTextENP.text.isNullOrBlank()) {
            showENPError("Ingrese un número válido")
            isValid = false
        } else if (!validateENP(editTextENP.text.toString())) {
            isValid = false
        }

        if (spinnerSisfoh.text.isNullOrBlank()) {
            layoutSisfoh.error = "Seleccione una clasificación SISFOH"
            isValid = false
        }

        if (spinnerDepartamento.text.isNullOrBlank()) {
            layoutDepartamento.error = "Seleccione un departamento"
            isValid = false
        }

        return isValid
    }

    fun onBackPressed(): Boolean {
        return when (currentWindow) {
            3 -> {
                showContinuationLayout()
                true
            }
            2 -> {
                showInitialLayout()
                true
            }
            else -> false
        }
    }

    private fun showInitialLayout() {
        layoutInicio.visibility = View.VISIBLE
        layoutContinuacion.visibility = View.GONE
        layoutResultado.visibility = View.GONE
        currentWindow = 1
    }

    private fun showContinuationLayout() {
        layoutInicio.visibility = View.GONE
        layoutContinuacion.visibility = View.VISIBLE
        layoutResultado.visibility = View.GONE
        currentWindow = 2
        guardarDatos() // Guardar datos cuando se muestra la ventana 2
    }

    private fun calculateAndShowResult() {
        val nombre = editTextNombre.text.toString()
        val modalidad = spinnerModalidad.text.toString()
        val enp = editTextENP.text.toString().toInt()
        val sisfoh = spinnerSisfoh.text.toString()
        val departamento = spinnerDepartamento.text.toString().split(" - ")[0]

        // Validación para la lengua originaria en modalidad EIB
        if (modalidad == "EIB" && spinnerLenguaOriginaria.text.isNullOrBlank()) {
            (spinnerLenguaOriginaria.parent.parent as? TextInputLayout)?.error = "Este campo es obligatorio para la modalidad EIB"
            return
        }

        val puntajeENP = enp
        val puntajeSisfoh = calcularPuntajeSisfoh(sisfoh, modalidad)
        val puntajeQuintil = calcularPuntajeQuintil(departamento)
        val puntajeExtracurricular = calcularPuntajeExtracurricular()
        val puntajePriorizable = calcularPuntajePriorizable()
        val puntajeLengua = if (modalidad == "EIB") calcularPuntajeLengua() else 0

        val puntajeTotal = puntajeENP + puntajeSisfoh + puntajeQuintil + puntajeExtracurricular + puntajePriorizable + puntajeLengua

        mostrarResultado(nombre, modalidad, puntajeTotal, puntajeENP, puntajeSisfoh, puntajeQuintil, puntajeExtracurricular, puntajePriorizable, puntajeLengua)
        currentWindow = 3
        guardarDatos() // Guardar datos cuando se muestra la ventana 3
    }

    private fun calcularPuntajeSisfoh(sisfoh: String, modalidad: String): Int {
        return when {
            sisfoh.contains("extrema") -> 5
            sisfoh.contains("Pobreza (P)") && modalidad != "Ordinaria" -> 2
            else -> 0
        }
    }

    private fun calcularPuntajeQuintil(departamento: String): Int {
        val quintil1 = listOf("Amazonas", "Ucayali", "Ayacucho", "Puno", "Loreto")
        val quintil2 = listOf("San Martín", "Cusco", "Huánuco", "Apurímac", "Huancavelica")
        val quintil3 = listOf("Áncash", "Tacna", "Madre de Dios", "Moquegua", "Pasco", "Cajamarca")
        val quintil4 = listOf("Arequipa", "Piura", "Junín", "Tumbes")

        return when {
            quintil1.contains(departamento) -> 10
            quintil2.contains(departamento) -> 7
            quintil3.contains(departamento) -> 5
            quintil4.contains(departamento) -> 2
            else -> 0
        }
    }

    private fun calcularPuntajeExtracurricular(): Int {
        var puntaje = 0
        if (checkboxConcursoNacional.isChecked) puntaje += 5
        if (checkboxConcursoParticipacion.isChecked) puntaje += 2
        if (checkboxJuegosNacionales.isChecked) puntaje += 5
        if (checkboxJuegosParticipacion.isChecked) puntaje += 2
        return min(puntaje, 10)
    }

    private fun calcularPuntajePriorizable(): Int {
        var puntaje = 0
        if (checkboxDiscapacidad.isChecked) puntaje += 5
        if (checkboxBomberos.isChecked) puntaje += 5
        if (checkboxVoluntarios.isChecked) puntaje += 5
        if (checkboxComunidadNativa.isChecked) puntaje += 5
        if (checkboxMetalesPesados.isChecked) puntaje += 5
        if (checkboxPoblacionBeneficiaria.isChecked) puntaje += 5
        if (checkboxOrfandad.isChecked) puntaje += 5
        if (checkboxDesproteccion.isChecked) puntaje += 5
        if (checkboxAgenteSalud.isChecked) puntaje += 5
        return min(puntaje, 25)
    }

    private fun calcularPuntajeLengua(): Int {
        return when (spinnerLenguaOriginaria.text.toString()) {
            "Hablante de lengua de primera prioridad - 10 puntos" -> 10
            "Hablante de lengua de segunda prioridad - 5 puntos" -> 5
            else -> 0
        }
    }

    private fun mostrarResultado(nombre: String, modalidad: String, puntajeTotal: Int,
                                 puntajeENP: Int, puntajeSisfoh: Int, puntajeQuintil: Int,
                                 puntajeExtracurricular: Int, puntajePriorizable: Int, puntajeLengua: Int) {
        layoutContinuacion.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE

        textViewNombreResultado.text = "Reporte de Preselección para $nombre"

        val puntajeMaximo = if (modalidad == "EIB") 180 else 170
        val puntajeFinal = min(puntajeTotal, puntajeMaximo)

        buttonPuntajeResultado.text = "Tu puntaje estimado de preselección es: $puntajeFinal puntos"
        buttonPuntajeResultado.setBackgroundColor(obtenerColorPuntaje(puntajeFinal))
        obtenerColorPuntaje(puntajeFinal)

        val desglose = StringBuilder()
        desglose.append("✅ Modalidad: $modalidad\n")
        desglose.append("✅ ENP: $puntajeENP puntos\n")
        desglose.append("✅ SISFOH: $puntajeSisfoh puntos\n")
        desglose.append("✅ Quintil: $puntajeQuintil puntos\n")
        desglose.append("✅ Actividades extracurriculares: $puntajeExtracurricular puntos\n")
        desglose.append("✅ Condiciones priorizables: $puntajePriorizable puntos\n")
        if (modalidad == "EIB") {
            desglose.append("✅ Lengua originaria: $puntajeLengua puntos\n")
        }
        textViewDesglosePuntaje.text = desglose.toString()

        textViewFormula.text = "Fórmula: PS = ENP + S + T + (CE o CEP + JD o JDP)max 10 + (D + B + V + IA + PEM + PD + OR + DF + ACS)max 25" +
                if (modalidad == "EIB") " + LO" else ""

        textViewPuntajeMaximo.text = "Puntaje máximo para esta modalidad: $puntajeMaximo puntos"

        textViewMensajeAnimo.text = generarMensajeAnimo(puntajeFinal)
    }

    private fun obtenerColorPuntaje(puntaje: Int): Int {
        val color = when {
            puntaje >= 100 -> ContextCompat.getColor(requireContext(), R.color.green)
            puntaje >= 70 -> ContextCompat.getColor(requireContext(), R.color.yellow)
            else -> ContextCompat.getColor(requireContext(), R.color.red)
        }
        buttonPuntajeResultado.backgroundTintList = ColorStateList.valueOf(color)
        return color
    }

    private fun generarMensajeAnimo(puntaje: Int): String {
        return when {
            puntaje >= 100 -> "¡Excelente trabajo! Tienes grandes posibilidades de ganar la beca. ¡Sigue adelante!"
            puntaje >= 70 -> "¡Buen esfuerzo! Estás en buen camino para obtener la beca. ¡No te rindas!"
            else -> "Cada punto cuenta. Sigue trabajando duro y no pierdas la esperanza. ¡Tú puedes lograrlo!"
        }
    }

    private fun resetCalculator() {
        limpiarFormulario()
        layoutResultado.visibility = View.GONE
        layoutContinuacion.visibility = View.GONE
        layoutInicio.visibility = View.VISIBLE
        currentWindow = 1
    }

    private fun restoreCurrentWindow() {
        when (currentWindow) {
            1 -> {
                layoutInicio.visibility = View.VISIBLE
                layoutContinuacion.visibility = View.GONE
                layoutResultado.visibility = View.GONE
            }
            2 -> {
                layoutInicio.visibility = View.GONE
                layoutContinuacion.visibility = View.VISIBLE
                layoutResultado.visibility = View.GONE
            }
            3 -> {
                layoutInicio.visibility = View.GONE
                layoutContinuacion.visibility = View.GONE
                layoutResultado.visibility = View.VISIBLE
                // Asegurarse de que los datos de la ventana 3 sean visibles
                textViewNombreResultado.visibility = View.VISIBLE
                buttonPuntajeResultado.visibility = View.VISIBLE
                textViewDesglosePuntaje.visibility = View.VISIBLE
                textViewFormula.visibility = View.VISIBLE
                textViewPuntajeMaximo.visibility = View.VISIBLE
                textViewMensajeAnimo.visibility = View.VISIBLE
            }
        }
    }

    private fun limpiarFormulario() {
        editTextNombre.text?.clear()
        spinnerModalidad.text?.clear()
        editTextENP.text?.clear()
        spinnerSisfoh.text?.clear()
        spinnerDepartamento.text?.clear()
        spinnerLenguaOriginaria.text?.clear()
        hideENPError()

        editTextNombre.error = null
        layoutModalidad.error = null
        layoutSisfoh.error = null
        layoutDepartamento.error = null
        (spinnerLenguaOriginaria.parent.parent as TextInputLayout).error = null

        // Limpiar checkboxes
        checkboxConcursoNacional.isChecked = false
        checkboxConcursoParticipacion.isChecked = false
        checkboxJuegosNacionales.isChecked = false
        checkboxJuegosParticipacion.isChecked = false

        checkboxDiscapacidad.isChecked = false
        checkboxBomberos.isChecked = false
        checkboxVoluntarios.isChecked = false
        checkboxComunidadNativa.isChecked = false
        checkboxMetalesPesados.isChecked = false
        checkboxPoblacionBeneficiaria.isChecked = false
        checkboxOrfandad.isChecked = false
        checkboxDesproteccion.isChecked = false
        checkboxAgenteSalud.isChecked = false

        updateCheckboxes()
        updateLenguaOriginariaVisibility()

        // Limpiar datos guardados
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
    }

    private fun guardarDatos() {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("nombre", editTextNombre.text.toString())
            putString("modalidad", spinnerModalidad.text.toString())
            putString("enp", editTextENP.text.toString())
            putString("sisfoh", spinnerSisfoh.text.toString())
            putString("departamento", spinnerDepartamento.text.toString())
            putString("lenguaOriginaria", spinnerLenguaOriginaria.text.toString())
            putInt("currentWindow", currentWindow)

            // Guardar datos de la ventana 3
            putString("nombreResultado", textViewNombreResultado.text.toString())
            putString("puntajeResultado", buttonPuntajeResultado.text.toString())
            putString("desglosePuntaje", textViewDesglosePuntaje.text.toString())
            putString("formula", textViewFormula.text.toString())
            putString("puntajeMaximo", textViewPuntajeMaximo.text.toString())
            putString("mensajeAnimo", textViewMensajeAnimo.text.toString())
            putInt("colorPuntaje", buttonPuntajeResultado.currentTextColor)
            putInt("colorBoton", buttonPuntajeResultado.backgroundTintList?.defaultColor ?: Color.BLACK)

            // Guardar estado de los checkboxes
            putBoolean("checkboxConcursoNacional", checkboxConcursoNacional.isChecked)
            putBoolean("checkboxConcursoParticipacion", checkboxConcursoParticipacion.isChecked)
            putBoolean("checkboxJuegosNacionales", checkboxJuegosNacionales.isChecked)
            putBoolean("checkboxJuegosParticipacion", checkboxJuegosParticipacion.isChecked)
            putBoolean("checkboxDiscapacidad", checkboxDiscapacidad.isChecked)
            putBoolean("checkboxBomberos", checkboxBomberos.isChecked)
            putBoolean("checkboxVoluntarios", checkboxVoluntarios.isChecked)
            putBoolean("checkboxComunidadNativa", checkboxComunidadNativa.isChecked)
            putBoolean("checkboxMetalesPesados", checkboxMetalesPesados.isChecked)
            putBoolean("checkboxPoblacionBeneficiaria", checkboxPoblacionBeneficiaria.isChecked)
            putBoolean("checkboxOrfandad", checkboxOrfandad.isChecked)
            putBoolean("checkboxDesproteccion", checkboxDesproteccion.isChecked)
            putBoolean("checkboxAgenteSalud", checkboxAgenteSalud.isChecked)

            apply()
        }
    }

    private fun cargarDatosGuardados() {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        editTextNombre.setText(sharedPrefs.getString("nombre", ""))

        val modalidadGuardada = sharedPrefs.getString("modalidad", "")
        spinnerModalidad.setText(modalidadGuardada, false)

        if (modalidadGuardada == "Ordinaria") {
            updateSisfohOptions()
        }

        val colorBoton = sharedPrefs.getInt("colorBoton", ContextCompat.getColor(requireContext(), R.color.black))

        editTextENP.setText(sharedPrefs.getString("enp", ""))
        spinnerSisfoh.setText(sharedPrefs.getString("sisfoh", ""), false)
        spinnerDepartamento.setText(sharedPrefs.getString("departamento", ""), false)
        spinnerLenguaOriginaria.setText(sharedPrefs.getString("lenguaOriginaria", ""), false)

        updateLenguaOriginariaVisibility()
        updateCheckboxes()
        currentWindow = sharedPrefs.getInt("currentWindow", 1)

        // Cargar datos de la ventana 3
        textViewNombreResultado.text = sharedPrefs.getString("nombreResultado", "")
        buttonPuntajeResultado.text = sharedPrefs.getString("puntajeResultado", "")
        textViewDesglosePuntaje.text = sharedPrefs.getString("desglosePuntaje", "")
        textViewFormula.text = sharedPrefs.getString("formula", "")
        textViewPuntajeMaximo.text = sharedPrefs.getString("puntajeMaximo", "")
        textViewMensajeAnimo.text = sharedPrefs.getString("mensajeAnimo", "")
        buttonPuntajeResultado.setTextColor(sharedPrefs.getInt("colorPuntaje", ContextCompat.getColor(requireContext(), R.color.black)))
        buttonPuntajeResultado.backgroundTintList = ColorStateList.valueOf(colorBoton)

        // Cargar estado de los checkboxes
        checkboxConcursoNacional.isChecked = sharedPrefs.getBoolean("checkboxConcursoNacional", false)
        checkboxConcursoParticipacion.isChecked = sharedPrefs.getBoolean("checkboxConcursoParticipacion", false)
        checkboxJuegosNacionales.isChecked = sharedPrefs.getBoolean("checkboxJuegosNacionales", false)
        checkboxJuegosParticipacion.isChecked = sharedPrefs.getBoolean("checkboxJuegosParticipacion", false)
        checkboxDiscapacidad.isChecked = sharedPrefs.getBoolean("checkboxDiscapacidad", false)
        checkboxBomberos.isChecked = sharedPrefs.getBoolean("checkboxBomberos", false)
        checkboxVoluntarios.isChecked = sharedPrefs.getBoolean("checkboxVoluntarios", false)
        checkboxComunidadNativa.isChecked = sharedPrefs.getBoolean("checkboxComunidadNativa", false)
        checkboxMetalesPesados.isChecked = sharedPrefs.getBoolean("checkboxMetalesPesados", false)
        checkboxPoblacionBeneficiaria.isChecked = sharedPrefs.getBoolean("checkboxPoblacionBeneficiaria", false)
        checkboxOrfandad.isChecked = sharedPrefs.getBoolean("checkboxOrfandad", false)
        checkboxDesproteccion.isChecked = sharedPrefs.getBoolean("checkboxDesproteccion", false)
        checkboxAgenteSalud.isChecked = sharedPrefs.getBoolean("checkboxAgenteSalud", false)

        updateCheckboxes()
        restoreCurrentWindow()
    }

    private fun showQuintilInfo() {
        AlertDialog.Builder(requireContext())
            .setTitle("Información de Quintiles")
            .setMessage(R.string.quintil_info)
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showLenguaInfo() {
        AlertDialog.Builder(requireContext())
            .setTitle("Información de Lenguas Originarias")
            .setMessage(R.string.lengua_originaria_info)
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onPause() {
        super.onPause()
        guardarDatos()
    }

    override fun onResume() {
        super.onResume()
        restoreCurrentWindow()
    }
}