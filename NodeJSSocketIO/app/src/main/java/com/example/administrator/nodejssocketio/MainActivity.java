package com.example.administrator.nodejssocketio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.0.11:3000");
        } catch (URISyntaxException e) {}
    }


    Button btnSent, btnSign;
    EditText edtNoiDung;


    ListView lvStatus;
    ArrayList<String> arrayUser;
    ArrayAdapter adapter = null;

    ListView lvMess;
    ArrayList<String> arrayMess;
    ArrayAdapter adapterMess = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSocket.connect();

        // Nhận dữ liệu từ server gửi về
        mSocket.on("server-sent-result", onNewStatus);
        mSocket.on("server-send-listuser", onNewMessage_List_User);
        mSocket.on("server-send-mess", onNewMessage_Mess);

        AddControl();

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtNoiDung.getText().toString().trim();
                mSocket.emit("client-send-user", username); // Đặt tên sự kiện để nhận dạng trên server.. để tạo username
                edtNoiDung.setText("");
            }
        });
        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = edtNoiDung.getText().toString();
                if (mess.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Chưa có nội dung!", Toast.LENGTH_SHORT).show();
                } else {
                    mSocket.emit("client-sent-chat", mess);
                    edtNoiDung.setText("");
                }
            }
        });
    }

    // Destroy
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mSocket.disconnect();
//
//    }

    private void AddControl() {

        btnSent = (Button) findViewById(R.id.buttonSent);
        btnSign = (Button) findViewById(R.id.buttonRegister);
        edtNoiDung = (EditText) findViewById(R.id.editTextNoiDung);

        lvStatus = (ListView) findViewById(R.id.lvStatus);
        arrayUser = new ArrayList<String>();
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayUser);
        lvStatus.setAdapter(adapter);

        lvMess = (ListView) findViewById(R.id.lvMess);
        arrayMess = new ArrayList<String>();
        adapterMess = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayMess);
        lvMess.setAdapter(adapterMess);
    }

    private Emitter.Listener onNewStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) { // Thêm 1 . vào
            runOnUiThread(new Runnable() { // Bỏ getActivity().
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0]; // Trên server chỉ trả về 1 giá trị nên lấy lại phần từ thứ 0 trong mảng
                    try {
                        int status = data.getInt("ketqua");
                        if (status == 0) {
                            Toast.makeText(MainActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onNewMessage_List_User = new Emitter.Listener() {
        @Override
        public void call(final Object...args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        arrayUser.clear();
                        JSONArray jsonArrayUsername = data.getJSONArray("danhsach");
                        for (int i = 0;i < jsonArrayUsername.length();i++) {
                            String Username = jsonArrayUsername.getString(i);
                            //Toast.makeText(MainActivity.this, Username + "Đã tham gia phòng chat", Toast.LENGTH_SHORT).show();
                            arrayUser.add(Username + " Đã tham gia phòng Chat!");
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    // Mess
    private Emitter.Listener onNewMessage_Mess = new Emitter.Listener() {
        @Override
        public void call(final Object...args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //arrayMess.clear();
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String mess = data.getString("mess");
                        arrayMess.add(mess);
                        adapterMess.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
