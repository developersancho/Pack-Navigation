package com.pablichjenkov.packnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pablichjenkov.packnav.NavBaseFragment
import com.pablichjenkov.packnav.NavigationMessage

class FragmentSplash : NavBaseFragment<SplashVM>() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun createViewModel(): SplashVM {
        return SplashVM()
    }

    override fun onFragmentStartWithInput(
        viewModel: SplashVM,
        inputMessage: NavigationMessage.Input
    ) {

    }

    override fun onFragmentResult(
        viewModel: SplashVM,
        inputMessage: NavigationMessage.Input,
        resultMessage: NavigationMessage.Result
    ) {

    }

}

class SplashVM