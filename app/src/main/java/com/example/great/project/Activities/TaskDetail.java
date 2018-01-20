package com.example.great.project.Activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.great.project.Adapter.HorizontalListView;
import com.example.great.project.Database.StudentDB;
import com.example.great.project.Database.TaskDB;
import com.example.great.project.Database.TaskInfoDB;
import com.example.great.project.Model.Student;
import com.example.great.project.Model.Task;
import com.example.great.project.Model.TaskInfo;
import com.example.great.project.R;
import com.example.great.project.View.TitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskDetail extends BaseActivity {

    //该act为任务详情act。根据用户id、课程id和任务名，从Database中读取详情。
    //页面包括DB中读取的信息填充相应文本框
    //提供编辑功能_TaskEdit
    //随后根据任务id从taskinfo表里读取该任务的相关信息，填充列表
    //该页面显示该任务所有共享者，从任务-user表中根据taskid，选择已经同意加入的用户进来
    //同时可以邀请用户加入。邀请时在任务_user表中新建条目，是否加入置否。
    //可以发布新的info，taskid在task_info表中更新内容

    TaskDB myTaskDB = new TaskDB(TaskDetail.this);
    TaskInfoDB myTaskInfoDB = new TaskInfoDB(TaskDetail.this);
    StudentDB myStudentDB = new StudentDB(TaskDetail.this);

    private SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    TitleBar taskDetailTitleBar;
    LinearLayout headerLayout;
    TextView briefTextView;
    TextView DDLTextView;
    TextView creatorTextView;
    HorizontalListView participantListView;
    RecyclerView taskInfoListView;
    EditText pusherEditor;
    Button sendBtn, showBtn;
    CommonAdapter<TaskInfo> taskInfoAdapter;

    Task curr_task;
    int taskId = 0;
    int courseId = 0;
    String sName = "";

    void initial(){
        taskDetailTitleBar = findViewById(R.id.taskDetail_titlebar);
        headerLayout = findViewById(R.id.taskDetail_header);
        briefTextView = findViewById(R.id.taskDetail_brief);
        creatorTextView = findViewById(R.id.taskDetail_creator);
        DDLTextView = findViewById(R.id.taskDetail_DDL);
        participantListView = findViewById(R.id.taskDetail_participants);
        taskInfoListView = findViewById(R.id.taskDetail_taskInfoList);
        pusherEditor = findViewById(R.id.taskDetail_editor);
        sendBtn = findViewById(R.id.taskDetail_sendBtn);
        showBtn = findViewById(R.id.task_arrow);

        briefTextView.setText(curr_task.getTaskBrief());
        creatorTextView.setText(myStudentDB.queryStu(curr_task.getCreatorName()).get(0).getNickName());
        DDLTextView.setText(DTF.format(curr_task.getTaskDDL()));
        //参与者列表
        List<String> participantNameList = myTaskDB.searchParticipantsByTaskID(taskId);
        SimpleAdapter participantSimpleAdaptor = new SimpleAdapter(this, turnStringsIntoNameList(participantNameList),
                R.layout.task_detail_participants_listitem,new String[]{"name"}, new int[]{R.id.taskDetail_participants_name});
        participantListView.setAdapter(participantSimpleAdaptor);

        //任务信息列表
        taskInfoAdapter = new CommonAdapter<TaskInfo>(this, R.layout.task_info_item_layout, myTaskInfoDB.queryByTask(taskId)){
            @Override
            public void convert(ViewHolder viewHolder, TaskInfo taskInfo) {
                RelativeLayout thisOuterLayout = viewHolder.getView(R.id.taskDetail_this_outer);
                RelativeLayout thatOuterLayout = viewHolder.getView(R.id.taskDetail_that_outer);
                Student stuInTaskInfoList = myStudentDB.queryStu(taskInfo.getPusherId()).get(0);
                String HeadPath = stuInTaskInfoList.getHeadImage();
                Bitmap bm;
                if(HeadPath != null){
                    bm = BitmapFactory.decodeFile(stuInTaskInfoList.getHeadImage());
                }else{
                    bm = BitmapFactory.decodeResource(getResources(), R.mipmap.xiaokeai);
                }
                if(taskInfo.getPusherId().equals(sName)){
                    thisOuterLayout.setVisibility(View.VISIBLE);
                    thatOuterLayout.setVisibility(View.GONE);
                    TextView content = viewHolder.getView(R.id.taskDetail_taskInfo_content2);
                    content.setText(taskInfo.getContent());
                    TextView pusher = viewHolder.getView(R.id.taskDetail_taskInfo_pusher2);
                    pusher.setText(stuInTaskInfoList.getNickName());
                    ImageView image = viewHolder.getView(R.id.taskDetail_avatar2);
                    image.setImageBitmap(bm);
                }else{
                    thisOuterLayout.setVisibility(View.GONE);
                    thatOuterLayout.setVisibility(View.VISIBLE);
                    TextView content = viewHolder.getView(R.id.taskDetail_taskInfo_content1);
                    content.setText(taskInfo.getContent());
                    TextView pusher = viewHolder.getView(R.id.taskDetail_taskInfo_pusher1);
                    pusher.setText(stuInTaskInfoList.getNickName());
                    ImageView image = viewHolder.getView(R.id.taskDetail_avatar1);
                    image.setImageBitmap(bm);
                }
            }
        };
        taskInfoListView.setAdapter(taskInfoAdapter);
        taskInfoListView.setLayoutManager(new LinearLayoutManager(this));
        if(taskInfoAdapter.getItemCount() > 0){
            taskInfoListView.smoothScrollToPosition(taskInfoAdapter.getItemCount()-1);
        }

        //titleBar
        taskDetailTitleBar.setLeftText("返回");
        taskDetailTitleBar.setLeftImageResource(R.drawable.ic_left_black);
        taskDetailTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                TaskDetail.this.finish();
            }
        });
        taskDetailTitleBar.setLeftTextColor(Color.parseColor("#FFFFFF"));
        taskDetailTitleBar.setTitle(curr_task.getTaskName());
        taskDetailTitleBar.setTitleColor(Color.parseColor("#FFFFFF"));
        taskDetailTitleBar.setRightText("邀请好友");
        taskDetailTitleBar.setRightTextColor(Color.parseColor("#FFFFFF"));
        taskDetailTitleBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Student> stuListInDialog = myStudentDB.queryStu(null);
                final String[] stuNameInDialog = new String[stuListInDialog.size()];
                for(int i = 0; i < stuListInDialog.size(); i++){
                    stuNameInDialog[i] = stuListInDialog.get(i).getSName();
                }
                final AlertDialog.Builder pickInvitation = new AlertDialog.Builder(TaskDetail.this);
                pickInvitation.setTitle("你要邀请谁呢?")
                        .setSingleChoiceItems(stuNameInDialog, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myTaskDB.inviteTask(curr_task.getId(), stuListInDialog.get(which).getSName());
                                Toast.makeText(TaskDetail.this, "你邀请了"+stuNameInDialog[which], Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("不邀请了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }

    void setListeners(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = pusherEditor.getText().toString();
                if(!inputStr.equals("")){
                    int newId = myTaskInfoDB.addTaskInfo(new TaskInfo(1, taskId, sName, inputStr));
                    TaskInfo temp = new TaskInfo(newId, taskId, sName, inputStr);
                    taskInfoAdapter.addItem(taskInfoAdapter.getItemCount(), temp);
                    pusherEditor.setText("");
                    taskInfoListView.smoothScrollToPosition(taskInfoAdapter.getItemCount()-1);
                }
            }
        });
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headerLayout.getVisibility() == View.VISIBLE) {
                    headerLayout.setVisibility(View.GONE);
                    showBtn.setBackgroundResource(R.drawable.ic_keyboard_up);
                } else {
                    headerLayout.setVisibility(View.VISIBLE);
                    showBtn.setBackgroundResource(R.drawable.ic_keyboard_down);
                }
            }
        });
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sName.equals(curr_task.getCreatorName())){
                    AlertDialog.Builder addTaskAlertDialog = new AlertDialog.Builder(TaskDetail.this);
                    addTaskAlertDialog.setTitle("修改任务");
                    LayoutInflater factor = LayoutInflater.from(TaskDetail.this);
                    View view_in = factor.inflate(R.layout.course_detail_add_task_dialog_layout, null);
                    addTaskAlertDialog.setView(view_in);
                    final EditText editTaskName = view_in.findViewById(R.id.course_detail_add_task_dialog_taskname);
                    final EditText editTaskBrief = view_in.findViewById(R.id.course_detail_add_task_dialog_taskbrief);
                    final DatePicker editTaskDDL = view_in.findViewById(R.id.course_detail_add_task_dialog_taskDDL);
                    editTaskName.setText(curr_task.getTaskName());
                    editTaskBrief.setText(curr_task.getTaskBrief());
                    String tempDate = DTF.format(curr_task.getTaskDDL());
                    String[] tempDateSplit = tempDate.split("-");
                    editTaskDDL.init(Integer.parseInt(tempDateSplit[0]), Integer.parseInt(tempDateSplit[1])-1, Integer.parseInt(tempDateSplit[2]), null);
                    addTaskAlertDialog.setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Date d = new Date(editTaskDDL.getYear()-1900, editTaskDDL.getMonth(), editTaskDDL.getDayOfMonth());
                            String editTaskBriefRes = editTaskBrief.getText().toString();
                            String editTaskNameRes =  editTaskName.getText().toString();
                            Boolean success = myTaskDB.updateByTaskId(new Task(curr_task.getId(), courseId, editTaskNameRes,
                                    editTaskBriefRes, d , sName), sName);
                            if(success){
                                curr_task = myTaskDB.searchByTaskID(curr_task.getId());
                                taskDetailTitleBar.setTitle(editTaskNameRes);
                                briefTextView.setText(editTaskBriefRes);
                                DDLTextView.setText(DTF.format(d));
                            }
                        }
                    });
                    addTaskAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    addTaskAlertDialog.show();
                }
            }//end onClick
        });//end Listener
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            taskId = extras.getInt("taskId");
            courseId = extras.getInt("courseId");
            sName = extras.getString("sName");
        }
        curr_task = myTaskDB.searchByTaskID(taskId);

        initial();
        setListeners();
    }

    ArrayList<Map<String, Object>> turnStringsIntoNameList(List<String> raw_list){
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        for(int i  = 0; i < raw_list.size(); i++){
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("name", raw_list.get(i));
            res.add(temp);
        }
        return res;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        TaskDetail.this.finish();
    }
}
