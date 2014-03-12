package ru.synthet.synthit.model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.synthet.synthit.R;

import java.util.ArrayList;

public class CompsArrayProvider extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> rows;
    private final ArrayList<String> values;

        public CompsArrayProvider(Activity context, ArrayList<String> rows, ArrayList<String> values) {
            super(context, R.layout.comps_row, R.id.textRow, values);
            this.context = context;
            this.values  = values;
            this.rows    = rows;
        }

        // Класс для сохранения во внешний класс и для ограничения доступа
        // из потомков класса
        static class ViewHolder {
            public TextView textRowView;
            public TextView textHeaderRowView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // ViewHolder буферизирует оценку различных полей шаблона элемента

            ViewHolder holder;
            // Очищает сущетсвующий шаблон, если параметр задан
            // Работает только если базовый шаблон для всех классов один и тот же
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.comps_row, null, true);
                holder = new ViewHolder();
                holder.textHeaderRowView = (TextView) rowView.findViewById(R.id.textRowHeader);
                holder.textRowView       = (TextView) rowView.findViewById(R.id.textRow);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.textHeaderRowView.setText(rows.get(position));
            holder.textRowView.setText(values.get(position));

            return rowView;
        }
    }
