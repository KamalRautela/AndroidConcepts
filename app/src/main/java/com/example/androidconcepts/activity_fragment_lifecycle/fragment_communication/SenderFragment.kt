package com.example.androidconcepts.activity_fragment_lifecycle.fragment_communication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.androidconcepts.databinding.FragmentSenderBinding

class SenderFragment : Fragment() {

    private var _binding: FragmentSenderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUi()
    }

    private fun bindUi() = with(binding) {
        btnSendResultApi.setOnClickListener {
            val msg = etMessage.text.toString().trim()
            if (msg.isEmpty()) return@setOnClickListener
            val bundle = Bundle().apply { putString("message", msg) }
            parentFragmentManager.setFragmentResult("comm_key", bundle)
            etMessage.text?.clear()
        }

        btnSendViewModel.setOnClickListener {
            val msg = etMessage.text.toString().trim()
            if (msg.isEmpty()) return@setOnClickListener
            viewModel.sendMessage(msg)
            etMessage.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
