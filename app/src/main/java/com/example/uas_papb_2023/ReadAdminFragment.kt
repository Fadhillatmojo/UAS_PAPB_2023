package com.example.uas_papb_2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.uas_papb_2023.adapter.RvAdminAdapter
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.FragmentReadAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class ReadAdminFragment : Fragment() {
    private lateinit var binding: FragmentReadAdminBinding
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val movieCollection = firebaseFirestore.collection("movies")
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReadAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMovie()
        observeMoviesChanges()
    }

    private fun observeMovie(){
        movieListLiveData.observe(this){ movies ->
            // Setel data ke RecyclerView Adapter
            val adapter = RvAdminAdapter(movies,
                onClickItemMovie =  { movie->
                    // Implementasi ketika item diklik
                    val intentToDetailFilmActivity = Intent(context, DetailFilmActivity::class.java)
                    intentToDetailFilmActivity.putExtra("EXT_MOVIE", movie)
                    startActivity(intentToDetailFilmActivity)
                }
                , onLongClickItemMovie = { movie->
                    // Implementasi ketika item diklik lama
                    val viewPagerAdmin = MainAdminActivity.viewPagerAdmin
                    // Mendapatkan referensi ke fragment ke-0 dalam ViewPager
                    val crudFragment = viewPagerAdmin.adapter?.instantiateItem(viewPagerAdmin, 0) as CrudFragment
                    crudFragment.setDataUpdate(movie)
                    viewPagerAdmin.currentItem = 0
                })
            binding.rvMovie.adapter = adapter
        }
    }

    private fun observeMoviesChanges() {
        movieCollection.addSnapshotListener{ snapshots, error ->
            // jika dia terjadi error maka dia akan memunculkan log
            if(error != null){
                Log.d("MainAdminActivity", "Error listening for movies changes: ", error)
                return@addSnapshotListener
            }
            // jika gak error maka akan langsung ke sini
            val movies = snapshots?.toObjects(Movie::class.java)

            if (movies != null) {
                movieListLiveData.postValue(movies)
            }
        }
    }
}