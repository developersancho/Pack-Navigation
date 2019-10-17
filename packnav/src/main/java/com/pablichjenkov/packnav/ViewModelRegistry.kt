package com.pablichjenkov.packnav

import androidx.lifecycle.ViewModel


class ViewModelRegistry : ViewModel() {

    private val registry: MutableMap<NavigationMessage, Any> = mutableMapOf()

    fun <T> get(
        inputMessage: NavigationMessage.Input
    ): T {

        return registry[inputMessage] as T

    }

    fun put(
        inputMessage: NavigationMessage.Input,
        viewModel: Any
    ) {

        registry[inputMessage] = viewModel

    }

}
