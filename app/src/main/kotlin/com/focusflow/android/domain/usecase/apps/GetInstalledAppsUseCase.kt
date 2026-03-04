package com.focusflow.android.domain.usecase.apps

import com.focusflow.android.domain.model.AppInfo
import com.focusflow.android.domain.repository.AppRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    suspend operator fun invoke(): List<AppInfo> = repository.getInstalledApps()
}
