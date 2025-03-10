package de.stubbe.jaem_client.view.screens.uds

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.model.enums.ServerListType
import de.stubbe.jaem_client.network.NetworkConnectionState
import de.stubbe.jaem_client.network.rememberConnectivityState
import de.stubbe.jaem_client.view.components.JAEMCircularProgressIndicator
import de.stubbe.jaem_client.view.components.JAEMPullToRefresh
import de.stubbe.jaem_client.view.components.LoadingOverlay
import de.stubbe.jaem_client.view.components.NoItemsText
import de.stubbe.jaem_client.view.components.dialogs.EditServerListDialog
import de.stubbe.jaem_client.view.screens.ScreenBase
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.viewmodel.NavigationViewModel
import de.stubbe.jaem_client.viewmodel.UDSViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UDSScreen(
    navigationViewModel: NavigationViewModel
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val noInternetConnectionString = stringResource(id = R.string.no_internet_connection)

    val viewModel: UDSViewModel = hiltViewModel()

    val udsUsersPagingItems = viewModel.udsUsersPagingFlow.collectAsLazyPagingItems()

    val searchText by viewModel.searchText.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState()

    var editServerListDialogIsOpen by remember { mutableStateOf(false) }

    var isUpdatingAfterUrlChange by remember { mutableStateOf(false) }

    val connectivityState by rememberConnectivityState()

    LaunchedEffect(connectivityState) {
        if (connectivityState == NetworkConnectionState.Unavailable) {
            Toast.makeText(
                context,
                noInternetConnectionString,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(udsUsersPagingItems.loadState) {
        println(udsUsersPagingItems.loadState)
    }

    ScreenBase(
        topBar = {
            UDSTopBar(
                navigationViewModel = navigationViewModel,
                searchText = searchText,
                onSearchTextChange = { newSearchText ->
                    viewModel.changeSearchText(newSearchText)
                },
                onOpenEditServerListDialog = {
                    editServerListDialogIsOpen = true
                }
            )
        }
    ) {
        LoadingOverlay(isUpdatingAfterUrlChange) {
            JAEMPullToRefresh(
                modifier = Modifier.fillMaxSize(),
                refreshing = udsUsersPagingItems.loadState.refresh is LoadState.Loading,
                onRefresh = {
                    udsUsersPagingItems.refresh()
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = udsUsersPagingItems.itemCount,
                        key = udsUsersPagingItems.itemKey { user -> user.id }) { index ->
                        UDSUserRow(udsUserDto = udsUsersPagingItems[index]!!)
                    }
                    item {
                        if (udsUsersPagingItems.loadState.append is LoadState.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = Dimensions.Padding.Small)
                            ) {
                                JAEMCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.padding(Dimensions.Spacing.Small))
                        if (udsUsersPagingItems.loadState.append is LoadState.Error) {
                            NoItemsText(
                                text = stringResource(id = R.string.error_fetching_uds_user)
                            )
                        } else if (udsUsersPagingItems.itemCount == 0 && udsUsersPagingItems.loadState.refresh !is LoadState.Loading && udsUsersPagingItems.loadState.append !is LoadState.Loading) {
                            NoItemsText(
                                text = stringResource(id = R.string.no_uds_user)
                            )
                        }
                    }
                }
            }
        }
    }

    if (editServerListDialogIsOpen) {
        EditServerListDialog(
            onDismissRequest = {
                coroutineScope.launch {
                    isUpdatingAfterUrlChange = true
                    delay(500)
                    udsUsersPagingItems.refresh()
                    isUpdatingAfterUrlChange = false
                }
                editServerListDialogIsOpen = false
            },
            serverListType = ServerListType.USER_DISCOVERY
        )
    }

}