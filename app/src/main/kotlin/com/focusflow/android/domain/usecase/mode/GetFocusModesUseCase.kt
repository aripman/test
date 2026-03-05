package com.focusflow.android.domain.usecase.mode

import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.repository.FocusModeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFocusModesUseCase @Inject constructor(
    private val repository: FocusModeRepository
) {
    operator fun invoke(): Flow<List<FocusMode>> = repository.getAllModes()
}
