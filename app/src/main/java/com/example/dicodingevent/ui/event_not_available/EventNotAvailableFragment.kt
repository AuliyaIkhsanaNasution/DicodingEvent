package com.example.dicodingevent.ui.event_not_available

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.databinding.FragmentEventNotAvailableBinding

class EventNotAvailableFragment : Fragment() {

    private var _binding: FragmentEventNotAvailableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(EventNotAvailableViewModel::class.java)

        _binding = FragmentEventNotAvailableBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotAvailable
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}