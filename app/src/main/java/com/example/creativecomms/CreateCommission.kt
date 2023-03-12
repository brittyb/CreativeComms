package com.example.creativecomms

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
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

    //UI elements
    private lateinit var profilePic : CircleImageView
    private lateinit var username : TextView
    private lateinit var rating : RatingBar

    //int variable for number of current commissions




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_commission)


        profilePic = findViewById(R.id.profileImage)
        username = findViewById(R.id.username_text)
        rating = findViewById(R.id.ratingBar)


        //Get user information for pfp, username, and rating
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get User from firebase
                user = dataSnapshot.getValue(User::class.java)!!
                username.text=user.username.toString()


                Glide.with(applicationContext).load(user.profileImageUri).into(profilePic)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        database.addListenerForSingleValueEvent(userListener)


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
        commPic = findViewById(R.id.commPic)
        val medium = findViewById<EditText>(R.id.mediumText)
        val tag1 = findViewById<EditText>(R.id.tag1Text)
        val tag2 = findViewById<EditText>(R.id.tag2Text)
        val days = findViewById<EditText>(R.id.daysText)

        //get text inputs
        val titleText = title.text
        val descriptionText = description.text
        val mediumText = medium.text
        val tag1Text = tag1.text
        val tag2Text = tag2.text

        //buttons
        val saveButton = findViewById<Button>(R.id.saveComm_btn)
        val imageButton = findViewById<Button>(R.id.btn_addImage)
        val homeButton = findViewById<ImageView>(R.id.homeButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        //error message text view
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
            errorMessage.text = getString(R.string.title_error)
            return false
        }

        if(descriptionText.isEmpty()){
            errorMessage.text = getString(R.string.description_error)
            return false
        }

        if(mediumText.isEmpty()){
            errorMessage.text = getString(R.string.medium_error)
            return false
        }
        //make sure tags are not the same
        if(tag1Text.isNotEmpty() && tag2Text.isNotEmpty()){
            if(tag1Text.toString() == tag2Text.toString()){
                errorMessage.text = getString(R.string.tags_error)
                return false
            }
        }

        if(imageUri == null){
            errorMessage.text = getString(R.string.image_error)
            return false
        }

        Log.d("CreateComms", min.toString())


        if(min.toString().isEmpty() || max.toString().isEmpty()){
            errorMessage.text = getString(R.string.enter_prices_error)
            return false
        }else{
            //make sure prices are not 0
            if(min.toString().toDouble() == 0.00 || max.toString().toDouble() == 0.00){
                errorMessage.text = getString(R.string.zero_price_error)
                return false
            }

            //make sure min not bigger than max
            if(min.toString().toDouble() > max.toString().toDouble()){
                errorMessage.text = getString(R.string.max_lower_error)
                return false
            }
        }

        if(days.toString().isEmpty()){
            errorMessage.text = getString(R.string.time_error)
            return false
        }

        //create a commission object
        createCommission(titleText.toString(), descriptionText.toString(), min.toString().toDouble(), max.toString().toDouble(),
        mediumText.toString(), tag1Text.toString(), tag2Text.toString(), imageUri.toString(), uid, days.toString().toInt())
        return true
    }


    //create a commission and add to firebase
    private fun createCommission(title:String, description:String, min:Double, max:Double, medium:String, tag1:String, tag2:String, uri:String,
    uid:String, time:Int){

        val ref2 = FirebaseDatabase.getInstance().getReference("/Commissions/$uid")
        //get random commission id
        val id = ref2.push().key
        val ref = FirebaseDatabase.getInstance().getReference("/Commissions/$uid/$id")
        commission = Commission(title, description, min, max, medium, tag1, tag2,uri,uid,time, id)
        ref.setValue(commission)
        //upload commission image to storage
        uploadImageToFirebaseStorage(id.toString())


    }

    private fun uploadImageToFirebaseStorage(id:String) {
        if(imageUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("CommissionPics/$filename")
        ref.putFile(imageUri!!)
            .addOnSuccessListener { it ->
                Log.d("EditProfileActivity", "Successfully uploaded pfp image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("EditProfileActivity", it.toString())
                    savePictureToFirebase(it.toString(), id)
                }
            }

    }


    private fun savePictureToFirebase(uri : String, id : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("Commissions/$uid/$id/imageUri")
        ref.setValue(uri).addOnSuccessListener { Log.d("CreateComm", "Successfully set value")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)}
    }
}