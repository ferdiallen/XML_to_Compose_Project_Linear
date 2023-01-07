package com.allen.browse.Data

import androidx.lifecycle.ViewModel

class VM: ViewModel() {
    var MeanX:Double = 0.0
    var MeanY:Double = 0.0
    var dx = mutableListOf<Double>()
    var dy = mutableListOf<Double>()
    var beta0 = 0.0
    var beta1= 0.0
    var a1 = 0.0
    var a2 = 0.0
    var variance = 0.0
    var covariance = 0.0
    var predicted = mutableListOf<Double>()

}