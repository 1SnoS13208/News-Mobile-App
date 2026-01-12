package com.example.thenewsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.ui.MainActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var newsViewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as MainActivity).newsViewModel
        val article = args.article

        binding.apply {
            // Load article image
            Glide.with(this@ArticleFragment)
                .load(article.urlToImage)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(articleImage)

            // Set article title
            articleTitle.text = article.title ?: "No title available"
            
            // Set source and date
            val sourceName = article.source?.name ?: "Unknown Source"
            val date = formatDate(article.publishedAt)
            articleSourceDate.text = getString(R.string.article_source_date, sourceName, date)
            
            // Set description
            articleDescription.text = article.description ?: ""
            if (article.description.isNullOrBlank()) {
                articleDescription.visibility = View.GONE
            } else {
                articleDescription.visibility = View.VISIBLE
            }
            
            // Set initial content (may be truncated from API)
            val initialContent = article.content?.replace(Regex("\\[\\+\\d+ chars\\]"), "") ?: ""
            articleContentText.text = if (initialContent.isNotBlank()) {
                initialContent
            } else {
                "Loading full article..."
            }

            // Try to fetch full content from the article URL
            article.url?.let { url ->
                newsViewModel.getArticleContent(url) { fullContent ->
                    if (!fullContent.isNullOrBlank() && fullContent.length > initialContent.length) {
                        articleContentText.text = fullContent
                        // Update article content for offline reading
                        article.content = fullContent
                    } else if (initialContent.isBlank()) {
                        // Could not load content
                        articleContentText.text = article.description 
                            ?: "Content could not be loaded. Please check your internet connection."
                    }
                }
            }
        }

        // Favourite button
        binding.favouritesButton.setOnClickListener {
            newsViewModel.addToFavourites(article)
            Snackbar.make(view, R.string.article_saved_success, Snackbar.LENGTH_SHORT).show()
        }

        // Back button
        binding.fabBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "Unknown date"
        return try {
            // Parse ISO date format and convert to readable format
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
            val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString.substring(0, 10)
        } catch (e: Exception) {
            // If parsing fails, try to extract just the date part
            try {
                dateString.substring(0, 10)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}