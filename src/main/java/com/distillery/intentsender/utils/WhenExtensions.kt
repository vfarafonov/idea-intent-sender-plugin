package com.distillery.intentsender.utils

/**
 * Forces `when` expression to validate if all possible values are used.
 *
 * Useful for `when` expressions with no return when an argument is a enum or sealed class. No need to use this in case
 * `when` expression returns a value.
 *
 * Usage:
 *
 *    when (someSealedClass) {
 *        ...
 *    }.exhaustive
 */
val Any?.exhaustive
    get() = Unit
