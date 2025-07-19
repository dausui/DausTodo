package com.dausnotes.app.ui.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dausnotes.app.databinding.FragmentPomodoroBinding
import com.dausnotes.app.utils.TimeFormatter

class PomodoroFragment : Fragment() {
    
    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PomodoroViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[PomodoroViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.timeRemaining.observe(viewLifecycleOwner) { timeRemaining ->
            // TODO: Update timer display
            // binding.tvTimer.text = TimeFormatter.formatTimer(timeRemaining)
        }
        
        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            // TODO: Update play/pause button state
        }
        
        viewModel.currentSessionType.observe(viewLifecycleOwner) { sessionType ->
            // TODO: Update session type display and colors
        }
        
        viewModel.isSessionComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                // TODO: Show completion dialog/notification
                viewModel.acknowledgeCompletion()
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                // TODO: Show error message
                viewModel.clearError()
            }
        }
    }
    
    private fun setupClickListeners() {
        // TODO: Setup timer control buttons
        // TODO: Setup session type selection
        // TODO: Setup note linking functionality
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}