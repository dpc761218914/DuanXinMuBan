package com.dpc.administrator.duanxinmuban;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private EditText et_muban;
    private TextView tv_muban;
    private TextView tv_dx;
    private TextView tv_lxr;
    private TextView tv_dh;
    private Button btn_send;
    private String muban = "亲爱的#name，你好，你们位于#address的甲醛超标了，考虑近期帮你做除甲醛处理！";
    private Button btn_getcontact;
    private static final int PICK_CONTACT = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private Intent mIntent;
    private String duanxinStr;
    private String phoneNumberStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_muban = (EditText) findViewById(R.id.et_muban);
        tv_muban = (TextView) findViewById(R.id.tv_muban);
        tv_dx = (TextView) findViewById(R.id.tv_dx);
        tv_lxr = (TextView) findViewById(R.id.tv_lxr);
        tv_dh = (TextView) findViewById(R.id.tv_dh);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_getcontact = (Button) findViewById(R.id.btn_getcontact);


        //发送短信
        btn_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //发送短信
                String et_muban_str = et_muban.getText().toString();
                //获取联系人
                String str_lxr=tv_lxr.getText().toString();
                //获取地址
               // String muban2 = muban.replace("#name", str_lxr).replace("#address",et_muban_str);
               // tv_dx.setText(muban2);
                //Toast.makeText(MainActivity.this,muban2,Toast.LENGTH_LONG).show();
                String phoneNumber=phoneNumberStr.replace(" ", "");
                sendMsg(duanxinStr,phoneNumber);
            }
        });

        //获取联系人
        btn_getcontact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

    }

    /*
    发送短信
    * */
    private void sendMsg(String message,String phoneNo) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "短信发送成功.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "短信发送失败啦.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    /*
    * 发送选择联系人后的回调
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_CONTACT:
                mIntent = data;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //申请授权，第一个参数为要申请用户授权的权限；第二个参数为requestCode 必须大于等于0，主要用于回调的时候检测，匹配特定的onRequestPermissionsResult。
                    //可以从方法名requestPermissions以及第二个参数看出，是支持一次性申请多个权限的，系统会通过对话框逐一询问用户是否授权。
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

                }else{
                    //如果该版本低于6.0，或者该权限已被授予，它则可以继续读取联系人。
                    getContacts(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /*
    * 获取权限的询问
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户成功授予权限
                getContacts(mIntent);
            } else {
                Toast.makeText(this, "你拒绝了此应用对读取联系人权限的申请！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
    *获取联系人方法
    * */

    private void getContacts(Intent data) {
        if (data == null) {
            return;
        }

        Uri contactData = data.getData();
        if (contactData == null) {
            return;
        }
        String name = "";
        String phoneNumber = "";

        Uri contactUri = data.getData();
        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String hasPhone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
            cursor.close();
            Toast.makeText(this, "用户："+name+"电话号码"+phoneNumber, Toast.LENGTH_SHORT).show();
          //  tv_lxr.setText(name);
            tv_dh.setText(name+phoneNumber);

            //设置真实短信
            //发送短信
            String et_muban_str = et_muban.getText().toString();
            //获取联系人
            // String str_lxr=tv_lxr.getText().toString();
            //获取地址
            phoneNumberStr=phoneNumber;
            duanxinStr = muban.replace("#name", name).replace("#address",et_muban_str);
            tv_dx.setText("短信内容："+duanxinStr);
        }
    }
}

