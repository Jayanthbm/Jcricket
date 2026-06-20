package com.jayanth.jcricket.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jayanth.jcricket.data.model.Match
import com.jayanth.jcricket.data.model.MatchDetailsData
import com.jayanth.jcricket.data.repository.MatchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val matches: List<Match>, val lastUpdated: Long) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class MatchViewModel : ViewModel() {
    private val repository = MatchRepository()

    private val _dashboardUiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val dashboardUiState: StateFlow<DashboardUiState> = _dashboardUiState.asStateFlow()

    private val _refreshingMatches = MutableStateFlow(false)
    val refreshingMatches: StateFlow<Boolean> = _refreshingMatches.asStateFlow()

    private val _selectedMatch = MutableStateFlow<Match?>(null)
    val selectedMatch: StateFlow<Match?> = _selectedMatch.asStateFlow()

    private val _matchDetails = MutableStateFlow<MatchDetailsData?>(null)
    val matchDetails: StateFlow<MatchDetailsData?> = _matchDetails.asStateFlow()

    private val _detailsLoading = MutableStateFlow(false)
    val detailsLoading: StateFlow<Boolean> = _detailsLoading.asStateFlow()

    private val _detailsRefreshing = MutableStateFlow(false)
    val detailsRefreshing: StateFlow<Boolean> = _detailsRefreshing.asStateFlow()

    // Tick flow to force recomposition of human-readable relative time strings
    private val _timeTick = MutableStateFlow(0)
    val timeTick: StateFlow<Int> = _timeTick.asStateFlow()

    private var pollingJob: Job? = null
    private var timeTickJob: Job? = null

    init {
        fetchMatches()
        startTimers()
    }

    fun fetchMatches(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _refreshingMatches.value = true
            } else {
                _dashboardUiState.value = DashboardUiState.Loading
            }
            try {
                val data = repository.getMatches()
                _dashboardUiState.value = DashboardUiState.Success(data.matches, data.lastUpdated)
            } catch (e: Exception) {
                if (!isRefresh) {
                    _dashboardUiState.value = DashboardUiState.Error(e.message ?: "Failed to fetch matches")
                }
            } finally {
                _refreshingMatches.value = false
            }
        }
    }

    fun openMatch(match: Match) {
        viewModelScope.launch {
            _selectedMatch.value = match
            _detailsLoading.value = true
            try {
                val details = repository.getMatchDetails(match.matchId)
                _matchDetails.value = details
            } catch (e: Exception) {
                e.printStackTrace()
                // Keep selectedMatch but details remain null; UI can render error state
            } finally {
                _detailsLoading.value = false
            }
        }
    }

    fun refreshMatchDetails() {
        val match = _selectedMatch.value ?: return
        viewModelScope.launch {
            _detailsRefreshing.value = true
            try {
                val details = repository.getMatchDetails(match.matchId)
                _matchDetails.value = details
            } catch (e: Exception) {
                e.printStackTrace()
                // Log/handle error
            } finally {
                _detailsRefreshing.value = false
            }
        }
    }

    fun closeMatchDetails() {
        _selectedMatch.value = null
        _matchDetails.value = null
    }

    private fun startTimers() {
        // 1. Time ticks every 10 seconds for relative time strings
        timeTickJob = viewModelScope.launch {
            while (true) {
                delay(10000)
                _timeTick.value += 1
            }
        }

        // 2. Dashboard or Live details auto-refresh loop
        pollingJob = viewModelScope.launch {
            while (true) {
                val match = _selectedMatch.value
                val details = _matchDetails.value

                if (match == null) {
                    // Dashboard view: refresh matches list every 15 seconds
                    delay(15000)
                    try {
                        val data = repository.getMatches()
                        _dashboardUiState.value = DashboardUiState.Success(data.matches, data.lastUpdated)
                    } catch (e: Exception) {
                        // Suppress background errors
                    }
                } else {
                    // Match details view: refresh details every 10 seconds if match is live
                    val state = details?.state?.lowercase() ?: ""
                    val isLive = state == "in progress" || state == "innings break"
                    if (isLive) {
                        delay(10000)
                        try {
                            val detailsUpdated = repository.getMatchDetails(match.matchId)
                            _matchDetails.value = detailsUpdated
                        } catch (e: Exception) {
                            // Suppress background errors
                        }
                    } else {
                        // Match is preview or finished; no auto-refresh loop needed, just check occasionally or wait
                        delay(30000)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        timeTickJob?.cancel()
    }
}
