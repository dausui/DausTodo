package com.dausnotes.app.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dausnotes.app.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {
    
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: NotesViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            // TODO: Update RecyclerView adapter
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                // TODO: Show error message
                viewModel.clearError()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading indicator
        }
    }
    
    private fun setupClickListeners() {
        // TODO: Setup FAB click listener for adding new note
        // TODO: Setup search functionality
        // TODO: Setup category filter
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}