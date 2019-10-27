package com.pablichjenkov.packnavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.pablichjenkov.packnav.NavHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var navHelper: NavHelper

    val stackA = Stack<Int>()
    val stackB = Stack<Int>()
    val stackC = Stack<Int>()

    var curStack: Stack<Int>? = null

    var ignoreBackStack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.bottomTabA -> {

                    curStack = stackA

                    if (stackA.isEmpty()) {
                        navHelper.navigateToRootFragment(
                            R.id.fragmentA,
                            FragmentA.Input("Activity", "Global")
                        )
                    } else {

                        navigateToCurrentStack(false)

                    }


                    true
                }

                R.id.bottomTabB -> {

                    curStack = stackB

                    if (stackB.isEmpty()) {
                        navHelper.navigateToRootFragment(
                            R.id.fragmentB,
                            FragmentB.Input("Activity", "Global")
                        )
                    } else {

                        navigateToCurrentStack(false)

                    }


                    true
                }

                R.id.bottomTabC -> {

                    curStack = stackC

                    if (stackC.isEmpty()) {
                        navHelper.navigateToRootFragment(
                            R.id.fragmentC,
                            FragmentC.Input("Activity", "Global")
                        )
                    } else {

                        navigateToCurrentStack(false)

                    }


                    true
                }

                else -> false
            }

        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            if (! ignoreBackStack) {
                curStack?.push(destination.id)
            }

            ignoreBackStack = false
        }

        navHelper = NavHelper(this, navController)

    }

    override fun onBackPressed() {

        if (! navigateToCurrentStack(true)) {
            finish()
        }

    }

    private fun navigateToCurrentStack(remove: Boolean): Boolean {

        return curStack?.let {

            return if (it.isNotEmpty()) {

                ignoreBackStack = true

                if (remove) {
                    navHelper.navigateToPathFragment(it.pop())
                } else {
                    navHelper.navigateToPathFragment(it.peek())
                }

                true

            } else false

        } ?: false

    }

}
