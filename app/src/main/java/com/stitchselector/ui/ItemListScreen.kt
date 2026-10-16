package com.stitchselector.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stitchselector.Item

/** Shared screen for the Colors and Stitches pages: list with add, edit and remove. */
@Composable
fun ItemListScreen(
    title: String,
    itemLabel: String,
    items: List<Item>,
    onAdd: (String) -> Unit,
    onRename: (Long, String) -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var adding by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Item?>(null) }
    var removing by remember { mutableStateOf<Item?>(null) }
    val listState = rememberLazyListState()

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text(
                "$title (${items.size})",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp),
            )
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScrollbar(listState, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
            ) {
                if (items.isEmpty()) {
                    item {
                        Text(
                            "No ${itemLabel}s yet. Tap Add to create one.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
                items(items, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.name) },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editing = item }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit ${item.name}")
                                }
                                IconButton(onClick = { removing = item }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Remove ${item.name}")
                                }
                            }
                        },
                    )
                    HorizontalDivider()
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { adding = true },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("Add $itemLabel") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )
    }

    if (adding) {
        NameDialog(
            title = "Add $itemLabel",
            initial = "",
            confirmLabel = "Add",
            onConfirm = {
                onAdd(it)
                adding = false
            },
            onDismiss = { adding = false },
        )
    }

    editing?.let { item ->
        NameDialog(
            title = "Edit $itemLabel",
            initial = item.name,
            confirmLabel = "Save",
            onConfirm = {
                onRename(item.id, it)
                editing = null
            },
            onDismiss = { editing = null },
        )
    }

    removing?.let { item ->
        AlertDialog(
            onDismissRequest = { removing = null },
            title = { Text("Remove $itemLabel?") },
            text = { Text("Remove \"${item.name}\" from the list?") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove(item.id)
                    removing = null
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { removing = null }) { Text("Cancel") } },
        )
    }
}
