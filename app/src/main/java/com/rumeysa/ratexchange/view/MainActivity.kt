package com.rumeysa.ratexchange

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rumeysa.ratexchange.model.SymbolsResponse
import com.rumeysa.ratexchange.service.CurrencyAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://data.fixer.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(CurrencyAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerFrom = findViewById(R.id.spinner_from)
        spinnerTo = findViewById(R.id.spinner_to)
        editTextAmount = findViewById(R.id.editText_amount)
        buttonConvert = findViewById(R.id.button_convert)
        textViewResult = findViewById(R.id.textView_result)

        loadCurrencies()
        buttonConvert.setOnClickListener {
            convertCurrency()
        }
    }

    private fun convertCurrency() {
        val fromCurrency = spinnerFrom.selectedItem?.toString()
            ?: return showError("From currency is not selected")
        val toCurrency =
            spinnerTo.selectedItem?.toString() ?: return showError("To currency is not selected")
        val amount =
            editTextAmount.text.toString().toDoubleOrNull() ?: return showError("Invalid amount")

        lifecycleScope.launch {
            try {
                val response2 = withContext(Dispatchers.IO) {
                    api.getLatestRates().execute()
                }
                if (response2.isSuccessful) {
                    val currencyData = response2.body()
                    val rates = currencyData?.rates

                    if (rates != null && fromCurrency in rates && toCurrency in rates) {
                        val fromRate = rates[fromCurrency] ?: 1.0
                        val toRate = rates[toCurrency] ?: 1.0

                        val amountInEUR = amount / fromRate
                        val convertedAmount = amountInEUR * toRate


                        textViewResult.text =
                            String.format("Converted Amount: %.2f %s", convertedAmount, toCurrency)
                    } else {
                        textViewResult.text = "Currency not available"
                    }
                } else {
                    println("API response unsuccessful: ${response2.code()} - ${response2.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadCurrencies() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getSymbols().execute() // Execute on IO thread
                }
                if (response.isSuccessful) {
                    val symbolsResponse = response.body()
                    symbolsResponse?.let {
                        val currencyCodes = it.symbols.keys.toList()
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_item,
                            currencyCodes
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerFrom.adapter = adapter
                        spinnerTo.adapter = adapter

                        spinnerFrom.setSelection(currencyCodes.indexOf("EUR"))
                        spinnerTo.setSelection(currencyCodes.indexOf("TRY"))
                    }


                } else {
                    Log.e("LoadSymbols", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}