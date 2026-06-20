# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified in
# /Users/jayanthbharadwajm/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and execution parameters in the Gradle build.

# Keep Kotlinx Serialization metadata and classes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# The Kotlinx Serialization compiler plugin automatically keeps serializable classes,
# but we can declare it explicitly if needed:
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
