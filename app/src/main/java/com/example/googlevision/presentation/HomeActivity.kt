package com.example.googlevision.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.googlevision.R
import com.example.googlevision.util.hasAllNeededPermissions
import com.example.googlevision.util.requestPermissions
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        add_button.setOnClickListener{
            if(!this.hasAllNeededPermissions()){
                this.requestPermissions()
            }else{

            }
        }
    }
}
