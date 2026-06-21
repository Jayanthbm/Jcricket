package com.jayanth.jcricket.wear.ui.viewmodel

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

sealed interface WearUiState {
    object Loading : WearUiState
    data class Success(val matches: List<Match>) : WearUiState
    data class Error(val message: String) : WearUiState
}

class WearMatchViewModel : ViewModel() {
    private val repository = MatchRepository()

    private val _uiState = MutableStateFlow<WearUiState>(WearUiState.Loading)
    val uiState: StateFlow<WearUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedMatch = MutableStateFlow<Match?>(null)
    val selectedMatch: StateFlow<Match?> = _selectedMatch.asStateFlow()

    private val _matchDetails = MutableStateFlow<MatchDetailsData?>(null)
    val matchDetails: StateFlow<MatchDetailsData?> = _matchDetails.asStateFlow()

    private val _detailsLoading = MutableStateFlow(false)
    val detailsLoading: StateFlow<Boolean> = _detailsLoading.asStateFlow()

    private val _isAmbient = MutableStateFlow(false)
    val isAmbient: StateFlow<Boolean> = _isAmbient.asStateFlow()

    private var isPollingActive = true

    private var pollingJob: Job? = null
    private var detailsJob: Job? = null

    init {
        fetchMatches()
        startPolling()
    }

    fun fetchMatches(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = WearUiState.Loading
            }
            try {
                val data = repository.getMatches()
                _uiState.value = WearUiState.Success(data.matches)
            } catch (e: Exception) {
                if (!isRefresh) {
                    _uiState.value = WearUiState.Error(e.message ?: "Failed to fetch matches")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun selectMatch(match: Match?) {
        detailsJob?.cancel()
        _selectedMatch.value = match
        _matchDetails.value = null
        if (match == null) {
            return
        }
        detailsJob = viewModelScope.launch {
            _detailsLoading.value = true
            try {
                val details = repository.getMatchDetails(match.matchId)
                _matchDetails.value = details
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _detailsLoading.value = false
            }
        }
    }

    fun refreshMatchDetails() {
        val match = _selectedMatch.value ?: return
        viewModelScope.launch {
            try {
                val details = repository.getMatchDetails(match.matchId)
                _matchDetails.value = details
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setAmbientMode(enabled: Boolean) {
        if (_isAmbient.value == enabled) return
        _isAmbient.value = enabled
        if (enabled) {
            pollingJob?.cancel()
            pollingJob = null
        } else {
            if (isPollingActive) {
                startPolling()
            }
        }
    }

    fun setPollingActive(active: Boolean) {
        if (isPollingActive == active) return
        isPollingActive = active
        if (active) {
            if (!_isAmbient.value) {
                startPolling()
            }
        } else {
            pollingJob?.cancel()
            pollingJob = null
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            var failureCount = 0
            while (true) {
                if (_isAmbient.value) break
                val match = _selectedMatch.value
                if (match == null) {
                    val baseDelay = 15000L
                    val currentDelay = baseDelay + (failureCount * 10000L).coerceAtMost(45000L)
                    delay(currentDelay)
                    try {
                        val data = repository.getMatches()
                        _uiState.value = WearUiState.Success(data.matches)
                        failureCount = 0
                    } catch (e: Exception) {
                        failureCount++
                    }
                } else {
                    val details = _matchDetails.value
                    val state = details?.state?.lowercase() ?: ""
                    val isLive = state == "in progress" || state == "innings break"
                    if (isLive) {
                        val baseDelay = 10000L
                        val currentDelay = baseDelay + (failureCount * 5000L).coerceAtMost(20000L)
                        delay(currentDelay)
                        try {
                            val detailsUpdated = repository.getMatchDetails(match.matchId)
                            _matchDetails.value = detailsUpdated
                            failureCount = 0
                        } catch (e: Exception) {
                            failureCount++
                        }
                    } else {
                        delay(30000)
                        try {
                            val detailsUpdated = repository.getMatchDetails(match.matchId)
                            _matchDetails.value = detailsUpdated
                            failureCount = 0
                        } catch (e: Exception) {
                            // Suppress completed/preview errors
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        detailsJob?.cancel()
    }
}
