package com.example.creativecomms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.w3c.dom.Text

class SearchActivity : AppCompatActivity() {
    private lateinit var searchButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        Log.d("SearchActivityLog", "on search activity")
        searchButton=findViewById(R.id.searchButton)

        val username : EditText = findViewById(R.id.usernameSearchText)
        val rating : Float = 0.0f
        val title : EditText = findViewById(R.id.searchTitleText)
        val min : EditText = findViewById(R.id.minPriceSearchText)
        val max : EditText = findViewById(R.id.maxPriceSearchText)
        val medium : EditText = findViewById(R.id.mediumSearchText)
        val tag : EditText = findViewById(R.id.tagSearchText)
        val error : TextView = findViewById(R.id.errorMessageText)

        val userText = username.text
        val titleText = title.text
        val minText = min.text
        val maxText = max.text
        val medText = medium.text
        val tagText = tag.text


        //validate fields when search button is clicked
        searchButton.setOnClickListener{
            validateFields(userText, rating, titleText, minText, maxText, medText, tagText, error)
        }
    }


    //class for search filters selected
    class SearchFilters : java.io.Serializable{
        var username:String = ""
        var rating:Float =0.0f
        var title:String = ""
        var min:Double = 0.0
        var max:Double = 0.0
        var medium:String = ""
        var tag:String=""

        constructor(username:String, rating:Float, title:String, min:Double, max:Double, medium:String, tag:String){
            this.username=username
            this.rating=rating
            this.title=title
            this.min=min
            this.max = max
            this.medium = medium
            this.tag = tag
        }
    }



    private fun validateFields(userText:Editable, rating: Float, titleText:Editable, minText:Editable, maxText:Editable,
                               mediumText:Editable, tagText:Editable, errorText:TextView) : Boolean{
        //convert editables to strings
        var user = userText.toString()
        val rating = rating
        var title = titleText.toString()
        var min = minText.toString()
        var max = maxText.toString()
        var medium = mediumText.toString()
        var tag = tagText.toString()

        //check if there are search fields
        if(user.isEmpty() && title.isEmpty() && minText.isEmpty() && maxText.isEmpty() &&
                mediumText.isEmpty() && tagText.isEmpty()){
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("search", true)
            //indicate that the fields are empty
            intent.putExtra("searchEmpty", true)
            startActivity(intent)
            return false
        }

        //check if min and max are empty before converting to doubles
        if(min.isNotEmpty() && maxText.isNotEmpty()){
            if(min.toDouble() == 0.00 || max.toDouble() == 0.00){
                errorText.text = "Minimum and maximum values cannot be 0.0"
                return false
            }

            if(min.toDouble() > max.toDouble()){
                errorText.text = "Maximum value cannot be lower than minimum value"
                return false
            }
        }

        //initialize empty fields to put into searchFilters class
        if(user.isEmpty()){
            user = ""
        }
        if(title.isEmpty()){
            title = ""
        }
        if(min.isEmpty()){
            min = "0.00"
        }
        if(max.isEmpty()){
            max = "0.00"
        }
        if(medium.isEmpty()){
            medium = ""
        }
        if(tag.isEmpty()){
            tag = ""
        }
    //call create search filters function
        createSearchFilters(user, rating, title, min.toDouble(), max.toDouble(), medium, tag)
        return true
    }

    //create filters and put in intent to go back to results page
    private fun createSearchFilters(user : String, rating: Float, title : String, min : Double, max : Double,
                                    medium: String, tag:String){
        //go to home activity
        val intent = Intent(this, HomeActivity::class.java)
        //indicate that the fields are not empty
        intent.putExtra("empty", false)
        //indicate that the home page should go to the search results fragment
        intent.putExtra("search", true)
        //pass filters class to home activity
        intent.putExtra("filters",SearchFilters(user, rating,title,min,max,medium,tag))
        startActivity(intent)
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
}