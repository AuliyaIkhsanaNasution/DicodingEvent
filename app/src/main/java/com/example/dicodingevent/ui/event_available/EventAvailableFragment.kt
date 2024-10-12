package com.example.dicodingevent.ui.event_available

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingevent.databinding.FragmentEventAvailableBinding
import data.response.ListEventsItem

class EventAvailableFragment : Fragment() {

    private var _binding: FragmentEventAvailableBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAvailableAdapter
    private lateinit var viewModel: EventAvailableViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventAvailableBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupViewModel()
        fetchActiveEvents()

        return binding.root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAvailableAdapter()
        binding.rvEventAvailable.layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvEventAvailable.addItemDecoration(itemDecoration)
        binding.rvEventAvailable.adapter = eventAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EventAvailableViewModel::class.java)

        viewModel.eventResponse.observe(viewLifecycleOwner) { eventResponse ->
            if (eventResponse != null) {
                setEventData(eventResponse.listEvents)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun fetchActiveEvents() {
        viewModel.fetchActiveEvents()
    }

    private fun setEventData(events: List<ListEventsItem>) {
        eventAdapter.submitList(events)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
