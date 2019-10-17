package com.pablichjenkov.packnav

import androidx.collection.SimpleArrayMap
import androidx.lifecycle.ViewModel
import java.util.*


class MailStore : ViewModel() {

    private val inputMailBox = SimpleArrayMap<Int, Stack<NavigationMessage.Input>>()

    private val resultMailBox = SimpleArrayMap<Int, NavigationMessage.Result>()

    fun getInputMessageInfoSentTo(sentTo: Int, remove: Boolean): InputMessageInfo {

        return when (val inputMessageStack = inputMailBox.get(sentTo)) {

            null -> InputMessageInfo(null, false)

            else -> {

                if (inputMessageStack.isNotEmpty()) {

                    if (remove) {

                        InputMessageInfo(inputMessageStack.pop(), getTotalInputMessageSize() == 0)

                    } else {

                        InputMessageInfo(inputMessageStack.peek(), getTotalInputMessageSize() == 0)

                    }

                } else
                    InputMessageInfo(null, false)

            }

        }

    }

    fun depositInputMessageTo(
        sendTo: Int,
        inputMessage: NavigationMessage.Input
    ) {

        var inputMessageStack = inputMailBox.get(sendTo)

        if (inputMessageStack == null) {
            inputMessageStack = Stack()
        }

        inputMessageStack.push(inputMessage)

        inputMailBox.put(sendTo, inputMessageStack)
    }

    private fun getTotalInputMessageSize(): Int {

        val size = inputMailBox.size()

        var totalSize = 0

        for (idx in 0 until size) {

            totalSize += inputMailBox.valueAt(idx).size

        }

        return totalSize
    }

    /**
     * In the case of a Result NavigationMessage we want to remove the event after read.
     * For that we use SimpleArrayMap.put(), it will return the existing value and set current
     * to null.
     * */
    fun getResultMessageSentTo(
        sentTo: Int
    ): NavigationMessage.Result? = resultMailBox.put(sentTo, null)

    fun depositResultMessageTo(
        sendTo: Int,
        resultMessage: NavigationMessage.Result
    ) {
        resultMailBox.put(sendTo, resultMessage)
    }

}

sealed class NavigationMessage(
    val replyTo: Int?,
    val payload: Any?
) {

    sealed class Input(
        replyTo: Int?,
        payload: Any?
    ) : NavigationMessage(replyTo, payload) {

        class NoReply(
            payload: Any? = null
        ) : Input(null, payload)

        class ReplyTo(
            replyTo: Int,
            payload: Any? = null
        ) : Input(replyTo, payload)

    }

    sealed class Result(
        replyTo: Int?,
        val success: Boolean,
        payload: Any?
    ) : NavigationMessage(replyTo, payload) {

        class Cancelled(
            payload: Any? = null
        ) : Result(null, false, payload)

        class Success(
            payload: Any? = null
        ) : Result(null, true, payload)

    }

}

class InputMessageInfo(
    val inputMessage: NavigationMessage.Input?,
    val isFirst: Boolean
)