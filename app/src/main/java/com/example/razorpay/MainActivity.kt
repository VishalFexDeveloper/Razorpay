package com.example.razorpay

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.razorpay.Modal.MyData
import com.example.razorpay.Modal.Notes
import com.google.gson.Gson
import com.razorpay.Checkout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    lateinit var editText: EditText
    lateinit var getText:TextView
    private lateinit var userData: MyData
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.buyBtn)
        editText = findViewById(R.id.amountEDit)
        getText = findViewById(R.id.gettext)
        val postBtn:Button = findViewById(R.id.RazorpayBtn)
        postBtn.setOnClickListener {
            postDailogBox()
        }

        listener()
        performRequest()
    }

    @SuppressLint("MissingInflatedId")
    private fun postDailogBox() {
     val alertDialog = AlertDialog.Builder(this).create()
        val layoutInflater = LayoutInflater.from(this).inflate(R.layout.post_layout_dailog,null,false)
       val editTextName = layoutInflater.findViewById<EditText>(R.id.putName)
        val editEmail = layoutInflater.findViewById<EditText>(R.id.putEmail)
        val editTextNumber = layoutInflater.findViewById<EditText>(R.id.putNumber)
        val postButton = layoutInflater.findViewById<Button>(R.id.putBtn)
        alertDialog.setView(layoutInflater)
        postButton.setOnClickListener {
            userData = MyData("${editTextNumber.text}","${editEmail.text}","1","12ABCDE2356F7GH","${editTextName.text}",
                Notes("Tea, Earl Grey, Hot","Tea, Earl Grey… decaf.")
            )
            alertDialog.dismiss()
            performRequestPost()
        }
        alertDialog.show()
    }

    private fun startPayment() {
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "vishal kumar")
            options.put("description", "Service Charge")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", R.drawable.img_1)
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            val payment: String = editText.getText().toString()
            var total = payment.toDouble()
            total *= 100
            options.put("amount", total)

            val preFill = JSONObject()
            preFill.put("email", "vishalfexvishal@gmail.com")
            preFill.put("contact", "8877310473")

            options.put("prefill", preFill)
            co.open(activity, options)
        }catch (e: Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun listener(){
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (editText.getText().toString() == "") {
                    Toast.makeText(this@MainActivity, "Please fill payment", Toast.LENGTH_LONG).show()
                    return
                }
                startPayment()
            }
        })
    }

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
                runOnUiThread {
                    // Update UI on the main thread
                    getText.text = responseBody ?: "Response body is null"
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Failure", Toast.LENGTH_LONG).show()

            }
        })
    }


    private fun performRequestPost() {
        val apiKey = "rzp_test_IHo8oE7yKPbPb7"
        val apiSecret = "2kvUwgEIB8ROWYfYjuRIXTQz"
        val credentials = Credentials.basic(apiKey, apiSecret)

        val client = OkHttpClient()
        val json = Gson().toJson(userData)

        val request = Request.Builder()
            .url("https://api.razorpay.com/v1/customers")
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .header("Authorization", credentials)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                // Handle successful response
                if (response.isSuccessful) {
                    runOnUiThread {
                        getText.text = responseBody ?: "Response body is null"
                        Toast.makeText(this@MainActivity, "Customer created successfully", Toast.LENGTH_SHORT).show()

                    }
                    // Optionally, you can perform additional actions here
                } else {
                    // Handle unsuccessful response
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Failed to create customer", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failure", Toast.LENGTH_LONG).show()
                }
            }
        })
    }




}