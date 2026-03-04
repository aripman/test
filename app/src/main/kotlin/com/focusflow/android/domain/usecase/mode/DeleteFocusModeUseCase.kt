package com.focusflow.android.domain.usecase.mode

import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.repository.FocusModeRepository
import javax.inject.Inject

class DeleteFocusModeUseCase @Inject constructor(
    private val repository: FocusModeRepository
) {
    suspend operator fun invoke(mode: FocusMode) = repository.deleteMode(mode)
}
