# ════════════════════════════════════════════════════════
#  proguard-rules.pro — kitty's premium
# ════════════════════════════════════════════════════════

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer {
    kotlinx.serialization.descriptors.SerialDescriptor descriptor;
}
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** serialVersionUID;
    private static final kotlinx.serialization.internal.SerializationConstructorMarker serialVersionUID;
}

# Supabase / Ktor
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Hilt
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Gemini AI
-keep class com.google.ai.client.generativeai.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep data classes used by Gson / Supabase serialization
-keep class com.kittys.premium.data.repository.**Dto { *; }
-keep class com.kittys.premium.domain.model.** { *; }
-keep class com.kittys.premium.data.local.**Entity { *; }

# Lottie
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**
