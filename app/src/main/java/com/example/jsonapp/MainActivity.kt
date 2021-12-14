package com.example.jsonapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.jsonapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.full.memberProperties


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currencyList :ArrayList<String>
    private lateinit var allCurrency : Cur
    private var selected = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrency(result = {
            val currency = it

            binding.tvDate.text = "Data:"+ currency?.data
            allCurrency = currency?.eur!!

            currencyList = arrayListOf()
            Cur::class.memberProperties.forEach { member ->
                currencyList.add(member.name)
            }
            setupSpinner()
        })
        binding.buttonFrom.setOnClickListener { convertCurrency("from EURO") }
        binding.buttonTo.setOnClickListener { convertCurrency("to EURO") }

    }
    private fun getCurrency(result: (Currency?)-> Unit){
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
    apiInterface?.getCurrency()?.enqueue(object  : Callback<Currency>{
        override fun onResponse(call: Call<Currency>, response: Response<Currency>) {
            try {
                result(response.body()!!)

            }catch(e: Exception) {
                Log.d("Main","Error: $e")
            }
        }

        override fun onFailure(call: Call<Currency>, t: Throwable) {
            result(null)
        }
    })
    }

    private fun setupSpinner(){
        val spinner = binding.convertFrom
        if (spinner != null){
            val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,currencyList)
        spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
              selected = currencyList[position]
                binding.buttonFrom.text ="EURO to $selected".capitalize()
                binding.buttonTo.text = "From $selected to EURO".capitalize()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }
    private fun convertCurrency(convert: String) {
        var number = 0.0
        Cur::class.memberProperties.forEach { member ->
            if (member.name == selected) {
                number = member.get(allCurrency).toString().toDouble()
            }
        }
        val userInput = binding.currencyToBeConverted.text.toString().toDouble()

        if (convert == "from EURO") {
            val result = userInput * number
            binding.currencyConverted.text = "Result: $result $selected"
        } else {
            val result = userInput / number
            binding.currencyConverted.text = "Result: $result EURO"
        }
    }
}
