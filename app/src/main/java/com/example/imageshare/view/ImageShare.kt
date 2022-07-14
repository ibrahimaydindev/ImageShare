package com.example.imageshare.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.imageshare.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_image_share.*
import java.util.*

class imageShare : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var imageSelected: Uri? = null
    var imageBitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_share)
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    fun share(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"

        val reference = storage.reference

        val imageReference = reference.child("images").child(imageName)

        if (imageSelected != null) {
            imageReference.putFile(imageSelected!!).addOnSuccessListener { taskSnapshot ->
                val updateImageReference =
                    FirebaseStorage.getInstance().reference.child("images").child(imageName)
                updateImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val userEmail = auth.currentUser!!.email.toString()
                    val userComment = commentText.text.toString()
                    val date = Timestamp.now()

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("imageUrl", downloadUrl)
                    postHashMap.put("userEmail", userEmail)
                    postHashMap.put("userComment", userComment)
                    postHashMap.put("date", date)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    fun imageSelect(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )

        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)

            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            imageSelected = data.data
            if (imageSelected != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, imageSelected!!)
                    imageBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(imageBitmap)
                } else {
                    imageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imageSelected)
                    imageView.setImageBitmap(imageBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}


