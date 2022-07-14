package com.example.imageshare.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageshare.R
import com.example.imageshare.adapter.feedRecyclerAdapter
import com.example.imageshare.view.imageShare
import com.example.imageshare.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class feedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: feedRecyclerAdapter
    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getData()
        var layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        recyclerViewAdapter= feedRecyclerAdapter(postList)
        recyclerView.adapter=recyclerViewAdapter



    }

    fun getData() {
        //Sıralama işlemi tarihe göre
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val documents = snapshot.documents
                            postList.clear()
                            for (document in documents) {
                                val userEmails = document.get("userEmail") as String
                                val userComments = document.get("userComment") as String
                                val imageUrls = document.get("imageUrl") as String

                                val downloadPost = Post(userEmails, userComments, imageUrls)
                                postList.add(downloadPost)

                            }
                            recyclerViewAdapter.notifyDataSetChanged()

                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.shareImage) {
            val intent = Intent(this, imageShare::class.java)
            startActivity(intent)


        } else if (item.itemId == R.id.logOut) {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        return super.onOptionsItemSelected(item)
    }
}