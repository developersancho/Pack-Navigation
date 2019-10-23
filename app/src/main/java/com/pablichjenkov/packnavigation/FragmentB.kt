package com.pablichjenkov.packnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pablichjenkov.packnav.NavBaseFragment
import com.pablichjenkov.packnav.NavigationMessage
import kotlinx.android.synthetic.main.fragment_b.*


class FragmentB : NavBaseFragment<ViewModelB>() {

    private lateinit var viewModelB: ViewModelB

    private var input: Input? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnGoToA.setOnClickListener {

            viewModelB.appendText("-> Launching Fragment A")

            navigateToFragmentForResult(
                R.id.fragmentA,
                FragmentA.Input("B", input?.callerMsg + " -> B")
            )

        }

        btnGoToB.setOnClickListener {

            viewModelB.appendText("-> Launching another Fragment B")

            navigateToFragmentForResult(
                R.id.fragmentB,
                FragmentB.Input("B", input?.callerMsg + " -> B")
            )

        }

        btnGoToC.setOnClickListener {

            viewModelB.appendText("-> Launching a Fragment C")

            navigateToFragmentForResult(
                R.id.fragmentC,
                FragmentC.Input("B", input?.callerMsg + " -> B")
            )

        }

    }

    override fun createViewModel(): ViewModelB {
        return ViewModelB()
    }

    override fun onFragmentStartWithInput(
        viewModel: ViewModelB,
        inputMessage: NavigationMessage.Input
    ) {

        this.viewModelB = viewModel

        this.input = inputMessage.payload as? Input

        when (inputMessage) {

            is NavigationMessage.Input.NoReply -> {

                viewModel.appendText(
                    "Launched Orphan Fragment B"
                )

            }

            is NavigationMessage.Input.ReplyTo -> {

                viewModel.appendText(
                    "Launched Child Fragment B with Parent ${input?.callerName}"
                )

            }

        }

        console.text = viewModel.consoleText

        message.text = "${input?.callerMsg} -> B"

    }

    override fun onFragmentResult(
        viewModel: ViewModelB,
        inputMessage: NavigationMessage.Input,
        resultMessage: NavigationMessage.Result
    ) {

        this.viewModelB = viewModel

        this.input = inputMessage.payload as? Input

        when (val resultPayload = resultMessage.payload) {

            null -> {

                viewModel.appendText(
                    "<- Receiving Result from Last Launched Child. " +
                            "No Result Payload came in"
                )

            }

            is FragmentB.Result -> {

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

        message.text = "${input?.callerMsg} -> B"

    }

    class Input(
        val callerName: String,
        val callerMsg: String
    )

    class Result(val message: String) {


    }

}

class ViewModelB {

    val name = "B"

    var consoleText = "ViewModel Record:\n"

    fun appendText(text: String) {
        consoleText += text + "\n"
    }

}