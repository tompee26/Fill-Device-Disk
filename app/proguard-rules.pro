# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\prnsoft\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# http://stackoverflow.com/questions/14123866/how-to-config-my-proguard-project-txt-file-to-remove-just-logs
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontwarn com.unicon_ltd.konect.sdk.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.**
-dontwarn okio.**
-keep class com.activeandroid.*** { *; }
-keep public class com.unicon_ltd.konect.sdk.** { *; }
-keep public class org.codehaus.**
-keep public class java.nio.**