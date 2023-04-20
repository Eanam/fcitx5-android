package org.fcitx.fcitx5.android.ui.main.settings

import android.content.Context
import androidx.preference.EditTextPreference

class EditTextTextPreference(context: Context) : EditTextPreference(context) {

    private var value = ""

    private val currentValue: String
        get() = getPersistedString(value)

    override fun onSetInitialValue(defaultValue: Any?) {
        value = defaultValue as? String ?: getPersistedString("")
    }

    init {
        setOnBindEditTextListener {
            it.setText(currentValue)
        }
    }

    override fun setText(text: String?) {
        val value = text ?: return
        persistString(value)
        notifyChanged()
    }

    override fun callChangeListener(newValue: Any?): Boolean {
        if (newValue !is String) return false
        val value = newValue.takeIf { it.isNotBlank() } ?: return false
        return super.callChangeListener(value)
    }

    object SimpleSummaryProvider : SummaryProvider<EditTextTextPreference> {
        override fun provideSummary(preference: EditTextTextPreference): CharSequence? {
            return preference.run { currentValue }
        }
    }
}
