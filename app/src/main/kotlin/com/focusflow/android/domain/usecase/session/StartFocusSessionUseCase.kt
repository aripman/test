package com.focusflow.android.domain.usecase.session

import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.repository.SessionRepository
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(mode: FocusMode): Long =
        sessionRepository.startSession(mode)
}
