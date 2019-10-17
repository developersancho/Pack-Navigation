package com.pablichjenkov.packnav

import java.io.InvalidObjectException

val nullViewModelException = InvalidObjectException(
    "Abstract function createViewModel() must return a non null object"
)
