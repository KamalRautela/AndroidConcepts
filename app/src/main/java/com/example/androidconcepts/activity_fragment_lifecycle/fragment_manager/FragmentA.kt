package com.example.androidconcepts.activity_fragment_lifecycle.fragment_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidconcepts.databinding.FragmentABinding

class FragmentA : Fragment() {
    private var _binding: FragmentABinding? = null
    private val binding get() = _binding!!
    private var tapCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentABinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTap.setOnClickListener {
            tapCount++
            binding.tvTapCount.text = "Taps: $tapCount"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
