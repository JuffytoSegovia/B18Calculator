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
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.min

class PreselectionFragment : Fragment() {

    private lateinit var layoutInicio: LinearLayout
    private lateinit var layoutContinuacion: LinearLayout
    private lateinit var layoutResultado: LinearLayout

    private lateinit var editTextNombre: TextInputEditText
    private lateinit var spinnerModalidad: AutoCompleteTextView
    private lateinit var editTextENP: TextInputEditText
    private lateinit var textViewENPError: TextView
    private lateinit var spinnerSisfoh: AutoCompleteTextView
    private lateinit var spinnerDepartamento: AutoCompleteTextView
    private lateinit var textViewResultado: TextView

    private lateinit var layoutModalidad: TextInputLayout
    private lateinit var layoutSisfoh: TextInputLayout
    private lateinit var layoutDepartamento: TextInputLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preselection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        layoutInicio = view.findViewById(R.id.layoutInicio)
        layoutContinuacion = view.findViewById(R.id.layoutContinuacion)
        layoutResultado = view.findViewById(R.id.layoutResultado)

        editTextNombre = view.findViewById(R.id.editTextNombre)
        spinnerModalidad = view.findViewById(R.id.spinnerModalidad)
        editTextENP = view.findViewById(R.id.editTextENP)
        textViewENPError = view.findViewById(R.id.textViewENPError)
        spinnerSisfoh = view.findViewById(R.id.spinnerSisfoh)
        spinnerDepartamento = view.findViewById(R.id.spinnerDepartamento)
        textViewResultado = view.findViewById(R.id.textViewResultado)

        layoutModalidad = view.findViewById(R.id.layoutModalidad)
        layoutSisfoh = view.findViewById(R.id.layoutSisfoh)
        layoutDepartamento = view.findViewById(R.id.layoutDepartamento)

        setupSpinners()
        setupENPValidation()

        view.findViewById<Button>(R.id.buttonContinuar).setOnClickListener {
            if (validateInitialInputs()) {
                showContinuationLayout()
            }
        }

        view.findViewById<Button>(R.id.buttonCalcular).setOnClickListener {
            calculateAndShowResult()
        }

        view.findViewById<Button>(R.id.buttonReiniciar).setOnClickListener {
            resetCalculator()
        }

        view.findViewById<Button>(R.id.buttonLimpiar).setOnClickListener {
            limpiarFormulario()
        }

        view.findViewById<ImageButton>(R.id.buttonInfoQuintil).setOnClickListener {
            showQuintilInfo()
        }

        // Cargar datos guardados, si existen
        cargarDatosGuardados()
    }

    private fun setupSpinners() {
        val modalidades = resources.getStringArray(R.array.modalidades)
        spinnerModalidad.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, modalidades))

        spinnerModalidad.setOnItemClickListener { _, _, _, _ ->
            updateSisfohOptions()
            layoutModalidad.error = null
        }

        updateSisfohOptions()

        val departamentos = resources.getStringArray(R.array.departamentos)
        spinnerDepartamento.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, departamentos))

        spinnerSisfoh.setOnItemClickListener { _, _, _, _ -> layoutSisfoh.error = null }
        spinnerDepartamento.setOnItemClickListener { _, _, _, _ -> layoutDepartamento.error = null }
    }

    private fun updateSisfohOptions() {
        val sisfohOptions = if (spinnerModalidad.text.toString() == "Ordinaria") {
            resources.getStringArray(R.array.sisfoh_options_ordinaria)
        } else {
            resources.getStringArray(R.array.sisfoh_options)
        }
        val sisfohAdapter = ArrayAdapter(requireContext(), R.layout.list_item, sisfohOptions)
        spinnerSisfoh.setAdapter(sisfohAdapter)

        // No limpiar el texto aquí, ya que podría borrar los datos cargados
        // spinnerSisfoh.text.clear()
        layoutSisfoh.error = null
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

    private fun showContinuationLayout() {
        layoutInicio.visibility = View.GONE
        layoutContinuacion.visibility = View.VISIBLE
    }

    private fun calculateAndShowResult() {
        // Aquí iría la lógica de cálculo completa
        // Por ahora, usaremos un cálculo simplificado
        val enp = editTextENP.text.toString().toInt()
        val resultado = min(enp + 50, 170) // Ejemplo simplificado

        val mensajeResultado = "Hola ${editTextNombre.text}, tu puntaje estimado es: $resultado"
        textViewResultado.text = mensajeResultado

        layoutContinuacion.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE
    }

    private fun resetCalculator() {
        limpiarFormulario()
        layoutResultado.visibility = View.GONE
        layoutContinuacion.visibility = View.GONE
        layoutInicio.visibility = View.VISIBLE
    }

    private fun limpiarFormulario() {
        editTextNombre.text?.clear()
        spinnerModalidad.text?.clear()
        editTextENP.text?.clear()
        spinnerSisfoh.text?.clear()
        spinnerDepartamento.text?.clear()
        hideENPError()

        editTextNombre.error = null
        layoutModalidad.error = null
        layoutSisfoh.error = null
        layoutDepartamento.error = null

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
            apply()
        }
    }

    private fun cargarDatosGuardados() {
        val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        editTextNombre.setText(sharedPrefs.getString("nombre", ""))

        // Primero configuramos los adaptadores
        setupSpinners()

        // Luego cargamos los datos guardados
        val modalidadGuardada = sharedPrefs.getString("modalidad", "")
        spinnerModalidad.setText(modalidadGuardada, false)

        // Actualizar opciones de SISFOH basadas en la modalidad guardada
        if (modalidadGuardada == "Ordinaria") {
            updateSisfohOptions()
        }

        editTextENP.setText(sharedPrefs.getString("enp", ""))
        spinnerSisfoh.setText(sharedPrefs.getString("sisfoh", ""), false)
        spinnerDepartamento.setText(sharedPrefs.getString("departamento", ""), false)
    }

    private fun showQuintilInfo() {
        AlertDialog.Builder(requireContext())
            .setTitle("Información de Quintiles")
            .setMessage(R.string.quintil_info)
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onPause() {
        super.onPause()
        guardarDatos()
    }
}