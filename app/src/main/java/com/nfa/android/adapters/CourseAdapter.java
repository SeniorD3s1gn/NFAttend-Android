package com.nfa.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nfa.android.CourseActivity;
import com.nfa.android.R;
import com.nfa.android.models.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<Course> courses;
    private Context context;

    public CourseAdapter(Context context, List<Course> courses) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.course_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.ItemName.setText(context.getString(R.string.course_item_name, course.getName(),
                course.getNumber(), course.getSection()));
        holder.item_date.setText(context.getString(R.string.course_item_date, course.translateDays()));
        holder.ItemTime.setText(context.getString(R.string.course_item_time, course.getStartTime(),
                course.getEndTime()));
        holder.ItemLocation.setText(context.getString(R.string.course_item_location, course.getLocation()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView ItemName;
        TextView item_date;
        TextView ItemTime;
        TextView ItemLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.ItemName);
            item_date = itemView.findViewById(R.id.item_date);
            ItemTime = itemView.findViewById(R.id.ItemTime);
            ItemLocation = itemView.findViewById(R.id.ItemLocation);

            //item view. onclick,
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   int pos = getAdapterPosition();

                   if(pos != RecyclerView.NO_POSITION)
                   {
                       Course selected = courses.get(pos);
                       Intent i = new Intent(context, CourseActivity.class);
                       i.putExtra("Course", selected);
                       context.startActivity(i);
                   }
                }
            });

        }




    }

}
