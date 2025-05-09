/*
package com.example.inidentreport2 // Ensure your package matches

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Button
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ensure this line is before any findViewById

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group) // Initialize the RadioGroup
        //Initialize the EditText
        val etDate = findViewById<EditText>(R.id.et_date) // Initialize the Date EditText
        val etTime = findViewById<EditText>(R.id.et_time) // Initialize the Time EditText
        val etBusNumber =
            findViewById<EditText>(R.id.et_bus_number) // Initialize the Bus Number EditText
        val buttonSubmit =
            findViewById<Button>(R.id.et_button_submit) // Initialize the Submit Button

        buttonSubmit.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                // Handle error: No radio button is selected
                return@setOnClickListener
            }

            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val category = selectedRadioButton.text.toString()

            //val route = etRoute.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val busNumber = etBusNumber.text.toString()

            // Check for empty fields and handle errors
            if (date.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                // Handle error (e.g., show a Toast message or alert)
                return@setOnClickListener
            }

            // Prepare the email address based on selected category
            val email = when (category) {
                "Sexual Harassment" -> "1@email.com"
                "Overcrowding" -> "2@email.com"
                "Reckless Driving" -> "3@email.com"
                "Pitpocketing or Theft" -> "4@email.com"
                "Overcharging by conductors" -> "5@email.com"
                "Unsanitary Conditions" -> "6@email.com"
                "Driver or conductor misconduct" -> "7@email.com"
                else -> "support@email.com"
            }

            // Create the email intent
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Incident Report: $category")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Date: $date\nTime: $time\nBus Number: $busNumber"
            )
            startActivity(intent)
        }
    }
}

 */

/*package com.example.inidentreport2

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etTime = findViewById<EditText>(R.id.et_time)
        val etBusNumber = findViewById<EditText>(R.id.et_bus_number)
        val buttonSubmit = findViewById<Button>(R.id.et_button_submit)

        buttonSubmit.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an incident type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val busNumber = etBusNumber.text.toString()

            if (date.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Please enter a valid date (DD/MM/YYYY)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTime(time)) {
                Toast.makeText(this, "Please enter a valid time (HH:MM)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showReportOptionsDialog(selectedRadioButtonId, date, time, busNumber)
        }
    }

    // Function to validate date format (DD/MM/YYYY) and actual date validity
    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.isLenient = false // Disallow invalid dates like 31/02/2023
        return try {
            dateFormat.parse(date) != null
        } catch (e: Exception) {
            false
        }
    }

    // Function to validate time format (HH:MM) and actual time validity
    private fun isValidTime(time: String): Boolean {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        timeFormat.isLenient = false // Disallow invalid times like 25:61
        return try {
            timeFormat.parse(time) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun showReportOptionsDialog(
        selectedRadioButtonId: Int,
        date: String,
        time: String,
        busNumber: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_report_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val category = selectedRadioButton.text.toString()

        val email = when (category) {
            "Sexual Harassment" -> "1@email.com"
            "Overcrowding" -> "2@email.com"
            "Reckless Driving" -> "3@email.com"
            "Pitpocketing or Theft" -> "4@email.com"
            "Overcharging by conductors" -> "5@email.com"
            "Unsanitary Conditions" -> "6@email.com"
            "Driver or conductor misconduct" -> "7@email.com"
            else -> "support@email.com"
        }

        dialogView.findViewById<Button>(R.id.btn_call_authorities).setOnClickListener {
            val phoneNumber = when (category) {
                "Sexual Harassment" -> "1929"
                "Overcrowding" -> "1919"
                "Reckless Driving" -> "119"
                "Pitpocketing or Theft" -> "118"
                else -> "911"
            }
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_report_via_email).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, category)
                putExtra(
                    Intent.EXTRA_TEXT, """
                    Incident Category: $category
                    Date: $date
                    Time: $time
                    Bus Number: $busNumber
                """.trimIndent()
                )
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
            dialog.dismiss()
        }

        dialog.show()
    }
}

 */

/*package com.example.inidentreport2

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etTime = findViewById<EditText>(R.id.et_time)
        val etBusNumber = findViewById<EditText>(R.id.et_bus_number)
        val buttonSubmit = findViewById<Button>(R.id.et_button_submit)

        // Set the current date and time into the EditTexts
        setCurrentDateAndTime(etDate, etTime)

        buttonSubmit.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an incident type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val busNumber = etBusNumber.text.toString()

            if (date.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Please enter a valid date (DD/MM/YYYY)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTime(time)) {
                Toast.makeText(this, "Please enter a valid time (hh:mm AM/PM)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showReportOptionsDialog(selectedRadioButtonId, date, time, busNumber)
        }
    }

    // Function to set the current date and time into the EditText fields
    private fun setCurrentDateAndTime(etDate: EditText, etTime: EditText) {
        val calendar = Calendar.getInstance()

        // Format the date in the format DD/MM/YYYY
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(calendar.time)
        etDate.setText(date)

        // Format the time in 12-hour format with AM/PM
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val time = timeFormat.format(calendar.time)
        etTime.setText(time)
    }

    // Function to validate date format (DD/MM/YYYY) and actual date validity
    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.isLenient = false // Disallow invalid dates like 31/02/2023
        return try {
            dateFormat.parse(date) != null
        } catch (e: Exception) {
            false
        }
    }

    // Function to validate time format (hh:mm AM/PM) and actual time validity
    private fun isValidTime(time: String): Boolean {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        timeFormat.isLenient = false // Disallow invalid times like 25:61 AM
        return try {
            timeFormat.parse(time) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun showReportOptionsDialog(
        selectedRadioButtonId: Int,
        date: String,
        time: String,
        busNumber: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_report_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val category = selectedRadioButton.text.toString()

        val email = when (category) {
            "Sexual Harassment" -> "1@email.com"
            "Overcrowding" -> "2@email.com"
            "Reckless Driving" -> "3@email.com"
            "Pitpocketing or Theft" -> "4@email.com"
            "Overcharging by conductors" -> "5@email.com"
            "Unsanitary Conditions" -> "6@email.com"
            "Driver or conductor misconduct" -> "7@email.com"
            else -> "support@email.com"
        }

        dialogView.findViewById<Button>(R.id.btn_call_authorities).setOnClickListener {
            val phoneNumber = when (category) {
                "Sexual Harassment" -> "1929"
                "Overcrowding" -> "1919"
                "Reckless Driving" -> "119"
                "Pitpocketing or Theft" -> "118"
                else -> "911"
            }
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_report_via_email).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, category)
                putExtra(
                    Intent.EXTRA_TEXT, """
                    Incident Category: $category
                    Date: $date
                    Time: $time
                    Bus Number: $busNumber
                """.trimIndent()
                )
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
            dialog.dismiss()
        }

        dialog.show()
    }
}

 */



/*package com.example.inidentreport2

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etTime = findViewById<EditText>(R.id.et_time)
        val etBusNumber = findViewById<EditText>(R.id.et_bus_number)
        val buttonSubmit = findViewById<Button>(R.id.et_button_submit)

        // Set the current date and time into the EditTexts
        setCurrentDateAndTime(etDate, etTime)

        buttonSubmit.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an incident type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val busNumber = etBusNumber.text.toString()

            if (date.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Please enter a valid date (DD/MM/YYYY)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTime(time)) {
                Toast.makeText(this, "Please enter a valid time (hh:mm AM/PM)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected category from the RadioGroup
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val category = selectedRadioButton.text.toString()

            // Create IncidentReport object
            val report = IncidentReport(category, date, time, busNumber)

            // Save the report to Firestore
            db.collection("IncidentReports")
                .add(report)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show()
                    showReportOptionsDialog(category, date, time, busNumber)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error submitting report", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to set the current date and time into the EditText fields
    private fun setCurrentDateAndTime(etDate: EditText, etTime: EditText) {
        val calendar = Calendar.getInstance()

        // Format the date in the format DD/MM/YYYY
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(calendar.time)
        etDate.setText(date)

        // Format the time in 12-hour format with AM/PM
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val time = timeFormat.format(calendar.time)
        etTime.setText(time)
    }

    // Function to validate date format (DD/MM/YYYY) and actual date validity
    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.isLenient = false // Disallow invalid dates like 31/02/2023
        return try {
            dateFormat.parse(date) != null
        } catch (e: Exception) {
            false
        }
    }

    // Function to validate time format (hh:mm AM/PM) and actual time validity
    private fun isValidTime(time: String): Boolean {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        timeFormat.isLenient = false // Disallow invalid times like 25:61 AM
        return try {
            timeFormat.parse(time) != null
        } catch (e: Exception) {
            false
        }
    }

    // Function to show the report options dialog
    private fun showReportOptionsDialog(
        category: String,
        date: String,
        time: String,
        busNumber: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_report_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val email = when (category) {
            "Sexual Harassment" -> "1@email.com"
            "Overcrowding" -> "2@email.com"
            "Reckless Driving" -> "3@email.com"
            "Pitpocketing or Theft" -> "4@email.com"
            "Overcharging by conductors" -> "5@email.com"
            "Unsanitary Conditions" -> "6@email.com"
            "Driver or conductor misconduct" -> "7@email.com"
            else -> "support@email.com"
        }

        dialogView.findViewById<Button>(R.id.btn_call_authorities).setOnClickListener {
            val phoneNumber = when (category) {
                "Sexual Harassment" -> "1929"
                "Overcrowding" -> "1919"
                "Reckless Driving" -> "119"
                "Pitpocketing or Theft" -> "118"
                else -> "911"
            }
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_report_via_email).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, category)
                putExtra(
                    Intent.EXTRA_TEXT, """
                    Incident Category: $category
                    Date: $date
                    Time: $time
                    Bus Number: $busNumber
                """.trimIndent()
                )
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
            dialog.dismiss()
        }

        dialog.show()
    }
}

// Data class for IncidentReport
data class IncidentReport(
    val category: String,
    val date: String,
    val time: String,
    val busNumber: String
)


 */

package com.example.inidentreport2

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.firestore // Initialize Firestore instance

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etTime = findViewById<EditText>(R.id.et_time)
        val etBusNumber = findViewById<EditText>(R.id.et_bus_number)
        val buttonSubmit = findViewById<Button>(R.id.et_button_submit)
        




        setCurrentDateAndTime(etDate, etTime)

        buttonSubmit.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an incident type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener



            }


            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val busNumber = etBusNumber.text.toString()

            if (date.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Please enter a valid date (DD/MM/YYYY)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTime(time)) {
                Toast.makeText(this, "Please enter a valid time (hh:mm AM/PM)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val category = selectedRadioButton.text.toString()

            // Send data to Firebase Firestore
            val incidentData = mapOf(
                "category" to category,
                "date" to date,
                "time" to time,
                "busNumber" to busNumber
            )

            db.collection("IncidentReports")
                .add(incidentData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to submit report: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            showReportOptionsDialog(selectedRadioButtonId, date, time, busNumber)
        }
    }

    private fun setCurrentDateAndTime(etDate: EditText, etTime: EditText) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        etDate.setText(dateFormat.format(calendar.time))
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        etTime.setText(timeFormat.format(calendar.time))
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidTime(time: String): Boolean {
        return try {
            SimpleDateFormat("hh:mm a", Locale.US).apply { isLenient = false }.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showReportOptionsDialog(
        selectedRadioButtonId: Int,
        date: String,
        time: String,
        busNumber: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_report_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val category = selectedRadioButton.text.toString()

        val email = when (category) {
            "Sexual Harassment" -> "1@email.com"
            "Overcrowding" -> "2@email.com"
            "Reckless Driving" -> "3@email.com"
            "Pitpocketing or Theft" -> "4@email.com"
            "Overcharging by conductors" -> "5@email.com"
            "Unsanitary Conditions" -> "6@email.com"
            "Driver or conductor misconduct" -> "7@email.com"
            else -> "support@email.com"
        }

        dialogView.findViewById<Button>(R.id.btn_call_authorities).setOnClickListener {
            val phoneNumber = when (category) {
                "Sexual Harassment" -> "1929"
                "Overcrowding" -> "1919"
                "Reckless Driving" -> "119"
                "Pitpocketing or Theft" -> "118"
                else -> "911"
            }
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_report_via_email).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, category)
                putExtra(
                    Intent.EXTRA_TEXT, """
                    Incident Category: $category
                    Date: $date
                    Time: $time
                    Bus Number: $busNumber
                """.trimIndent()
                )
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
            dialog.dismiss()
        }

        dialog.show()
    }
}




/*package com.example.inidentreport2

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.firestore // Initialize Firestore instance

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etTime = findViewById<EditText>(R.id.et_time)
        val etBusNumber = findViewById<EditText>(R.id.et_bus_number)
        val buttonSubmit = findViewById<Button>(R.id.et_button_submit)

       val btnMyReports = findViewById<Button>(R.id.btn_my_reports)
        btnMyReports.setOnClickListener {
            // Navigate to the item_incident_report activity
            val intent = Intent(this, IncidentReportAdapter::class.java)
            startActivity(intent)
        }

        setCurrentDateAndTime(etDate, etTime)

        buttonSubmit.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an incident type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val busNumber = etBusNumber.text.toString()

            if (date.isEmpty() || time.isEmpty() || busNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Please enter a valid date (DD/MM/YYYY)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTime(time)) {
                Toast.makeText(this, "Please enter a valid time (hh:mm AM/PM)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val category = selectedRadioButton.text.toString()

            // Send data to Firebase Firestore
            val incidentData = mapOf(
                "category" to category,
                "date" to date,
                "time" to time,
                "busNumber" to busNumber
            )

            db.collection("IncidentReports")
                .add(incidentData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to submit report: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            showReportOptionsDialog(selectedRadioButtonId, date, time, busNumber)
        }
    }

    private fun setCurrentDateAndTime(etDate: EditText, etTime: EditText) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        etDate.setText(dateFormat.format(calendar.time))
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        etTime.setText(timeFormat.format(calendar.time))
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.US).apply { isLenient = false }.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidTime(time: String): Boolean {
        return try {
            SimpleDateFormat("hh:mm a", Locale.US).apply { isLenient = false }.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showReportOptionsDialog(
        selectedRadioButtonId: Int,
        date: String,
        time: String,
        busNumber: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_report_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val category = selectedRadioButton.text.toString()

        val email = when (category) {
            "Sexual Harassment" -> "1@email.com"
            "Overcrowding" -> "2@email.com"
            "Reckless Driving" -> "3@email.com"
            "Pitpocketing or Theft" -> "4@email.com"
            "Overcharging by conductors" -> "5@email.com"
            "Unsanitary Conditions" -> "6@email.com"
            "Driver or conductor misconduct" -> "7@email.com"
            else -> "support@email.com"
        }

        dialogView.findViewById<Button>(R.id.btn_call_authorities).setOnClickListener {
            val phoneNumber = when (category) {
                "Sexual Harassment" -> "1929"
                "Overcrowding" -> "1919"
                "Reckless Driving" -> "119"
                "Pitpocketing or Theft" -> "118"
                else -> "911"
            }
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_report_via_email).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, category)
                putExtra(
                    Intent.EXTRA_TEXT, """
                    Incident Category: $category
                    Date: $date
                    Time: $time
                    Bus Number: $busNumber
                """.trimIndent()
                )
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
            dialog.dismiss()
        }

        dialog.show()
    }
}

 */

