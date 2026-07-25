package com.example.androidconcepts.activity_fragment_lifecycle.intent_flags

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidconcepts.R
import com.example.androidconcepts.databinding.ActivityFlagDemoBinding

class FlagDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlagDemoBinding
    private var onNewIntentCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFlagDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdge()
        handleBackPress()
        bindUi()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onNewIntentCount++
        binding.tvInstanceInfo.text = "onNewIntent() called: $onNewIntentCount time(s)\n(Naya instance nahi bana — same activity reused!)"
    }

    private fun bindUi() {
        val flagName = intent.getStringExtra("flag_name") ?: return

        val (fullName, desc, observe) = when (flagName) {
            "NO_HISTORY" -> Triple(
                "FLAG_ACTIVITY_NO_HISTORY",
                "Yeh activity back stack mein save nahi hoti. Press back karo aur dobara launch karo — yeh screen history mein nahi milegi.\n\nUse case: Splash screen, Login success screen.",
                "Yahan se Home button press karo → recents se app dobara kholo → FlagDemo nahi aayega, seedha IntentFlags screen pe aajao."
            )
            "SINGLE_TOP" -> Triple(
                "FLAG_ACTIVITY_SINGLE_TOP",
                "Agar target activity already back stack ke TOP pe hai, toh naya instance nahi banta — onNewIntent() call hota hai.\n\nUse case: Search screen, Notification deeplink.",
                "Neeche wala button click karo → FlagDemo already top pe hai → onNewIntent() call hoga, counter badhega. Naya screen nahi khuega."
            )
            "CLEAR_TOP" -> Triple(
                "FLAG_ACTIVITY_CLEAR_TOP",
                "Agar target activity stack mein already hai, toh uske upar ki saari activities destroy ho jaati hain aur woh activity top pe aa jaati hai.\n\nUse case: Notification tap pe Home/Dashboard pe directly jaana.",
                "Neeche wala button click karo → Dummy stack mein add hogi → phir Dummy se FlagDemo CLEAR_TOP ke saath launch karo → Dummy destroy ho jaayegi."
            )
            "CLEAR_TASK" -> Triple(
                "FLAG_ACTIVITY_CLEAR_TASK",
                "CLEAR_TASK | NEW_TASK lagane se pura back stack clear ho jaata hai. Sirf yeh activity bachti hai.\n\nNOTE: CLEAR_TASK akela kaam nahi karta — FLAG_ACTIVITY_NEW_TASK ke saath lagana zaroori hai.\n\nUse case: Logout ke baad Login screen pe bhejana.",
                "Back press karo — IntentFlags nahi aayega, seedha Home screen pe jaoge. Pura stack clear ho gaya!"
            )
            "REORDER_TO_FRONT" -> Triple(
                "FLAG_ACTIVITY_REORDER_TO_FRONT",
                "Agar yeh activity stack mein already hai (chahe kisi bhi position pe), toh use front mein le aata hai. Naya instance nahi banta.\n\nCLEAR_TOP se fark: Upar wali activities destroy nahi hoti, sirf reorder hoti hain.",
                "Neeche wala button click karo → Dummy stack mein add hogi → phir Dummy se FlagDemo REORDER ke saath launch karo → FlagDemo top pe aa jaayega, Dummy bhi stack mein rahegi."
            )
            else -> Triple("—", "—", "—")
        }

        binding.tvFlagName.text = fullName
        binding.tvFlagDesc.text = desc
        binding.tvObserve.text = observe

        when (flagName) {
            "SINGLE_TOP" -> {
                binding.btnLaunchSelf.visibility = View.VISIBLE
                binding.tvInstanceInfo.visibility = View.VISIBLE
                binding.btnLaunchSelf.setOnClickListener {
                    val intent = Intent(this@FlagDemoActivity, FlagDemoActivity::class.java).apply {
                        putExtra("flag_name", "SINGLE_TOP")
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(intent)
                }
            }
            "CLEAR_TOP", "REORDER_TO_FRONT" -> {
                binding.btnLaunchDummy.visibility = View.VISIBLE
                binding.btnLaunchDummy.setOnClickListener {
                    val intent = Intent(this@FlagDemoActivity, DummyActivity::class.java).apply {
                        putExtra("flag_name", flagName)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleBackPress() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
