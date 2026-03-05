package com.focusflow.android.domain.usecase.mode

import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.repository.FocusModeRepository
import javax.inject.Inject

class SaveFocusModeUseCase @Inject constructor(
    private val repository: FocusModeRepository
) {
    suspend operator fun invoke(mode: FocusMode): Long = repository.saveMode(mode)
}
