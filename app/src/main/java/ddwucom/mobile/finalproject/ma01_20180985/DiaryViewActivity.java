package ddwucom.mobile.finalproject.ma01_20180985;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DiaryViewActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etContent;
    TextView tvDate;
    ImageView img;
    Button share;

    String id;
    String date;
    String imgUri = null;

    DiaryDBHelper helper;
    Cursor cursor;
    DatePickerDialog datePickerDialog;
    private File imageFile;

    final int PERMISSION_REQ_CODE = 100;
    private static final int PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_view);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        etTitle = findViewById(R.id.et_diary_title);
        etContent = findViewById(R.id.et_diary_content);
        tvDate = findViewById(R.id.tv_diary_date);
        img = findViewById(R.id.iv_diary_image);
        img.setVisibility(View.GONE);
        share = findViewById(R.id.btn_share);
        share.setVisibility(View.INVISIBLE);
        helper = new DiaryDBHelper(this);
        if(id != null) {
            getDiary();
        }
        else {
            final Calendar calendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(DiaryViewActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if ((month + 1) < 10) {
                date = year + ".0" + (month + 1);
            }
            else {
                date = year + "." + (month + 1);
            }
            if (dayOfMonth < 10) {
                date += ".0" + (dayOfMonth);
            }
            else {
                date += "." + (dayOfMonth);
            }
            tvDate.setText(date);
        }
    };

    public void getDiary() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = "_id=?";
        String selectArgs[] = new String[] {id};
        cursor = db.query(DiaryDBHelper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        cursor.moveToNext();
        final String title = cursor.getString(cursor.getColumnIndex("title"));
        etTitle.setText(title);
        final String content = cursor.getString(cursor.getColumnIndex("content"));
        etContent.setText(content);
        date = cursor.getString(cursor.getColumnIndex("date"));
        tvDate.setText(date);
        final String uri = cursor.getString(cursor.getColumnIndex("image"));
        if(uri != null) {
            setImage(uri);
            img.setVisibility(View.VISIBLE);
        }
        String[] array = date.split("\\.");
        int y = Integer.parseInt(array[0]);
        int m = Integer.parseInt(array[1]);
        int d = Integer.parseInt(array[2]);
        m = m - 1;
        datePickerDialog = new DatePickerDialog(DiaryViewActivity.this, listener, y, m, d);
        share.setVisibility(View.VISIBLE);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedTemplate params = FeedTemplate
                        .newBuilder(ContentObject.newBuilder(title + "\n" + date, "uri",
                                LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                        .setMobileWebUrl("https://developers.kakao.com").build())
                                .setDescrption(content)
                                .build())
                        .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()))
                        .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .setAndroidExecutionParams("key1=value1")
                                .setIosExecutionParams("key1=value1")
                                .build()))
                        .build();

                Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                serverCallbackArgs.put("user_id", "${current_user_id}");
                serverCallbackArgs.put("product_id", "${current_user_id}");
                KakaoLinkService.getInstance().sendDefault(DiaryViewActivity.this, params, new ResponseCallback <KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                    }
                });
            }
        });
    }

    public void onClick (View v)
    {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_diary_register:
                if (etTitle.getText() == null || etTitle.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (tvDate.getText().toString().equals("날짜 선택")) {
                    Toast.makeText(this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues row = new ContentValues();
                row.put("title", etTitle.getText().toString());
                row.put("date", tvDate.getText().toString());
                row.put("content", etContent.getText().toString());
                row.put("image", imgUri);
                if (id != null) {
                    String updateId = "_id=?";
                    String[] update = new String[] {id};
                    db.update("diary_table", row, updateId, update);
                }
                else {
                    db.insert("diary_table", null, row);
                }
                helper.close();

                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_get_img:
                if (checkPermission()) {
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType
                            (android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, PICK);
                }
                break;
            case R.id.iv_diary_image:
                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryViewActivity.this);
                builder.setMessage("이미지를 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        img.setVisibility(View.GONE);
                        imgUri = null;
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if(imageFile != null) {
                if (imageFile.exists()) {
                    if (imageFile.delete()) {
                        imageFile = null;
                    }
                }
            }
            return;
        }
        if (requestCode == PICK) {
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imageFile = new File(cursor.getString(column_index));
                imgUri = imageFile.getAbsolutePath();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage(imgUri);
        }
    }

    private void setImage(String uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(uri, options);

        img.setImageBitmap(bitmap);
        img.setVisibility(View.VISIBLE);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
