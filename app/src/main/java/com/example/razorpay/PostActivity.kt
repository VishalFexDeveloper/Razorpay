package com.example.razorpay

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.razorpay.Modal.MyData
import com.example.razorpay.Modal.Notes
import com.example.razorpay.databinding.ActivityPutBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postBtnREz.setOnClickListener {
            performRequestPost()
        }

    }

    private fun performRequestPost() {

        val apiKey = "rzp_test_IHo8oE7yKPbPb7"
        val apiSecret = "2kvUwgEIB8ROWYfYjuRIXTQz"
        val credentials = Credentials.basic(apiKey, apiSecret)

        val userData =  MyData(binding.postNumber.text.toString(),binding.postEmail.text.toString(),"1","12ABCDE2356F7GH",binding.postName.text.toString(),
            Notes("Tea, Earl Grey, Hot","Tea, Earl Grey… decaf.")
        )

        val httpClient = OkHttpClient()
        val json = Gson().toJson(userData)

        val request = Request.Builder()
            .url("https://api.razorpay.com/v1/customers")
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .header("Authorization",credentials)
            .build()



        httpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                // Handle successful response
                if (response.isSuccessful) {
                    runOnUiThread {

                        Toast.makeText(this@PostActivity, "Customer created successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@PostActivity,HomeActivity::class.java))

                    }
                    // Optionally, you can perform additional actions here
                } else {
                    // Handle unsuccessful response
                    runOnUiThread {
                        Toast.makeText(this@PostActivity, "Failed to create customer", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@PostActivity,HomeActivity::class.java))
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@PostActivity, "Failure", Toast.LENGTH_LONG).show()
                }
            }
        })
    }



}