package io.yoath.sports.model

import org.json.JSONObject

/**
 * Created by ChazzCoin : December 2020.
 */

class PaymentIntentJson(val amount:String, val description:String) {

    var json: JSONObject? = null
    var map = mutableMapOf<String,Any>()
    var currency: String = "usd"
    var metadata = mutableMapOf<String,String>()

    init {
        metadata.apply {
            this["description"] = description
        }
        map.apply {
            this["amount"] = amount
            this["currency"] = currency
            this["metadata"] = metadata
        }

        json = JSONObject(map.toString())

    }

}