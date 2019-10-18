package com.pablichjenkov.packnavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.pablichjenkov.packnav.NavHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var navHelper: NavHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.bottomTabA -> {

                    navHelper.navigateToFragment(
                        R.id.fragmentA,
                        FragmentA.Input("Activity", "Global")
                    )

                    true
                }

                R.id.bottomTabB -> {

                    navHelper.navigateToFragment(
                        R.id.fragmentB,
                        FragmentB.Input("Activity", "Global")
                    )

                    true
                }

                R.id.bottomTabC -> {

                    navHelper.navigateToFragment(
                        R.id.fragmentC,
                        FragmentC.Input("Activity", "Global")
                    )

                    true
                }

                else -> false
            }

        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        navHelper = NavHelper(this, navController)

    }


}
