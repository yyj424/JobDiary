package ddwucom.mobile.finalproject.ma01_20180985;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class JobListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Job> jobList;
    LayoutInflater layoutInflater;

    public JobListAdapter(Context context, ArrayList<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Object getItem(int position) {
        return jobList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JobListAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_view_job_list, parent, false);

            holder = new JobListAdapter.ViewHolder();
            holder.name = convertView.findViewById(R.id.tv_jobNm);
            holder.jobClcdNM = convertView.findViewById(R.id.tv_jobClcdNM);
            convertView.setTag(holder);
        } else {
            holder = (JobListAdapter.ViewHolder) convertView.getTag();
        }

        holder.name.setText(jobList.get(position).getJobNm());
        holder.jobClcdNM.setText(jobList.get(position).getJobClcdNM());

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView jobClcdNM;
    }
}
