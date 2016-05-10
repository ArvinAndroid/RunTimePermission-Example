package likeit.cn.android6;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1;

    private final int CONTACTS_PERMISSION_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.test1);
        btn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test1:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission(Manifest.permission.READ_CONTACTS)) {
                        testContacts();
                    } else {
                        //request permission
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);
                    }
                } else {
                    testContacts();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "用户授予联系人权限", Toast.LENGTH_SHORT).show();
                testContacts();
            } else {
                Toast.makeText(this, "用户拒绝授予联系人权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void testContacts() {
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        while (cursor.moveToNext()) {
            //获得联系人ID
            String id = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            //获得联系人姓名
            String name = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
            //获得联系人手机号码
            Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

            StringBuilder sb = new StringBuilder("contactid=").append(id).append(name);
            while (phone.moveToNext()) { //取得电话号码(可能存在多个号码)
                int phoneFieldColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = phone.getString(phoneFieldColumnIndex);
                sb.append(phoneNumber + "www");
            }
            //建立一个Log，使得可以在LogCat视图查看结果
            Log.i("test", sb.toString());
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
