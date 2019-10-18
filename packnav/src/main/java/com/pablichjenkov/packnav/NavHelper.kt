package com.pablichjenkov.packnav

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController


class NavHelper(
    activity: AppCompatActivity,
    private val navController: NavController
) {

    private val mailStore by activity.viewModels<MailStore>()


    fun navigateToFragment(
        nextFragmentGraphId: Int,
        inputPayload: Any? = null
    ) {

        mailStore.depositInputMessageTo(
            nextFragmentGraphId,
            NavigationMessage.Input.NoReply(inputPayload)
        )

        navController.navigate(nextFragmentGraphId)

    }


}