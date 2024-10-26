package com.juffyto.b18calculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import android.net.Uri

class PreselectionFragment : Fragment() {

    private lateinit var buttonSimulacrosPDF: Button
    private lateinit var buttonSimulacroOnline: Button
    private var shouldShowErrors = false
    private var currentWindow = 1 // 1: Inicio, 2: Continuación, 3: Resultado
    private lateinit var layoutInicio: LinearLayout
    private lateinit var layoutContinuacion: LinearLayout
    private lateinit var layoutResultado: LinearLayout
    private lateinit var layoutLenguaOriginaria: LinearLayout

    private lateinit var editTextNombre: TextInputEditText
    private lateinit var spinnerModalidad: AutoCompleteTextView
    private lateinit var editTextENP: TextInputEditText
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
        buttonSimulacrosPDF = view.findViewById(R.id.buttonSimulacrosPDF)
        buttonSimulacroOnline = view.findViewById(R.id.buttonSimulacroOnline)
    }

    private fun setupListeners() {
        // Validación en tiempo real para los campos
        editTextNombre.addTextChangedListener(createTextWatcher {
            if (shouldShowErrors) {
                validateField(editTextNombre)
            }
        })

        spinnerModalidad.setOnItemClickListener { _, _, _, _ ->
            if (!spinnerModalidad.text.isNullOrBlank()) {
                layoutModalidad.error = null
            }
            updateSisfohOptions()
            updateLenguaOriginariaVisibility()
            updateCheckboxes()
        }

        // Validación en tiempo real para ENP
        editTextENP.addTextChangedListener(createTextWatcher {
            val enpText = editTextENP.text.toString()
            if (enpText.isNotEmpty()) {
                val enpValue = enpText.toIntOrNull()
                val textInputLayout = editTextENP.parent.parent as? TextInputLayout
                when {
                    enpValue == null -> {
                        textInputLayout?.error = "Ingrese un número válido"
                    }
                    enpValue < 0 || enpValue > 120 -> {
                        textInputLayout?.error = "El puntaje debe estar entre 0 y 120"
                    }
                    enpValue % 2 != 0 -> {
                        textInputLayout?.error = "El puntaje debe ser un número par"
                    }
                    else -> {
                        textInputLayout?.error = null
                    }
                }
            } else {
                (editTextENP.parent.parent as? TextInputLayout)?.error = null
            }
        })

        spinnerSisfoh.setOnItemClickListener { _, _, _, _ ->
            if (!spinnerSisfoh.text.isNullOrBlank()) {
                layoutSisfoh.error = null
            }
        }

        spinnerDepartamento.setOnItemClickListener { _, _, _, _ ->
            if (!spinnerDepartamento.text.isNullOrBlank()) {
                layoutDepartamento.error = null
            }
        }

        buttonContinuar.setOnClickListener {
            shouldShowErrors = true
            if (!validateInitialInputs()) {
                Toast.makeText(context, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            } else {
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

        spinnerLenguaOriginaria.setOnItemClickListener { _, _, _, _ ->
            if (!spinnerLenguaOriginaria.text.isNullOrBlank()) {
                (spinnerLenguaOriginaria.parent.parent as? TextInputLayout)?.error = null
            }
        }

        buttonSimulacrosPDF.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/u/0/folders/1-EixtoVjF2siolxZ9nd1o337Bfq2XLUp"))
            startActivity(intent)
        }

        buttonSimulacroOnline.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pronabec-app.pronabec.gob.pe/"))
            startActivity(intent)
        }
    }

    private fun createTextWatcher(afterTextChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged()
            }
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

        // Solo limpiar el error si ya hay un valor seleccionado
        if (!spinnerSisfoh.text.isNullOrBlank()) {
            layoutSisfoh.error = null
        }
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

    private fun validateInitialInputs(): Boolean {
        shouldShowErrors = true
        var isValid = true

        // Validar nombre
        validateField(editTextNombre)
        isValid = isValid && (editTextNombre.parent.parent as? TextInputLayout)?.error == null

        // Validar modalidad
        validateField(spinnerModalidad)
        isValid = isValid && layoutModalidad.error == null

        // Validar ENP
        validateField(editTextENP)
        isValid = isValid && (editTextENP.parent.parent as? TextInputLayout)?.error == null

        // Validar SISFOH
        validateField(spinnerSisfoh)
        isValid = isValid && layoutSisfoh.error == null

        // Validar Departamento
        validateField(spinnerDepartamento)
        isValid = isValid && layoutDepartamento.error == null

        // Validar Lengua Originaria para modalidad EIB
        if (spinnerModalidad.text.toString() == "EIB") {
            validateField(spinnerLenguaOriginaria)
            isValid = isValid && (spinnerLenguaOriginaria.parent.parent as? TextInputLayout)?.error == null
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
        guardarDatos() // Añadir esta línea
    }

    private fun showContinuationLayout() {
        layoutInicio.visibility = View.GONE
        layoutContinuacion.visibility = View.VISIBLE
        layoutResultado.visibility = View.GONE
        currentWindow = 2
        guardarDatos()
    }

    private fun calculateAndShowResult() {
        try {
            shouldShowErrors = true
            if (!validateInitialInputs()) {
                Toast.makeText(context, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return
            }

            val nombre = editTextNombre.text.toString()
            val modalidad = spinnerModalidad.text.toString()
            val enpText = editTextENP.text.toString()

            // Validación adicional del ENP
            val enp = try {
                enpText.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "El puntaje ENP debe ser un número válido", Toast.LENGTH_SHORT).show()
                return
            }

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

            mostrarResultado(nombre, modalidad, puntajeTotal, puntajeENP, puntajeSisfoh, puntajeQuintil,
                puntajeExtracurricular, puntajePriorizable, puntajeLengua)
            currentWindow = 3
            guardarDatos()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ocurrió un error al calcular el puntaje", Toast.LENGTH_SHORT).show()
        }
    }

    // Agregar este método auxiliar para validación adicional
    private fun validateField(view: View) {
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
                    layoutModalidad.error = "Seleccione una modalidad"
                } else {
                    layoutModalidad.error = null
                }
            }
            editTextENP -> {
                val enpValue = editTextENP.text.toString().toIntOrNull()
                when {
                    editTextENP.text.isNullOrBlank() -> {
                        (editTextENP.parent.parent as? TextInputLayout)?.error = "El puntaje ENP es requerido"
                    }
                    enpValue == null -> {
                        (editTextENP.parent.parent as? TextInputLayout)?.error = "Ingrese un número válido"
                    }
                    enpValue < 0 || enpValue > 120 -> {
                        (editTextENP.parent.parent as? TextInputLayout)?.error = "El puntaje debe estar entre 0 y 120"
                    }
                    enpValue % 2 != 0 -> {
                        (editTextENP.parent.parent as? TextInputLayout)?.error = "El puntaje debe ser un número par"
                    }
                    else -> {
                        (editTextENP.parent.parent as? TextInputLayout)?.error = null
                    }
                }
            }
            spinnerSisfoh -> {
                if (spinnerSisfoh.text.isNullOrBlank()) {
                    layoutSisfoh.error = "Seleccione una clasificación SISFOH"
                } else {
                    layoutSisfoh.error = null
                }
            }
            spinnerDepartamento -> {
                if (spinnerDepartamento.text.isNullOrBlank()) {
                    layoutDepartamento.error = "Seleccione un departamento"
                } else {
                    layoutDepartamento.error = null
                }
            }
            spinnerLenguaOriginaria -> {
                if (spinnerModalidad.text.toString() == "EIB" && spinnerLenguaOriginaria.text.isNullOrBlank()) {
                    (spinnerLenguaOriginaria.parent.parent as? TextInputLayout)?.error = "Este campo es obligatorio para la modalidad EIB"
                } else {
                    (spinnerLenguaOriginaria.parent.parent as? TextInputLayout)?.error = null
                }
            }
        }
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

    @SuppressLint("SetTextI18n")
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

        // Scroll al inicio
        view?.findViewById<ScrollView>(R.id.scrollViewPreselection)?.smoothScrollTo(0, 0)

        currentWindow = 3
        guardarDatos()
    }

    private fun obtenerColorPuntaje(puntaje: Int): Int {
        val color = when {
            puntaje >= 100 -> ContextCompat.getColor(requireContext(), R.color.green)
            puntaje >= 70 -> ContextCompat.getColor(requireContext(), R.color.orange)
            else -> ContextCompat.getColor(requireContext(), R.color.red)
        }
        buttonPuntajeResultado.backgroundTintList = ColorStateList.valueOf(color)
        return color
    }

    private fun generarMensajeAnimo(puntaje: Int): String {
        val puntajeENP = editTextENP.text.toString().toIntOrNull() ?: 0
        val preguntasCorrectas = puntajeENP / 2
        val preguntasFaltantes = 60 - preguntasCorrectas
        val puntosPosiblesMejora = preguntasFaltantes * 2

        val mensajeBase = when {
            puntaje >= 100 -> "¡Felicidades! Tienes grandes posibilidades de ganar la beca. ¡Sigue adelante!"
            puntaje >= 70 -> "¡Buen esfuerzo! Estás en buen camino para obtener la beca. ¡No te rindas!"
            else -> "Cada punto cuenta. Sigue trabajando duro y no pierdas la esperanza. ¡Tú puedes lograrlo!"
        }

        val analisisPuntaje = """
    
    📊 𝗔𝗻𝗮́𝗹𝗶𝘀𝗶𝘀 𝗱𝗲 𝘁𝘂 𝗽𝘂𝗻𝘁𝗮𝗷𝗲 𝗘𝗡𝗣:
    • Has respondido correctamente aproximadamente $preguntasCorrectas de 60 preguntas
    • Podrías mejorar tu puntaje hasta en $puntosPosiblesMejora puntos adicionales si respondes correctamente las $preguntasFaltantes preguntas restantes
    """.trimIndent()

        val recomendacionENP = """

    📝 𝗥𝗲𝗰𝗼𝗺𝗲𝗻𝗱𝗮𝗰𝗶𝗼𝗻𝗲𝘀 𝗽𝗮𝗿𝗮 𝗺𝗲𝗷𝗼𝗿𝗮𝗿 𝘁𝘂 𝗽𝘂𝗻𝘁𝗮𝗷𝗲:

    𝗘𝗹 𝗘𝘅𝗮𝗺𝗲𝗻 𝗡𝗮𝗰𝗶𝗼𝗻𝗮𝗹 𝗱𝗲 𝗣𝗿𝗲𝘀𝗲𝗹𝗲𝗰𝗰𝗶𝗼́𝗻 (𝗘𝗡𝗣):
    Es tu mejor oportunidad para aumentar significativamente tu puntaje:

    📋 𝗘𝘀𝘁𝗿𝘂𝗰𝘁𝘂𝗿𝗮 𝗱𝗲𝗹 𝗲𝘅𝗮𝗺𝗲𝗻:
    • El ENP consta de 60 preguntas en total:
      - 30 preguntas de competencia matemática
      - 30 preguntas de competencia lectora

    ⚖️ 𝗦𝗶𝘀𝘁𝗲𝗺𝗮 𝗱𝗲 𝗰𝗮𝗹𝗶𝗳𝗶𝗰𝗮𝗰𝗶𝗼́𝗻:
    • Cada pregunta correcta vale 2 puntos
    • Puntaje máximo posible: 120 puntos
    • No hay puntaje en contra
    • Tiene una duración de 2 horas (120 minutos)

    💡 𝗘𝘀𝘁𝗿𝗮𝘁𝗲𝗴𝗶𝗮𝘀 𝗽𝗮𝗿𝗮 𝗺𝗲𝗷𝗼𝗿𝗮𝗿:
    • Practica constantemente ejercicios de matemáticas y comprensión lectora
    • Practica con simulacros de ENP pasados
    • Enfócate en resolver correctamente la mayor cantidad de preguntas posible
    • Gestiona bien tu tiempo durante el examen
    • Considera que el ENP podría ser tu principal fuente de puntos si no cumples con otros criterios de bonificación

    🎯 𝗥𝗲𝗰𝘂𝗲𝗿𝗱𝗮: Cada pregunta correcta te acerca más a tu meta. ¡Prepárate bien!

    📚 𝗥𝗲𝗰𝘂𝗿𝘀𝗼𝘀 𝗱𝗲 𝗲𝘀𝘁𝘂𝗱𝗶𝗼:
    Utiliza los botones de abajo para acceder a recursos de práctica:
    """.trimIndent()

        return if (puntajeENP >= 120) {
            """
        $mensajeBase
        
        🌟 ¡𝗘𝘅𝗰𝗲𝗹𝗲𝗻𝘁𝗲 𝘁𝗿𝗮𝗯𝗮𝗷𝗼 𝗲𝗻 𝗲𝗹 𝗘𝗡𝗣!
        Has alcanzado el puntaje máximo posible en el Examen Nacional de Preselección.
        """.trimIndent()
        } else {
            "$mensajeBase\n\n$analisisPuntaje\n$recomendacionENP"
        }
    }

    private fun resetCalculator() {
        limpiarFormulario()
        layoutResultado.visibility = View.GONE
        layoutContinuacion.visibility = View.GONE
        layoutInicio.visibility = View.VISIBLE
        currentWindow = 1
        guardarDatos() // Añadir esta línea
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
        shouldShowErrors = false

        editTextNombre.text?.clear()
        spinnerModalidad.text?.clear()
        editTextENP.text?.clear()
        spinnerSisfoh.text?.clear()
        spinnerDepartamento.text?.clear()
        spinnerLenguaOriginaria.text?.clear()

        // Limpiar errores y restablecer espaciados
        (editTextNombre.parent.parent as? TextInputLayout)?.apply {
            error = null
            isErrorEnabled = false
        }
        layoutModalidad.apply {
            error = null
            isErrorEnabled = false
        }
        (editTextENP.parent.parent as? TextInputLayout)?.apply {
            error = null
            isErrorEnabled = false
        }
        layoutSisfoh.apply {
            error = null
            isErrorEnabled = false
        }
        layoutDepartamento.apply {
            error = null
            isErrorEnabled = false
        }
        (spinnerLenguaOriginaria.parent.parent as? TextInputLayout)?.apply {
            error = null
            isErrorEnabled = false
        }

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
        with(sharedPrefs.edit()) {
            sharedPrefs.all.keys
                .filter { it.startsWith("preselection_") }
                .forEach { remove(it) }
            apply()
        }
    }

    private fun guardarDatos() {
        // Guardar el estado de la ventana actual
        (activity as? MainActivity)?.saveFragmentState(
            MainActivity.PRESELECTION_WINDOW_STATE,
            currentWindow
        )

        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("preselection_nombre", editTextNombre.text.toString())
            putString("preselection_modalidad", spinnerModalidad.text.toString())
            putString("preselection_enp", editTextENP.text.toString())
            putString("preselection_sisfoh", spinnerSisfoh.text.toString())
            putString("preselection_departamento", spinnerDepartamento.text.toString())
            putString("preselection_lenguaOriginaria", spinnerLenguaOriginaria.text.toString())

            // Guardar datos de la ventana 3
            putString("preselection_nombreResultado", textViewNombreResultado.text.toString())
            putString("preselection_puntajeResultado", buttonPuntajeResultado.text.toString())
            putString("preselection_desglosePuntaje", textViewDesglosePuntaje.text.toString())
            putString("preselection_formula", textViewFormula.text.toString())
            putString("preselection_puntajeMaximo", textViewPuntajeMaximo.text.toString())
            putString("preselection_mensajeAnimo", textViewMensajeAnimo.text.toString())
            putInt("preselection_colorPuntaje", buttonPuntajeResultado.currentTextColor)
            putInt("preselection_colorBoton", buttonPuntajeResultado.backgroundTintList?.defaultColor ?: Color.BLACK)

            // Guardar estado de los checkboxes
            putBoolean("preselection_checkboxConcursoNacional", checkboxConcursoNacional.isChecked)
            putBoolean("preselection_checkboxConcursoParticipacion", checkboxConcursoParticipacion.isChecked)
            putBoolean("preselection_checkboxJuegosNacionales", checkboxJuegosNacionales.isChecked)
            putBoolean("preselection_checkboxJuegosParticipacion", checkboxJuegosParticipacion.isChecked)
            putBoolean("preselection_checkboxDiscapacidad", checkboxDiscapacidad.isChecked)
            putBoolean("preselection_checkboxBomberos", checkboxBomberos.isChecked)
            putBoolean("preselection_checkboxVoluntarios", checkboxVoluntarios.isChecked)
            putBoolean("preselection_checkboxComunidadNativa", checkboxComunidadNativa.isChecked)
            putBoolean("preselection_checkboxMetalesPesados", checkboxMetalesPesados.isChecked)
            putBoolean("preselection_checkboxPoblacionBeneficiaria", checkboxPoblacionBeneficiaria.isChecked)
            putBoolean("preselection_checkboxOrfandad", checkboxOrfandad.isChecked)
            putBoolean("preselection_checkboxDesproteccion", checkboxDesproteccion.isChecked)
            putBoolean("preselection_checkboxAgenteSalud", checkboxAgenteSalud.isChecked)

            apply()
        }
    }

    private fun cargarDatosGuardados() {
        // Restaurar el estado de la ventana
        currentWindow = (activity as? MainActivity)?.getFragmentState(
            MainActivity.PRESELECTION_WINDOW_STATE
        ) ?: 1

        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)

        editTextNombre.setText(sharedPrefs.getString("preselection_nombre", ""))

        val modalidadGuardada = sharedPrefs.getString("preselection_modalidad", "")
        spinnerModalidad.setText(modalidadGuardada, false)

        if (modalidadGuardada == "Ordinaria") {
            updateSisfohOptions()
        }

        val colorBoton = sharedPrefs.getInt("preselection_colorBoton", ContextCompat.getColor(requireContext(), R.color.black))

        editTextENP.setText(sharedPrefs.getString("preselection_enp", ""))
        spinnerSisfoh.setText(sharedPrefs.getString("preselection_sisfoh", ""), false)
        spinnerDepartamento.setText(sharedPrefs.getString("preselection_departamento", ""), false)
        spinnerLenguaOriginaria.setText(sharedPrefs.getString("preselection_lenguaOriginaria", ""), false)

        updateLenguaOriginariaVisibility()
        updateCheckboxes()

        // Cargar datos de la ventana 3
        textViewNombreResultado.text = sharedPrefs.getString("preselection_nombreResultado", "")
        buttonPuntajeResultado.text = sharedPrefs.getString("preselection_puntajeResultado", "")
        textViewDesglosePuntaje.text = sharedPrefs.getString("preselection_desglosePuntaje", "")
        textViewFormula.text = sharedPrefs.getString("preselection_formula", "")
        textViewPuntajeMaximo.text = sharedPrefs.getString("preselection_puntajeMaximo", "")
        textViewMensajeAnimo.text = sharedPrefs.getString("preselection_mensajeAnimo", "")
        buttonPuntajeResultado.backgroundTintList = ColorStateList.valueOf(colorBoton)

        // Cargar estado de los checkboxes
        checkboxConcursoNacional.isChecked = sharedPrefs.getBoolean("preselection_checkboxConcursoNacional", false)
        checkboxConcursoParticipacion.isChecked = sharedPrefs.getBoolean("preselection_checkboxConcursoParticipacion", false)
        checkboxJuegosNacionales.isChecked = sharedPrefs.getBoolean("preselection_checkboxJuegosNacionales", false)
        checkboxJuegosParticipacion.isChecked = sharedPrefs.getBoolean("preselection_checkboxJuegosParticipacion", false)
        checkboxDiscapacidad.isChecked = sharedPrefs.getBoolean("preselection_checkboxDiscapacidad", false)
        checkboxBomberos.isChecked = sharedPrefs.getBoolean("preselection_checkboxBomberos", false)
        checkboxVoluntarios.isChecked = sharedPrefs.getBoolean("preselection_checkboxVoluntarios", false)
        checkboxComunidadNativa.isChecked = sharedPrefs.getBoolean("preselection_checkboxComunidadNativa", false)
        checkboxMetalesPesados.isChecked = sharedPrefs.getBoolean("preselection_checkboxMetalesPesados", false)
        checkboxPoblacionBeneficiaria.isChecked = sharedPrefs.getBoolean("preselection_checkboxPoblacionBeneficiaria", false)
        checkboxOrfandad.isChecked = sharedPrefs.getBoolean("preselection_checkboxOrfandad", false)
        checkboxDesproteccion.isChecked = sharedPrefs.getBoolean("preselection_checkboxDesproteccion", false)
        checkboxAgenteSalud.isChecked = sharedPrefs.getBoolean("preselection_checkboxAgenteSalud", false)

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
        cargarDatosGuardados() // Cambiado de restoreCurrentWindow() a cargarDatosGuardados()
    }
}