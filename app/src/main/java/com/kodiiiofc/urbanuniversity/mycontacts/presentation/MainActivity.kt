package com.kodiiiofc.urbanuniversity.mycontacts.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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
    }

    @SuppressLint("Range")
    private fun getContact() {
        contactModelList = mutableListOf()
        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phone =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
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
                "Разрешение на чтение контактов предоставлено.",
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
        scrollPosition = (binding.contactsRv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()
        (binding.contactsRv.layoutManager as LinearLayoutManager).scrollToPosition(scrollPosition)
    }

/*
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("rvState", binding.contactsRv.layoutManager?.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val rvState = savedInstanceState.getParcelable<Parcelable>("rvState")
        if (rvState != null) binding.contactsRv.layoutManager?.onRestoreInstanceState(rvState)
    }*/

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
}