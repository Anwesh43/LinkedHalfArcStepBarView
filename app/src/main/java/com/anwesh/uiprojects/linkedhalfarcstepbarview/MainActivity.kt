package com.anwesh.uiprojects.linkedhalfarcstepbarview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.halfarcstepbarview.HalfArcStepBarView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HalfArcStepBarView.create(this)
    }
}
