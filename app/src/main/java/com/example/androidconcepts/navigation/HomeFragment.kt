package com.example.androidconcepts.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidconcepts.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun bindUi() {
        binding.itemKotlin.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(1,"Kotlin")
            findNavController().navigate(action)
        }
        binding.itemAndroid.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(itemId = 2, itemTitle = "Android")
            findNavController().navigate(action)
        }

        binding.itemRetrofit.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(itemId = 3, itemTitle = "Retrofit")
            findNavController().navigate(action)
        }
    }
}