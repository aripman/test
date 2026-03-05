package com.focusflow.android.domain.usecase.session

import com.focusflow.android.domain.repository.SessionRepository
import javax.inject.Inject

class EndFocusSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(sessionId: Long, wasCompleted: Boolean) =
        sessionRepository.endSession(sessionId, wasCompleted)
}
