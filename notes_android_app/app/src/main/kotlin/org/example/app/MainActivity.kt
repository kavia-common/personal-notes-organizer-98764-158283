package org.example.app

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.example.app.data.Note
import org.example.app.data.NoteRepository
import org.example.app.ui.NoteAdapter

/**
 * The main activity displaying the list of notes.
 * Provides a search action and a floating action button to create new notes.
 */
class MainActivity : AppCompatActivity(), NoteAdapter.NoteInteractionListener {

    private lateinit var repository: NoteRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fab: FloatingActionButton

    // PUBLIC_INTERFACE
    override fun onCreate(savedInstanceState: Bundle?) {
        /** Initialize UI, repository, and events for the main screen. */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = NoteRepository(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoteAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, NoteEditorActivity::class.java))
        }

        // Configure search action in the toolbar
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.queryHint = getString(R.string.search_hint)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText ?: "")
                return true
            }
        })

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> true
                else -> false
            }
        }
    }

    // PUBLIC_INTERFACE
    override fun onResume() {
        /** Refresh the list of notes whenever the activity is resumed. */
        super.onResume()
        loadNotes()
    }

    private fun performSearch(query: String) {
        Thread {
            val results = repository.searchNotes(query)
            runOnUiThread {
                adapter.updateData(results.toMutableList())
            }
        }.start()
    }

    private fun loadNotes() {
        Thread {
            val notes = repository.getAllNotes()
            runOnUiThread {
                adapter.updateData(notes.toMutableList())
            }
        }.start()
    }

    // PUBLIC_INTERFACE
    override fun onNoteClicked(note: Note) {
        /** Open the detail screen for the selected note. */
        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
        startActivity(intent)
    }

    // PUBLIC_INTERFACE
    override fun onNoteLongPressed(note: Note) {
        /** Delete the selected note after a long press with undo feedback. */
        val deleted = note.copy()
        Thread {
            repository.deleteNote(note.id)
            val notes = repository.getAllNotes()
            runOnUiThread {
                adapter.updateData(notes.toMutableList())
                Snackbar.make(recyclerView, R.string.note_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        Thread {
                            repository.addNote(deleted.title, deleted.content)
                            val refreshed = repository.getAllNotes()
                            runOnUiThread {
                                adapter.updateData(refreshed.toMutableList())
                            }
                        }.start()
                    }.show()
            }
        }.start()
    }
}
