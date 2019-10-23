package com.pablichjenkov.packnav

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import java.util.*


class NavHelper(
    activity: AppCompatActivity,
    private val navController: NavController
) {

    private val mailStore by activity.viewModels<MailStore>()


    fun navigateToRootFragment(
        nextFragmentGraphId: Int,
        inputPayload: Any? = null
    ) {

        val destination = try {

            navController.getBackStackEntry(nextFragmentGraphId)

        } catch (th: Throwable) { null }

        if (destination == null) {

            mailStore.depositInputMessageTo(
                nextFragmentGraphId,
                NavigationMessage.Input.NoReply(inputPayload)
            )

            navController.navigate(nextFragmentGraphId)

        } else {

            popInputsUntilBackStackDestination(nextFragmentGraphId)

            navController.popBackStack(nextFragmentGraphId, false)

        }

    }

    private fun popInputsUntilBackStackDestination(
        backStackFragmentGraphId: Int
    ) {

        val field = NavController::class.java.getDeclaredField("mBackStack")

        field.isAccessible = true

        val mBackStack = field.get(navController) as Deque<NavBackStackEntry>

        if (mBackStack.isEmpty()) {
            // Nothing to pop if the navController back stack is empty
            return
        }

        val iterator = mBackStack.descendingIterator()

        while (iterator.hasNext()) {

            val destination = iterator.next().destination

            val foundDestination = destination.id == backStackFragmentGraphId

            if (!foundDestination) {

                mailStore.getInputMessageInfoSentTo(destination.id, true)

            } else break

        }

    }

}