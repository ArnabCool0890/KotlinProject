package com.example.contactfetcher

import adapter.ContactDetailAdapter
import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import model.Contact
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    val READ_CONTACTS_REQUEST_CODE = 100
    private lateinit var contactListView: RecyclerView
    private lateinit var getContButton: Button
    private lateinit var progress: ProgressBar
    private lateinit var contactList: ArrayList<Contact>
    var cursor: Cursor? = null
    private lateinit var adapter: ContactDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactList = ArrayList<Contact>()
        contactListView = findViewById(R.id.contact_list)
        getContButton = findViewById(R.id.contact_button)
        progress = findViewById(R.id.progress_circular)

        getContButton.setOnClickListener {
            setupPermissions()
        }
        contactListView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        contactList.add(Contact("amit", "13334343"))
//        contactList.add(Contact("amit", "13334343"))
//        contactList.add(Contact("amit", "13334343"))
//        contactList.add(Contact("amit", "13334343"))

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            contactListView.visibility = View.VISIBLE
            getContacts()
        }


    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "CONTACTS Permission Granted", Toast.LENGTH_SHORT)
                    .show()
                getContacts()
            } else {
                Toast.makeText(this@MainActivity, "CONTACTS Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun getContacts() {
        val someRunnable = object : Runnable {
            override fun run() {
                runOnUiThread {
                    getContButton.visibility = View.GONE

                    progress.visibility = View.VISIBLE
                };
                readContact()

                /*
                runOnUiThread {
                  // update ui if you are in an activity
                }
                * */
            }
        };
        Executors.newSingleThreadExecutor().execute(someRunnable);
    }


    private fun readContact() {
        var contactId: String?
        var displayName: String?

        val cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val hasPhoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt()
                if (hasPhoneNumber > 0) {
                    contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

//

                    val phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )
                    var phoneNumber: String? = null
                    if (phoneCursor!!.moveToNext()) {
                        phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    }
                    phoneCursor.close()
                    val contactsInfo = Contact(displayName, phoneNumber.toString())

                    contactList.add(contactsInfo)
                }
            }
        }
        cursor.close()
        runOnUiThread {
            progress.visibility = View.GONE
            contactListView.visibility=View.VISIBLE
        };


        adapter = ContactDetailAdapter(contactList)
        contactListView.adapter = adapter


    }


}
