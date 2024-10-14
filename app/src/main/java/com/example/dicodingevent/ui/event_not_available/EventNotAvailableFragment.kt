package com.example.dicodingevent.ui.event_not_available

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        viewModel = ViewModelProvider(this)[EventNotAvailableViewModel::class.java]
        adapter = EventNotAvailableAdapter { eventId ->
            val action = EventNotAvailableFragmentDirections.actionNavigationEventNotAvailableToEventDetail(eventId)
            findNavController().navigate(action)
        }

        with(binding) {
            rvEventNotAvailable.layoutManager = LinearLayoutManager(context)
            rvEventNotAvailable.adapter = adapter


            searchView.setupWithSearchBar(searchBar)


            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.editText.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.searchEvents(query)
                    searchBar.setText(query)
                    searchView.hide()
                    // Reset error message saat pencarian baru dimulai
                    binding.tvErrorMessage.visibility = View.GONE
                } else {

                    viewModel.fetchNotAvailableEvents()
                }
                false
            }


            searchView.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = s.toString()
                    if (query.isEmpty()) {

                        viewModel.fetchNotAvailableEvents()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })


            viewModel.eventResponse.observe(viewLifecycleOwner) { eventResponse ->
                if (eventResponse?.listEvents.isNullOrEmpty()) {

                    rvEventNotAvailable.visibility = View.GONE
                    tvErrorMessage.visibility = View.VISIBLE
                } else {

                    rvEventNotAvailable.visibility = View.VISIBLE
                    if (eventResponse != null) {
                        adapter.submitList(eventResponse.listEvents)
                    }
                    tvErrorMessage.visibility = View.GONE
                    viewModel.clearErrorMessage()

                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {

                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvErrorMessage.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }


            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                if (!errorMessage.isNullOrEmpty()) {
                    tvErrorMessage.text = errorMessage
                    tvErrorMessage.visibility = View.VISIBLE
                    rvEventNotAvailable.visibility = View.GONE
                } else {
                    tvErrorMessage.visibility = View.GONE
                }
            }
        }

        // Fetch the initial data
        viewModel.fetchNotAvailableEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
