package com.stitchselector

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Saves the whole app state as one JSON blob in SharedPreferences. */
class Storage(context: Context) {
    private val prefs = context.getSharedPreferences("stitch_selector", Context.MODE_PRIVATE)

    fun load(): AppState {
        val json = prefs.getString(KEY, null) ?: return AppState.defaults()
        return try {
            fromJson(JSONObject(json))
        } catch (e: Exception) {
            AppState.defaults()
        }
    }

    fun save(state: AppState) {
        prefs.edit().putString(KEY, toJson(state).toString()).apply()
    }

    private fun toJson(state: AppState) = JSONObject().apply {
        put("projectName", state.projectName)
        put("colors", itemsToJson(state.colors))
        put("stitches", itemsToJson(state.stitches))
        put("usedStitchIds", JSONArray(state.usedStitchIds))
        put("log", JSONArray().apply {
            state.log.forEach {
                put(JSONObject().put("stitch", it.stitch).put("color", it.color).put("time", it.timeMillis))
            }
        })
        put("nextId", state.nextId)
    }

    private fun fromJson(o: JSONObject): AppState {
        val used = o.getJSONArray("usedStitchIds")
        val log = o.getJSONArray("log")
        return AppState(
            projectName = o.getString("projectName"),
            colors = itemsFromJson(o.getJSONArray("colors")),
            stitches = itemsFromJson(o.getJSONArray("stitches")),
            usedStitchIds = List(used.length()) { used.getLong(it) },
            log = List(log.length()) {
                val e = log.getJSONObject(it)
                LogEntry(e.getString("stitch"), e.getString("color"), e.getLong("time"))
            },
            nextId = o.getLong("nextId"),
        )
    }

    private fun itemsToJson(items: List<Item>) = JSONArray().apply {
        items.forEach { put(JSONObject().put("id", it.id).put("name", it.name)) }
    }

    private fun itemsFromJson(a: JSONArray) = List(a.length()) {
        val o = a.getJSONObject(it)
        Item(o.getLong("id"), o.getString("name"))
    }

    private companion object {
        const val KEY = "state"
    }
}
