package br.com.metaro.portal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.net.http.SslError
import android.window.OnBackInvokedDispatcher
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorPanel: View
    private lateinit var errorMessage: TextView
    private lateinit var fullscreenContainer: FrameLayout
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private val captureOutputFiles = linkedMapOf<Uri, File>()
    private val ioExecutor = Executors.newSingleThreadExecutor()
    private val homeUrl by lazy { getString(R.string.portal_url) }
    private val portalHost by lazy {
        Uri.parse(homeUrl).host?.lowercase(Locale.ROOT).orEmpty()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.web_view)
        progressBar = findViewById(R.id.progress_bar)
        errorPanel = findViewById(R.id.error_panel)
        errorMessage = findViewById(R.id.error_message)
        fullscreenContainer = findViewById(R.id.fullscreen_container)

        configureSystemBars()
        cleanupOldTemporaryFiles()

        findViewById<Button>(R.id.retry_button).setOnClickListener {
            hideError()
            if (isOnline()) webView.reload() else showError(getString(R.string.no_connection))
        }

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(false)
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            userAgentString = "$userAgentString PortalMetaroAndroid/1.0"
            mediaPlaybackRequiresUserGesture = true
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, false)
        }

        val isDebuggable = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        WebView.setWebContentsDebuggingEnabled(isDebuggable)
        webView.settings.safeBrowsingEnabled = true
        webView.addJavascriptInterface(FileBridge(), FILE_BRIDGE_NAME)
        webView.webViewClient = PortalWebViewClient()
        webView.webChromeClient = PortalWebChromeClient()
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            if (isPdf(url, mimeType, contentDisposition)) {
                downloadPdf(url, null, userAgent, contentDisposition, mimeType)
            } else {
                openOutsideApp(Uri.parse(url))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { handleBackNavigation() }
        }

        if (savedInstanceState == null) {
            if (isOnline()) webView.loadUrl(homeUrl) else showError(getString(R.string.no_connection))
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    private fun configureSystemBars() {
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.BLACK
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        hideCustomView()
        fileChooserCallback?.onReceiveValue(null)
        fileChooserCallback = null
        clearCaptureOutputs(emptyArray())
        ioExecutor.shutdownNow()
        webView.removeJavascriptInterface(FILE_BRIDGE_NAME)
        webView.stopLoading()
        webView.webChromeClient = null
        webView.webViewClient = WebViewClient()
        webView.destroy()
        super.onDestroy()
    }

    @SuppressLint("GestureBackNavigation")
    @Deprecated("Compatibilidade com Android 12 e anteriores; Android 13+ usa OnBackInvokedDispatcher")
    override fun onBackPressed() {
        handleBackNavigation()
    }

    private fun handleBackNavigation() {
        if (customView != null) {
            hideCustomView()
        } else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    private inner class PortalWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return routeUri(request?.url)
        }

        @Deprecated("Usado por implementações antigas do WebView")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return routeUri(url?.let(Uri::parse))
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            progressBar.visibility = View.VISIBLE
            hideError()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            progressBar.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            if (request?.isForMainFrame == true) {
                progressBar.visibility = View.GONE
                showError(error?.description?.toString() ?: getString(R.string.load_error))
            }
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            handler?.cancel()
            progressBar.visibility = View.GONE
            showError(getString(R.string.ssl_error))
        }
    }

    private inner class PortalWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            progressBar.progress = newProgress
            progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
        }
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            if (view == null) {
                callback?.onCustomViewHidden()
                return
            }
            showCustomView(view, callback)
        }

        override fun onHideCustomView() {
            hideCustomView()
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            fileChooserCallback?.onReceiveValue(null)
            fileChooserCallback = filePathCallback

            val acceptedTypes = normalizeAcceptTypes(fileChooserParams?.acceptTypes)
            val acceptsImages = acceptedTypes.any {
                it == "image/*" || it.startsWith("image/")
            }
            val acceptsVideos = acceptedTypes.any {
                it == "video/*" || it.startsWith("video/")
            }

            val contentIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = primaryMimeType(acceptedTypes)
                if (acceptedTypes.size > 1) {
                    putExtra(Intent.EXTRA_MIME_TYPES, acceptedTypes)
                }
                putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE
                )
            }

            clearCaptureOutputs(emptyArray())
            val captureIntents = buildList {
                if (acceptsImages) createCaptureIntent(
                    action = MediaStore.ACTION_IMAGE_CAPTURE,
                    prefix = "foto",
                    extension = ".jpg",
                    clipLabel = "Foto"
                )?.let(::add)
                if (acceptsVideos) createCaptureIntent(
                    action = MediaStore.ACTION_VIDEO_CAPTURE,
                    prefix = "video",
                    extension = ".mp4",
                    clipLabel = "Video"
                )?.let(::add)
            }
            val captureOnlyIntent = if (fileChooserParams?.isCaptureEnabled == true) {
                when {
                    acceptsImages && !acceptsVideos -> captureIntents.firstOrNull {
                        it.action == MediaStore.ACTION_IMAGE_CAPTURE
                    }
                    acceptsVideos && !acceptsImages -> captureIntents.firstOrNull {
                        it.action == MediaStore.ACTION_VIDEO_CAPTURE
                    }
                    else -> null
                }
            } else {
                null
            }

            return try {
                if (captureOnlyIntent != null) {
                    startActivityForResult(captureOnlyIntent, FILE_CHOOSER_REQUEST)
                } else {
                    val chooser = Intent.createChooser(contentIntent, getString(R.string.choose_file))
                    if (captureIntents.isNotEmpty()) {
                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, captureIntents.toTypedArray())
                    }
                    startActivityForResult(chooser, FILE_CHOOSER_REQUEST)
                }
                true
            } catch (_: ActivityNotFoundException) {
                clearCaptureOutputs(emptyArray())
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = null
                Toast.makeText(this@MainActivity, R.string.no_file_picker, Toast.LENGTH_LONG).show()
                false
            }
        }
    }

    @Deprecated("Compatibilidade do seletor de arquivos com WebChromeClient")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != FILE_CHOOSER_REQUEST) return

        val result = if (resultCode == RESULT_OK) {
            selectedUrisFrom(data) ?: captureOutputFiles.entries
                .firstOrNull { (_, file) -> file.length() > 0L }
                ?.key
                ?.let { arrayOf(it) }
        } else {
            null
        }

        clearCaptureOutputs(result.orEmpty())
        fileChooserCallback?.onReceiveValue(result)
        fileChooserCallback = null
    }

    private fun normalizeAcceptTypes(rawTypes: Array<String>?): Array<String> {
        return rawTypes.orEmpty()
            .flatMap { it.split(',') }
            .map { it.trim().lowercase(Locale.ROOT) }
            .filter { it.isNotBlank() }
            .mapNotNull { value ->
                when (value) {
                    ".png" -> "image/png"
                    ".jpg", ".jpeg" -> "image/jpeg"
                    ".webp" -> "image/webp"
                    ".mp4" -> "video/mp4"
                    ".webm" -> "video/webm"
                    ".mov" -> "video/quicktime"
                    ".pdf" -> PDF_MIME
                    else -> when {
                        value.startsWith(".") -> MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(value.removePrefix("."))
                        value.contains('/') -> value.substringBefore(';')
                        else -> null
                    }
                }
            }
            .distinct()
            .toTypedArray()
    }

    private fun primaryMimeType(acceptedTypes: Array<String>): String {
        if (acceptedTypes.isEmpty()) return "*/*"
        if (acceptedTypes.size == 1) return acceptedTypes.first()
        if (acceptedTypes.all { it.startsWith("image/") }) return "image/*"
        if (acceptedTypes.all { it.startsWith("video/") }) return "video/*"
        return "*/*"
    }

    private fun createCaptureIntent(
        action: String,
        prefix: String,
        extension: String,
        clipLabel: String
    ): Intent? {
        val cameraDirectory = File(cacheDir, "shared/camera").apply { mkdirs() }
        val outputFile = runCatching {
            File.createTempFile("$prefix-${System.currentTimeMillis()}-", extension, cameraDirectory)
        }.getOrNull() ?: return null

        val outputUri = PortalFileProvider.getUriForFile(this, outputFile)
        val intent = Intent(action).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            clipData = ClipData.newRawUri(clipLabel, outputUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) == null) {
            outputFile.delete()
            return null
        }

        val permissionFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        packageManager.queryIntentActivities(intent, 0).forEach { resolveInfo ->
            grantUriPermission(resolveInfo.activityInfo.packageName, outputUri, permissionFlags)
        }

        captureOutputFiles[outputUri] = outputFile
        return intent
    }

    private fun selectedUrisFrom(data: Intent?): Array<Uri>? {
        val clipData = data?.clipData
        if (clipData != null) {
            return Array(clipData.itemCount) { index -> clipData.getItemAt(index).uri }
        }
        return data?.data?.let { arrayOf(it) }
    }

    private fun clearCaptureOutputs(selectedUris: Array<out Uri>) {
        val selected = selectedUris.toSet()
        captureOutputFiles.forEach { (uri, file) ->
            revokeUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            if (uri !in selected) file.delete()
        }
        captureOutputFiles.clear()
    }

    private fun showCustomView(view: View, callback: WebChromeClient.CustomViewCallback?) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallback = callback
        fullscreenContainer.addView(
            view,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        fullscreenContainer.visibility = View.VISIBLE
        webView.visibility = View.GONE
        enterFullscreenMode()
    }

    private fun hideCustomView() {
        if (customView == null) return

        fullscreenContainer.removeAllViews()
        fullscreenContainer.visibility = View.GONE
        customView = null
        webView.visibility = View.VISIBLE
        exitFullscreenMode()
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
    }

    private fun enterFullscreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    private fun exitFullscreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.systemBars())
        }
        configureSystemBars()
    }

    private fun routeUri(uri: Uri?): Boolean {
        uri ?: return true

        if (isPdf(uri.toString(), null, null)) {
            downloadPdf(uri.toString(), null, webView.settings.userAgentString, null, PDF_MIME)
            return true
        }

        val scheme = uri.scheme?.lowercase(Locale.ROOT)
        val host = uri.host?.lowercase(Locale.ROOT)
        if ((scheme == "http" || scheme == "https") &&
            host == portalHost && isPortalImageUrl(uri)
        ) {
            downloadImage(uri.toString(), webView.settings.userAgentString)
            return true
        }
        if ((scheme == "http" || scheme == "https") && host == portalHost) {
            return false
        }

        openOutsideApp(uri)
        return true
    }

    private fun openOutsideApp(uri: Uri) {
        try {
            val externalIntent = if (uri.scheme == "intent") {
                Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
            } else {
                Intent(Intent.ACTION_VIEW, uri).apply {
                    if (uri.scheme == "http" || uri.scheme == "https") {
                        addCategory(Intent.CATEGORY_BROWSABLE)
                    }
                }
            }
            startActivity(externalIntent)
        } catch (_: Exception) {
            Toast.makeText(this, R.string.no_app_for_link, Toast.LENGTH_LONG).show()
        }
    }

    private fun isPdf(url: String?, mimeType: String?, contentDisposition: String?): Boolean {
        if (mimeType?.contains(PDF_MIME, ignoreCase = true) == true) return true
        if (contentDisposition?.contains(".pdf", ignoreCase = true) == true) return true
        val path = url?.let { runCatching { URI(it).path }.getOrNull() }
        return path?.endsWith(".pdf", ignoreCase = true) == true
    }

    private fun isPortalImageUrl(uri: Uri): Boolean {
        val segments = uri.pathSegments
        return segments.size == 2 &&
            segments.firstOrNull()?.equals("images", ignoreCase = true) == true &&
            !segments.getOrNull(1).isNullOrBlank()
    }

    private fun resolveHttpUrl(value: String): String? {
        val candidate = value.trim()
        if (candidate.isBlank()) return null

        val parsedCandidate = runCatching { Uri.parse(candidate) }.getOrNull() ?: return null
        val candidateScheme = parsedCandidate.scheme?.lowercase(Locale.ROOT)
        if (candidateScheme == "http" || candidateScheme == "https") return candidate
        if (candidateScheme != null) return null

        val baseUrl = webView.url?.takeIf { it.isNotBlank() } ?: homeUrl
        val resolvedUrl = runCatching {
            URI(baseUrl).resolve(URI(candidate)).toString()
        }.getOrNull() ?: return null
        val resolvedScheme = runCatching { Uri.parse(resolvedUrl).scheme?.lowercase(Locale.ROOT) }
            .getOrNull()
        return resolvedUrl.takeIf { resolvedScheme == "http" || resolvedScheme == "https" }
    }

    private fun downloadImage(url: String, userAgent: String?) {
        val resolvedUrl = resolveHttpUrl(url)
        if (resolvedUrl == null) {
            Toast.makeText(this, R.string.invalid_image, Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, R.string.preparing_image, Toast.LENGTH_SHORT).show()
        ioExecutor.execute {
            var connection: HttpURLConnection? = null
            var temporaryFile: File? = null
            try {
                connection = URL(resolvedUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = NETWORK_TIMEOUT_MS
                connection.readTimeout = NETWORK_TIMEOUT_MS
                connection.instanceFollowRedirects = true
                connection.setRequestProperty("Accept", "image/*")

                val cookies = CookieManager.getInstance().getCookie(resolvedUrl)
                if (!cookies.isNullOrBlank()) connection.setRequestProperty("Cookie", cookies)
                if (!userAgent.isNullOrBlank()) connection.setRequestProperty("User-Agent", userAgent)

                val status = connection.responseCode
                if (status !in 200..299) throw IllegalStateException("HTTP $status")

                val responseMime = connection.contentType
                    ?.substringBefore(';')
                    ?.trim()
                    ?.lowercase(Locale.ROOT)
                if (responseMime?.startsWith("image/") != true) {
                    throw IllegalStateException("Resposta não é uma imagem")
                }

                val contentDisposition = connection.getHeaderField("Content-Disposition")
                val guessedName = URLUtil.guessFileName(resolvedUrl, contentDisposition, responseMime)
                val safeName = sanitizeImageName(guessedName, responseMime)
                val directory = File(cacheDir, "shared/images").apply { mkdirs() }
                val outputFile = File(directory, "${System.currentTimeMillis()}-$safeName")
                temporaryFile = outputFile

                connection.inputStream.use { input ->
                    outputFile.outputStream().use { output -> input.copyTo(output) }
                }
                if (outputFile.length() == 0L) throw IllegalStateException("Imagem vazia")

                val completedFile = outputFile
                runOnUiThread {
                    if (isFinishing || isDestroyed) {
                        completedFile.delete()
                    } else {
                        openTemporaryImage(completedFile, responseMime)
                    }
                }
            } catch (_: Exception) {
                temporaryFile?.delete()
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, R.string.image_download_error, Toast.LENGTH_LONG).show()
                    }
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun openTemporaryImage(file: File, mimeType: String) {
        val contentUri = PortalFileProvider.getUriForFile(this, file)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, mimeType)
            clipData = ClipData.newRawUri("Imagem", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(Intent.createChooser(viewIntent, getString(R.string.open_image_with)))
        } catch (_: ActivityNotFoundException) {
            file.delete()
            Toast.makeText(this, R.string.no_image_app, Toast.LENGTH_LONG).show()
        }
    }

    private fun sanitizeImageName(name: String, mimeType: String): String {
        val cleaned = name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().ifBlank { "imagem" }
        val currentExtension = cleaned.substringAfterLast('.', "").lowercase(Locale.ROOT)
        val currentMime = currentExtension.takeIf { it.isNotBlank() }
            ?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
        if (currentMime?.startsWith("image/") == true) return cleaned

        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            ?.takeIf { it.isNotBlank() }
            ?: "jpg"
        return "$cleaned.$extension"
    }

    private fun downloadFile(
        url: String,
        bearerToken: String?,
        userAgent: String?,
        suggestedName: String?,
        suggestedMimeType: String?
    ) {
        val resolvedUrl = resolveHttpUrl(url)
        if (resolvedUrl == null) {
            Toast.makeText(this, R.string.invalid_file, Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, R.string.preparing_file, Toast.LENGTH_SHORT).show()
        ioExecutor.execute {
            var connection: HttpURLConnection? = null
            var temporaryFile: File? = null
            try {
                connection = URL(resolvedUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = NETWORK_TIMEOUT_MS
                connection.readTimeout = NETWORK_TIMEOUT_MS
                connection.instanceFollowRedirects = true
                connection.setRequestProperty("Accept", suggestedMimeType ?: "*/*")

                val cookies = CookieManager.getInstance().getCookie(resolvedUrl)
                if (!cookies.isNullOrBlank()) connection.setRequestProperty("Cookie", cookies)
                if (!bearerToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer $bearerToken")
                }
                if (!userAgent.isNullOrBlank()) connection.setRequestProperty("User-Agent", userAgent)

                val status = connection.responseCode
                if (status !in 200..299) throw IllegalStateException("HTTP $status")

                val responseMime = normalizeMimeType(connection.contentType)
                val contentDisposition = connection.getHeaderField("Content-Disposition")
                val serverName = URLUtil.guessFileName(resolvedUrl, contentDisposition, responseMime)
                val preferredName = suggestedName?.takeIf { it.isNotBlank() } ?: serverName
                val nameMime = mimeTypeFromFileName(preferredName)
                val resolvedMime = nameMime
                    ?: responseMime?.takeUnless { it == GENERIC_BINARY_MIME }
                    ?: suggestedMimeType
                    ?: GENERIC_BINARY_MIME
                val safeName = sanitizeFileName(preferredName, resolvedMime)
                val directory = File(cacheDir, "shared/files").apply { mkdirs() }
                val outputFile = File(directory, "${System.currentTimeMillis()}-$safeName")
                temporaryFile = outputFile

                connection.inputStream.use { input ->
                    outputFile.outputStream().use { output -> input.copyTo(output) }
                }
                if (outputFile.length() == 0L) throw IllegalStateException("Arquivo vazio")

                val completedFile = outputFile
                runOnUiThread {
                    if (isFinishing || isDestroyed) {
                        completedFile.delete()
                    } else {
                        openTemporaryFile(completedFile, resolvedMime)
                    }
                }
            } catch (_: Exception) {
                temporaryFile?.delete()
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, R.string.file_download_error, Toast.LENGTH_LONG).show()
                    }
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun openTemporaryFile(file: File, mimeType: String) {
        val contentUri = PortalFileProvider.getUriForFile(this, file)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, mimeType)
            clipData = ClipData.newRawUri("Arquivo", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooserTitle = when {
            mimeType == PDF_MIME -> getString(R.string.open_pdf_with)
            mimeType.startsWith("image/") -> getString(R.string.open_image_with)
            else -> getString(R.string.open_file_with)
        }

        try {
            startActivity(Intent.createChooser(viewIntent, chooserTitle))
        } catch (_: ActivityNotFoundException) {
            file.delete()
            Toast.makeText(this, R.string.no_file_app, Toast.LENGTH_LONG).show()
        }
    }

    private fun sanitizeFileName(name: String, mimeType: String): String {
        val cleaned = name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().ifBlank { "arquivo" }
        if (cleaned.substringAfterLast('.', "").isNotBlank()) return cleaned
        val extension = extensionFromMimeType(mimeType)
        return if (extension.isNullOrBlank()) cleaned else "$cleaned.$extension"
    }

    private fun normalizeMimeType(value: String?): String? {
        return value
            ?.substringBefore(';')
            ?.trim()
            ?.lowercase(Locale.ROOT)
            ?.takeIf { it.contains('/') }
    }

    private fun mimeTypeFromFileName(fileName: String?): String? {
        val extension = fileName
            ?.substringBefore('?')
            ?.substringAfterLast('.', "")
            ?.lowercase(Locale.ROOT)
            ?.takeIf { it.isNotBlank() }
            ?: return null
        return when (extension) {
            "pdf" -> PDF_MIME
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "csv" -> "text/csv"
            "txt" -> "text/plain"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
    }

    private fun extensionFromMimeType(mimeType: String): String? {
        return when (mimeType) {
            PDF_MIME -> "pdf"
            "image/jpeg" -> "jpg"
            "text/csv" -> "csv"
            "application/vnd.ms-excel" -> "xls"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            "application/vnd.ms-powerpoint" -> "ppt"
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx"
            else -> MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }

    private fun downloadPdf(
        url: String,
        bearerToken: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?
    ) {
        val resolvedUrl = resolveHttpUrl(url)
        if (resolvedUrl == null) {
            Toast.makeText(this, R.string.invalid_pdf, Toast.LENGTH_LONG).show()
            return
        }

        val guessedName = URLUtil.guessFileName(resolvedUrl, contentDisposition, mimeType ?: PDF_MIME)
        val safeName = sanitizePdfName(guessedName)
        val destinationName = "${System.currentTimeMillis()}-$safeName"

        Toast.makeText(this, R.string.preparing_pdf, Toast.LENGTH_SHORT).show()
        ioExecutor.execute {
            var connection: HttpURLConnection? = null
            var temporaryFile: File? = null
            try {
                val directory = File(cacheDir, "shared/pdfs").apply { mkdirs() }
                val outputFile = File(directory, destinationName)
                temporaryFile = outputFile
                connection = URL(resolvedUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = NETWORK_TIMEOUT_MS
                connection.readTimeout = NETWORK_TIMEOUT_MS
                connection.instanceFollowRedirects = true
                connection.setRequestProperty("Accept", PDF_MIME)

                val cookies = CookieManager.getInstance().getCookie(resolvedUrl)
                if (!cookies.isNullOrBlank()) connection.setRequestProperty("Cookie", cookies)
                if (!bearerToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer $bearerToken")
                }
                if (!userAgent.isNullOrBlank()) connection.setRequestProperty("User-Agent", userAgent)

                val status = connection.responseCode
                if (status !in 200..299) throw IllegalStateException("HTTP $status")

                connection.inputStream.use { input ->
                    outputFile.outputStream().use { output -> input.copyTo(output) }
                }
                if (outputFile.length() == 0L) throw IllegalStateException("PDF vazio")

                val completedFile = outputFile
                runOnUiThread {
                    if (isFinishing || isDestroyed) {
                        completedFile.delete()
                    } else {
                        openTemporaryPdf(completedFile)
                    }
                }
            } catch (_: Exception) {
                temporaryFile?.delete()
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, R.string.pdf_download_error, Toast.LENGTH_LONG).show()
                    }
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun openTemporaryPdf(file: File) {
        val contentUri = PortalFileProvider.getUriForFile(this, file)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, PDF_MIME)
            clipData = ClipData.newRawUri("PDF", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(Intent.createChooser(viewIntent, getString(R.string.open_pdf_with)))
        } catch (_: ActivityNotFoundException) {
            file.delete()
            Toast.makeText(this, R.string.no_pdf_app, Toast.LENGTH_LONG).show()
        }
    }

    private fun sanitizePdfName(name: String): String {
        val cleaned = name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().ifBlank { "documento.pdf" }
        return if (cleaned.endsWith(".pdf", ignoreCase = true)) cleaned else "$cleaned.pdf"
    }

    private fun cleanupOldTemporaryFiles() {
        ioExecutor.execute {
            val sharedDirectory = File(cacheDir, "shared")
            val cutoff = System.currentTimeMillis() - TEMP_FILE_MAX_AGE_MS
            sharedDirectory.walkBottomUp().forEach { file ->
                if (file.isFile && file.lastModified() < cutoff) file.delete()
                if (file.isDirectory && file != sharedDirectory && file.listFiles().isNullOrEmpty()) {
                    file.delete()
                }
            }
        }
    }

    private fun isOnline(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showError(message: String) {
        errorMessage.text = message
        errorPanel.visibility = View.VISIBLE
        webView.visibility = View.INVISIBLE
    }

    private fun hideError() {
        errorPanel.visibility = View.GONE
        webView.visibility = View.VISIBLE
    }

    /**
     * Contrato usado somente pela página confiável do portal.
     * O Angular informa a URL protegida, o token atual e o nome do arquivo.
     */
    private inner class FileBridge {
        @JavascriptInterface
        fun openFile(url: String?, bearerToken: String?, fileName: String?) {
            if (url.isNullOrBlank()) return
            runOnUiThread {
                downloadFile(
                    url = url,
                    bearerToken = bearerToken,
                    userAgent = webView.settings.userAgentString,
                    suggestedName = fileName,
                    suggestedMimeType = mimeTypeFromFileName(fileName)
                )
            }
        }

        /** Mantido para versões anteriores do Angular. */
        @JavascriptInterface
        fun openPdf(url: String?, bearerToken: String?, fileName: String?) {
            if (url.isNullOrBlank()) return
            runOnUiThread {
                val fileMimeType = mimeTypeFromFileName(fileName)
                if (fileMimeType != null && fileMimeType != PDF_MIME) {
                    downloadFile(
                        url = url,
                        bearerToken = bearerToken,
                        userAgent = webView.settings.userAgentString,
                        suggestedName = fileName,
                        suggestedMimeType = fileMimeType
                    )
                    return@runOnUiThread
                }
                val disposition = fileName
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "attachment; filename=\"${sanitizePdfName(it)}\"" }
                downloadPdf(
                    url = url,
                    bearerToken = bearerToken,
                    userAgent = webView.settings.userAgentString,
                    contentDisposition = disposition,
                    mimeType = PDF_MIME
                )
            }
        }
    }

    companion object {
        private const val PDF_MIME = "application/pdf"
        private const val GENERIC_BINARY_MIME = "application/octet-stream"
        private const val FILE_BRIDGE_NAME = "PortalMetaroAndroid"
        private const val FILE_CHOOSER_REQUEST = 4102
        private const val NETWORK_TIMEOUT_MS = 60_000
        private const val TEMP_FILE_MAX_AGE_MS = 24L * 60L * 60L * 1000L
    }
}
