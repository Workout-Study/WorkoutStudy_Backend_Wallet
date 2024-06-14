package com.fitmate.walletservice.common

class BatchServiceURI {

    companion object {
        const val FIT_GROUP_ROOT_URI = "http://batch-service:8010/batch-service"

        const val FIT_CERTIFICATION_RESULT_ROOT = "$FIT_GROUP_ROOT_URI/certifications/results"

        const val FIT_PENALTY_ROOT = "$FIT_GROUP_ROOT_URI/penalties"
    }
}