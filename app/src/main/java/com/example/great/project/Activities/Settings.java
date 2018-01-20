package com.example.great.project.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.great.project.Database.StudentDB;
import com.example.great.project.Model.StuSet;
import com.example.great.project.Model.Student;
import com.example.great.project.R;
import com.example.great.project.View.TitleBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Settings extends BaseActivity {

    private static final int CHOOSE_PHOTO = 0;
    private static final int TAKE_PHOTO = 1;
    private static final int CROP_SMALL_PHOTO = 2;
    private static final int CHOOSE_PHOTO_BG = 3;
    private static final int TAKE_PHOTO_BG = 4;
    private static boolean hasPermission = false;
    private Uri imageUri;
    private Bitmap bitmap;
    private ImageView setHeadImage;

    private TitleBar titleBar;
    private AlertDialog.Builder builder;
    private AlertDialog.Builder simple;
    private String sName;
    private String nickName;
    private String password;
    private String headImage;
    private String editOldPW;
    private StudentDB sdb;
    private LinearLayout main_layout;
    private LinearLayout setpw_layout;

    private FrameLayout currentLayout;
    private Intent BroadcastIntent;

    /*
    动态申请权限
     */
    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有读取权限，申请权限弹出对话框
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                hasPermission = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentLayout = (FrameLayout)findViewById(R.id.activity_settings);
        BroadcastIntent = new Intent("UpdateUI");
        currentLayout.setBackgroundResource(R.mipmap.course_detail_bg);


        titleBar = findViewById(R.id.settings_titlebar);
        titleBar.setLeftText("返回");
        titleBar.setLeftImageResource(R.drawable.ic_left_black);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sdb = new StudentDB(this);
        Log.d("TAG", String.valueOf(Environment.getExternalStorageDirectory()));
        // 跳转传参
        Intent stuData= getIntent();
        sName = stuData.getStringExtra("sName");
        nickName = stuData.getStringExtra("nickName");
        password = stuData.getStringExtra("password");
        headImage = stuData.getStringExtra("headImage");
        Log.d("TAG", sName+','+nickName+','+password+','+headImage);


        // 两个界面
        main_layout = (LinearLayout)findViewById(R.id.main_layout);
        setpw_layout = (LinearLayout)findViewById(R.id.layout_setpw);


        // 主界面元素
        setHeadImage = (ImageView)findViewById(R.id.setheadimage);
        TextView showSName = (TextView) findViewById(R.id.showSName);
        final TextView showNickName = (TextView) findViewById(R.id.showNickName);
        TextView pwbtn = (TextView) findViewById(R.id.pwbtn);
        TextView bgbtn = (TextView) findViewById(R.id.bgImage);
        TextView studyBtn = (TextView) findViewById(R.id.study_setting);

        showSName.setText(sName);
        showNickName.setText(nickName);
        if (headImage != null) {
            bitmap = BitmapFactory.decodeFile(headImage);
            setHeadImage.setImageBitmap(bitmap);
        } else {
            setHeadImage.setImageResource(R.mipmap.xiaokeai);
        }

        //修改界面元素
        final EditText setpw1 = (EditText)findViewById(R.id.setpw1);
        final EditText setpw2 = (EditText)findViewById(R.id.setpw2);
        CheckBox showpw = (CheckBox)findViewById(R.id.showpw);
        final TextView hint = (TextView)findViewById(R.id.hint);
        Button ok = (Button) findViewById(R.id.ok);
        Button no = (Button) findViewById(R.id.no);


        builder = new AlertDialog.Builder(Settings.this);
        simple = new AlertDialog.Builder(Settings.this);


        /*
        更改头像
         */
        final String[] options = {"选择本地照片", "拍照"};
        setHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simple.setTitle("更换头像");
                simple.setNegativeButton("取消", null);
                simple.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case CHOOSE_PHOTO:    // 选择本地照片
//                                verifyStoragePermissions(Settings.this);
                                try {
                                    int permission = ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                                    if (permission != PackageManager.PERMISSION_GRANTED) {
                                        // 没有读取权限，申请权限弹出对话框
                                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
                                    } else {
                                        choose_photo(CHOOSE_PHOTO);
                                        Log.d("TAG", "CHOOSE_PHOTO");
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case TAKE_PHOTO:      // 拍照
                                try {
                                    int permission = ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.CAMERA);
                                    if (permission != PackageManager.PERMISSION_GRANTED) {
                                        // 没有读取权限，申请权限弹出对话框
                                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
                                    } else {
                                        take_photo(TAKE_PHOTO);
                                        Log.d("TAG", "TAKE_PHOTO");
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                take_photo();
//                                Log.d("TAG", "TAKE_PHOTO");
                                break;
                        }
                    }
                });
                simple.show();
            }
        });

        /*
        更改昵称
         */
        LinearLayout L3 = (LinearLayout)findViewById(R.id.Line3);
        L3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(Settings.this).inflate(R.layout.dialog_nickname, null);
                final EditText editName = dialogView.findViewById(R.id.editName);
                builder.setView(dialogView)
                       .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nickName = editName.getText().toString();
                                // 保存到数据库
                                Student stu = new Student(sName, nickName, password, headImage);
                                sdb.updateStu(stu);
                                showNickName.setText(nickName);
                            }
                        }).create().show();
            }
        });
        /*
        更改密码
         */
        pwbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(Settings.this).inflate(R.layout.dialog_password, null);
                final EditText oldpw = dialogView.findViewById(R.id.oldpw);
                builder.setView(dialogView)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editOldPW = oldpw.getText().toString();
                                if(password.equals(editOldPW)) {
                                    main_layout.setVisibility(View.GONE);
                                    setpw_layout.setVisibility(View.VISIBLE);
                                } else {
                                    simple.setTitle("提示").setMessage("密码错误，请重新输入。")
                                            .setPositiveButton("确定", null).create().show();
                                }
                            }
                        }).create().show();
            }
        });

        showpw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    setpw1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    setpw2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    setpw1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    setpw2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw1 = setpw1.getText().toString();
                String pw2 = setpw2.getText().toString();
                if(pw1.equals(pw2)) {
                    // 保存到数据库
                    Student stu = new Student(sName, nickName, pw1, headImage);
                    sdb.updateStu(stu);
                    main_layout.setVisibility(View.VISIBLE);
                    setpw_layout.setVisibility(View.GONE);
                } else {
                    hint.setVisibility(View.VISIBLE);
                }
            }
        });
        setpw1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hint.setVisibility(View.INVISIBLE);
                }
            }
        });
        setpw2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hint.setVisibility(View.INVISIBLE);
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_layout.setVisibility(View.VISIBLE);
                setpw_layout.setVisibility(View.GONE);
            }
        });

        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(Settings.this).inflate(R.layout.dialog_study_setting, null);
                final EditText editMaxTask = dialogView.findViewById(R.id.setting_max_task);
                final EditText editStudyTime = dialogView.findViewById(R.id.setting_study_time);
                final EditText editRestTime = dialogView.findViewById(R.id.setting_rest_time);

                builder.setView(dialogView)
                        .setTitle("学习设置")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int maxTask = Integer.parseInt(editMaxTask.getText().toString());
                                int studyTime = Integer.parseInt(editStudyTime.getText().toString());
                                int restTime = Integer.parseInt(editRestTime.getText().toString());
                                // 保存到数据库
                                StuSet settingTemp = new StuSet(sName, maxTask, studyTime, restTime);
                                sdb.StudySetting(settingTemp);
                            }
                        }).create().show();
            }
        });

        /*
         *更改背景
         */
        bgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simple.setTitle("更换背景");
                simple.setNegativeButton("取消", null);
                simple.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case CHOOSE_PHOTO:    // 选择本地照片
                                try {
                                    int permission = ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                                    if (permission != PackageManager.PERMISSION_GRANTED) {
                                        // 没有读取权限，申请权限弹出对话框
                                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CHOOSE_PHOTO_BG);
                                    } else {
                                        choose_photo(CHOOSE_PHOTO_BG);
                                        Log.d("TAG", "CHOOSE_PHOTO_BG");
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case TAKE_PHOTO:      // 拍照
                                try {
                                    int permission = ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.CAMERA);
                                    if (permission != PackageManager.PERMISSION_GRANTED) {
                                        // 没有读取权限，申请权限弹出对话框
                                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO_BG);
                                    } else {
                                        take_photo(TAKE_PHOTO_BG);
                                        Log.d("TAG", "TAKE_PHOTO_BG");
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                }).show();
            }
        });
    }

    private void take_photo(int mode){
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.png");
        try {
            if(outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(Settings.this, getApplicationContext().getPackageName() + ".fileprovider", outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        // 启动相机程序
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (mode == TAKE_PHOTO)
            startActivityForResult(openCameraIntent, TAKE_PHOTO);
        else
            startActivityForResult(openCameraIntent, TAKE_PHOTO_BG);
    }

    private void choose_photo(int mode){
        //启动相册
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        openAlbumIntent.setType("image/*");
        if (mode == CHOOSE_PHOTO)
            startActivityForResult(openAlbumIntent, CHOOSE_PHOTO);
        else
            startActivityForResult(openAlbumIntent, CHOOSE_PHOTO_BG);
    }

    /*
     *对图片剪裁
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.d("TAG", "The uri is not exist");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PHOTO);
    }


    /*
     *保存剪裁之后的图片显示到界面
     */
    protected void setImageToView(Intent data){
        Bundle extras = data.getExtras();
        if (extras != null) {
            bitmap = extras.getParcelable("data");
            setHeadImage.setImageBitmap(bitmap);

            // 创建文件夹
            String filePath = "/storage/emulated/0/students/";
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                localFile.mkdir();
            }


            FileOutputStream fos = null;
            String imagePath = filePath + sName + "_image.png";
            Log.d("TAG", "savepath as "+ imagePath);
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Student stu = new Student(sName, nickName, password, imagePath);
            sdb.updateStu(stu);
        }
    }

    /*
     *保存到文件夹广播路径
     */
    protected void setImageToBG(Uri uri) {
        if (uri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 创建文件夹
            String filePath = "/storage/emulated/0/students/";
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                localFile.mkdir();
            }

            FileOutputStream fos = null;
            String imagePath = filePath  + sName + "_bg.png";
            Log.d("TAG", "savepath as "+ imagePath);
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable bd = new BitmapDrawable(Resources.getSystem(), bitmap);
            currentLayout.setBackground(bd);
            BroadcastIntent = new Intent("UpdateUI");
            BroadcastIntent.putExtra("bgImage", imagePath);
            sendBroadcast(BroadcastIntent);
        }
    }

    /*
     *接受相机activity的返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK)
                    cutImage(data.getData());
                break;
            case TAKE_PHOTO:
                cutImage(imageUri);
                break;
            case CROP_SMALL_PHOTO:
                if (data != null) {
                    setImageToView(data);
                }
                break;
            case CHOOSE_PHOTO_BG:
                if (data!=null) {
                    imageUri = data.getData();
                    setImageToBG(imageUri);
                    Log.d("TAG", "CHOOSE_PHOTO_BG OK!");
                }
                break;
            case TAKE_PHOTO_BG:
                setImageToBG(imageUri);
                Log.d("TAG", "TAKE_PHOTO_BG OK!");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CHOOSE_PHOTO:
                    choose_photo(CHOOSE_PHOTO);
                    break;
                case TAKE_PHOTO:
                    take_photo(TAKE_PHOTO);
                    break;
                case CHOOSE_PHOTO_BG:
                    choose_photo(CHOOSE_PHOTO_BG);
                    break;
                case TAKE_PHOTO_BG:
                    take_photo(TAKE_PHOTO_BG);
                    break;
            }
            Log.d("TAG", "onRequestPermissionsResult");
        } else {
            // 拒绝权限
            finish();
            System.exit(0);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
