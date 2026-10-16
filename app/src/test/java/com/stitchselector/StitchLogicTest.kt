package com.stitchselector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class StitchLogicTest {

    @Test
    fun defaultsHaveAllStitchesAndColors() {
        val state = AppState.defaults()
        assertEquals("Project1", state.projectName)
        assertEquals(21, state.stitches.size)
        assertEquals(9, state.colors.size)
        assertEquals(21, state.availableStitches.size)
    }

    @Test
    fun everyStitchIsUsedOnceBeforeAnyRepeats() {
        var state = AppState.defaults()
        val random = Random(42)
        repeat(state.stitches.size) { state = StitchLogic.pick(state, random) }

        val picked = state.log.map { it.stitch }
        assertEquals(state.stitches.map { it.name }.toSet(), picked.toSet())
        assertEquals(picked.size, picked.toSet().size)
        assertTrue(state.availableStitches.isEmpty())
        assertTrue(state.log.all { entry -> state.colors.any { it.name == entry.color } })
    }

    @Test
    fun rowCountIsBetweenFourAndTen() {
        var state = AppState.defaults()
        val random = Random(11)
        repeat(500) { state = StitchLogic.pick(state, random) }

        val counts = state.log.map { it.rows }.toSet()
        assertEquals((4..10).toSet(), counts)
    }

    @Test
    fun poolRefillsAfterAllStitchesUsed() {
        var state = AppState.defaults()
        val random = Random(7)
        repeat(state.stitches.size + 1) { state = StitchLogic.pick(state, random) }

        assertEquals(1, state.usedStitchIds.size)
        assertEquals(state.stitches.size - 1, state.availableStitches.size)
        assertEquals(state.stitches.size + 1, state.log.size)
    }

    @Test
    fun newProjectResetsPoolAndLog() {
        var state = AppState.defaults()
        repeat(5) { state = StitchLogic.pick(state, Random(1)) }
        state = StitchLogic.newProject(state, "Blanket")

        assertEquals("Blanket", state.projectName)
        assertTrue(state.log.isEmpty())
        assertEquals(state.stitches.size, state.availableStitches.size)
        assertEquals(9, state.colors.size)
    }

    @Test
    fun removingAUsedStitchDropsItFromUsedList() {
        var state = StitchLogic.pick(AppState.defaults(), Random(3))
        val usedId = state.usedStitchIds.single()
        state = StitchLogic.removeStitch(state, usedId)

        assertTrue(state.usedStitchIds.isEmpty())
        assertEquals(20, state.stitches.size)
    }

    @Test
    fun renameKeepsUsedState() {
        var state = StitchLogic.pick(AppState.defaults(), Random(3))
        val usedId = state.usedStitchIds.single()
        state = StitchLogic.renameStitch(state, usedId, "Renamed")

        assertEquals("Renamed", state.usedStitches.single().name)
    }

    @Test
    fun addAssignsUniqueIds() {
        var state = AppState.defaults()
        state = StitchLogic.addColor(state, "Rose")
        state = StitchLogic.addStitch(state, "Bobble stitch")
        val ids = (state.colors + state.stitches).map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals(22, state.availableStitches.size)
    }

    @Test
    fun pickWithNoColorsDoesNothing() {
        var state = AppState.defaults()
        state.colors.forEach { state = StitchLogic.removeColor(state, it.id) }
        assertSame(state, StitchLogic.pick(state))
    }

    @Test
    fun nextProjectNameIncrements() {
        assertEquals("Project2", StitchLogic.nextProjectName("Project1"))
        assertEquals("Blanket 10", StitchLogic.nextProjectName("Blanket 9"))
        assertEquals("Project1", StitchLogic.nextProjectName("Scarf"))
    }
}
