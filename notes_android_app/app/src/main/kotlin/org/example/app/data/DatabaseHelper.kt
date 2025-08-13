package org.example.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite helper managing the local notes database.
 * This encapsulates table creation and upgrade logic.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // PUBLIC_INTERFACE
    override fun onCreate(db: SQLiteDatabase) {
        /** Create the notes table for first-time installs. */
        db.execSQL(
            """
            CREATE TABLE $TABLE_NOTES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_CONTENT TEXT DEFAULT '',
                $COL_CREATED_AT INTEGER NOT NULL,
                $COL_UPDATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    // PUBLIC_INTERFACE
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /** Handle database schema upgrades. Currently a simple rebuild. */
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NOTES = "notes"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_CONTENT = "content"
        const val COL_CREATED_AT = "created_at"
        const val COL_UPDATED_AT = "updated_at"
    }
}
