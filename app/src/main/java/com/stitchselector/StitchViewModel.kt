package com.stitchselector

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StitchViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = Storage(application)
    private val _state = MutableStateFlow(storage.load())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private fun update(change: (AppState) -> AppState) {
        val newState = change(_state.value)
        _state.value = newState
        storage.save(newState)
    }

    fun setProjectName(name: String) = update { it.copy(projectName = name) }
    fun newStitch() = update { StitchLogic.pick(it) }
    fun newProject(name: String) = update { StitchLogic.newProject(it, name) }

    fun addColor(name: String) = update { StitchLogic.addColor(it, name) }
    fun renameColor(id: Long, name: String) = update { StitchLogic.renameColor(it, id, name) }
    fun removeColor(id: Long) = update { StitchLogic.removeColor(it, id) }

    fun addStitch(name: String) = update { StitchLogic.addStitch(it, name) }
    fun renameStitch(id: Long, name: String) = update { StitchLogic.renameStitch(it, id, name) }
    fun removeStitch(id: Long) = update { StitchLogic.removeStitch(it, id) }
}
