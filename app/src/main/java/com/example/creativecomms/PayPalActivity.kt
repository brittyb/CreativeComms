package com.example.creativecomms

import android.app.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import java.math.BigDecimal


class PayPalActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.paypal_activity)
        var config:PayPalConfiguration ?=null

        config=PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId("AeSQAhrGexPCgXzpkxzXFMoFVhG0iSwb3n7wNfdnFFWAPIHPy9sNTgr61GphTIFbgTCXP1VeXgA8D-bF")
        getPayment(config)
    }


    private fun getPayment(config: PayPalConfiguration) {
        var amount = 0.0
        // Getting the amount from bundle
        val extras = intent
        if(extras!=null){
            amount = extras.getDoubleExtra("Price", 0.0)
        }

        // Creating a paypal payment on below line.
        val payment = PayPalPayment(
            BigDecimal(amount), "USD", "Commission Fees",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        // Creating Paypal Payment activity intent
        val intent = Intent(this, PaymentActivity::class.java)

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        // Putting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        val PAYPAL_REQUEST_CODE = 123
       startActivityForResult(intent, PAYPAL_REQUEST_CODE)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123) {
            if(resultCode==Activity.RESULT_OK) {

            }
        }
    }


}