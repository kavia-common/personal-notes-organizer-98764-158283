package org.example.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for rendering a list of notes.
 */
class NoteAdapter(
    private val items: MutableList<Note>,
    private val listener: NoteInteractionListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface NoteInteractionListener {
        // PUBLIC_INTERFACE
        fun onNoteClicked(note: Note)
        // PUBLIC_INTERFACE
        fun onNoteLongPressed(note: Note)
    }

    fun updateData(newItems: MutableList<Note>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.noteTitle)
        private val content: TextView = itemView.findViewById(R.id.noteContent)
        private val date: TextView = itemView.findViewById(R.id.noteDate)

        fun bind(note: Note, listener: NoteInteractionListener) {
            title.text = note.title
            content.text = if (note.content.isBlank()) itemView.context.getString(R.string.no_content) else note.content
            val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
            date.text = sdf.format(Date(note.updatedAt))

            itemView.setOnClickListener { listener.onNoteClicked(note) }
            itemView.setOnLongClickListener { listener.onNoteLongPressed(note); true }
        }
    }
}
