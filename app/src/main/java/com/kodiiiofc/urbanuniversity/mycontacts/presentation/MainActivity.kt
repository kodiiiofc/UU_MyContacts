package com.kodiiiofc.urbanuniversity.mycontacts.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kodiiiofc.urbanuniversity.mycontacts.R
import com.kodiiiofc.urbanuniversity.mycontacts.databinding.ActivityMainBinding
import com.kodiiiofc.urbanuniversity.mycontacts.domain.Callable
import com.kodiiiofc.urbanuniversity.mycontacts.domain.ContactModel
import com.kodiiiofc.urbanuniversity.mycontacts.domain.Messenger

class MainActivity : AppCompatActivity(), Callable, Messenger {
    private lateinit var binding: ActivityMainBinding
    private var contactModelList: MutableList<ContactModel>? = null
    private var scrollPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarInclude.toolbar)
        val layoutManager = LinearLayoutManager(this)
        binding.contactsRv.layoutManager = layoutManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionContacts.launch(Manifest.permission.READ_CONTACTS)
        } else {
            getContact()
        }

        binding.addContactFab.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionContacts.launch(Manifest.permission.WRITE_CONTACTS)
            } else {
                addContact()
            }
        }


        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d("aaa", "$query")
            val suggestions = SearchRecentSuggestions(
                this,
                MySuggestionProvider.AUTHORITY,
                MySuggestionProvider.MODE
            )
            suggestions.saveRecentQuery(query, null)
        }

    }

    private fun addContact() {
        val editableFieldView = layoutInflater.inflate(R.layout.add_contact_dialog, null)
        AlertDialog.Builder(this)
            .setTitle("Добавить контакт")
            .setMessage("Введите данные контакта в поля ниже:")
            .setView(editableFieldView)
            .setPositiveButton("Добавить") { d, _ ->
                val newContactName =
                    editableFieldView.findViewById<EditText>(R.id.new_name_et).text.toString()
                val newContactPhone =
                    editableFieldView.findViewById<EditText>(R.id.new_phone_et).text.toString()
                val listCPO = ArrayList<ContentProviderOperation>()
                listCPO.add(
                    ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                        .withValue(RawContacts.ACCOUNT_TYPE, null)
                        .withValue(RawContacts.ACCOUNT_NAME, null)
                        .build()
                )
                listCPO.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.DISPLAY_NAME, newContactName)
                        .build()
                )
                listCPO.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, newContactPhone)
                        .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                        .build()
                )
                Toast.makeText(
                    this@MainActivity,
                    "$newContactName добавлен в список контактов",
                    Toast.LENGTH_LONG
                ).show()
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, listCPO)
                } catch (e: Exception) {
                    Log.e("eee", e.message ?: "ошибка на уровне contentResolver")
                }
                finally {
                    getContact()
                    d.dismiss()
                }

            }
            .setNeutralButton("Отмена", null)
            .create().show()

    }

    @SuppressLint("Range")
    private fun getContact() {
        contactModelList = mutableListOf()
        val phones = contentResolver.query(
            Phone.CONTENT_URI,
            null,
            null,
            null,
            Phone.DISPLAY_NAME + " ASC"
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(Phone.DISPLAY_NAME))
            val phone =
                phones.getString(phones.getColumnIndex(Phone.NUMBER))
            val contactModel = ContactModel(name, phone)
            Log.d("Con", "getContact: $contactModel")
            contactModelList?.add(contactModel)
        }
        phones.close()
        binding.contactsRv.adapter = ContactsRecyclerViewAdapter(contactModelList!!)
    }

    private val permissionContacts = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Snackbar.make(
                binding.root,
                "Разрешение на доступ к контактам предоставлено.",
                Snackbar.LENGTH_LONG
            ).show()
            getContact()
        } else {
            Snackbar.make(binding.root, "В разрешениях отказано.", Snackbar.LENGTH_LONG)
                .setTextColor(Color.RED)
                .show()
        }
    }

    private val permissionPhoneCallSMS = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Snackbar.make(binding.root, "Разрешения предоставлены.", Snackbar.LENGTH_LONG).show()
            getContact()
        } else {
            Snackbar.make(binding.root, "В разрешениях отказано.", Snackbar.LENGTH_LONG)
                .setTextColor(Color.RED)
                .show()
        }
    }

    override fun call(phone: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionPhoneCallSMS.launch(Manifest.permission.CALL_PHONE)
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phone")
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        scrollPosition =
            (binding.contactsRv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()
        (binding.contactsRv.layoutManager as LinearLayoutManager).scrollToPosition(scrollPosition)
    }

    override fun createMessage(phone: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionPhoneCallSMS.launch(Manifest.permission.SEND_SMS)
        } else {
            val intent = Intent(this@MainActivity, MessengerActivity::class.java)
            intent.putExtras(bundleOf("phone" to phone))
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.menu_search)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                sortList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                sortList(newText)
                return true
            }

        })
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)

        return true
    }

    private fun sortList(newString: String?) {
        if (newString == null) {
            return
        } else {
            val filteredList = contactModelList?.filter { contact ->
                contact.name?.contains(newString, true) ?: false
            }
            binding.contactsRv.adapter = ContactsRecyclerViewAdapter(filteredList!!)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_exit -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}