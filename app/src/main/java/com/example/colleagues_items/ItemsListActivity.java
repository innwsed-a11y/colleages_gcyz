package com.example.colleagues_items;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemsListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private ItemDAO itemDAO;
    private EditText etSearch;
    private Button btnSearch;
    private Button btnFilter;
    private Button btnSort;
    private String searchKeyword;
    private String showType;

    private UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        // 初始化用户偏好设置
        userPrefs = new UserPrefs(this);
        
        // 检查用户是否已登录
        if (!userPrefs.getRememberUser()) {
            // 用户未登录，跳转到登录页面
            Intent intent = new Intent(ItemsListActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化组件
        recyclerView = findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        btnFilter = findViewById(R.id.btn_filter);
        // btnSort = findViewById(R.id.btnSort); // 移除不存在的按钮引用

        // 初始化数据库
        itemDAO = new ItemDAO(this);

        // 初始化物品列表
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                // 跳转到物品详情页面
                Intent intent = new Intent(ItemsListActivity.this, ItemDetailActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Item item) {
                // 显示删除确认对话框
                showDeleteConfirmationDialog(item);
            }
        });
        recyclerView.setAdapter(adapter);

        // 设置搜索按钮点击事件
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    loadSearchResults(keyword);
                } else {
                    loadAllItems();
                }
            }
        });

        // 设置筛选按钮点击事件
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        // 获取传递的参数
        Intent intent = getIntent();
        searchKeyword = intent.getStringExtra("search_keyword");
        showType = intent.getStringExtra("show_type");
        String seller = intent.getStringExtra("seller");

        // 如果有搜索关键词，加载搜索结果
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            etSearch.setText(searchKeyword);
            loadSearchResults(searchKeyword);
        } else if (showType != null) {
            // 根据show_type加载不同类型的物品
            if ("published".equals(showType) && seller != null) {
                // 加载指定卖家发布的物品
                loadSellerItems(seller);
            } else if ("liked".equals(showType)) {
                // 加载用户点赞的物品
                // 注意：这里需要实现用户点赞功能才能正常工作
                Toast.makeText(this, "已点赞商品功能开发中...", Toast.LENGTH_SHORT).show();
                loadAllItems();
            } else {
                // 加载所有物品
                loadAllItems();
            }
        } else {
            // 加载所有物品
            loadAllItems();
        }
    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    // 从数据库加载所有物品
    private void loadAllItems() {
        executorService.execute(() -> {
            List<Item> items = itemDAO.getAllItems();
            handler.post(() -> {
                updateItemsList(items);
            });
        });
    }

    // 加载搜索结果
    private void loadSearchResults(String keyword) {
        executorService.execute(() -> {
            List<Item> items = itemDAO.searchItems(keyword);
            handler.post(() -> {
                updateItemsList(items);
                Toast.makeText(ItemsListActivity.this, "找到 " + items.size() + " 个匹配的物品", Toast.LENGTH_SHORT).show();
            });
        });
    }

    // 加载指定卖家的物品
    private void loadSellerItems(String seller) {
        executorService.execute(() -> {
            List<Item> items = itemDAO.getItemsBySeller(seller);
            handler.post(() -> {
                updateItemsList(items);
            });
        });
    }

    // 更新物品列表
    private void updateItemsList(List<Item> items) {
        itemList.clear();
        itemList.addAll(items);
        adapter.notifyDataSetChanged();
    }

    // 显示删除确认对话框
    private void showDeleteConfirmationDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除确认")
                .setMessage("确定要删除这个物品吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 在后台线程中删除物品
                        executorService.execute(() -> {
                            // 使用ItemDAO删除物品
                            itemDAO.deleteItem(item.getId());

                            // 在主线程中重新加载列表
                            handler.post(() -> {
                                if (searchKeyword != null && !searchKeyword.isEmpty()) {
                                    loadSearchResults(searchKeyword);
                                } else if (showType != null && "published".equals(showType)) {
                                    String seller = getIntent().getStringExtra("seller");
                                    if (seller != null) {
                                        loadSellerItems(seller);
                                    } else {
                                        loadAllItems();
                                    }
                                } else {
                                    loadAllItems();
                                }
                                Toast.makeText(ItemsListActivity.this, "物品已删除", Toast.LENGTH_SHORT).show();
                            });
                        });
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items_list, menu);
        return true;
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            // 刷新列表
            if (searchKeyword != null && !searchKeyword.isEmpty()) {
                loadSearchResults(searchKeyword);
            } else if (showType != null && "published".equals(showType)) {
                String seller = getIntent().getStringExtra("seller");
                if (seller != null) {
                    loadSellerItems(seller);
                } else {
                    loadAllItems();
                }
            } else {
                loadAllItems();
            }
            Toast.makeText(this, "列表已刷新", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_add) {
            // 跳转到发布物品页面
            Intent intent = new Intent(ItemsListActivity.this, PublishItemActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 显示筛选对话框
    private void showFilterDialog() {
        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView)
                .setTitle("筛选条件")
                .setPositiveButton("应用筛选", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取筛选条件
                        EditText etMinPrice = dialogView.findViewById(R.id.et_min_price);
                        EditText etMaxPrice = dialogView.findViewById(R.id.et_max_price);
                        Spinner spCategory = dialogView.findViewById(R.id.sp_category);
                        Spinner spCondition = dialogView.findViewById(R.id.sp_condition);
                        Spinner spCampus = dialogView.findViewById(R.id.sp_campus);
                        Spinner spSortBy = dialogView.findViewById(R.id.sp_sort_by);

                        // 解析筛选条件
                        double minPrice = etMinPrice.getText().toString().isEmpty() ? 0 : Double.parseDouble(etMinPrice.getText().toString());
                        double maxPrice = etMaxPrice.getText().toString().isEmpty() ? 0 : Double.parseDouble(etMaxPrice.getText().toString());
                        String category = spCategory.getSelectedItem().toString();
                        String condition = spCondition.getSelectedItem().toString();
                        String campus = spCampus.getSelectedItem().toString();
                        String sortBy = spSortBy.getSelectedItem().toString();

                        // 转换筛选条件
                        category = category.equals("全部") ? "" : category;
                        condition = condition.equals("全部") ? "" : condition;
                        campus = campus.equals("全部") ? "" : campus;

                        // 转换排序条件
                        String sortByValue = "newest"; // 默认按最新发布
                        if (sortBy.equals("价格从低到高")) {
                            sortByValue = "price_asc";
                        } else if (sortBy.equals("价格从高到低")) {
                            sortByValue = "price_desc";
                        } else if (sortBy.equals("最新发布")) {
                            sortByValue = "newest";
                        } else if (sortBy.equals("最早发布")) {
                            sortByValue = "oldest";
                        }

                        // 执行筛选
                        filterItems(category, condition, campus, minPrice, maxPrice, sortByValue);
                    }
                })
                .setNegativeButton("取消", null)
                .show();

        // 初始化筛选对话框中的Spinner
        initFilterSpinners(dialogView);
    }

    // 初始化筛选对话框中的Spinner
    private void initFilterSpinners(View dialogView) {
        // 商品分类
        String[] categories = {"全部", "数码产品", "图书教材", "生活用品", "体育器材", "服饰箱包", "文具用品"};
        Spinner spCategory = dialogView.findViewById(R.id.sp_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // 新旧程度
        String[] conditions = {"全部", "全新", "九成新", "八成新", "七成及以下"};
        Spinner spCondition = dialogView.findViewById(R.id.sp_condition);
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conditions);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCondition.setAdapter(conditionAdapter);

        // 所在校区
        String[] campuses = {"广州校区", "三水校区"};
        Spinner spCampus = dialogView.findViewById(R.id.sp_campus);
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, campuses);
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCampus.setAdapter(campusAdapter);

        // 排序方式
        String[] sortByOptions = {"最新发布", "价格从低到高", "价格从高到低", "最早发布"};
        Spinner spSortBy = dialogView.findViewById(R.id.sp_sort_by);
        ArrayAdapter<String> sortByAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortByOptions);
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortBy.setAdapter(sortByAdapter);
    }

    // 筛选物品
    private void filterItems(String category, String condition, String campus, double minPrice, double maxPrice, String sortBy) {
        // 在后台线程中执行筛选
        executorService.execute(() -> {
            // 使用ItemDAO筛选物品
            List<Item> items = itemDAO.filterItems(category, condition, campus, minPrice, maxPrice, sortBy);

            // 在主线程中更新UI
            handler.post(() -> {
                updateItemsList(items);
                Toast.makeText(ItemsListActivity.this, "筛选结果：" + items.size() + " 个物品", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当页面重新可见时，刷新数据
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            loadSearchResults(searchKeyword);
        } else if (showType != null && "published".equals(showType)) {
            String seller = getIntent().getStringExtra("seller");
            if (seller != null) {
                loadSellerItems(seller);
            } else {
                loadAllItems();
            }
        } else {
            loadAllItems();
        }
    }
}