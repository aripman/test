# Hilt
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel *;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class com.focusflow.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# App model classes
-keep class com.focusflow.android.domain.model.** { *; }
-keep class com.focusflow.android.data.local.entity.** { *; }
