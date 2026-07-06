package com.example.androidconcepts.common

import com.example.androidconcepts.R

interface CONCEPTS {
    val topicId: Int
    val topicNameResId : Int
}

enum class TOPICS(override val topicId: Int, override val topicNameResId : Int) : CONCEPTS {
    UI_BASICS(1,R.string.ui_basics)
}

enum class UiBASICS(override val topicId: Int, override val topicNameResId: Int) : CONCEPTS {
    TEXT_VIEW(1, R.string.text_view),
    BUTTON(2, R.string.button),
    IMAGE_VIEW(3, R.string.image_view),
    EDIT_TEXT(4, R.string.edit_text),
    CHECKBOX(5, R.string.checkbox),
    RADIO_BUTTON(6, R.string.radio_button),
    SWITCH(7, R.string.switch_toggle),
    PROGRESS_BAR(8, R.string.progress_bar),
    LINEAR_LAYOUT(9, R.string.linear_layout),
    RELATIVE_LAYOUT(10, R.string.relative_layout),
    CONSTRAINT_LAYOUT(11, R.string.constraint_layout),
    FRAME_LAYOUT(12, R.string.frame_layout),
    RECYCLER_VIEW(13, R.string.recycler_view),
    SCROLL_VIEW(14, R.string.scroll_view),
    CARD_VIEW(15, R.string.card_view),
    FAB(16, R.string.fab),
    SNACKBAR_TOAST(17, R.string.snackbar_toast),
    ALERT_DIALOG(18, R.string.alert_dialog),
    BOTTOM_NAVIGATION(19, R.string.bottom_navigation),
    GLIDE_COIL(20, R.string.glide_coil),
}

