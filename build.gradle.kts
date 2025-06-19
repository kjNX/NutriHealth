plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.maps.secrets) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
