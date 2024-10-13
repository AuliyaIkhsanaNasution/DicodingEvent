package com.example.dicodingevent.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.dicodingevent.databinding.FragmentEventDetailBinding
import data.response.DetailEvent

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId")
        if (eventId != null) {
            if (viewModel.eventData.value == null) {
                viewModel.loadDetailEvent(eventId)
            }
            observeViewModel()
        } else {
            showErrorMessage("Event ID not available")
        }
    }

    private fun observeViewModel() {
        viewModel.eventData.observe(viewLifecycleOwner) { event ->
            event?.let { updateUI(it) }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            showErrorMessage(errorMessage)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(event: DetailEvent) {
        binding.apply {
            eventName.text = event.name
            category.text = event.category
            ownerName.text = "Penyelenggara: ${event.ownerName}"
            beginTime.text = "Waktu Mulai: ${event.beginTime}"
            endTime.text = "Waktu Selesai: ${event.endTime}"
            quota.text = "Kuota: ${event.quota}"
            eventDetails.text = event.summary
            description.text =
                HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            Glide.with(this@EventDetailFragment)
                .load(event.mediaCover)
                .into(eventImage)


            openLinkButton.setOnClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(event.link)).also { intent ->
                    startActivity(intent)
                }
            }
        }
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
