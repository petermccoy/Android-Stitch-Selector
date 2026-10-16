package com.stitchselector.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stitchselector.AppState
import java.text.DateFormat
import java.util.Date

@Composable
fun LogScreen(state: AppState, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val dateFormat = remember { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT) }
    // Newest first, keeping each pick's original number.
    val entries = state.log.withIndex().reversed()

    Column(modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            Text("Stitch Log", style = MaterialTheme.typography.headlineSmall)
            Text(
                "${state.projectName} · ${state.log.size} picks",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollbar(listState, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
        ) {
            if (entries.isEmpty()) {
                item {
                    Text(
                        "Nothing picked yet for this project.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
            items(entries, key = { it.index }) { (index, entry) ->
                ListItem(
                    overlineContent = { Text("#${index + 1} · ${dateFormat.format(Date(entry.timeMillis))}") },
                    headlineContent = { Text(entry.stitch) },
                    supportingContent = { Text(entry.color) },
                )
                HorizontalDivider()
            }
        }
    }
}
