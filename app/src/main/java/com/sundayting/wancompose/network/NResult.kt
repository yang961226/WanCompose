package com.sundayting.wancompose.network

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class NResult<T> {

    data class Success<T>(val body: T) : NResult<T>()

    class Error(val ex: Throwable) : NResult<Nothing>()

}

@OptIn(ExperimentalContracts::class)
fun <T> NResult<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is NResult.Success)
        returns(false) implies (this@isSuccess is NResult.Error)
    }
    return this is NResult.Success
}

