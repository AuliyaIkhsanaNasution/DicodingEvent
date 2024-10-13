package com.example.dicodingevent.ui.event_not_available

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingevent.databinding.FragmentEventNotAvailableBinding
import data.response.ListEventsItem

class EventNotAvailableFragment : Fragment() {

    private var _binding: FragmentEventNotAvailableBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventNotAvailableAdapter
    private lateinit var viewModel: EventNotAvailableViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventNotAvailableBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventNotAvailableAdapter { eventId ->
            val action = EventNotAvailableFragmentDirections.actionNavigationEventNotAvailableToEventDetail(eventId)
            findNavController().navigate(action)
        }
        binding.rvEventNotAvailable.layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvEventNotAvailable.addItemDecoration(itemDecoration)
        binding.rvEventNotAvailable.adapter = eventAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EventNotAvailableViewModel::class.java)

        viewModel.eventResponse.observe(viewLifecycleOwner) { eventResponse ->
            if (eventResponse != null) {
                setEventData(eventResponse.listEvents)
                binding.tvErrorMessage.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                showErrorMessage(errorMessage)
            }
        }

        if (viewModel.eventResponse.value == null) {
            viewModel.fetchNotAvailableEvents()
        }
    }

    private fun setEventData(events: List<ListEventsItem>) {
        eventAdapter.submitList(events)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
