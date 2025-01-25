package Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;
import Controller.Note;

public class NotesAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;

    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Реализуйте создание и настройку View для каждого элемента списка
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.note_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);

        Note note = notes.get(position);
        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());

        return convertView;
    }
}