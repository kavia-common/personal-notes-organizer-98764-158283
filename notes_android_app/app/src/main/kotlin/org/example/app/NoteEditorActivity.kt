package org.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.example.app.data.NoteRepository

/**
 * Editor screen to add a new note or edit an existing one.
 * Displays title and content fields and allows saving.
 */
class NoteEditorActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var repository: NoteRepository

    private var noteId: Long = -1L

    // PUBLIC_INTERFACE
    override fun onCreate(savedInstanceState: Bundle?) {
        /** Initialize UI for note creation or editing and load note if provided. */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_editor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.edit_note)

        titleInput = findViewById(R.id.inputTitle)
        contentInput = findViewById(R.id.inputContent)
        repository = NoteRepository(this)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (noteId != -1L) {
            loadNote()
        }
    }

    private fun loadNote() {
        Thread {
            val note = repository.getNoteById(noteId)
            runOnUiThread {
                if (note != null) {
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)
                }
            }
        }.start()
    }

    // PUBLIC_INTERFACE
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        /** Inflate the save action in the toolbar. */
        menuInflater.inflate(R.menu.menu_note_editor, menu)
        return true
    }

    // PUBLIC_INTERFACE
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /** Handle toolbar actions: save or navigate up. */
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.action_save -> { saveNote(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = titleInput.text.toString().trim()
        val content = contentInput.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.title_required, Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            if (noteId == -1L) {
                repository.addNote(title, content)
            } else {
                repository.updateNote(noteId, title, content)
            }
            runOnUiThread { finish() }
        }.start()
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
