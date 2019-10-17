package com.pablichjenkov.packnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pablichjenkov.packnav.NavBaseFragment
import com.pablichjenkov.packnav.NavigationMessage
import kotlinx.android.synthetic.main.fragment_a.*

class FragmentA : NavBaseFragment<ViewModelA>() {

    private lateinit var viewModelA: ViewModelA

    private var input: Input? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoToA.setOnClickListener {

            viewModelA.appendText("-> Launching another Fragment A")

            navigateToFragmentForResult(
                R.id.fragmentA,
                FragmentA.Input("A", input?.callerMsg + " -> A")
            )

        }

        btnGoToB.setOnClickListener {

            viewModelA.appendText("-> Launching a Fragment B")

            navigateToFragmentForResult(
                R.id.fragmentB,
                FragmentB.Input("A", input?.callerMsg + " -> A")
            )

        }

        btnGoToC.setOnClickListener {

            viewModelA.appendText("-> Launching a Fragment C")

            navigateToFragmentForResult(
                R.id.fragmentC,
                FragmentC.Input("A", input?.callerMsg + " -> A")
            )

        }

    }

    override fun createViewModel(): ViewModelA {
        return ViewModelA()
    }

    override fun onFragmentStartWithInput(
        viewModel: ViewModelA,
        inputMessage: NavigationMessage.Input
    ) {

        this.viewModelA = viewModel

        this.input = inputMessage.payload as? FragmentA.Input

        when (inputMessage) {

            is NavigationMessage.Input.NoReply -> {

                viewModel.appendText(
                    "Launched Orphan Fragment A"
                )

            }

            is NavigationMessage.Input.ReplyTo -> {

                viewModel.appendText(
                    "Launched Child Fragment A with Parent ${input?.callerName} "
                )

            }

        }

        console.text = viewModel.consoleText

        message.text = "${input?.callerMsg} -> A"

    }

    override fun onFragmentResult(
        viewModel: ViewModelA,
        inputMessage: NavigationMessage.Input,
        resultMessage: NavigationMessage.Result
    ) {

        this.viewModelA = viewModel

        this.input = inputMessage.payload as? Input

        when (val resultPayload = resultMessage.payload) {

            null -> {

                viewModel.appendText(
                    "<- Receiving Result from Last Launched Child. " +
                            "No Result Payload came in"
                )

            }

            is FragmentA.Result -> {

                viewModel.appendText(
                    "<- Receiving Result from Child A. " +
                            "Msg: ${resultPayload.message}"
                )

            }

            is FragmentB.Result -> {

                viewModel.appendText(
                    "<- Receiving Result from Child B. " +
                            "Msg: ${resultPayload.message}"
                )
            }

            is FragmentC.Result -> {

                viewModel.appendText(
                    "<- Receiving Result from Child C. " +
                            "Msg: ${resultPayload.message}"
                )

            }

        }

        console.text = viewModel.consoleText

        message.text = "${input?.callerMsg} -> A"

    }

    override fun onBackStackEmpty(): Boolean {

        //activity?.finish()

        return false
    }


    class Input(
        val callerName: String,
        val callerMsg: String
    )

    class Result(val message: String)

}

class ViewModelA {

    val name = "A"

    var consoleText = "ViewModel Record:\n"
        private set

    fun appendText(text: String) {
        consoleText += "$text \n"
    }

}