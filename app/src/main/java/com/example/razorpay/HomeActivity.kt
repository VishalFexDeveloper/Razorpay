package com.example.razorpay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.razorpay.Modal.ApiResponse
import com.example.razorpay.Modal.GetItem
import com.example.razorpay.Modal.PutModal
import com.example.razorpay.databinding.ActivityHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class HomeActivity : AppCompatActivity(),ClickListener {
    lateinit var binding: ActivityHomeBinding
    private lateinit var putModal:PutModal
    var list = ArrayList<GetItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        performRequest()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.businessBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.addBtn.setOnClickListener {
            startActivity(Intent(this,PostActivity::class.java))
        }

    }


    //get method razorpay
    private fun performRequest() {
        val apiKey = "rzp_test_IHo8oE7yKPbPb7"
        val apiSecret = "2kvUwgEIB8ROWYfYjuRIXTQz"
        val credentials = Credentials.basic(apiKey, apiSecret)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.razorpay.com/v1/customers")
            .get()
            .header("Authorization", credentials)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                // Handle successful response
                if (response.isSuccessful) {
                    val gson = Gson()
                    val itemType = object : TypeToken<ApiResponse<GetItem>>() {}.type
                    val apiResponse: ApiResponse<GetItem> = gson.fromJson(responseBody, itemType)
                    val userList = apiResponse.items
                    runOnUiThread {
                        // Update UI on the main thread
                        list.addAll(userList)
                        binding.recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
                        binding.recyclerView.adapter = USerAdapter(this@HomeActivity, userList,this@HomeActivity)
                    }
                } else {
                    // Handle unsuccessful response
                    runOnUiThread {
                        Toast.makeText(this@HomeActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Failure", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    //put method razorpay as update dialog box
    @SuppressLint("MissingInflatedId")
    private fun putDialogBox(position: Int){
        val alertDialog = AlertDialog.Builder(this).create()
        val layoutInflater = LayoutInflater.from(this).inflate(R.layout.post_layout_dailog,null,false)
        val editTextName = layoutInflater.findViewById<EditText>(R.id.putName)
        val editEmail = layoutInflater.findViewById<EditText>(R.id.putEmail)
        val editTextNumber = layoutInflater.findViewById<EditText>(R.id.putNumber)
        val postButton = layoutInflater.findViewById<Button>(R.id.putBtn)
        editEmail.setText(list[position].email)
        editTextName.setText(list[position].name)
        editTextNumber.setText(list[position].contact)
        alertDialog.setView(layoutInflater)
        postButton.setOnClickListener {
            putModal = PutModal(editTextNumber.text.toString(),editEmail.text.toString(),editTextName.text.toString())
            performRequestPut(list[position].id)
            alertDialog.dismiss()
        }
        alertDialog.show()

    }
    //put method razorpay as update dialog box
    private fun performRequestPut(id:String) {
        val apiKey = "rzp_test_IHo8oE7yKPbPb7"
        val apiSecret = "2kvUwgEIB8ROWYfYjuRIXTQz"
        val credentials = Credentials.basic(apiKey, apiSecret)

        val client = OkHttpClient()
        val json = Gson().toJson(putModal)

        val request = Request.Builder()
            .url("https://api.razorpay.com/v1/customers/$id")
            .put(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .header("Authorization", credentials)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
               if (response.isSuccessful){
                   performRequest()
               }

            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Failure", Toast.LENGTH_LONG).show()
                }
            }
        })
    }



    override fun putData(position: Int) {
        putDialogBox(position)
    }
}