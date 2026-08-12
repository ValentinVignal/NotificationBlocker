package com.valentinvignal.notificationblocker.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.valentinvignal.notificationblocker.R
import com.valentinvignal.notificationblocker.data.App
import com.valentinvignal.notificationblocker.data.allApplicationsAppId
import com.valentinvignal.notificationblocker.data.doNotDisturbAppId
import com.valentinvignal.notificationblocker.ui.AppViewModelProvider
import com.valentinvignal.notificationblocker.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onGroupClick: (id: Int) -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.home),
                        style = MaterialTheme.typography.displaySmall
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            StartButton()
        },
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            HomeBody(
                onGroupClick = onGroupClick,
                viewModel = viewModel,
            )
        }
    }
}


@Composable
fun HomeBody(
    onGroupClick: (id: Int) -> Unit,
    viewModel: HomeViewModel,
) {
    val scope = rememberCoroutineScope()
    val groups by viewModel.groups.collectAsState()
    val appIds by viewModel.apps.collectAsState()



    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            SectionHeader(stringResource(R.string.global_controls))
            Surface(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.extraLarge, // The 28dp Expressive shape
                tonalElevation = 2.dp
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.do_not_disturb)) },
                        trailingContent = {
                            Switch(
                                checked = appIds.contains(doNotDisturbAppId),
                                onCheckedChange = {
                                    viewModel.toggleApp(doNotDisturbAppId, it)
                                },
                            )
                        },
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.all_applications)) },
                        trailingContent = {
                            Switch(
                                checked = appIds.contains(allApplicationsAppId),
                                onCheckedChange = {
                                    viewModel.toggleApp(allApplicationsAppId, it)
                                },
                            )
                        },
                    )
                }
            }
        }

        item {
            SectionHeader(stringResource(R.string.groups))
        }
        item {
            Surface(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 2.dp
            ) {
                Column {
                    // Add Group Button inside the container
                    ListItem(
                        headlineContent = {
                            Text(stringResource(R.string.add_group), color = MaterialTheme.colorScheme.primary)
                        },
                        leadingContent = {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.clickable {
                            scope.launch {
                                viewModel.addGroup().also { onGroupClick(it) }
                            }
                        }
                    )

                    if (groups.isNotEmpty()) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    groups.forEach { group ->
                        ListItem(
                            modifier = Modifier.clickable { onGroupClick(group.id) },
                            headlineContent = { Text(group.name) },
                            trailingContent = {
                                Switch(
                                    checked = group.active,
                                    onCheckedChange = { viewModel.toggleGroup(group, it) },
                                )
                            },
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(stringResource(R.string.applications))
        }


        items(App.allApps, key = { it.id }) { application ->
            ListItem(
                leadingContent = {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 1.dp
                    ) {

                        Image(
                            painter = rememberDrawablePainter(application.icon),
                            contentDescription = application.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(4.dp),
                        )
                    }
                },
                headlineContent = {
                    Text(application.name, style = MaterialTheme.typography.bodyLarge)
                },
                supportingContent = {
                    Text(application.id, style = MaterialTheme.typography.labelSmall)
                },
                trailingContent = {
                    Switch(
                        checked = appIds.contains(application.id),
                        onCheckedChange = {
                            viewModel.toggleApp(application.id, it)
                        },
                    )
                },
            )

        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp, top = 24.dp)
    )
}