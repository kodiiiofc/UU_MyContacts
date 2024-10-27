package com.kodiiiofc.urbanuniversity.mycontacts.presentation

import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kodiiiofc.urbanuniversity.mycontacts.databinding.ActivityMessengerBinding

class MessengerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessengerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarInclude.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val phone = intent.extras?.getString("phone")
        binding.addresseeTv.text = "${binding.addresseeTv.text} $phone"
        binding.sendBtn.setOnClickListener {
            try {
                val smsManager: SmsManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    smsManager = applicationContext.getSystemService<SmsManager>(SmsManager::class.java)
                } else {
                    smsManager = SmsManager.getDefault()
                }
                smsManager.sendTextMessage(phone,
                    null,
                    binding.textEt.text.toString(),
                    null,
                    null)
                Toast.makeText(applicationContext, "Сообщение отправлено", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Пожалуйста, введите все данные..."+e.message.toString(),                 Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}