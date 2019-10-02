package com.eit.kickit.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.util.IOUtils
import com.eit.kickit.MainActivity
import com.eit.kickit.R
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import java.io.FileOutputStream
import javax.net.ssl.ManagerFactoryParameters

class FileHandler(_context: Context){

    companion object {
        var downloading = false
    }

    private val credentials = BasicAWSCredentials(ACCESSKEY,  SECRET)
    private val client = AmazonS3Client(credentials)
    private val context = _context
    private lateinit var transferUtility: TransferUtility

    private fun initTransferUtil(){
        transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(client)
            .build()
    }

    fun createTransferUtil() : TransferUtility{

        initTransferUtil()

        return transferUtility

    }

    fun uploadFile(fileUri: Uri?, fileName: String, profilePic: Boolean){

        initTransferUtil()

        val file = File(context.filesDir, "tempFile")

        val inputStream = context.contentResolver.openInputStream(fileUri!!)
        val outputStream = FileOutputStream(file)
        IOUtils.copy(inputStream, outputStream)
        inputStream!!.close()
        outputStream.close()


        val uploadObserver = transferUtility.upload(S3LINK + fileName, file)


        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    if (profilePic){
                        MainActivity.adventurer?.setPic(BitmapFactory.decodeFile(file.path))
                    }
                    Toast.makeText(context, "File Uploaded", Toast.LENGTH_LONG).show()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                println(percentDone)
            }

            override fun onError(id: Int, ex: Exception) {
                Toast.makeText(context, "Error uploading your photo, please try again!", Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
            }
        })

        if (TransferState.COMPLETED == uploadObserver.state) {
            Toast.makeText(context, "File Uploaded", Toast.LENGTH_LONG).show()
        }
    }

    fun downloadImage(fileName: String, imgView: ImageView, loading: ProgressBar) : Bitmap {
        downloading = true

        val tempFile = File.createTempFile(fileName, ".jpg")

        initTransferUtil()

        println(S3LINK + fileName)

        val downloadObserver = transferUtility.download(S3LINK + fileName, tempFile)

        downloadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    imgView.setImageBitmap(createBitmap(tempFile))
                    loading.visibility = View.INVISIBLE
                    downloading = false
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                println(percentDone)
                loading.progress = percentDone
            }

            override fun onError(id: Int, ex: Exception) {
                Toast.makeText(context, "Downloading File Failed", Toast.LENGTH_SHORT).show()
                downloading = false
                ex.printStackTrace()
            }
        })



        return if (TransferState.COMPLETED == downloadObserver.state) {
            createBitmap(tempFile)
        } else{
            BitmapFactory.decodeResource(context.resources, R.drawable.placeholder_image)
        }
    }

    private fun createBitmap(file: File) : Bitmap {
        val filePath = file.path
        return BitmapFactory.decodeFile(filePath)
    }
}