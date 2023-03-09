package com.example.creativecomms

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.media.Rating
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class CreateCommission : AppCompatActivity() {
    //pick image variables
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var imageUriText : String? = ""
    private lateinit var commPic : ImageView

    //user's id
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    //commission variable and user
    lateinit var commission : Commission
    lateinit var user : User
    //var selectedET : String = "Varies"

    //UI elements
    private lateinit var profilePic : CircleImageView
    private lateinit var username : TextView
    private lateinit var rating : RatingBar

    //int variable for number of current commissions

    private var numComms : Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_commission)


        profilePic = findViewById<CircleImageView>(R.id.profileImage)
        username = findViewById<TextView>(R.id.username_text)
        rating = findViewById<RatingBar>(R.id.ratingBar)


        //Get user information for pfp, username, and rating
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get User from firebase
                user = dataSnapshot.getValue(User::class.java)!!
                username.text=user.username.toString()


                //get profile uri
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImageUri.toString())
                Log.d("DatabaseLog", user.profileImageUri.toString())
                val MAX_SIZE = 1024*1024*5
                imageRef.getBytes(MAX_SIZE.toLong())
                    .addOnSuccessListener { data ->
                        // Convert the retrieved data to a Bitmap
                        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                        profilePic.setImageBitmap(bitmap)
                        // Use the bitmap as needed
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        Log.d("DatabaseLog", "Did not get bytes")
                    }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        database.addListenerForSingleValueEvent(userListener)


        //listener for number of comms in firebase
        val database2 = FirebaseDatabase.getInstance().getReference("/Commissions/$uid")
        val commListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Number of Commissions from firebase
                numComms = dataSnapshot.childrenCount.toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        database2.addListenerForSingleValueEvent(commListener)

        /*
        //Dropdown menu
        val dropdownItems = resources.getStringArray(R.array.EstimatedTimeOptions)
        val spinner = findViewById<Spinner>(R.id.spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, dropdownItems)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                        selectedET = spinner.selectedItem.toString()
                    Log.d("CreateCommission", selectedET)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

*/
        //get inputs
        val title = findViewById<EditText>(R.id.editTitle)
        val description = findViewById<EditText>(R.id.editDesc)
        val min = findViewById<EditText>(R.id.minPriceText)
        min.addDecimalLimiter()
        val max = findViewById<EditText>(R.id.maxPriceText)
        max.addDecimalLimiter()
        commPic = findViewById<ImageView>(R.id.commPic)
        val medium = findViewById<EditText>(R.id.mediumText)
        val tag1 = findViewById<EditText>(R.id.tag1Text)
        val tag2 = findViewById<EditText>(R.id.tag2Text)
        val days = findViewById<EditText>(R.id.daysText)

        val titleText = title.text
        val descriptionText = description.text

        val mediumText = medium.text
        val tag1Text = tag1.text
        val tag2Text = tag2.text

        val saveButton = findViewById<Button>(R.id.saveComm_btn)
        val imageButton = findViewById<Button>(R.id.btn_addImage)

        val errorMessage = findViewById<TextView>(R.id.createComm_error)
        imageUriText = imageUri.toString()

        saveButton.setOnClickListener{

            //validate entered fields when save button is clicked
            validateFields(titleText, descriptionText, mediumText, tag1Text, tag2Text,
                    imageUri, errorMessage, min.text, max.text, days.text)
        }

        imageButton.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


    }



    //Limit number inputs to 2 decimals

    fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {

        var str = string
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count{ ".".contains(it) }

        if (decimalCount > 1)
            return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }


    //Open gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            commPic.setImageURI(imageUri)
        }
    }

    //validate that all necessary fields have been filled out
    private fun validateFields(titleText : Editable, descriptionText : Editable, mediumText : Editable,
                               tag1Text : Editable, tag2Text : Editable, imageUri : Uri?,
                               errorMessage : TextView, min: Editable, max: Editable, days : Editable) : Boolean{
        if(titleText.isEmpty()){
            errorMessage.text = "Enter a Title"
            return false
        }

        if(descriptionText.isEmpty()){
            errorMessage.text = "Enter a Description"
            return false
        }

        if(mediumText.isEmpty()){
            errorMessage.text = "Enter a Medium"
            return false
        }
        if(tag1Text.isNotEmpty() && tag2Text.isNotEmpty()){
            if(tag1Text.toString() == tag2Text.toString()){
                errorMessage.text = "Tags must be different from each other"
                return false
            }
        }

        if(imageUri == null){
            errorMessage.text = "Must upload an image"
            return false
        }

        Log.d("CreateComms", min.toString())


        if(min.toString().isEmpty() || max.toString().isEmpty()){
            errorMessage.text = "Enter a minimum and maximum price value"
            return false
        }else{
            if(min.toString().toDouble() == 0.00 || max.toString().toDouble() == 0.00){
                errorMessage.text = "Minimum and maximum values cannot be 0.0"
                return false
            }

            if(min.toString().toDouble() > max.toString().toDouble()){
                errorMessage.text = "Maximum value cannot be lower or equal to minimum value"
                return false
            }
        }

        if(days.toString().isEmpty()){
            errorMessage.text = "Enter an estimated completion time"
            return false
        }

        createCommission(titleText.toString(), descriptionText.toString(), min.toString().toDouble(), max.toString().toDouble(),
        mediumText.toString(), tag1Text.toString(), tag2Text.toString(), imageUri.toString(), uid, days.toString().toInt())
        return true
    }


    private fun createCommission(title:String, description:String, min:Double, max:Double, medium:String, tag1:String, tag2:String, uri:String,
    uid:String, time:Int){
        //val
        numComms += 1
        val ref = FirebaseDatabase.getInstance().getReference("/Commissions/$uid/$numComms")
        commission = Commission(title, description, min, max, medium, tag1, tag2,uri,uid,time, "")
        ref.setValue(commission)
        val ref2 = FirebaseDatabase.getInstance().getReference("/Commissions/$uid/$numComms/commID")
        val id = ref2.push().key
        ref2.setValue(id)

        uploadImageToFirebaseStorage()


    }

    private fun uploadImageToFirebaseStorage() {
        if(imageUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("CommissionPics/$filename")
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                Log.d("EditProfileActivity", "Successfully uploaded pfp image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("EditProfileActivity", "${it.toString()}")
                    savePictureToFirebase(it.toString())
                }
            }
    }


    private fun savePictureToFirebase(uri : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("Commissions/$uid/$numComms/imageUri")
        ref.setValue(uri).addOnSuccessListener { Log.d("CreateComm", "Successfully set value") }
    }
}