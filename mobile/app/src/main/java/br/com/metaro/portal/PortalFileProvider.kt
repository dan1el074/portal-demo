package br.com.metaro.portal

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileNotFoundException

/** Compartilha somente arquivos temporários criados em cacheDir/shared. */
class PortalFileProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String {
        val extension = resolveFile(uri).extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val file = resolveFile(uri)
        val requested = projection ?: arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        val supported = requested.filter {
            it == OpenableColumns.DISPLAY_NAME || it == OpenableColumns.SIZE
        }
        return MatrixCursor(supported.toTypedArray(), 1).apply {
            addRow(supported.map { column ->
                when (column) {
                    OpenableColumns.DISPLAY_NAME -> file.name.substringAfter('-', file.name)
                    OpenableColumns.SIZE -> file.length()
                    else -> null
                }
            })
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val file = resolveFile(uri)
        val flags = when (mode) {
            "r" -> ParcelFileDescriptor.MODE_READ_ONLY
            "w", "wt" -> ParcelFileDescriptor.MODE_WRITE_ONLY or
                ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_TRUNCATE
            "wa" -> ParcelFileDescriptor.MODE_WRITE_ONLY or
                ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_APPEND
            "rw" -> ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE
            "rwt" -> ParcelFileDescriptor.MODE_READ_WRITE or
                ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_TRUNCATE
            else -> throw IllegalArgumentException("Modo de acesso inválido")
        }
        file.parentFile?.mkdirs()
        return ParcelFileDescriptor.open(file, flags)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? =
        throw UnsupportedOperationException("Somente arquivos temporários")

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = throw UnsupportedOperationException("Somente arquivos temporários")

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return if (resolveFile(uri).delete()) 1 else 0
    }

    private fun resolveFile(uri: Uri): File {
        val appContext = context ?: throw FileNotFoundException("Contexto indisponível")
        if (uri.authority != "${appContext.packageName}.fileprovider") {
            throw FileNotFoundException("Autoridade inválida")
        }

        val root = File(appContext.cacheDir, "shared").canonicalFile
        val relativePath = uri.pathSegments.joinToString(File.separator)
        val target = File(root, relativePath).canonicalFile
        if (target != root && !target.path.startsWith(root.path + File.separator)) {
            throw SecurityException("Caminho fora do cache compartilhado")
        }
        return target
    }

    companion object {
        fun getUriForFile(context: Context, file: File): Uri {
            val root = File(context.cacheDir, "shared").canonicalFile
            val target = file.canonicalFile
            if (!target.path.startsWith(root.path + File.separator)) {
                throw IllegalArgumentException("Arquivo fora do cache compartilhado")
            }
            val relativePath = target.relativeTo(root).invariantSeparatorsPath
            return Uri.Builder()
                .scheme("content")
                .authority("${context.packageName}.fileprovider")
                .appendEncodedPath(relativePath)
                .build()
        }
    }
}
