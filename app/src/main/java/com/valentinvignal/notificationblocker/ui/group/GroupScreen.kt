package com.valentinvignal.notificationblocker.ui.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinvignal.notificationblocker.R
import com.valentinvignal.notificationblocker.data.App
import com.valentinvignal.notificationblocker.ui.AppViewModelProvider
import com.valentinvignal.notificationblocker.ui.navigation.NavigationDestination
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch


object GroupDestination : NavigationDestination {
    override val route = "group"
    override val titleRes = R.string.group
    const val itemIdArg = "groupId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    navigateBack: () -> Unit,
    viewModel: GroupViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val uiState = viewModel.uiState.collectAsState()
    val linkedApps = viewModel.appsState.collectAsState();

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val editSheetState = rememberModalBottomSheetState()
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val deleteSheetState = rememberModalBottomSheetState()
    var showDeleteBottomSheet by remember { mutableStateOf(false) }


    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        MediumTopAppBar(title = { Text(uiState.value.name) }, navigationIcon = {
            IconButton(
                onClick = navigateBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        }, actions = {
            IconButton(
                onClick = {
                    showDeleteBottomSheet = true

                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }, scrollBehavior = scrollBehavior
        )
    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = {
            showEditBottomSheet = true
        }, icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
            )
        }, text = { Text(stringResource(R.string.edit)) }, shape = MaterialTheme.shapes.large
        )
    }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding + PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.apps_in_group),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            items(App.allApps, key = { it.id }) { application ->
                ListItem(leadingContent = {
                    Surface(
                        shape = MaterialTheme.shapes.medium, tonalElevation = 1.dp
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
                }, headlineContent = {
                    Text(application.name, style = MaterialTheme.typography.bodyLarge)
                }, supportingContent = {
                    Text(application.id, style = MaterialTheme.typography.labelSmall)
                }, trailingContent = {
                    Switch(checked = application.id in linkedApps.value, onCheckedChange = {
                        if (it) {
                            viewModel.addApp(application.id)
                        } else {
                            viewModel.deleteApp(application.id)
                        }
                    })
                })

            }
        }

        if (showEditBottomSheet) {
            EditGroupBottomSheet(
                onDismiss = {
                    showEditBottomSheet = false
                },
                sheetState = editSheetState,
                viewModel = viewModel,
            )
        }

        if (showDeleteBottomSheet) {
            DeleteGroupBottomSheet(
                onDismiss = {
                    showDeleteBottomSheet = false
                },
                sheetState = deleteSheetState,
                viewModel = viewModel,
                navigateBack = navigateBack,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    viewModel: GroupViewModel,
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val uiState = viewModel.uiState.collectAsState()
    var textFieldLoaded by remember { mutableStateOf(false) }
    var groupName by remember {
        mutableStateOf(
            TextFieldValue(
                uiState.value.name,
                TextRange(uiState.value.name.length),
            ),
        )
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sheet content

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(value = groupName,
                onValueChange = { groupName = it },
                label = { Text(stringResource(R.string.group_name)) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onGloballyPositioned {
                        if (!textFieldLoaded) {
                            focusRequester.requestFocus() // IMPORTANT
                            textFieldLoaded = true // stop cyclic recompositions
                        }
                    }

            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {

                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        if (groupName.text != uiState.value.name) {

                            viewModel.updateName(groupName.text)
                        }
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteGroupBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    viewModel: GroupViewModel,
    navigateBack: () -> Unit,
) {

    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sheet content

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.delete_question))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {

                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {

                        viewModel.delete()
                        navigateBack()
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}

// Add this at the very bottom of GroupScreen.kt
@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + other.calculateStartPadding(
            layoutDirection
        ),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}