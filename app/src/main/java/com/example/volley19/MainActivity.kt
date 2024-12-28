package com.example.volley19

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.volley19.MODELS.Valyuta
import com.example.volley19.adapters.SpinnerAdapter
import com.example.volley19.adapters.ValyutaAdapter
import com.example.volley19.databinding.ActivityMainBinding
import com.example.volley19.utils.NetworkHelper
import android.view.View
import android.widget.AdapterView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    var url = "https://cbu.uz/uzc/arkhiv-kursov-valyut/json/"
    lateinit var networkHelper: NetworkHelper
    lateinit var list:List<Valyuta>
    lateinit var spinnerAdapter: SpinnerAdapter
    private val TAG = "MainActivity"
    lateinit var listNomi:ArrayList<String>
    lateinit var adapter: ValyutaAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkHelper = NetworkHelper(this)

        requestQueue = Volley.newRequestQueue(this)
        VolleyLog.DEBUG = true //qanday ma'lumot kelayotganini Logda ko'rsatib turadi

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {
                    var qiymat1 = 0f
                    var qiymat2 = 0f
                    var nomi1 = ""
                    var nomi2 = ""
                    val type = object : TypeToken<List<Valyuta>>(){}.type
                    list = Gson().fromJson<List<Valyuta>>(response.toString(), type)
                    adapter = ValyutaAdapter(list as ArrayList)
                    binding.rv.adapter = adapter

                    listNomi = ArrayList()

                    for (valyuta in list) {
                        listNomi.add(valyuta.Ccy)
                    }
                    spinnerAdapter = SpinnerAdapter(listNomi)
                    binding.valyuta1.adapter = spinnerAdapter
                    binding.valyuta2.adapter = spinnerAdapter

                    binding.valyuta1.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            qiymat1 = list[position].Rate.toFloat()
                            nomi1 = list[position].Ccy
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                    binding.valyuta2.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            qiymat2 = list[position].Rate.toFloat()
                            nomi2 = list[position].Ccy
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }

                    binding.btnConvertr.setOnClickListener {
                        if (binding.miqdor.text.isNotEmpty()){
                            val c = qiymat1/qiymat2
                            val result = c*binding.miqdor.text.toString().toFloat()
                            binding.javob.text = "%.3f".format(result)
                            binding.raqam1.text = binding.miqdor.text
                            binding.ccy1.text = nomi1
                            binding.ccy2.text = nomi2
                            binding.natija.visibility = View.VISIBLE

                        }
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }
            })

        //jsonArrayRequest.tag = "tag1" //tag berilyapti
        requestQueue.add(jsonArrayRequest)



//        requestQueue.cancelAll("tag1") // tag1 dagi so'rovlarni ortga qaytarish uchun

    }
}

