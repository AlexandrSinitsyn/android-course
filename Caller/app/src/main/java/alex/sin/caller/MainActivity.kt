package alex.sin.caller

import alex.sin.caller.databinding.ActivityMainBinding
import alex.sin.caller.domain.Contact
import alex.sin.caller.domain.fetchAllContacts
import alex.sin.caller.view.ContactAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.util.Log


var selectedContact: Contact? = null

const val defaultName = "Name"
const val defaultNumber = "+7 (987) 654-32-10"

var contactList = listOf<Contact>()

class MainActivity : AppCompatActivity() {

    private val contactsPermission = 154237
    private val smsPermission = 32167483

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Request", "Start")
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_CONTACTS), contactsPermission)
        } else {
            contactList = fetchAllContacts()
        }

        if (contactList.isNotEmpty()) {
            setContentView(binding.root)

            val viewManager = LinearLayoutManager(this)
            binding.contactsList.apply {
                layoutManager = viewManager
                adapter = ContactAdapter(contactList,
                    onNameClick = {
                        selectedContact = it

                        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.SEND_SMS), smsPermission)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "You are sending message to $it!",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(context, SmsActivity::class.java))
                        }
                    }, onNumberClick = {
                        selectedContact = it

                        Toast.makeText(
                            this@MainActivity,
                            "You are calling $it!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(context, CallActivity::class.java))
                    })
            }
        } else {
            setContentView(R.layout.no_permission_activity)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.i("Request", "onRequest $requestCode ${grantResults[0]}")

        when (requestCode) {
            contactsPermission -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactList = fetchAllContacts()

                    Toast.makeText(
                        this@MainActivity,
                        resources.getQuantityString(R.plurals.contacts, contactList.size, contactList.size),
                        Toast.LENGTH_SHORT
                    ).show()

                    onStart()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "This application has no permission for looking at your contacts",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            smsPermission -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this.baseContext, SmsActivity::class.java))
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "This application has no permission for sending messages",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}
