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
                } else {
                    // Jika teks dihapus, tampilkan ulang data acara
                    viewModel.fetchNotAvailableEvents()
                }
                false
            }

            // Menambahkan TextWatcher untuk mendeteksi perubahan teks pencarian
            searchView.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = s.toString()
                    if (query.isEmpty()) {
                        // Tampilkan ulang data acara jika teks pencarian dihapus
                        viewModel.fetchNotAvailableEvents()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Observe data dari ViewModel
            viewModel.eventResponse.observe(viewLifecycleOwner) { eventResponse ->
                if (eventResponse?.listEvents.isNullOrEmpty()) {
                    // Jika hasil pencarian tidak ada, sembunyikan RecyclerView dan tampilkan pesan error
                    rvEventNotAvailable.visibility = View.GONE
                    tvErrorMessage.visibility = View.VISIBLE
                } else {
                    // Jika ada hasil, tampilkan RecyclerView dan sembunyikan pesan error
                    rvEventNotAvailable.visibility = View.VISIBLE
                    if (eventResponse != null) {
                        adapter.submitList(eventResponse.listEvents)
                    }
                    tvErrorMessage.visibility = View.GONE
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
