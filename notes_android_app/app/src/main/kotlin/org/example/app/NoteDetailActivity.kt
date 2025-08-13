package org.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.example.app.data.Note
import org.example.app.data.NoteRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Displays the details of a single note.
 * Provides menu actions to edit or delete the note.
 */
class NoteDetailActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private var noteId: Long = -1L

    private lateinit var titleView: TextView
    private lateinit var contentView: TextView
    private lateinit var dateView: TextView

    // PUBLIC_INTERFACE
    override fun onCreate(savedInstanceState: Bundle?) {
        /** Initialize UI and load the note details. */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.note_detail)

        repository = NoteRepository(this)
        titleView = findViewById(R.id.detailTitle)
        contentView = findViewById(R.id.detailContent)
        dateView = findViewById(R.id.detailDate)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        loadNote()
    }

    private fun loadNote() {
        Thread {
            val note = repository.getNoteById(noteId)
            runOnUiThread {
                if (note != null) {
                    bindNote(note)
                } else {
                    finish()
                }
            }
        }.start()
    }

    private fun bindNote(note: Note) {
        titleView.text = note.title
        contentView.text = note.content
        val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        dateView.text = sdf.format(Date(note.updatedAt))
    }

    // PUBLIC_INTERFACE
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        /** Inflate menu actions for editing and deleting the note. */
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

    // PUBLIC_INTERFACE
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /** Handle menu actions for editing and deleting the note. */
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.action_edit -> {
                val intent = Intent(this, NoteEditorActivity::class.java)
                intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, noteId)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                Thread {
                    repository.deleteNote(noteId)
                    runOnUiThread { finish() }
                }.start()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
