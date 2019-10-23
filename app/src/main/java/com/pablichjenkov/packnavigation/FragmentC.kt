package com.pablichjenkov.packnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pablichjenkov.packnav.NavBaseFragment
import com.pablichjenkov.packnav.NavigationMessage
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.android.synthetic.main.fragment_b.*
import kotlinx.android.synthetic.main.fragment_c.*
import kotlinx.android.synthetic.main.fragment_c.btnGoToA
import kotlinx.android.synthetic.main.fragment_c.btnGoToB
import kotlinx.android.synthetic.main.fragment_c.btnGoToC
import kotlinx.android.synthetic.main.fragment_c.console
import kotlinx.android.synthetic.main.fragment_c.message


class FragmentC : NavBaseFragment<ViewModelC>() {

    private lateinit var viewModelC: ViewModelC

    private var input: Input? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_c, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnGoToA.setOnClickListener {

            viewModelC.appendText("-> Launching Fragment A")

            navigateToFragmentForResult(
                R.id.fragmentA,
                FragmentA.Input("C", input?.callerMsg + " -> C")
            )

        }

        btnGoToB.setOnClickListener {

            viewModelC.appendText("-> Launching a Fragment B")

            navigateToFragmentForResult(
                R.id.fragmentB,
                FragmentB.Input("C", input?.callerMsg + " -> C")
            )

        }

        btnGoToC.setOnClickListener {

            viewModelC.appendText("-> Launching another Fragment C")

            navigateToFragmentForResult(
                R.id.fragmentC,
                FragmentC.Input("C", input?.callerMsg + " -> C")
            )

        }

    }

    override fun createViewModel(): ViewModelC {
        return ViewModelC()
    }

    override fun onFragmentStartWithInput(
        viewModel: ViewModelC,
        inputMessage: NavigationMessage.Input
    ) {

        this.viewModelC = viewModel

        this.input = inputMessage.payload as? Input

        when (inputMessage) {

            is NavigationMessage.Input.NoReply -> {

                viewModel.appendText(
                    "Launched Orphan Fragment C"
                )

            }

            is NavigationMessage.Input.ReplyTo -> {

                viewModel.appendText(
                    "Launched Child Fragment C with Parent ${input?.callerName} "
                )

            }

        }

        console.text = viewModel.consoleText

        message.text = "${input?.callerMsg} -> C"

    }

    override fun onFragmentResult(
        viewModel: ViewModelC,
        inputMessage: NavigationMessage.Input,
        resultMessage: NavigationMessage.Result
    ) {

        this.viewModelC = viewModel

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

        message.text = "${input?.callerMsg} -> C"
    }

    class Input(
        val callerName: String,
        val callerMsg: String
    )

    class Result(val message: String) {


    }

}

class ViewModelC {

    val name = "C"

    var consoleText = "ViewModel Record:\n"

    fun appendText(text: String) {
        consoleText += text + "\n"
    }

}