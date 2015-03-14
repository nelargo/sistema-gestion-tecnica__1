package cl.inexcell.sistemadegestion;
import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class listAdapter extends ArrayAdapter<itemList> {
 
        private final Context context;
        private final ArrayList<itemList> itemsArrayList;
        public ImageView iv;
 
        public listAdapter(Context context, ArrayList<itemList> itemsArrayList) {
 
            super(context, R.layout.list_item, itemsArrayList);
 
            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
 
            // 1. Create inflater 
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.list_item, parent, false);
 
            // 3. Get the two text view from the rowView
            TextView labelView = (TextView) rowView.findViewById(R.id.numero);
            TextView valueView = (TextView) rowView.findViewById(R.id.modelo);
            iv = (ImageView) rowView.findViewById(R.id.imageView1);
 
            // 4. Set the text for textView 
            labelView.setText(itemsArrayList.get(position).getTitle());
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            valueView.setText(itemsArrayList.get(position).getDescription());
            
            if(itemsArrayList.get(position).getMensaje() == -1)
            	iv.setImageResource(android.R.drawable.arrow_down_float);
            else
            	iv.setImageResource(itemsArrayList.get(position).getMensaje());
 
            // 5. retrn rowView
            return rowView;
        }
}