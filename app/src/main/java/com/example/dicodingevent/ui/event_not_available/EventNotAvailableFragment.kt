package com.example.dicodingevent.ui.event_not_available

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentEventNotAvailableBinding

class EventNotAvailableFragment : Fragment() {

    private var _binding: FragmentEventNotAvailableBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventNotAvailableViewModel
    private lateinit var adapter: EventNotAvailableAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventNotAvailableBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(EventNotAvailableViewModel::class.java)
        adapter = EventNotAvailableAdapter { eventId ->
            val action = EventNotAvailableFragmentDirections.actionNavigationEventNotAvailableToEventDetail(eventId)
            findNavController().navigate(action)
        }

        with(binding) {
            rvEventNotAvailable.layoutManager = LinearLayoutManager(context)
            rvEventNotAvailable.adapter = adapter

            // Mengaitkan SearchBar dengan SearchView
            searchView.setupWithSearchBar(searchBar)

            // Set listener untuk editor action (misalnya ketika pengguna menekan "Enter")
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.editText.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.searchEvents(query)
                    searchBar.setText(query)
                    searchView.hide()
                }
                false
            }

            // Observe data dari ViewModel
            viewModel.eventResponse.observe(viewLifecycleOwner) { eventResponse ->
                eventResponse?.listEvents?.let { events ->
                    adapter.submitList(events)
                    tvErrorMessage.visibility = View.GONE // Hide error message
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                if (!errorMessage.isNullOrEmpty()) {
                    tvErrorMessage.text = errorMessage
                    tvErrorMessage.visibility = View.VISIBLE
                } else {
                    tvErrorMessage.visibility = View.GONE
                }
            }
        }

        viewModel.fetchNotAvailableEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
