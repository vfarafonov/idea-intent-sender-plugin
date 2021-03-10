package com.distillery.intentsender.testutils

import com.nhaarman.mockitokotlin2.mock

/** Prepares dummy based on [mock] function by activating stubOnly mode. */
inline fun <reified T : Any> stubMock(): T = mock(stubOnly = true)
