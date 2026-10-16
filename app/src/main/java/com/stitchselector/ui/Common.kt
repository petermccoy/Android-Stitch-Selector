package com.stitchselector.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Draws a thin always-visible scrollbar on the right edge whenever the list can scroll. */
fun Modifier.verticalScrollbar(state: LazyListState, color: Color): Modifier = drawWithContent {
    drawContent()
    val info = state.layoutInfo
    val visible = info.visibleItemsInfo
    val total = info.totalItemsCount
    if (total == 0 || visible.isEmpty()) return@drawWithContent
    if (!state.canScrollForward && !state.canScrollBackward) return@drawWithContent

    val first = visible.first()
    val firstFraction = if (first.size > 0) -first.offset.toFloat() / first.size else 0f
    val start = (state.firstVisibleItemIndex + firstFraction.coerceIn(0f, 1f)) / total
    val thumbFraction = (visible.size.toFloat() / total).coerceIn(0.1f, 1f)

    val width = 4.dp.toPx()
    val top = (start * size.height).coerceAtMost(size.height * (1f - thumbFraction))
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width - width - 2.dp.toPx(), top),
        size = Size(width, thumbFraction * size.height),
        cornerRadius = CornerRadius(width / 2),
    )
}

/** A titled, bordered, scrollable list of names. */
@Composable
fun ScrollingNameBox(
    title: String,
    names: List<String>,
    modifier: Modifier = Modifier,
    emptyText: String = "None",
    numbered: Boolean = false,
) {
    val listState = rememberLazyListState()
    Column(modifier) {
        Text(
            "$title (${names.size})",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .verticalScrollbar(listState, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        ) {
            if (names.isEmpty()) {
                item {
                    Text(
                        emptyText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
            itemsIndexed(names) { index, name ->
                Text(
                    if (numbered) "${index + 1}. $name" else name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
            }
        }
    }
}

/** Dialog with a single text field, used for adding/renaming items and naming projects. */
@Composable
fun NameDialog(
    title: String,
    initial: String,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    message: String? = null,
    label: String = "Name",
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (message != null) {
                    Text(message, modifier = Modifier.padding(bottom = 12.dp))
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(label) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text.trim()) },
                enabled = text.isNotBlank(),
            ) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
