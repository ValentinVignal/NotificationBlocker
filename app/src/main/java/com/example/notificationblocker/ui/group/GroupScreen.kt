package com.example.notificationblocker.ui.group

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.notificationblocker.R
import com.example.notificationblocker.ui.navigation.NavigationDestination

object GroupDestination : NavigationDestination {
    override val route = "group"
    override val titleRes = R.string.group
    const val itemIdArg = "groupId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    navigateBack: () -> Unit
) {

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("group") },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            },
        )
    }

    ) { innerPadding ->

        Surface(modifier = Modifier.padding(innerPadding)) {
            Text("Group screen")
        }
    }
}
