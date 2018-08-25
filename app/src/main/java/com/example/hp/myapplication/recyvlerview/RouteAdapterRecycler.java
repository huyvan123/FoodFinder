package com.example.hp.myapplication.recyvlerview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.myapplication.R;
import com.example.hp.myapplication.model.directions.Step;

import java.util.List;
import java.util.Random;

public class RouteAdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Step> stepList;
    private Activity activity;
    public RouteAdapterRecycler(List<Step> stepList, Activity activity) {
        this.stepList = stepList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("ResourceType") View view = LayoutInflater.from(activity)
                .inflate(R.layout.step_listview, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new RouteHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Step step = stepList.get(i);
        RouteHolder routeHolder = (RouteHolder) viewHolder;
        routeHolder.step.setText(String.valueOf(i+1));
        routeHolder.distance.setText(step.getDistance().getText());
        routeHolder.duration.setText(step.getDuration().getText());
        routeHolder.travel.setText(step.getTravelMode());
        routeHolder.instruction.setText(getInStruction(step,i));
        System.out.println("route step: " +step.getHtmlInstruction());
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    private String getInStruction(Step step, int index){
        String instruction = "";
        String htmlIstruction = Html.fromHtml(step.getHtmlInstruction()).toString();
        switch (index) {
            case 0:
                instruction += "Bắt đầu ";
                break;
            case 1:
                instruction += "Tiếp theo ";
                break;
            case 2:
                instruction += "Sau đó ";
                break;
                default:
                    Random random = new Random();
                    int r = random.nextInt(3);
                    instruction += getNextString(r);
                    break;
        }
        instruction += htmlIstruction.toLowerCase();
        instruction += " một đoạn " + step.getDistance().getText();
        return instruction;
    }

    private String getNextString(int index){
        switch (index) {
            case 0: return "Tiếp tục ";
            case 1: return "Tiếp theo ";
            case 2: return "Sau đó ";
            default: return "Đi ";
        }
    }

    class RouteHolder extends RecyclerView.ViewHolder{
        private TextView step, distance, duration, travel, instruction;
        public RouteHolder(@NonNull View itemView) {
            super(itemView);
            step = itemView.findViewById(R.id.step);
            distance = itemView.findViewById(R.id.distance);
            duration = itemView.findViewById(R.id.duration);
            travel = itemView.findViewById(R.id.travel);
            instruction = itemView.findViewById(R.id.instruction);
        }
    }
}
