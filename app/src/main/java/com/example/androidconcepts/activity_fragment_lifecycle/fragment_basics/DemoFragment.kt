package com.example.androidconcepts.activity_fragment_lifecycle.fragment_basics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidconcepts.databinding.FragmentDemoBinding

class DemoFragment : Fragment() {

    interface FragmentLogger {
        fun onFragmentLog(message: String)
    }

    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!
    private var logger: FragmentLogger? = null
    private var tapCount = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger = context as? FragmentLogger
        logger?.onFragmentLog("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger?.onFragmentLog("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        logger?.onFragmentLog("onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger?.onFragmentLog("onViewCreated")
        binding.btnTap.setOnClickListener {
            tapCount++
            binding.tvTapCount.text = "Taps: $tapCount"
        }
    }

    override fun onStart() {
        super.onStart()
        logger?.onFragmentLog("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger?.onFragmentLog("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger?.onFragmentLog("onPause")
    }

    override fun onStop() {
        super.onStop()
        logger?.onFragmentLog("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger?.onFragmentLog("onDestroyView  ← _binding = null")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        logger?.onFragmentLog("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logger?.onFragmentLog("onDetach")
        logger = null
    }
}
