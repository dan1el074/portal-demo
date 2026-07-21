# A interface JavaScript precisa manter seus métodos anotados em builds ofuscados.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

