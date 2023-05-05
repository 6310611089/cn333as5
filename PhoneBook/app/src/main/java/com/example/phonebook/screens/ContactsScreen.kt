package com.example.phonebook.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.routing.Screen
import com.example.phonebook.ui.components.AppDrawer
import com.example.phonebook.ui.components.Contact
import com.example.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun ContactsScreen(viewModel: MainViewModel) {
    val contacts by viewModel.contactsNotInTrash.observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Phone Book",
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Contacts,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewContactClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Contact Button"
                    )
                }
            )
        }
    ) {
        if (contacts.isNotEmpty()) {
            ContactsList(
                contacts = contacts,
                onContactCheckedChange = {
                    viewModel.onContactCheckedChange(it)
                },
                onContactClick = { viewModel.onContactClick(it) }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ContactsList(
    contacts: List<PhoneBookModel>,
    onContactCheckedChange: (PhoneBookModel) -> Unit,
    onContactClick: (PhoneBookModel) -> Unit,
) {
    val sortedContacts = contacts.sortedBy { it.name.first().toUpperCase() }
    val groups = sortedContacts.groupBy { it.name.first().toUpperCase() }

    LazyColumn {
        groups.forEach { (letter, group) ->
            item {
                Text(
                    text = letter.toString(),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground,
                )
            }
            items(count = group.size) { groupIndex ->
                val contact = group[groupIndex]
                val isSelected = false
                Contact(
                    contact = contact,
                    onContactClick = onContactClick,
                    isSelected = isSelected
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun ContactsListPreview() {
    ContactsList(
        contacts = listOf(
            PhoneBookModel(1, "Contact 1", "Phone Number 1"),
            PhoneBookModel(2, "Contact 2", "Phone Number 2"),
            PhoneBookModel(3, "Contact 3", "Phone Number 3")
        ),
        onContactCheckedChange = {},
        onContactClick = {}
    )
}