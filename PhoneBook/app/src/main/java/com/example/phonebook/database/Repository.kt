package com.example.phonebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.PhoneBookModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val contactDao: ContactDao,
    private val colorDao: ColorDao,
    private val dbMapper: DbMapper
) {

    private val contactsNotInTrashLiveData: MutableLiveData<List<PhoneBookModel>> by lazy {
        MutableLiveData<List<PhoneBookModel>>()
    }

    fun getAllContactsNotInTrash(): LiveData<List<PhoneBookModel>> = contactsNotInTrashLiveData

    private val contactsInTrashLiveData: MutableLiveData<List<PhoneBookModel>> by lazy {
        MutableLiveData<List<PhoneBookModel>>()
    }

    fun getAllContactsInTrash(): LiveData<List<PhoneBookModel>> = contactsInTrashLiveData

    init {
        initDatabase(this::updateContactsLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
            val dbColors = colorDao.getAllSync()
            if (dbColors.isNullOrEmpty()) {
                colorDao.insertAll(*colors)
            }

            val contacts = PhoneBookDbModel.DEFAULT_CONTACTS.toTypedArray()
            val dbContacts = contactDao.getAllSync()
            if (dbContacts.isNullOrEmpty()) {
                contactDao.insertAll(*contacts)
            }

            postInitAction.invoke()
        }
    }

    private fun getAllContactsDependingOnTrashStateSync(inTrash: Boolean): List<PhoneBookModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
        val dbContacts: List<PhoneBookDbModel> =
            contactDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapContacts(dbContacts, colorDbModels)
    }

    fun insertContact(contact: PhoneBookModel) {
        contactDao.insert(dbMapper.mapDbContact(contact))
        updateContactsLiveData()
    }

    fun deleteContacts(contactIds: List<Long>) {
        contactDao.delete(contactIds)
        updateContactsLiveData()
    }

    fun moveContactToTrash(contactId: Long) {
        val dbContact = contactDao.findByIdSync(contactId)
        val newDbContact = dbContact.copy(isInTrash = true)
        contactDao.insert(newDbContact)
        updateContactsLiveData()
    }

    fun restoreContactsFromTrash(contactIds: List<Long>) {
        val dbContactsInTrash = contactDao.getContactsByIdsSync(contactIds)
        dbContactsInTrash.forEach {
            val newDbContact = it.copy(isInTrash = false)
            contactDao.insert(newDbContact)
        }
        updateContactsLiveData()
    }

    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    private fun updateContactsLiveData() {
        contactsNotInTrashLiveData.postValue(getAllContactsDependingOnTrashStateSync(false))
        contactsInTrashLiveData.postValue(getAllContactsDependingOnTrashStateSync(true))
    }
}