package com.example.phonebook.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonebook.database.AppDatabase
import com.example.phonebook.database.DbMapper
import com.example.phonebook.database.Repository
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.routing.PhoneBookRouter
import com.example.phonebook.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val contactsNotInTrash: LiveData<List<PhoneBookModel>> by lazy {
        repository.getAllContactsNotInTrash()
    }

    private var _contactEntry = MutableLiveData(PhoneBookModel())

    val contactEntry: LiveData<PhoneBookModel> = _contactEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val contactsInTrash by lazy { repository.getAllContactsInTrash() }

    private var _selectedContacts = MutableLiveData<List<PhoneBookModel>>(listOf())

    val selectedContacts: LiveData<List<PhoneBookModel>> = _selectedContacts

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.contactDao(), db.colorDao(), DbMapper())
    }

    fun onCreateNewContactClick() {
        _contactEntry.value = PhoneBookModel()
        PhoneBookRouter.navigateTo(Screen.SaveContact)
    }

    fun onContactClick(contact: PhoneBookModel) {
        _contactEntry.value = contact
        PhoneBookRouter.navigateTo(Screen.SaveContact)
    }

    fun onContactCheckedChange(contact: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertContact(contact)
        }
    }

    fun onContactSelected(contact: PhoneBookModel) {
        _selectedContacts.value = _selectedContacts.value!!.toMutableList().apply {
            if (contains(contact)) {
                remove(contact)
            } else {
                add(contact)
            }
        }
    }

    fun restoreContacts(contacts: List<PhoneBookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreContactsFromTrash(contacts.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedContacts.value = listOf()
            }
        }
    }

    fun permanentlyDeleteContacts(contacts: List<PhoneBookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteContacts(contacts.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedContacts.value = listOf()
            }
        }
    }

    fun onContactEntryChange(contact: PhoneBookModel) {
        _contactEntry.value = contact
    }

    fun saveContact(contact: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertContact(contact)

            withContext(Dispatchers.Main) {
                PhoneBookRouter.navigateTo(Screen.Contacts)

                _contactEntry.value = PhoneBookModel()
            }
        }
    }

    fun moveContactToTrash(contact: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveContactToTrash(contact.id)

            withContext(Dispatchers.Main) {
                PhoneBookRouter.navigateTo(Screen.Contacts)
            }
        }
    }
}