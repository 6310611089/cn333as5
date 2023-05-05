package com.example.phonebook.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.example.phonebook.R
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.routing.Screen
import com.example.phonebook.ui.components.AppDrawer
import com.example.phonebook.ui.components.Contact
import com.example.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private const val NO_DIALOG = 1
private const val RESTORE_CONTACTS_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun TrashScreen(viewModel: MainViewModel) {

    val contactsInTrash: List<PhoneBookModel> by viewModel.contactsInTrash.observeAsState(listOf())
    val selectedContacts: List<PhoneBookModel> by viewModel.selectedContacts.observeAsState(listOf())
    val dialogState = rememberSaveable { mutableStateOf(NO_DIALOG) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedContacts.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                },
                onRestoreContactsClick = { dialogState.value = RESTORE_CONTACTS_DIALOG },
                onDeleteContactsClick = { dialogState.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        content = {
            Content(
                contacts = contactsInTrash,
                onContactClick = { viewModel.onContactSelected(it) },
                selectedContacts = selectedContacts
            )

            val dialog = dialogState.value
            if (dialog != NO_DIALOG) {
                val confirmAction: () -> Unit = when (dialog) {
                    RESTORE_CONTACTS_DIALOG -> {
                        {
                            viewModel.restoreContacts(selectedContacts)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG -> {
                        {
                            viewModel.permanentlyDeleteContacts(selectedContacts)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    else -> {
                        {
                            dialogState.value = NO_DIALOG
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { dialogState.value = NO_DIALOG },
                    title = { Text(mapDialogTitle(dialog)) },
                    text = { Text(mapDialogText(dialog)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogState.value = NO_DIALOG }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun TrashTopAppBar(
    onNavigationIconClick: () -> Unit,
    onRestoreContactsClick: () -> Unit,
    onDeleteContactsClick: () -> Unit,
    areActionsVisible: Boolean
) {
    TopAppBar(
        title = { Text(text = "Trash", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Drawer Button"
                )
            }
        },
        actions = {
            if (areActionsVisible) {
                IconButton(onClick = onRestoreContactsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_restore_from_trash_24),
                        contentDescription = "Restore Contacts Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                IconButton(onClick = onDeleteContactsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                        contentDescription = "Delete Contacts Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
@ExperimentalMaterialApi
private fun Content(
    contacts: List<PhoneBookModel>,
    onContactClick: (PhoneBookModel) -> Unit,
    selectedContacts: List<PhoneBookModel>,
) {
    val deletedContacts = contacts

    LazyColumn {
        items(count = deletedContacts.size) { contactIndex ->
            val contact = deletedContacts[contactIndex]
            val isContactSelected = selectedContacts.contains(contact)
            Contact(
                contact = contact,
                onContactClick = onContactClick,
                isSelected = isContactSelected
            )
        }
    }
}

private fun mapDialogTitle(dialog: Int): String = when (dialog) {
    RESTORE_CONTACTS_DIALOG -> "Restore contacts"
    PERMANENTLY_DELETE_DIALOG -> "Delete contacts forever"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}

private fun mapDialogText(dialog: Int): String = when (dialog) {
    RESTORE_CONTACTS_DIALOG -> "Are you sure you want to restore selected contacts?"
    PERMANENTLY_DELETE_DIALOG -> "Are you sure you want to delete selected contacts permanently?"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}