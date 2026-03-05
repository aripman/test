package com.focusflow.android.domain.usecase.apps

import com.focusflow.android.domain.model.AppInfo
import com.focusflow.android.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWhitelistedAppsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(modeId: Long): Flow<List<AppInfo>> = repository.getWhitelistedApps(modeId)
}
