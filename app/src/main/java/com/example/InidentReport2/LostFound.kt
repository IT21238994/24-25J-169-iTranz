/*package com.example.inidentreport2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LostFound : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lost_found) // Load the lost_found.xml layout

        val inputDescription = findViewById<EditText>(R.id.editTextDescription)
        val inputType = findViewById<EditText>(R.id.editTextItemType)
        val buttonMatch = findViewById<Button>(R.id.buttonMatch)
        val resultText = findViewById<TextView>(R.id.textViewResult)

        buttonMatch.setOnClickListener {
            val description = inputDescription.text.toString()
            val itemType = inputType.text.toString()

            if (description.isEmpty() || itemType.isEmpty()) {
                resultText.text = "Please fill all fields."
                return@setOnClickListener
            }

            val request = ItemRequest(description, itemType)

            RetrofitClient.instance.matchItem(request).enqueue(object : Callback<MatchResponse> {
                override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                    if (response.isSuccessful) {
                        val match = response.body()
                        resultText.text = match?.found_item_description ?: match?.message ?: "No match found."
                    } else {
                        resultText.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<MatchResponse>, t: Throwable) {
                    resultText.text = "Request failed: ${t.message}"
                }
            })
        }
    }
}

 */


/*package com.example.inidentreport2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LostFound : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lost_found) // Load the lost_found.xml layout

        val inputDescription = findViewById<EditText>(R.id.editTextDescription)
        val inputType = findViewById<EditText>(R.id.editTextItemType)
        val buttonMatch = findViewById<Button>(R.id.buttonMatch)
        val resultText = findViewById<TextView>(R.id.textViewResult)

        buttonMatch.setOnClickListener {
            val description = inputDescription.text.toString()
            val itemType = inputType.text.toString()

            if (description.isEmpty() || itemType.isEmpty()) {
                resultText.text = "Please fill all fields."
                return@setOnClickListener
            }

            // Create a request object with description and item type (Lost or Found)
            val request = ItemRequest(description, itemType)

            // Call the Retrofit API to match the item
            RetrofitClient.instance.matchItem(request).enqueue(object : Callback<MatchResponse> {
                override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                    if (response.isSuccessful) {
                        val match = response.body()

                        // Check if the user submitted a Lost or Found item
                        if (itemType.equals("Lost", ignoreCase = true)) {
                            // If Lost item, show the Found item details if a match is found
                            resultText.text = match?.found_item_description ?: match?.message ?: "No Found item match found."
                        } else if (itemType.equals("Found", ignoreCase = true)) {
                            // If Found item, show the Lost item details if a match is found
                            resultText.text = match?.lost_item_description ?: match?.message ?: "No Lost item match found."
                        } else {
                            resultText.text = "Invalid item type. Please use 'Lost' or 'Found'."
                        }

                    } else {
                        resultText.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<MatchResponse>, t: Throwable) {
                    resultText.text = "Request failed: ${t.message}"
                }
            })
        }
    }
}

 */

package com.example.inidentreport2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LostFound : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lost_found) // Load the lost_found.xml layout

        val inputDescription = findViewById<EditText>(R.id.editTextDescription)
        val radioGroupItemType = findViewById<RadioGroup>(R.id.radioGroupItemType)
        val buttonMatch = findViewById<Button>(R.id.buttonMatch)
        val resultText = findViewById<TextView>(R.id.textViewResult)

        buttonMatch.setOnClickListener {
            val description = inputDescription.text.toString()

            // Check for description input
            if (description.isEmpty()) {
                resultText.text = "Please fill in the item description."
                return@setOnClickListener
            }

            // Get the selected item type (Lost or Found)
            val selectedRadioButtonId = radioGroupItemType.checkedRadioButtonId
            val itemType = if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                selectedRadioButton.text.toString()
            } else {
                resultText.text = "Please select 'Lost' or 'Found'."
                return@setOnClickListener
            }

            // Save the description to Firebase Firestore with appropriate field names
            val itemData = hashMapOf<String, String>("description" to description)

            if (itemType == "Lost") {
                db.collection("LostItems")
                    .add(hashMapOf("lost_item_description" to description))
                    .addOnSuccessListener {
                        resultText.text = "Lost item description saved successfully!"
                    }
                    .addOnFailureListener { e ->
                        resultText.text = "Error saving to LostItems: ${e.message}"
                    }
            } else if (itemType == "Found") {
                db.collection("FoundItems")
                    .add(hashMapOf("found_item_description" to description))
                    .addOnSuccessListener {
                        resultText.text = "Found item description saved successfully!"
                    }
                    .addOnFailureListener { e ->
                        resultText.text = "Error saving to FoundItems: ${e.message}"
                    }
            }

            // Create ItemRequest object to send to the API
            val request = ItemRequest(description, itemType)

            // Make the API call to match the item
            RetrofitClient.instance.matchItem(request).enqueue(object : Callback<MatchResponse> {
                override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                    if (response.isSuccessful) {
                        val match = response.body()
                        resultText.text = match?.found_item_description ?: match?.lost_item_description
                                ?: match?.message ?: "No match found."
                    } else {
                        resultText.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<MatchResponse>, t: Throwable) {
                    resultText.text = "Request failed: ${t.message}"
                }
            })
        }
    }
}

