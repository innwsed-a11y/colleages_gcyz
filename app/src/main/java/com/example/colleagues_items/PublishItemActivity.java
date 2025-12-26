package com.example.colleagues_items;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PublishItemActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView ivItemImage;
    private EditText etItemName;
    private EditText etItemPrice;
    private EditText etItemDescription;
    private Button btnSelectDate;
    private TextView tvSelectedDate;
    private EditText etSellerName;
    private EditText etContactInfo;
    private Button btnTakePhoto;
    private Button btnChoosePhoto;
    private Button btnCancel;
    private Button btnPublish;
    private CheckBox cbRememberUser;
    private Spinner spCategory;
    private Spinner spCondition;
    private Spinner spCampus;
    private CheckBox cbFreeShipping;
    private CheckBox cbNegotiable;
    private CheckBox cbUrgent;
    private CheckBox cbSelfPickup;

    private String selectedDate;
    private String imagePath;
    private DBHelper dbHelper;
    private ItemDAO itemDAO;
    private UserPrefs userPrefs;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_item);

        // 初始化控件
        ivItemImage = findViewById(R.id.iv_item_image);
        etItemName = findViewById(R.id.et_item_name);
        etItemPrice = findViewById(R.id.et_item_price);
        etItemDescription = findViewById(R.id.et_item_description);
        btnSelectDate = findViewById(R.id.btn_select_date);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        etSellerName = findViewById(R.id.et_seller_name);
        etContactInfo = findViewById(R.id.et_contact_info);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnChoosePhoto = findViewById(R.id.btn_choose_photo);
        btnCancel = findViewById(R.id.btn_cancel);
        btnPublish = findViewById(R.id.btn_publish);
        cbRememberUser = findViewById(R.id.cb_remember_user);
        spCategory = findViewById(R.id.sp_category);
        spCondition = findViewById(R.id.sp_condition);
        spCampus = findViewById(R.id.sp_campus);
        cbFreeShipping = findViewById(R.id.cb_free_shipping);
        cbNegotiable = findViewById(R.id.cb_negotiable);
        cbUrgent = findViewById(R.id.cb_urgent);
        cbSelfPickup = findViewById(R.id.cb_self_pickup);

        // 初始化数据库和SharedPreferences
        dbHelper = new DBHelper(this);
        itemDAO = new ItemDAO(this);
        userPrefs = new UserPrefs(this);

        // 检查用户是否已登录
        if (!userPrefs.getRememberUser()) {
            // 用户未登录，跳转到登录页面
            Intent intent = new Intent(PublishItemActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化Spinner数据源
        initSpinners();

        // 加载保存的用户信息
        loadUserInfo();

        // 设置当前日期为默认日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        tvSelectedDate.setText(selectedDate);

        // 日期选择按钮点击事件
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // 拍照按钮点击事件
        btnTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());

        // 从相册选择按钮点击事件
        btnChoosePhoto.setOnClickListener(v -> dispatchPickPictureIntent());

        // 取消按钮点击事件
        btnCancel.setOnClickListener(v -> finish());

        // 发布按钮点击事件
        btnPublish.setOnClickListener(v -> publishItem());
    }

    // 显示日期选择器
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker datePicker = new DatePicker(this);
        datePicker.init(year, month, day, (view, year1, monthOfYear, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year1, monthOfYear + 1, dayOfMonth);
            tvSelectedDate.setText(selectedDate);
        });
        Toast.makeText(this, "已选择日期: " + selectedDate, Toast.LENGTH_SHORT).show();
    }

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    
    // 调用相机拍照
    private void dispatchTakePictureIntent() {
        // 检查相机权限
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 已有权限，直接调用相机
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            // 请求相机权限
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    // 从相册选择图片
    private void dispatchPickPictureIntent() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK);
    }

    // 处理相机和相册返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivItemImage.setImageBitmap(imageBitmap);
                saveImage(imageBitmap); // 保存图片到文件
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                ivItemImage.setImageURI(selectedImage);
                saveUriToFile(selectedImage); // 将Uri保存为文件并获取路径
            }
        }
    }

    // 将Uri保存为文件并获取路径
    private void saveUriToFile(Uri uri) {
        try {
            // 从Uri获取输入流
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                // 创建文件
                File directory = getExternalFilesDir(null);
                File imageFile = new File(directory, "item_image_" + System.currentTimeMillis() + ".jpg");
                
                // 将输入流内容写入文件
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                // 关闭流
                outputStream.close();
                inputStream.close();
                
                // 保存图片路径
                imagePath = imageFile.getAbsolutePath();
                Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 初始化Spinner数据源
    private void initSpinners() {
        // 商品分类
        String[] categories = {"数码产品", "图书教材", "生活用品", "体育器材", "服饰箱包", "文具用品"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // 新旧程度
        String[] conditions = {"全新", "九成新", "八成新", "七成及以下"};
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, conditions);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCondition.setAdapter(conditionAdapter);

        // 所在校区
        String[] campuses = {"广州校区", "三水校区"};
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, campuses);
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCampus.setAdapter(campusAdapter);
    }

    // 保存图片到文件
    private void saveImage(Bitmap bitmap) {
        File directory = getExternalFilesDir(null);
        File imageFile = new File(directory, "item_image_" + System.currentTimeMillis() + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            imagePath = imageFile.getAbsolutePath();
            Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 加载保存的用户信息
    private void loadUserInfo() {
        String username = userPrefs.getUsername();
        String contact = userPrefs.getContact();
        boolean remember = userPrefs.getRememberUser();

        if (!username.isEmpty()) {
            etSellerName.setText(username);
        }
        if (!contact.isEmpty()) {
            etContactInfo.setText(contact);
        }
        cbRememberUser.setChecked(remember);
    }

    // 发布物品（完整实现）
    private void publishItem() {
        // 获取输入的物品信息
        final String name = etItemName.getText().toString().trim();
        final String priceStr = etItemPrice.getText().toString().trim();
        final String description = etItemDescription.getText().toString().trim();
        final String seller = etSellerName.getText().toString().trim();
        final String contact = etContactInfo.getText().toString().trim();
        final boolean rememberUser = cbRememberUser.isChecked();
        final String category = spCategory.getSelectedItem().toString();
        final String condition = spCondition.getSelectedItem().toString();
        final String campus = spCampus.getSelectedItem().toString();

        // 收集选中的标签
        StringBuilder tagsBuilder = new StringBuilder();
        if (cbFreeShipping.isChecked()) tagsBuilder.append("免运费,");
        if (cbNegotiable.isChecked()) tagsBuilder.append("可议价,");
        if (cbUrgent.isChecked()) tagsBuilder.append("急出,");
        if (cbSelfPickup.isChecked()) tagsBuilder.append("自提,");
        String tags = tagsBuilder.length() > 0 ? tagsBuilder.substring(0, tagsBuilder.length() - 1) : "";

        // 数据校验
        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || seller.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, "请选择商品图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 保存用户信息（如果勾选）
        if (rememberUser) {
            userPrefs.saveUserInfo(seller, contact, true);
        }

        // 构建Item对象
        final Item item = new Item();
        item.setName(name);
        try {
            item.setPrice(Double.parseDouble(priceStr));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "价格格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        item.setDescription(description);
        item.setSeller(seller);
        item.setContact(contact);
        item.setCategory(category);
        item.setCondition(condition);
        item.setCampus(campus);
        item.setTags(tags);
        item.setPublishDate(selectedDate);
        item.setImagePath(imagePath);
        item.setLikes(0); // 初始点赞数为0

        // 保存到数据库
        executorService.execute(() -> {
            long result = itemDAO.insertItem(item);
            handler.post(() -> {
                if (result != -1) {
                    Toast.makeText(PublishItemActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish(); // 发布后返回上一页
                } else {
                    Toast.makeText(PublishItemActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了相机权限，再次调用拍照方法
                dispatchTakePictureIntent();
            } else {
                // 用户拒绝了相机权限，显示提示
                Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}