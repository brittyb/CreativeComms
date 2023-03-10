package com.example.creativecomms

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
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

class RequestActivity : AppCompatActivity() {
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var refImage : ImageView
    private var commID : String? = ""

    //user's id
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    lateinit var request : Request
    lateinit var user : User

    //UI elements
    private lateinit var profilePic : CircleImageView
    private lateinit var username : TextView
    private lateinit var rating : RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        var correctCommID : String = ""

        profilePic = findViewById<CircleImageView>(R.id.profileImage)
        username = findViewById<TextView>(R.id.usernameText)
        rating = findViewById<RatingBar>(R.id.ratingBar)
        val title = findViewById<TextView>(R.id.commTitleText)
        val price = findViewById<EditText>(R.id.priceRequestText)
        price.addDecimalLimiter()


        val addImageButton = findViewById<Button>(R.id.addRefButton)
        val requestButton = findViewById<Button>(R.id.requestButton)
        val errorText = findViewById<TextView>(R.id.errorText)
        val description = findViewById<EditText>(R.id.infoText)
        refImage = findViewById<ImageView>(R.id.referenceImage)

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

        val extras = intent
        if(extras !=null){
            correctCommID = extras.getStringExtra("ID").toString()
            val database2 = FirebaseDatabase.getInstance().getReference("/Commissions")

            val commListener = object:ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for(userSnapshot in dataSnapshot.children){
                        val commission = userSnapshot
                        //iterate through all commissions in each uid and add to list
                        for(userSnapshot in commission.children){
                            val comm = userSnapshot.getValue(Commission::class.java)
                            if(comm?.commID == correctCommID){
                                title.text = comm.title
                            }
                        }


                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            database2.addListenerForSingleValueEvent(commListener)
        }

        addImageButton.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        requestButton.setOnClickListener(){
            if(validateFields(price.text, errorText) == true){
                val commUID = extras.getStringExtra("commUID")
                request = Request(description.text.toString(), imageUri.toString(), correctCommID, uid,
                    commUID, price.text.toString().toDouble(), true, false, false)
                var ref = FirebaseDatabase.getInstance().getReference("/Requests/$uid/")
                ref = ref.push()
                ref.setValue(request)
                var refArtist = FirebaseDatabase.getInstance().getReference("/ArtistRequests/$commUID")
                refArtist = refArtist.push()
                refArtist.setValue(request)
                uploadImageToFirebaseStorage()
            }

        }
    }

    private fun validateFields(price : Editable, errorText: TextView) : Boolean{
        if(price.toString().isEmpty()){
            errorText.text = "Enter a price request"
            return false
        }
       return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            refImage.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if(imageUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("CommissionPics/$filename")
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                Log.d("RequestLog", "Successfully uploaded pfp image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RequestLog", "${it.toString()}")
                    savePictureToFirebase(it.toString())
                }
            }
    }

    private fun savePictureToFirebase(uri : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("Requests/$uid/one")
        ref.setValue(uri).addOnSuccessListener { Log.d("RequestLog", "Successfully set value") }
    }



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
}