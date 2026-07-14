package com.example.androidconcepts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidconcepts.common.ConceptAdapter
import com.example.androidconcepts.common.UiBASICS
import com.example.androidconcepts.common.setDynamicSpacing
import com.example.androidconcepts.databinding.ActivityUibasicsBinding
import com.example.androidconcepts.ui_basics.ButtonActivity
import com.example.androidconcepts.ui_basics.CheckBoxActivity
import com.example.androidconcepts.ui_basics.ConstraintLayoutActivity
import com.example.androidconcepts.ui_basics.EditTextActivity
import com.example.androidconcepts.ui_basics.FrameLayoutActivity
import com.example.androidconcepts.ui_basics.ImageViewActivity
import com.example.androidconcepts.ui_basics.LinearLayoutActivity
import com.example.androidconcepts.ui_basics.ProgressBarActivity
import com.example.androidconcepts.ui_basics.RadioButtonActivity
import com.example.androidconcepts.ui_basics.recyclerView.RecyclerViewActivity
import com.example.androidconcepts.ui_basics.RelativeLayoutActivity
import com.example.androidconcepts.ui_basics.ScrollViewActivity
import com.example.androidconcepts.ui_basics.SwitchActivity
import com.example.androidconcepts.ui_basics.TextViewActivity

class UIBasicsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUibasicsBinding
    private lateinit var conceptAdapter: ConceptAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUibasicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        bindUi()
    }
    private fun setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun bindUi() = with(binding) {
        recyclerViewProjects.apply {
            conceptAdapter = ConceptAdapter(concepts = UiBASICS.entries, onOptionClicked = {
                    topicId -> navigateToTopic(topicId)
            })
            layoutManager = GridLayoutManager(this@UIBasicsActivity,2)
            setHasFixedSize(true)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_8)
            setDynamicSpacing(spacing)
            adapter = conceptAdapter
        }
    }

    private fun navigateToTopic(topicId : Int) {
        when(topicId) {
            UiBASICS.TEXT_VIEW.topicId -> startActivity(Intent(this, TextViewActivity::class.java))
            UiBASICS.BUTTON.topicId -> startActivity(Intent(this, ButtonActivity::class.java))
            UiBASICS.IMAGE_VIEW.topicId -> startActivity(Intent(this, ImageViewActivity::class.java))
            UiBASICS.EDIT_TEXT.topicId -> startActivity(Intent(this, EditTextActivity::class.java))
            UiBASICS.CHECKBOX.topicId -> startActivity(Intent(this, CheckBoxActivity::class.java))
            UiBASICS.RADIO_BUTTON.topicId -> startActivity(Intent(this, RadioButtonActivity::class.java))
            UiBASICS.SWITCH.topicId -> startActivity(Intent(this, SwitchActivity::class.java))
            UiBASICS.PROGRESS_BAR.topicId -> startActivity(Intent(this, ProgressBarActivity::class.java))
            UiBASICS.LINEAR_LAYOUT.topicId -> startActivity(Intent(this, LinearLayoutActivity::class.java))
            UiBASICS.RELATIVE_LAYOUT.topicId -> startActivity(Intent(this, RelativeLayoutActivity::class.java))
            UiBASICS.CONSTRAINT_LAYOUT.topicId -> startActivity(Intent(this, ConstraintLayoutActivity::class.java))
            UiBASICS.FRAME_LAYOUT.topicId -> startActivity(Intent(this, FrameLayoutActivity::class.java))
            UiBASICS.RECYCLER_VIEW.topicId -> startActivity(Intent(this, RecyclerViewActivity::class.java))
            UiBASICS.SCROLL_VIEW.topicId -> startActivity(Intent(this, ScrollViewActivity::class.java))
        }
    }
}