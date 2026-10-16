package com.stitchselector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stitchselector.ui.ItemListScreen
import com.stitchselector.ui.LogScreen
import com.stitchselector.ui.ProjectScreen
import com.stitchselector.ui.StitchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StitchTheme {
                App()
            }
        }
    }
}

private enum class Tab(val label: String, val icon: ImageVector) {
    Project("Project", Icons.Filled.Home),
    Colors("Colors", Icons.Filled.Favorite),
    Stitches("Stitches", Icons.Filled.Star),
    Log("Log", Icons.AutoMirrored.Filled.List),
}

@Composable
private fun App(vm: StitchViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tab = Tab.entries[tabIndex]

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { index, t ->
                    NavigationBarItem(
                        selected = index == tabIndex,
                        onClick = { tabIndex = index },
                        icon = { Icon(t.icon, contentDescription = null) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (tab) {
            Tab.Project -> ProjectScreen(
                state = state,
                onProjectNameChange = vm::setProjectName,
                onNewStitch = vm::newStitch,
                onNewProject = vm::newProject,
                modifier = modifier,
            )
            Tab.Colors -> ItemListScreen(
                title = "Colors",
                itemLabel = "color",
                items = state.colors,
                onAdd = vm::addColor,
                onRename = vm::renameColor,
                onRemove = vm::removeColor,
                modifier = modifier,
            )
            Tab.Stitches -> ItemListScreen(
                title = "Stitches",
                itemLabel = "stitch",
                items = state.stitches,
                onAdd = vm::addStitch,
                onRename = vm::renameStitch,
                onRemove = vm::removeStitch,
                modifier = modifier,
            )
            Tab.Log -> LogScreen(state = state, modifier = modifier)
        }
    }
}
