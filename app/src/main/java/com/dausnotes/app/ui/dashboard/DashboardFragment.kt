package com.dausnotes.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dausnotes.app.databinding.FragmentDashboardBinding
import com.dausnotes.app.utils.TimeFormatter

class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: DashboardViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        
        setupObservers()
    }
    
    private fun setupObservers() {
        viewModel.dashboardStats.observe(viewLifecycleOwner) { stats ->
            binding.apply {
                tvTotalNotes.text = stats.totalNotes.toString()
                tvFavoriteNotes.text = stats.favoriteNotes.toString()
                tvSessionsToday.text = stats.sessionsToday.toString()
                tvFocusTime.text = TimeFormatter.formatDuration(stats.focusTimeToday)
                tvCurrentStreak.text = stats.currentStreak.toString()
            }
        }
        
        viewModel.recentNotes.observe(viewLifecycleOwner) { notes ->
            if (notes.isEmpty()) {
                binding.rvRecentNotes.visibility = View.GONE
                binding.tvNoRecentNotes.visibility = View.VISIBLE
            } else {
                binding.rvRecentNotes.visibility = View.VISIBLE
                binding.tvNoRecentNotes.visibility = View.GONE
                // TODO: Setup RecyclerView adapter
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}