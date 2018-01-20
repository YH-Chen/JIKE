package com.example.great.project.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.example.great.project.Database.StudentDB;
import com.example.great.project.Database.TaskDB;
import com.example.great.project.Model.Task;
import com.example.great.project.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class DDLListAppWidget extends AppWidgetProvider {
    private static List<Task> sList;
    private ComponentName thisWidget;
    private RemoteViews remoteViews;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ddllist_app_widget);
//        ListView initList = views

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction() != null && intent.getAction().equals("static_action")){
            String sName = "";
            Bundle bundle = intent.getExtras();
            if(bundle != null && bundle.getString("sName") != null){
                sName = bundle.getString("sName");
            }
            sList = getSortedDDLList(context, sName);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            thisWidget = new ComponentName(context, DDLListAppWidget.class);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.ddllist_app_widget);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            Intent intentToService = new Intent(context, UpdateService.class);
            for (int appWidgetId : appWidgetIds) {
                intentToService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            }
            //设置适配器
            remoteViews.setRemoteAdapter(R.id.widget_ddl_list, intentToService);

            //更新RemoteViews
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);

            for (int appWidgetId : appWidgetIds) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_ddl_list);
            }
        }
    }

    private static List<Task> getSortedDDLList(Context context, String sName){
        TaskDB myTaskDB = new TaskDB(context);
        StudentDB myStudentDB = new StudentDB(context);
        List<Task> taskList = myTaskDB.searchByParticipantName(sName);
        ArrayList<Map<String, Object>> taskTimeList = new ArrayList<>();
        Date curr_date = new Date(System.currentTimeMillis());
        for(int i = 0; i < taskList.size(); i++){
            long time_diff = taskList.get(i).getTaskDDL().getTime() - curr_date.getTime();
            if(time_diff >= 0){
                Map<String, Object> temp = new HashMap<>();
                temp.put("taskId", taskList.get(i).getId());
                temp.put("time_diff", time_diff);
                taskTimeList.add(temp);
            }
        }
        Collections.sort(taskTimeList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Long value1 = (Long)o1.get("time_diff");
                Long value2 = (Long)o2.get("time_diff");
                return value1-value2 > 0 ? 1 : 0;
            }
        });
        int maxTask = myStudentDB.QuerySetting(sName).getMaxTask();
        List<Task> res = new ArrayList<>();
        int taskAmount = maxTask > taskTimeList.size() ? taskTimeList.size() : maxTask;
        for(int i = 0; i < taskAmount; i++){
            res.add(myTaskDB.searchByTaskID((Integer)taskTimeList.get(i).get("taskId")));
        }
        return res;
    }

    public static List<Task> getList(){
        return sList;
    }
}

