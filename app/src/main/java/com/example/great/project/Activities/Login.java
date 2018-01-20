package com.example.great.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.great.project.Database.StudentDB;
import com.example.great.project.Model.Student;
import com.example.great.project.R;

import java.util.ArrayList;
import java.util.List;

public class Login extends BaseActivity {

    //记录用户首次点击返回键的时间
    private long firstTime=0;
    private Button login;
    private Button reg;
    private Button confirmreg;
    private Button back;
    private LinearLayout loginBtns;
    private LinearLayout regBtns;
    private EditText username;
    private EditText pwd;
    private EditText confirmpwd;

    private StudentDB sdb = new StudentDB(this);
    private List<Student> stuList = new ArrayList<>();

    private void initial(){
        login = findViewById(R.id.login_login);
        reg = findViewById(R.id.login_reg);
        loginBtns = findViewById(R.id.login_btns);
        confirmreg = findViewById(R.id.login_confirmreg);
        username = findViewById(R.id.login_username);
        pwd = findViewById(R.id.login_pwd);
        confirmpwd = findViewById(R.id.login_confirmpwd);
        back = findViewById(R.id.back_login);
        regBtns = findViewById(R.id.reg_btns);
        regBtns.setVisibility(View.GONE);
        confirmpwd.setVisibility(View.GONE);
    }

    private void setListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String pwdStr = pwd.getText().toString();
                if(usernameStr.isEmpty()) {
                    Toast.makeText(Login.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(pwdStr.isEmpty()) {
                    Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    /*search username in Database
                    if username not exist : toast
                    else if name and pwd do not match : toast
                    else
                    */
                    stuList = sdb.queryStu(usernameStr);
                    if(stuList.isEmpty() || !pwdStr.equals(stuList.get(0).getPassword())) {
                        Toast.makeText(Login.this, "用户不存在或密码错误", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("username", usernameStr);
                        Login.this.setResult(1, intent);
                        Login.this.finish();
                    }
                }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtns.setVisibility(View.VISIBLE);
                confirmpwd.setVisibility(View.VISIBLE);
                loginBtns.setVisibility(View.GONE);
            }
        });

        confirmreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String pwdStr = pwd.getText().toString();
                String confirmpwdStr = confirmpwd.getText().toString();
                stuList = sdb.queryStu(usernameStr);
                if(usernameStr.isEmpty()){
                    Toast.makeText(Login.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if(!stuList.isEmpty()) {
                    Toast.makeText(Login.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                } else if(pwdStr.isEmpty() || confirmpwdStr.isEmpty()){
                    Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if(!pwdStr.equals(confirmpwdStr)){
                    Toast.makeText(Login.this, "密码不匹配", Toast.LENGTH_SHORT).show();
                } else {
                    Student item = new Student(usernameStr, usernameStr, pwdStr);
                    sdb.insertStu(item);
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("username", usernameStr);
                    Login.this.setResult(1, intent);
                    Login.this.finish();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtns.setVisibility(View.GONE);
                confirmpwd.setVisibility(View.GONE);
                loginBtns.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initial();
        setListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis() - firstTime>2000){
                Toast.makeText(Login.this,"再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            }else{
                Intent intent = new Intent("action.exit");
                sendBroadcast(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
