package com.example.feemanagementsystem.global

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.feemanagementsystem.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Suppress("DEPRECATION")
class CaptureImage(var context: Context) {
    var imagePath = ""

    fun setImageUri(): Uri {
        val imgUri: Uri
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val file = File(
                Environment.getExternalStorageDirectory().toString() + "/DCIM/",
                context.getString(R.string.app_name) + Calendar.getInstance().timeInMillis + ".png"
            )
            //imgUri = Uri.fromFile(file);
            imgUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            imagePath = file.absolutePath
        } else {
            val file = File(
                context.filesDir,
                context.getString(R.string.app_name) + Calendar.getInstance().timeInMillis + ".png"
            )
            imgUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            imagePath = file.absolutePath
        }
        return imgUri
    }

    fun getRightAngleImage(photoPath: String): String {
        try {
            val ei = ExifInterface(photoPath)
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            var degree = 0
            degree = when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }
            return rotateImage(degree, photoPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return photoPath
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        var path: String? = ""
        path = try {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                System.currentTimeMillis().toString(),
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?, mContext: Context): String {
        val cursor = mContext.contentResolver.query(uri!!, null, null, null, null)
        var path = ""
        try {
            cursor!!.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            path = cursor.getString(idx)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return path
    }

    fun decodeFile(path: String?): Bitmap? {
        try {
            // Decode deal_image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, o)
            // The new size we want to scale to
            val REQUIRED_SIZE = 1024

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2
            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeFile(path, o2)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun getPath(uri: Uri, context: Context): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    companion object {
        private fun rotateImage(degree: Int, imagePath: String): String {
            if (degree <= 0) {
                return imagePath
            }
            try {
                var b = BitmapFactory.decodeFile(imagePath)
                val matrix = Matrix()
                if (b.width > b.height) {
                    matrix.setRotate(degree.toFloat())
                    b = Bitmap.createBitmap(
                        b, 0, 0, b.width, b.height,
                        matrix, true
                    )
                }
                val fOut = FileOutputStream(imagePath)
                val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)
                val out = FileOutputStream(imagePath)
                if (imageType.equals("png", ignoreCase = true)) {
                    b.compress(Bitmap.CompressFormat.PNG, 100, out)
                } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                        "jpg",
                        ignoreCase = true
                    )
                ) {
                    b.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                fOut.flush()
                fOut.close()
                b.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return imagePath
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }
    }
}