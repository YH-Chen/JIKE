package com.example.great.project.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.great.project.Database.CourseDB;
import com.example.great.project.Database.TaskDB;
import com.example.great.project.Model.CourseModel;
import com.example.great.project.Model.Task;
import com.example.great.project.R;
import com.example.great.project.View.TitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LessonDetail extends AppCompatActivity {


    //课程详细信息。根据main传来的intent中课程名称去数据库搜索相关信息，填充页面。
    //初步计划信息：课程名，课程时间，课程地点，老师名称，以及一个课程任务列表
    //课程要有一个修改按钮，跳转到LessonEdit进行编辑。可以不提供删除，因为加了之后act的跳转比较难。
    //页面排版很重要，记得做美观一点。
    //记得每次修改后要及时更新信息，包括数据库和界面上的内容
    //记得设置返回按键之类的东西
    //单击课程表里的任务表，跳转到任务界面
    //课程下可以新增任务，类似于从主界面新增课程。TaskEdit类为新增任务

    public static int TOTASKINFO = 1;
    private SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private CourseDB cdb = new CourseDB(this);
    private TaskDB tdb = new TaskDB(this);

    private TextView courseRoom;
    private TextView courseStratTime;
    private TextView courseEndTime;
    private TextView courseTeacher;
    private TextView courseDate;
    private Button changeBtn;
    private Button deleteBtn;
    private Button newTaskBtn;
    private RecyclerView TaskRec;
    private TitleBar titlebar;

    private String sname;
    private List<Map<String, Object>> taskItem = new ArrayList<>();
    private CommonAdapter<Object> taskAdp;
    private CourseModel course;

    private LayoutInflater factor;
    private View view_in;

    private void initial(){
        courseRoom = findViewById(R.id.course_detail_room);
        courseStratTime = findViewById(R.id.course_detail_start_time);
        courseEndTime = findViewById(R.id.course_detail_end_time);
        courseTeacher = findViewById(R.id.course_detail_teacher);
        courseDate = findViewById(R.id.course_detail_weekday);
//        changeBtn = findViewById(R.id.course_detail_change);
//        deleteBtn = findViewById(R.id.course_detail_delete);
//        newTaskBtn = findViewById(R.id.course_detail_new_task_btn);
        TaskRec = findViewById(R.id.course_detail_taskrec);
//        titlebar = findViewById(R.id.course_detail_titlebar);

        Intent intent = getIntent();
        sname = intent.getStringExtra("sname");
        course = (CourseModel) intent.getSerializableExtra("course");

        courseRoom.setText(course.getRoom());
        courseStratTime.setText(course.getStartTime());
        courseEndTime.setText(course.getEndTime());
        courseTeacher.setText(course.getTeacherName());
        courseDate.setText(course.getWeekDay());

        setTitle(course.getCourseName());
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //任务列表初始化
        taskAdp = new CommonAdapter<Object>(this, R.layout.course_detail_task_items, generateTaskList(sname, course.getCourseId())) {
            @Override
            public void convert(ViewHolder viewHolder, Object object) {
                if(object instanceof Task){
                    LinearLayout outer1 = viewHolder.getView(R.id.course_detail_task_item_real);
                    outer1.setVisibility(View.VISIBLE);
                    LinearLayout outer2 = viewHolder.getView(R.id.course_detail_task_item_type_outer);
                    outer2.setVisibility(View.GONE);
                    Task task = (Task)object;
                    TextView name = viewHolder.getView(R.id.course_detail_task_name);
                    name.setText(task.getTaskName());
                    TextView ddl = viewHolder.getView(R.id.course_detail_task_ddl);
                    ddl.setText(DTF.format(task.getTaskDDL()));
                }
                else if(object instanceof String){
                    LinearLayout outer1 = viewHolder.getView(R.id.course_detail_task_item_real);
                    outer1.setVisibility(View.GONE);
                    LinearLayout outer2 = viewHolder.getView(R.id.course_detail_task_item_type_outer);
                    outer2.setVisibility(View.VISIBLE);
                    String joinType = (String)object;
                    TextView typeTextView = viewHolder.getView(R.id.course_detail_task_item_type);
                    typeTextView.setText(joinType);
                }
            }
        };
        TaskRec.setLayoutManager(new LinearLayoutManager(this));
        TaskRec.setAdapter(taskAdp);
    }

    private void setListener(){
//        titlebar.setTitle(course.getCourseName());
//        titlebar.setLeftText("返回");
//        titlebar.setLeftImageResource(R.drawable.ic_left_black);
//        titlebar.setLeftClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setResult(2);
//                LessonDetail.this.finish();
//            }
//        });


//        changeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder editCourse = new AlertDialog.Builder(LessonDetail.this);
//                editCourse.setTitle("修改课程");
//                LayoutInflater factor = LayoutInflater.from(LessonDetail.this);
//                View view_in = factor.inflate(R.layout.course_edit_dialog_layout, null);
//                editCourse.setView(view_in);
//                final EditText editCourseName = view_in.findViewById(R.id.course_edit_name);
//                final EditText editCourseRoom = view_in.findViewById(R.id.course_edit_room);
//                final EditText editCourseStartHour = view_in.findViewById(R.id.course_edit_start_hour);
//                final EditText editCourseStratMinute = view_in.findViewById(R.id.course_edit_start_minute);
//                final EditText editCourseEndHour = view_in.findViewById(R.id.course_edit_end_hour);
//                final EditText editCourseEndMinute = view_in.findViewById(R.id.course_edit_end_minute);
//                final EditText editCourseTeacher = view_in.findViewById(R.id.course_edit_teacher);
//                final Spinner editCourseweekday = view_in.findViewById(R.id.course_edit_weekday);
//                editCourseName.setText(course.getCourseName());
//                editCourseRoom.setText(course.getRoom());
//                editCourseTeacher.setText(course.getTeacherName());
//                editCourse.setPositiveButton("修改课程", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        course.setRoom(editCourseRoom.getText().toString());
//                        course.setCourseName(editCourseName.getText().toString());
//                        course.setStartTime(editCourseStartHour.getText().toString() + ":" + editCourseStratMinute.getText().toString());
//                        course.setEndTime(editCourseEndHour.getText().toString() + ":" + editCourseEndMinute.getText().toString());
//                        course.setWeekDay(editCourseweekday.getSelectedItem().toString());
//                        course.setTeacherName(editCourseTeacher.getText().toString());
//                        if (!editCourseName.getText().toString().isEmpty()) cdb.updateCourse(sname, course);
//                        courseRoom.setText(course.getRoom());
//                        courseStratTime.setText(course.getStartTime());
//                        courseEndTime.setText(course.getEndTime());
//                        courseTeacher.setText(course.getTeacherName());
//                        courseDate.setText(course.getWeekDay());
//                    }
//                });
//                editCourse.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {}
//                });
//                editCourse.show();
//            }
//        });
//
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder deleteCourse = new AlertDialog.Builder(LessonDetail.this);
//                deleteCourse.setTitle("确认删除该课程？");
//                deleteCourse.setPositiveButton("删除课程", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        cdb.deleteCourse(sname, course.getCourseId());
//                        setResult(2);
//                        LessonDetail.this.finish();
//                    }
//                });
//                deleteCourse.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {}
//                });
//                deleteCourse.show();
//            }
//        });

        taskAdp.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if(taskAdp.getItem(position) instanceof Task){
                    final int pos = position;
                    final Task click_task = tdb.searchByTaskID(((Task)taskAdp.getItem(position)).getId());
                    int click_join_type = tdb.getJoinType(sname, click_task.getId());
                    if(click_join_type == 0 || click_join_type == 1){
                        AlertDialog.Builder joinTaskDiaglog = new AlertDialog.Builder(LessonDetail.this);
                        joinTaskDiaglog.setTitle("是否加入任务'"+click_task.getTaskName()+"'")
                                .setNegativeButton("不了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setPositiveButton("是的，我加入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tdb.joinTask(click_task.getId(), sname);
                                taskAdp.removeItem(pos);
                                taskAdp.addItem(1, click_task);
                            }
                        }).show();
                    }else if(click_join_type == 2){
                        Intent intent = new Intent(LessonDetail.this, TaskDetail.class);
                        intent.putExtra("taskId", ((Task)taskAdp.getItem(position)).getId());
                        intent.putExtra("courseId", course.getCourseId());
                        intent.putExtra("sName", sname);
                        startActivityForResult(intent, TOTASKINFO);
                    }
                }
            }

            @Override
            public void onLongClick(int position) {
                if(taskAdp.getItem(position) instanceof Task){
                    final int pos = position;
                    final Task click_task = tdb.searchByTaskID(((Task)taskAdp.getItem(position)).getId());
                    int click_join_type = tdb.getJoinType(sname, click_task.getId());
                    AlertDialog.Builder deleteConfirmDialog = new AlertDialog.Builder(LessonDetail.this);
                    if(click_join_type == 2){
                        deleteConfirmDialog.setTitle("是否删除任务？" + ((Task)taskAdp.getItem(pos)).getTaskName())
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                })
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tdb.quitTask(((Task)taskAdp.getItem(pos)).getId(), sname);
                                        taskAdp.removeItem(pos);
                                        taskAdp.addItem(taskAdp.getItemCount(), click_task);
                                    }
                                }).show();
                    }else if(click_join_type == 1){
                        deleteConfirmDialog.setTitle("拒绝被邀请参加这个任务？" + ((Task)taskAdp.getItem(pos)).getTaskName())
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                })
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tdb.quitTask(((Task)taskAdp.getItem(pos)).getId(), sname);
                                        taskAdp.removeItem(pos);
                                        taskAdp.addItem(taskAdp.getItemCount(), click_task);
                                    }
                                }).show();
                    }
                }
            }
        });
//        newTaskBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder addTaskAlertDialog = new AlertDialog.Builder(LessonDetail.this);
//                addTaskAlertDialog.setTitle("任务");
//                LayoutInflater factor = LayoutInflater.from(LessonDetail.this);
//                View view_in = factor.inflate(R.layout.course_detail_add_task_dialog_layout, null);
//                addTaskAlertDialog.setView(view_in);
//                final EditText editTaskName = view_in.findViewById(R.id.course_detail_add_task_dialog_taskname);
//                final EditText editTaskBrief = view_in.findViewById(R.id.course_detail_add_task_dialog_taskbrief);
//                final DatePicker editTaskDDL = view_in.findViewById(R.id.course_detail_add_task_dialog_taskDDL);
//                addTaskAlertDialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Date d = new Date(editTaskDDL.getYear()-1900, editTaskDDL.getMonth(), editTaskDDL.getDayOfMonth());
//                        int newId = tdb.newTask(new Task(1, course.getCourseId(), editTaskName.getText().toString(),
//                                editTaskBrief.getText().toString(), d , sname), true);
//                        //加入后得到id
//                        Task temp = new Task(newId, course.getCourseId(), editTaskName.getText().toString(),
//                                editTaskBrief.getText().toString(), d , sname);
//                        taskAdp.addItem(1, temp);
//                    }
//                });
//                addTaskAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {}
//                });
//                addTaskAlertDialog.show();
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        initial();
        setListener();
    }

    ArrayList<Object> generateTaskList(String sname, int courseId){
        ArrayList<Object> res = new ArrayList<>();
        res.add("已参加");
        res.addAll(tdb.searchByJoinType(sname, courseId, 2));
        res.add("被邀请");
        res.addAll(tdb.searchByJoinType(sname, courseId, 1));
        res.add("未参加");
        res.addAll(tdb.searchByJoinType(sname, courseId, 0));
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initial();
        setListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lesson_set, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backHome:
                finish();
                return true;
            case R.id.addTask:
                Log.d("TAG", "添加任务");
                AlertDialog.Builder addTaskAlertDialog = new AlertDialog.Builder(LessonDetail.this);
                addTaskAlertDialog.setTitle("任务");
                factor = LayoutInflater.from(LessonDetail.this);
                view_in = factor.inflate(R.layout.course_detail_add_task_dialog_layout, null);
                addTaskAlertDialog.setView(view_in);
                final EditText editTaskName = view_in.findViewById(R.id.course_detail_add_task_dialog_taskname);
                final EditText editTaskBrief = view_in.findViewById(R.id.course_detail_add_task_dialog_taskbrief);
                final DatePicker editTaskDDL = view_in.findViewById(R.id.course_detail_add_task_dialog_taskDDL);
                addTaskAlertDialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date d = new Date(editTaskDDL.getYear()-1900, editTaskDDL.getMonth(), editTaskDDL.getDayOfMonth());
                        int newId = tdb.newTask(new Task(1, course.getCourseId(), editTaskName.getText().toString(),
                                editTaskBrief.getText().toString(), d , sname), true);
                        //加入后得到id
                        Task temp = new Task(newId, course.getCourseId(), editTaskName.getText().toString(),
                                editTaskBrief.getText().toString(), d , sname);
                        taskAdp.addItem(1, temp);
                    }
                });
                addTaskAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
                return true;
            case R.id.modify_course:
                AlertDialog.Builder editCourse = new AlertDialog.Builder(LessonDetail.this);
                editCourse.setTitle("修改课程");
                factor = LayoutInflater.from(LessonDetail.this);
                view_in = factor.inflate(R.layout.course_edit_dialog_layout, null);
                editCourse.setView(view_in);
                final EditText editCourseName = view_in.findViewById(R.id.course_edit_name);
                final EditText editCourseRoom = view_in.findViewById(R.id.course_edit_room);
                final EditText editCourseStartHour = view_in.findViewById(R.id.course_edit_start_hour);
                final EditText editCourseStratMinute = view_in.findViewById(R.id.course_edit_start_minute);
                final EditText editCourseEndHour = view_in.findViewById(R.id.course_edit_end_hour);
                final EditText editCourseEndMinute = view_in.findViewById(R.id.course_edit_end_minute);
                final EditText editCourseTeacher = view_in.findViewById(R.id.course_edit_teacher);
                final Spinner editCourseweekday = view_in.findViewById(R.id.course_edit_weekday);
                editCourseName.setText(course.getCourseName());
                editCourseRoom.setText(course.getRoom());
                editCourseTeacher.setText(course.getTeacherName());
                editCourse.setPositiveButton("修改课程", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        course.setRoom(editCourseRoom.getText().toString());
                        course.setCourseName(editCourseName.getText().toString());
                        course.setStartTime(editCourseStartHour.getText().toString() + ":" + editCourseStratMinute.getText().toString());
                        course.setEndTime(editCourseEndHour.getText().toString() + ":" + editCourseEndMinute.getText().toString());
                        course.setWeekDay(editCourseweekday.getSelectedItem().toString());
                        course.setTeacherName(editCourseTeacher.getText().toString());
                        if (!editCourseName.getText().toString().isEmpty()) cdb.updateCourse(sname, course);
                        courseRoom.setText(course.getRoom());
                        courseStratTime.setText(course.getStartTime());
                        courseEndTime.setText(course.getEndTime());
                        courseTeacher.setText(course.getTeacherName());
                        courseDate.setText(course.getWeekDay());
                    }
                });
                editCourse.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
                return true;
            case R.id.delete_course:
                AlertDialog.Builder deleteCourse = new AlertDialog.Builder(LessonDetail.this);
                deleteCourse.setTitle("确认删除该课程？");
                deleteCourse.setPositiveButton("删除课程", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cdb.deleteCourse(sname, course.getCourseId());
                        setResult(2);
                        LessonDetail.this.finish();
                    }
                });
                deleteCourse.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
