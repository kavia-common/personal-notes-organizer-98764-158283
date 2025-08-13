package org.example.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

/**
 * Repository handling CRUD operations for notes using SQLite.
 * This abstracts persistence and can be replaced by a remote database integration later.
 */
class NoteRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context.applicationContext)

    // PUBLIC_INTERFACE
    fun getAllNotes(): List<Note> {
        /** Retrieve all notes ordered by last update descending. */
        val db = dbHelper.readableDatabase
        val notes = mutableListOf<Note>()
        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null, null, null, null, null,
            "${DatabaseHelper.COL_UPDATED_AT} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                notes.add(cursorToNote(it))
            }
        }
        return notes
    }

    // PUBLIC_INTERFACE
    fun searchNotes(query: String): List<Note> {
        /** Search notes by title or content using a LIKE query. */
        if (query.isBlank()) return getAllNotes()
        val db = dbHelper.readableDatabase
        val notes = mutableListOf<Note>()
        val like = "%${query.replace("%", "\\%").replace("_", "\\_")}%"
        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            "${DatabaseHelper.COL_TITLE} LIKE ? ESCAPE '\\' OR ${DatabaseHelper.COL_CONTENT} LIKE ? ESCAPE '\\'",
            arrayOf(like, like),
            null, null,
            "${DatabaseHelper.COL_UPDATED_AT} DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                notes.add(cursorToNote(it))
            }
        }
        return notes
    }

    // PUBLIC_INTERFACE
    fun getNoteById(id: Long): Note? {
        /** Get an individual note by its primary key. */
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        cursor.use {
            return if (it.moveToFirst()) cursorToNote(it) else null
        }
    }

    // PUBLIC_INTERFACE
    fun addNote(title: String, content: String): Long {
        /** Insert a new note and return the new row ID. */
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TITLE, title)
            put(DatabaseHelper.COL_CONTENT, content)
            put(DatabaseHelper.COL_CREATED_AT, now)
            put(DatabaseHelper.COL_UPDATED_AT, now)
        }
        val db = dbHelper.writableDatabase
        return db.insert(DatabaseHelper.TABLE_NOTES, null, values)
    }

    // PUBLIC_INTERFACE
    fun updateNote(id: Long, title: String, content: String): Int {
        /** Update an existing note by ID. Returns number of rows updated. */
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TITLE, title)
            put(DatabaseHelper.COL_CONTENT, content)
            put(DatabaseHelper.COL_UPDATED_AT, now)
        }
        val db = dbHelper.writableDatabase
        return db.update(
            DatabaseHelper.TABLE_NOTES,
            values,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // PUBLIC_INTERFACE
    fun deleteNote(id: Long): Int {
        /** Delete a note by ID. Returns number of rows deleted. */
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun cursorToNote(cursor: Cursor): Note {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTENT))
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT))
        val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UPDATED_AT))
        return Note(id, title, content, createdAt, updatedAt)
    }
}
