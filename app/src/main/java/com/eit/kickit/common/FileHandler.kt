package com.eit.kickit.common

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.util.IOUtils
import com.eit.kickit.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class FileHandler(_context: Context){

    private val credentials = BasicAWSCredentials("AKIAWA23UQDWZ3L2WO7Y", "sFG8c/GhvUHgDMV8V83agbkQJ7Okk/FAQZS9XToE" )
    private val client = AmazonS3Client(credentials)
    private val context = _context

    fun uploadFile(fileUri: Uri?){

        val transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(client)
            .build()

        var file = File(context.filesDir, MainActivity.adventurer?.advEmail.toString())

        var inputStream = context.contentResolver.openInputStream(fileUri!!)
        var outputStream = FileOutputStream(file)
        IOUtils.copy(inputStream, outputStream)
        inputStream.close()
        outputStream.close()


        val uploadObserver = transferUtility.upload("jsaS3/placeholder.jpg", file)

        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    println("SUCEŠSSS")
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                println(percentDone)
            }

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
            }
        })

        if (TransferState.COMPLETED == uploadObserver.state) {
            println("SUCCESS!!!!!")
        }
    }

//    private fun createFile(fileUri: Uri?){
//        try {
//            val inputStream = context.contentResolver.openInputStream(fileUri!!) ?: return
//            val outputStream = FileOutputStream(file)
//            IOUtils.copy(inputStream, outputStream)
//            inputStream.close()
//            outputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

    private fun getFileExtension(fileUri: Uri): String{
        val contentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri)).toString()
    }

}