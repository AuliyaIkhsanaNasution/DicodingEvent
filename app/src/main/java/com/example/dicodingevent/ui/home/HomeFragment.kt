package com.example.dicodingevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.example.dicodingevent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Setup ImageSlider
        val imageSlider: ImageSlider = binding.imageSlider

        // Setup RecyclerView
        homeAdapter = HomeAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        // Observe LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorMessage.text = errorMessage ?: ""
            binding.errorMessage.visibility = if (errorMessage != null) View.VISIBLE else View.GONE
        }

        // Observe slider data
        viewModel.eventSlider.observe(viewLifecycleOwner) { slides ->
            if (slides != null && slides.isNotEmpty()) {
                imageSlider.setImageList(slides)
            }
        }

        // Observe completed event list for RecyclerView
        viewModel.completedEventList.observe(viewLifecycleOwner) { events ->
            if (events != null) {
                homeAdapter.submitList(events)
            }
        }

        // Load data
        viewModel.loadEventData()
        viewModel.loadCompletedEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

