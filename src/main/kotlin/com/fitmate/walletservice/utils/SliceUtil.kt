package com.fitmate.walletservice.utils

class SliceUtil {

    companion object {
        fun getSliceLimit(pageSize: Int): Long {
            return (pageSize + 1).toLong()
        }
    }
}