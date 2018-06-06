package com.example.administrator.hzulife;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.hzulife.adapter.PhotoAdapter;
import com.example.administrator.hzulife.model.bean.LostBean;
import com.example.administrator.hzulife.util.GlideImageLoader;
import com.example.administrator.hzulife.util.OkHttpUtils;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * <pre>
 *     author : 蔡文豪
 *     e-mail : 1261654234@qq.com
 *     time   : 2018/5/1
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class LostActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "权限";
    private RadioButton eadioXw;
    private RadioButton eadioZl;
    private EditText et_title;
    private EditText et_thing;
    private EditText et_name;
    private EditText et_where;
    private EditText et_phone;
    private EditText et_qq;
    private EditText et_info;
    private ProgressBar pg;
    private TextView tvLostJd;
    private Button btnAddlostUp;
    private RecyclerView recyAddlost;
    private  TextView tvAddlostAddpic;
    private ArrayList<String> path = new ArrayList<>();
    private ArrayList<String> uppath = new ArrayList<>();
    private Map<String,String> mpost=new HashMap<>();
    private int num;
    public static final int REQUEST_CODE = 1000;
    private boolean upload = false;//判断是否正在上传中 true是正在上传
    private ProgressDialog progressDialog;
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 8;
    private Context mcontext;
    private Activity mActivity;
    private GalleryConfig galleryConfig;
    private IHandlerCallBack iHandlerCallBack;
    private PhotoAdapter photoAdapter;
    public static final MediaType MEDIA_TYPE_MARKDOWN//定义文件上传类型
            = MediaType.parse("text/x-markdown; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("失物招领");
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        initGallery();
        initView();
        }
        private void initView(){
            mcontext=LostActivity.this;
            mActivity=LostActivity.this;
            et_title = findViewById(R.id.edt_addlost_title);
            et_thing = findViewById(R.id.edt_addlost_thing);
            et_name  = findViewById(R.id.edt_addlost_name);
            et_where = findViewById(R.id.edt_addlost_where);
            et_phone = findViewById(R.id.edt_addlost_linktel);
            et_qq    = findViewById(R.id.edt_addlost_linkqq);
            et_info  = findViewById(R.id.edt_addlost_infor);
            eadioXw  = findViewById(R.id.eadio_xw);
            eadioZl  = findViewById(R.id.eadio_zl);
            pg       = findViewById(R.id.progress_addlost_progressBar);
            tvLostJd = findViewById(R.id.tv_lost_jd);
            btnAddlostUp = findViewById(R.id.btn_addlost_up);
            recyAddlost  = findViewById(R.id.recy_addlost);
            tvAddlostAddpic=findViewById(R.id.tv_addlost_addpic);
            tvAddlostAddpic.setOnClickListener(this);
            eadioXw.setOnClickListener(this);
            eadioZl.setOnClickListener(this);
            btnAddlostUp.setOnClickListener(this);
            galleryConfig = new GalleryConfig.Builder()     //配置 GalleryConfig
                    .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
                    .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                    .provider("com.hzulife.fileprovider")   // provider(必填)
                    .pathList(path)                         // 记录已选的图片
                    .multiSelect(true)                      // 是否多选   默认：false
                    .multiSelect(false, 9)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
                    .maxSize(3)                             // 配置多选时 的多选数量。    默认：9
                    .crop(true, 1, 1, 500, 500)  // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效            // 配置裁剪功能的参数，   默认裁剪比例 1:1
                    .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                    .filePath("/Gallery/Pictures")          // 图片存放路径
                    .build();
            galleryConfig.getBuilder().imageLoader(new GlideImageLoader()).build();//图片加载框架
            RecyclerView recycler = (RecyclerView) findViewById(R.id.recy_addlost);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycler.setLayoutManager(gridLayoutManager);
            photoAdapter = new PhotoAdapter(this, path);
            recycler.setAdapter(photoAdapter);
        }


    /**
     * 上传资料
     */
    private void upLost(){
        uploadcontent();
        List<File>list=new ArrayList<>();
        if(list.size()>0) {
            File file1 = new File(path.get(0));
            File file2 = new File(path.get(1));
            File file3 = new File(path.get(2));
            list.add(file1);
            list.add(file2);
            list.add(file3);
        }
        //进行服务器连接
        OkHttpUtils.sendMultipart(mpost,list,"pic_fb").enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(LostActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                   }
               });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Toast.makeText(LostActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        }
    /**
     * 选择图片
     */
    public  void checkPic(){
        galleryConfig.getBuilder().isOpenCamera(false).build();
        initPermissions();
    }
    // 授权管理
    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "需要授权 ");
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.i(TAG, "拒绝过了");
                Toast.makeText(mcontext, "请在 设置-应用管理 中开启此应用的储存授权。", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "进行授权");
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Log.i(TAG, "不需要授权 ");
            GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(LostActivity.this);
        }
    }
    //用户反馈
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],  int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "同意授权");
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(LostActivity.this);
            } else {
                Log.i(TAG, "拒绝授权");
            }
        }
    }
    /**
     * 添加动态
 */
public void uploadcontent() {
    //判断是否登录
    //获取数据并发布
    String title = et_title.getText().toString().trim();
    String info = et_info.getText().toString().trim();
    String name = et_name.getText().toString().trim();
    String qq = et_qq.getText().toString().trim();
    String phone = et_phone.getText().toString().trim();
    String thing = et_thing.getText().toString().trim();
    String where = et_where.getText().toString().trim();
    mpost.put("title",title);
    mpost.put("info",info);
    mpost.put("name",name);
    mpost.put("qq",qq);
    mpost.put("phone",phone);
    mpost.put("thing",thing);
    mpost.put("where",where);
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_addlost_addpic:
                checkPic();
                break;
            case R.id.btn_addlost_up:
                if(!upload){
                    upLost();
                }
                break;
            case R.id.eadio_xw:
                eadioZl.setChecked(false);
                break;
            case R.id.eadio_zl:
                eadioXw.setChecked(false);
                break;
        }
    }
    //创建监听接口IHandlerCallBack
    private void initGallery() {
        iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }

            @Override
            public void onSuccess(List<String> photoList) {
                Log.i(TAG, "onSuccess: 返回数据");
                path.clear();
                for (String s : photoList) {
                    Log.i(TAG, s);
                    path.add(s);
                }
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 取消");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 结束");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: 出错");
            }
        };

    }
}

/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */