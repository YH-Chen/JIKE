package com.example.great.project.Widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.example.great.project.Model.Task;
import com.example.great.project.R;
import com.example.great.project.Widget.DDLListAppWidget;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UpdateService extends RemoteViewsService {
    public UpdateService() {
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        private final Context mContext;
        private final List<Task> mList;
        private SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        public ListRemoteViewsFactory(Context context, Intent intent){
            mContext = context;
            mList = DDLListAppWidget.getList();

            if(Looper.myLooper() == null){
                Looper.prepare();
            }
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
            mList.clear();
        }

        @Override
        public int getCount() {
            if(mList == null){
                return 0;
            }
            return mList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(position<0 || position>=mList.size())
                return null;
            String content = mList.get(position).getTaskName();
            String deadlineStr = DTF.format(mList.get(position).getTaskDDL());
            final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_layout);

            Intent intent = new Intent();
            rv.setOnClickFillInIntent(R.id.widget_list_item, intent);
            rv.setTextViewText(R.id.widget_list_item_tv, content);
            rv.setTextViewText(R.id.widget_list_item_ddl_tv, deadlineStr);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
