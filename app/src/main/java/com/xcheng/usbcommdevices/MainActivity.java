package com.xcheng.usbcommdevices;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity implements IReceiveListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String XC_FILE_TRANSFER_START = "xc_file_transfer_start";
    private static final String XC_SUB_FILE_TRANSFER_READY = "xc_sub_file_transfer_ready";
    private static final String XC_FILE_TRANSFER_END = "xc_file_transfer_end";
    public static final String FILE_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";

    private int dataLen;
    private String fileName;
    private CommunicateManager manager;
    private DataAdapter dataAdapter;

    private RecyclerView rvData;
    private EditText etSendData;
    private Button btnSendData;
    private boolean isSendingFile = false;
    private File mFile;
    private int offset = 0;
    private int targetPro = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = new CommunicateManager(this);
        manager.setReceiveListener(this);

        initView();

    }

    private void initView() {
        rvData = findViewById(R.id.rv_data);
        etSendData = findViewById(R.id.et_send);
        btnSendData = findViewById(R.id.btn_send);

        dataAdapter = new DataAdapter(new ArrayList<>(), this);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(dataAdapter);

        btnSendData.setOnClickListener(v -> {
            String data = etSendData.getText().toString();
            if (TextUtils.isEmpty(data)) {
                Toast.makeText(this, getString(R.string.et_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            manager.sendData(data.getBytes());
            updateData(getString(R.string.device_prompt) + data);
            etSendData.setText("");
        });
    }

    private void updateData(String line) {
        dataAdapter.update(line);
        rvData.smoothScrollToPosition(dataAdapter.getItemCount() - 1);
    }


    @Override
    public void onReceiveData(byte[] data) {
        runOnUiThread(() -> {
            String receiveData = new String(data);
            if (receiveData.startsWith(XC_FILE_TRANSFER_START)) {
                String[] splitData = receiveData.split(",");
                dataLen = Integer.parseInt(splitData[1]);
                fileName = splitData[2];
                Log.d(TAG, "onReceiveData: dataLen=" + dataLen + ",fileName=" + fileName);
                if (dataLen != 0 && !TextUtils.isEmpty(fileName)) {
                    manager.sendData(XC_SUB_FILE_TRANSFER_READY.getBytes());
                    isSendingFile = true;
                    createFile();
                    updateData(" =========================file receive start =========================");
                }
                return;
            }
            if (receiveData.equals(XC_FILE_TRANSFER_END)) {
                isSendingFile = false;
                offset = 0;
                targetPro = 0;
                updateData(" =========================file receive end =========================");
                return;
            }
            if (isSendingFile) {
                receiveFile(data, data.length);
                return;
            }
            updateData(getString(R.string.host_prompt) + receiveData);
        });
    }

    private void createFile() {
        try {
            mFile = new File(FILE_SAVE_PATH, fileName);
            if (mFile.exists()) {
                mFile.delete();
            }
            mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "createFile: " + mFile.getAbsolutePath());
    }

    public void receiveFile(byte[] payload, int length) {
        try {
            write(payload, length);
            offset += length;
            float progress = ((float) offset / dataLen) * 100;
            if (targetPro != (int) progress) {
                targetPro = (int) progress;
                updateData("total length =" + dataLen + " ,transfer length=" + offset + " ,Length of each pass =" + length);
                updateData("The transmission progress is：" + format(progress) + "%");
                //Log.d(TAG, "total length =" + dataLen + " ,transfer length=" + offset + " ,Length of each pass =" + length);
                //Log.d(TAG, "Devices Accept data progress is：" + format(progress) + "%");
            }
            if ((int) progress == 100) {
                manager.sendData(XC_FILE_TRANSFER_END.getBytes());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void write(byte[] bytes, int length) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(mFile, true)) { // 追加模式
            fos.write(bytes, 0, length);
        }
    }

    public String format(double progress) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(progress);
    }
}
