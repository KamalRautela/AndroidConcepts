package com.example.androidconcepts.common

import com.example.androidconcepts.R

interface CONCEPTS {
    val topicId: Int
    val topicNameResId : Int
}

enum class TOPICS(override val topicId: Int, override val topicNameResId : Int) : CONCEPTS {
    UI_BASICS(1, R.string.ui_basics),
    ACTIVITY_LIFECYCLE(2, R.string.activity_lifecycle),
    MVVM_CONCEPTS(3, R.string.mvvm_jetpack)
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

enum class ActivityLifecycleConcepts(override val topicId: Int, override val topicNameResId: Int) : CONCEPTS {
    ACTIVITY_LIFECYCLE(1, R.string.al_activity_lifecycle),
    SAVE_INSTANCE_STATE(2, R.string.al_save_instance_state),
    EXPLICIT_INTENT(4, R.string.al_explicit_intent),
    IMPLICIT_INTENT(5, R.string.al_implicit_intent),
    PASS_DATA(6, R.string.al_pass_data),
    ACTIVITY_RESULT_LAUNCHER(7, R.string.al_activity_result_launcher),
    INTENT_FLAGS(8, R.string.al_intent_flags),
    FRAGMENT_BASICS(9, R.string.al_fragment_basics),
    FRAGMENT_MANAGER(10, R.string.al_fragment_manager),
    BACK_STACK(11, R.string.al_back_stack),
    FRAGMENT_COMMUNICATION(12, R.string.al_fragment_communication),
    PERMISSION_TYPES(13, R.string.al_permission_types),
    PERMISSION_REQUEST(14, R.string.al_permission_request),
    PERMISSION_DENIED(15, R.string.al_permission_denied),
}

enum class MVVMConcepts(override val topicId: Int, override val topicNameResId: Int) : CONCEPTS {
    BASIC_VIEWMODEL(1, R.string.basic_mvvm_view_model),
    LIVE_DATA(2, R.string.mvvm_live_data),
    STATE_FLOW(3, R.string.mvvm_state_flow),
    LIVEDATA_VS_STATEFLOW(4, R.string.mvvm_livedata_vs_stateflow),
    MVVM_PATTERN(5, R.string.mvvm_pattern),
    REPOSITORY_PATTERN(6, R.string.mvvm_repository_pattern),
}

