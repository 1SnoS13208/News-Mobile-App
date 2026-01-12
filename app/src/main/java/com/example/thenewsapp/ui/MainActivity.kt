package com.example.thenewsapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.thenewsapp.R
import com.example.thenewsapp.database.ArticleDatabase
import com.example.thenewsapp.databinding.ActivityMainBinding
import com.example.thenewsapp.repository.NewsRepository

class MainActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById((R.id.nav_host_news)) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        // Handle destination changes for bottom nav visibility and state
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.article_fragment -> {
                    // Hide bottom nav when viewing article for cleaner experience
                    binding.bottomNav.visibility = View.GONE
                }
                R.id.headlines_fragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.bottomNav.menu.findItem(R.id.headlines_fragment)?.isChecked = true
                }
                R.id.favourites_fragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.bottomNav.menu.findItem(R.id.favourites_fragment)?.isChecked = true
                }
                R.id.search_fragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.bottomNav.menu.findItem(R.id.search_fragment)?.isChecked = true
                }
            }
        }

        // Handle bottom nav item reselection to pop back stack
        binding.bottomNav.setOnItemReselectedListener { item ->
            // Pop the back stack to the start destination of that tab
            navController.popBackStack(item.itemId, inclusive = false)
        }
    }
}