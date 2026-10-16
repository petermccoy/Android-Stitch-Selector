package com.stitchselector

import kotlin.random.Random

data class Item(val id: Long, val name: String)

data class LogEntry(val stitch: String, val color: String, val timeMillis: Long)

data class AppState(
    val projectName: String = DEFAULT_PROJECT_NAME,
    val colors: List<Item> = emptyList(),
    val stitches: List<Item> = emptyList(),
    /** Ids of stitches already chosen in the current round, in the order they were chosen. */
    val usedStitchIds: List<Long> = emptyList(),
    /** Every stitch/color pick for the current project, oldest first. */
    val log: List<LogEntry> = emptyList(),
    val nextId: Long = 1,
) {
    val usedStitches: List<Item>
        get() = usedStitchIds.mapNotNull { id -> stitches.firstOrNull { it.id == id } }

    val availableStitches: List<Item>
        get() = stitches.filter { it.id !in usedStitchIds }

    val current: LogEntry? get() = log.lastOrNull()

    companion object {
        const val DEFAULT_PROJECT_NAME = "Project1"

        val DEFAULT_STITCHES = listOf(
            "SC cross stitch",
            "Trinity stitch",
            "Uneven berry stitch",
            "Vase stitch",
            "Waffle stitch",
            "Wave stitch 2",
            "Wicker stitch",
            "Feather stitch",
            "Zipper stitch",
            "Star stitch",
            "Moss stitch",
            "Bead stitch",
            "Blanket stitch",
            "Cable stitch",
            "Iris stitch",
            "Open ridge stitch",
            "Pretty picot mesh stitch",
            "Royal ridge stitch",
            "Shell stitch",
            "Waistcoat stitch",
            "Puff stitch",
        )

        val DEFAULT_COLORS = listOf(
            "Katera's purple",
            "Periwinkle",
            "Mint tea",
            "Purple",
            "Grape",
            "Blue green",
            "Light sage",
            "Dark olive",
            "Forest",
        )

        fun defaults(): AppState {
            var id = 1L
            val colors = DEFAULT_COLORS.map { Item(id++, it) }
            val stitches = DEFAULT_STITCHES.map { Item(id++, it) }
            return AppState(colors = colors, stitches = stitches, nextId = id)
        }
    }
}

/** All state changes live here as pure functions so they are easy to test. */
object StitchLogic {

    /**
     * Picks a random color (from all colors) and a random stitch from those not yet used this
     * round. When every stitch has been used, all stitches go back into the available pool
     * before picking. Returns the state unchanged if there are no colors or no stitches.
     */
    fun pick(state: AppState, random: Random = Random.Default, now: Long = System.currentTimeMillis()): AppState {
        if (state.colors.isEmpty() || state.stitches.isEmpty()) return state

        var used = state.usedStitchIds.filter { id -> state.stitches.any { it.id == id } }
        var available = state.stitches.filter { it.id !in used }
        if (available.isEmpty()) {
            used = emptyList()
            available = state.stitches
        }

        val stitch = available.random(random)
        val color = state.colors.random(random)
        return state.copy(
            usedStitchIds = used + stitch.id,
            log = state.log + LogEntry(stitch.name, color.name, now),
        )
    }

    /** Resets the stitch pool and log, keeping the color and stitch lists. */
    fun newProject(state: AppState, name: String): AppState =
        state.copy(projectName = name, usedStitchIds = emptyList(), log = emptyList())

    /** Suggests "Project2" after "Project1", and so on. */
    fun nextProjectName(current: String): String {
        val match = Regex("""^(.*?)(\d+)$""").find(current.trim())
        return if (match != null) {
            val (prefix, number) = match.destructured
            prefix + (number.toLong() + 1)
        } else {
            AppState.DEFAULT_PROJECT_NAME
        }
    }

    fun addColor(state: AppState, name: String) =
        state.copy(colors = state.colors + Item(state.nextId, name), nextId = state.nextId + 1)

    fun renameColor(state: AppState, id: Long, name: String) =
        state.copy(colors = state.colors.map { if (it.id == id) it.copy(name = name) else it })

    fun removeColor(state: AppState, id: Long) =
        state.copy(colors = state.colors.filterNot { it.id == id })

    fun addStitch(state: AppState, name: String) =
        state.copy(stitches = state.stitches + Item(state.nextId, name), nextId = state.nextId + 1)

    fun renameStitch(state: AppState, id: Long, name: String) =
        state.copy(stitches = state.stitches.map { if (it.id == id) it.copy(name = name) else it })

    fun removeStitch(state: AppState, id: Long) =
        state.copy(
            stitches = state.stitches.filterNot { it.id == id },
            usedStitchIds = state.usedStitchIds - id,
        )
}
