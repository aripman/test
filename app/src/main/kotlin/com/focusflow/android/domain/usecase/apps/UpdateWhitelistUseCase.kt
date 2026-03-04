package com.focusflow.android.domain.usecase.apps

import com.focusflow.android.domain.repository.AppRepository
import javax.inject.Inject

class UpdateWhitelistUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(modeId: Long, packageNames: List<String>) =
        repository.setWhitelist(modeId, packageNames)
}
