package com.stitchselector.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stitchselector.AppState
import com.stitchselector.StitchLogic

@Composable
fun ProjectScreen(
    state: AppState,
    onProjectNameChange: (String) -> Unit,
    onNewStitch: () -> Unit,
    onNewProject: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showNewProject by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = state.projectName,
            onValueChange = onProjectNameChange,
            label = { Text("Project Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        CurrentPickCard(state)
        Spacer(Modifier.height(12.dp))

        ScrollingNameBox(
            title = "Colors",
            names = state.colors.map { it.name },
            emptyText = "Add colors on the Colors page",
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f),
        )
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScrollingNameBox(
                title = "Available stitches",
                names = state.availableStitches.map { it.name },
                emptyText = if (state.stitches.isEmpty()) "Add stitches on the Stitches page"
                else "All used — next pick starts a fresh round",
                modifier = Modifier.weight(1f),
            )
            ScrollingNameBox(
                title = "Chosen stitches",
                names = state.usedStitches.map { it.name },
                numbered = true,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onNewStitch,
                enabled = state.colors.isNotEmpty() && state.stitches.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) { Text("New Stitch") }
            OutlinedButton(
                onClick = { showNewProject = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) { Text("New Project") }
        }
    }

    if (showNewProject) {
        NameDialog(
            title = "Start a new project?",
            message = "This puts every stitch back in the available list and clears the stitch log.",
            label = "Project Name",
            initial = StitchLogic.nextProjectName(state.projectName),
            confirmLabel = "Start",
            onConfirm = {
                onNewProject(it)
                showNewProject = false
            },
            onDismiss = { showNewProject = false },
        )
    }
}

@Composable
private fun CurrentPickCard(state: AppState) {
    val current = state.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            if (current == null) {
                Text("Tap New Stitch to pick your first stitch and color.", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("Pick #${state.log.size}", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Text(current.stitch, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("in ${current.color}", style = MaterialTheme.typography.titleMedium)
                if (current.rows > 0) {
                    Text("for ${current.rows} rows", style = MaterialTheme.typography.titleMedium)
                }
            }
            if (state.stitches.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "${state.usedStitches.size} of ${state.stitches.size} stitches used this round",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
