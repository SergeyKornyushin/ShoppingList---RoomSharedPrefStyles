package com.example.shoppinglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityNewNoteBinding
import com.example.shoppinglist.entities.NoteItem
import com.example.shoppinglist.fragments.NoteFragment
import com.example.shoppinglist.utils.HtmlManager
import com.example.shoppinglist.utils.TimeManager.getCurrentTime
import com.example.shoppinglist.utils.TouchListener
import java.text.SimpleDateFormat
import java.util.*

//todo refacor
@SuppressLint("ClickableViewAccessibility")
class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var noteItem: NoteItem? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getNote()
        binding.llColorPicker.setOnTouchListener(TouchListener())
        onClickColorPicker()
        actionMenuCallback()
        setTextSize()
    }

    private fun getNote() = with(binding) {
        if (intent.hasExtra(NoteFragment.NEW_NOTE_KEY)) {
            noteItem = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY) as NoteItem
            if (noteItem != null) {
                etTitle.setText(noteItem?.title)
                etDescription.setText(HtmlManager.getFromHtml(noteItem?.content!!).trim())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.note_menu_save) {
            setMainResult()
        } else if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.note_menu_bold) {
            setSelectedTextBold()
        } else if (item.itemId == R.id.note_menu_color_picker) {
            if (binding.llColorPicker.isShown) {
                closeColorPicker()
            } else {
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSelectedTextBold() = with(binding.etDescription) {
        val styles = text.getSpans(selectionStart, selectionEnd, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }

        text.setSpan(boldStyle, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.trim()
        setSelection(selectionStart)
    }

    private fun onClickColorPicker() = with(binding) {
        picRed.setOnClickListener { setSelectedTextColor(R.color.picker_red) }
        picBlack.setOnClickListener { setSelectedTextColor(R.color.picker_black) }
        picBlue.setOnClickListener { setSelectedTextColor(R.color.picker_blue) }
        picGreen.setOnClickListener { setSelectedTextColor(R.color.picker_green) }
        picYellow.setOnClickListener { setSelectedTextColor(R.color.picker_yellow) }
        picOrange.setOnClickListener { setSelectedTextColor(R.color.picker_orange) }
    }

    private fun setSelectedTextColor(colorId: Int) = with(binding.etDescription) {
        val styles = text.getSpans(selectionStart, selectionEnd, ForegroundColorSpan::class.java)

        if (styles.isNotEmpty()) {
            text.removeSpan(styles[0])
        }

        text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this@NewNoteActivity, colorId)),
            selectionStart,
            selectionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text.trim()
        setSelection(selectionStart)
    }

    private fun setMainResult() {
        val editState: String
        val tempNote = if (noteItem == null) {
            editState = NoteFragment.CREATE_NOTE_KEY
            createNewNote()
        } else {
            editState = NoteFragment.UPDATE_NOTE_KEY
            updateNote()
        }
        setResult(RESULT_OK, Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        })
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        noteItem?.copy(
            title = etTitle.text.toString(),
            content = HtmlManager.toHtml(etDescription.text)
        )
    }

    private fun createNewNote(): NoteItem =
        NoteItem(
            null,
            binding.etTitle.text.toString(),
            HtmlManager.toHtml(binding.etDescription.text),
            getCurrentTime(), ""
        )

    private fun openColorPicker() {
        binding.llColorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.llColorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker() {
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                binding.llColorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
        binding.llColorPicker.startAnimation(openAnim)
    }

    private fun actionMenuCallback() {
        val actionCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(mode: android.view.ActionMode?, item: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {}
        }
        binding.etDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun setTextSize() = with(binding){
        etTitle.setTextSize(sharedPreferences
            .getString("title_text_size_key", "18"))
        etDescription.setTextSize(sharedPreferences
            .getString("content_text_size_key", "16"))
    }

    private fun EditText.setTextSize(size: String?){
        if (size != null) this.textSize = size.toFloat()
    }

    private fun getSelectedTheme(): Int =
        when {
            sharedPreferences.getString(getString(R.string.pref_theme_key), "Dark") == "Dark" -> {
                R.style.Theme_ShoppingListDark
            }
            sharedPreferences.getString(getString(R.string.pref_theme_key), "Dark") == "Orange" -> {
                R.style.Theme_ShoppingListOrange
            }
            else -> {
                R.style.Theme_ShoppingListLight
            }

        }
}