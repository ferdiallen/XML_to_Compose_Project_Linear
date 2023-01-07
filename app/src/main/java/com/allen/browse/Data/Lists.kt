package com.allen.browse.Data

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.allen.browse.MainActivity

data class Lists(
    var predict:Double
)

fun getpredict(data:List<Double>):List<Lists>{
    val mydata = mutableListOf<Lists>()
    data.forEach {
        mydata+=Lists(it)
    }
 return mydata
}
