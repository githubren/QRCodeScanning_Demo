package com.example.yfsl.qrcodescanning_demo;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button simpleModeBtn, chooseImgBtn, commissionBtn, generateImgBtn;
    private EditText text;
    private ImageView imageView;
    private static final int REQUEST_CODE = 0x0001;
    private static final int REQUEST_IMAGE = 0x0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) return;
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });

                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initData() {
        simpleModeBtn.setOnClickListener(this);
        chooseImgBtn.setOnClickListener(this);
        commissionBtn.setOnClickListener(this);
        generateImgBtn.setOnClickListener(this);
        //初始化扫描二维码
        ZXingLibrary.initDisplayOpinion(this);
    }

    private void initView() {
        simpleModeBtn = findViewById(R.id.btn_simple_mode);
        chooseImgBtn = findViewById(R.id.choose_picture);
        commissionBtn = findViewById(R.id.commission);
        generateImgBtn = findViewById(R.id.generate_QR_code);
        text = findViewById(R.id.text);
        imageView = findViewById(R.id.image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_simple_mode://简单模式
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.choose_picture://选择照片扫描
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                intent1.setType("image/*");
                startActivityForResult(intent1, REQUEST_IMAGE);
                break;
            case R.id.commission://定制扫描框
                break;
            case R.id.generate_QR_code://生成二维码图片
                String textContent = text.getText().toString();
                if (TextUtils.isEmpty(textContent)) {
                    Toast.makeText(MainActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                text.setText("");
                Bitmap mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background));
                imageView.setImageBitmap(mBitmap);
                break;
        }
    }
}
