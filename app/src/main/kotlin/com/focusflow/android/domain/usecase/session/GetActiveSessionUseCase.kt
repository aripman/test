package com.focusflow.android.domain.usecase.session

import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<SessionState> = sessionRepository.observeSessionState()
}
