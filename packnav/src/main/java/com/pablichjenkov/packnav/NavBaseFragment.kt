package com.pablichjenkov.packnav

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


abstract class NavBaseFragment<VM> : Fragment() {

    private val viewModelsRegistry by activityViewModels<ViewModelRegistry>()

    private val mailStore by activityViewModels<MailStore>()

    private val navController by lazy { findNavController() }

    private val ownDestinationId: Int by lazy {

        navController.currentDestination?.id
            ?: throw Exception("No nav_graph ID assigned to ${javaClass.simpleName}")

    }

    private lateinit var backPressedCallback: OnBackPressedCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressedCallback =
            requireActivity()
                .onBackPressedDispatcher
                .addCallback(this) {

                    /**
                     * If handleBackNavigation()returns false which is the default if not override
                     * by a Subclass. The the behaviour will be popping this Fragment from the stack
                     * and delivering back a NavigationMessage.Result.Cancelled() message.
                     * */
                    if (!handleBackNavigation()) {

                        finishWithResult(NavigationMessage.Result.Cancelled())

                    }

                }

    }

    override fun onStart() {
        super.onStart()

        var inputMessage = incomingInputMessageInfo(false).inputMessage

        // If this Fragment was launched from the System or using the JetPack navigation methods
        // outside from this class. Then the Launching inputMessage will be null since
        // the input mailBox is empty.
        // However, we need to guarantee an Input Message for consistency of our API.
        // In such a case we inject an artificial NavigationMessage.Input.NoReply()
        // to act as if we were started that way.
        if (inputMessage == null) {

            inputMessage = NavigationMessage.Input.NoReply()

            mailStore.depositInputMessageTo(ownDestinationId, inputMessage)

        }

        var viewModel: VM = viewModelsRegistry.get(inputMessage)

        if (viewModel == null) {

            viewModel = createViewModel()

            if (viewModel != null) {

                viewModelsRegistry.put(inputMessage, viewModel)

            } else throw nullViewModelException

        }

        // If there is a Result message enqueued, pop it and start this Fragment as "OnResult" Mode.
        val resultMessage = incomingResultMessage()

        if (resultMessage != null) {

            onFragmentResult(viewModel, inputMessage, resultMessage)

        } else {

            // If there was no Result message enqueued, it means this Fragment was launched as Input Mode.
            // In this case, lets peek the input message from the Input queue and call the proper Callback.
            onFragmentStartWithInput(viewModel, inputMessage)

        }

    }

    protected abstract fun createViewModel(): VM

    protected abstract fun onFragmentStartWithInput(
        viewModel: VM,
        inputMessage: NavigationMessage.Input
    )

    protected abstract fun onFragmentResult(
        viewModel: VM,
        inputMessage: NavigationMessage.Input,
        resultMessage: NavigationMessage.Result
    )

    protected open fun handleBackNavigation(): Boolean = false

    protected open fun onBackStackEmpty(): Boolean = false

    private fun incomingInputMessageInfo(remove: Boolean): InputMessageInfo {

        return ownDestinationId.let { ownNavFragmentId ->

            mailStore.getInputMessageInfoSentTo(ownNavFragmentId, remove)

        }

    }

    private fun incomingResultMessage(): NavigationMessage.Result? {

        return ownDestinationId.let { ownNavFragmentId ->

            mailStore.getResultMessageSentTo(ownNavFragmentId)

        }

    }

    protected fun navigateToFragment(
        nextFragmentGraphId: Int,
        inputPayload: Any? = null
    ) {

        mailStore.depositInputMessageTo(
            nextFragmentGraphId,
            NavigationMessage.Input.NoReply(inputPayload)
        )

        navController.navigate(nextFragmentGraphId)

    }

    protected fun navigateToFragmentForResult(
        nextFragmentGraphId: Int,
        inputPayload: Any? = null
    ) {

        mailStore.depositInputMessageTo(
            nextFragmentGraphId,
            NavigationMessage.Input.ReplyTo(ownDestinationId, inputPayload)
        )

        navController.navigate(nextFragmentGraphId)

    }

    protected fun finishWithResult(resultMessage: NavigationMessage.Result): Boolean {

        // When delivering a Result to a Caller Fragment we want to remove the Input message from
        // the Queue.

        // When delivering a Result to a Caller Fragment we want to remove the Input message from
        // the Queue.
        // If this is the last message left in the Stack then we call back to the onBackStackEmpty()
        // method to allow the Subclass do whatever it wants. Usually close the App. Otherwise, we
        // inject an Artificial message in the Stack to keep it active.
        val inputMessageInfo = incomingInputMessageInfo(true)

        if (inputMessageInfo.isFirst) {

            if (onBackStackEmpty()) return true

            // The user did not consume it and we need to keep the inputMessage Mail Stack with at
            // least one message so our API works properly. So we inject an artificial one.
            mailStore.depositInputMessageTo(ownDestinationId, NavigationMessage.Input.NoReply())

            return true
        }

        return when (val replyTo = inputMessageInfo.inputMessage?.replyTo) {

            null -> navController.popBackStack()

            else -> {

                mailStore.depositResultMessageTo(replyTo, resultMessage)

                // It might happen that the replyTo is a Fragment of the same type as this.
                // In such a case we want to pop inclusive, so we pop all the way back to
                // the previous same type/id Fragment.
                val inclusive = replyTo == ownDestinationId

                navController.popBackStack(replyTo, inclusive)

            }

        }

    }

    protected fun finishWithResultAndBackStackDestination(
        backStackFragmentGraphId: Int,
        resultMessage: NavigationMessage.Result
    ): Boolean {

        // When delivering a Result to a Caller Fragment we want to remove the Input message from
        // the Queue.
        // If this is the last message left in the Stack then we call back to the onBackStackEmpty()
        // method to allow the Subclass do whatever it wants. Usually close the App. Otherwise, we
        // inject an Artificial message in the Stack to keep it active.
        val inputMessageInfo = incomingInputMessageInfo(true)

        if (inputMessageInfo.isFirst) {

            val consumed = onBackStackEmpty()

            if (consumed) {
                return true
            }

            // The user did not consume it and we need to keep the inputMessage Mail Stack with at
            // least one message so our API works properly. So we inject an artificial one.
            mailStore.depositInputMessageTo(ownDestinationId, NavigationMessage.Input.NoReply())

            return true
        }

        mailStore.depositResultMessageTo(backStackFragmentGraphId, resultMessage)

        // It might happen that the back stack destination is a Fragment of the same type as this.
        // In such a case we want to pop inclusive, so we pop all the way back to
        // the previous same type/id Fragment.
        val inclusive = backStackFragmentGraphId == ownDestinationId

        return navController.popBackStack(backStackFragmentGraphId, inclusive)

    }

}
