package com.api.demo.db;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.api.demo.R;
import com.ljy.devring.DevRing;
import com.ljy.devring.base.activity.IBaseActivity;
import com.ljy.devring.other.toast.RingToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:  ljy
 * date:    2018/12/12
 * description: 演示数据库模块的使用
 *
 * DevRing使用文档：<a>https://www.jianshu.com/p/abede6623c58</a>
 * GreenDao博客介绍：<a>https://www.jianshu.com/p/11bdd9d761e6</a>
 */

public class DBActivity extends AppCompatActivity implements IBaseActivity {

    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.rv_result)
    RecyclerView mRvResult;
    @BindView(R.id.et_id)
    EditText mEtId;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_age)
    EditText mEtAge;

    QueryResultAdapter mQueryResultAdapter;//查询结果列表的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        ButterKnife.bind(this);
        setTitle("数据库模块");
    }

    @OnClick({R.id.btn_insert, R.id.btn_query_name, R.id.btn_query_age, R.id.btn_query_all, R.id.btn_query_count, R.id.btn_update, R.id.btn_delete_age, R.id.btn_delete_id, R.id
            .btn_delete_all})
    protected void onClick(View view) {
        switch (view.getId()) {
            //插入一条数据（如果主键ID已存在则会替换原有数据）
            case R.id.btn_insert:
                String id = mEtId.getText().toString();
                String name = mEtName.getText().toString().trim();
                String age = mEtAge.getText().toString();
                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(age)) {
                    User user = new User(Long.parseLong(id), name, Integer.parseInt(age));
                    DevRing.tableManager(User.class).insertOrReplaceOne(user);
                } else {
                    RingToast.show("输入不能为空");
                }
                break;

            //查询全部用户数据
            case R.id.btn_query_all:
                List<User> listAll = DevRing.tableManager(User.class).loadAll();
                showResult(((Button) view).getText() + "：", listAll);
                break;

            //查询姓名为“张三”的用户数据
            case R.id.btn_query_name:
                List<User> listQueryName = DevRing.tableManager(User.class).queryBySQL("select * from USER where name=?", new String[]{"张三"});
                //如果使用GreenDao框架，建议使用以下代码进行操作，更便捷。
//                List<User> listQueryName = DevRing.<GreenTableManager>tableManager(User.class).queryBuilder().where(UserDao.Properties.Name.eq("张三")).list();
                showResult(((Button) view).getText() + "：", listQueryName);
                break;

            //查询年龄大于等于18的用户数据
            case R.id.btn_query_age:
                List<User> listQueryAge = DevRing.tableManager(User.class).queryBySQL("select * from USER where age>=18 order by age desc", null);
                //如果使用GreenDao框架，建议使用以下代码进行操作，更便捷。
//                List<User> listQueryAge = DevRing.<GreenTableManager>tableManager(User.class).queryBuilder().where(UserDao.Properties.Age.ge(18)).orderDesc(UserDao.Properties
//                        .Age).list();
                showResult(((Button) view).getText() + "：", listQueryAge);
                break;

            //查询当前存在的用户数量
            case R.id.btn_query_count:
                long count = DevRing.tableManager(User.class).count();
                RingToast.show("当前用户数量：" + count);
                break;

            //将所有年龄大于等于18岁的用户名更改为“成年人”
            case R.id.btn_update:
                List<User> listUpdate = DevRing.tableManager(User.class).queryBySQL("select * from USER where age>=18", null);
                //如果使用GreenDao框架，建议使用以下代码进行操作，更便捷。
//                List<User> listUpdate = DevRing.<GreenTableManager>tableManager(User.class).queryBuilder().where(UserDao.Properties.Age.ge(18)).list();
                if (listUpdate != null) {
                    for (User user : listUpdate) {
                        user.setName("成年人");
                    }
                }
                DevRing.tableManager(User.class).updateSome(listUpdate);
                break;

            //根据主键删除数据
            case R.id.btn_delete_id:
                DevRing.tableManager(User.class).deleteOneByKey(Long.valueOf(1));
                break;

            //删除年龄小于18的用户数据
            case R.id.btn_delete_age:
                List<User> listDelete = DevRing.tableManager(User.class).queryBySQL("select * from USER where age<18", null);
                //如果使用GreenDao框架，建议使用以下代码进行操作，更便捷。
//                List<User> listDelete = DevRing.<GreenTableManager>tableManager(User.class).queryBuilder().where(UserDao.Properties.Age.lt(18)).list();
                DevRing.tableManager(User.class).deleteSome(listDelete);
                break;

            //删除所有用户数据
            case R.id.btn_delete_all:
                DevRing.tableManager(User.class).deleteAll();
                break;

        }
    }

    //显示查询结果
    private void showResult(String title, List<User> list) {
        mTvResult.setText(title);
        mQueryResultAdapter = new QueryResultAdapter(list);
        mRvResult.setLayoutManager(new LinearLayoutManager(this));
        mRvResult.setNestedScrollingEnabled(false);
        mRvResult.setAdapter(mQueryResultAdapter);
    }

    @Override
    public boolean isUseEventBus() {
        return false;
    }

    @Override
    public boolean isUseFragment() {
        return false;
    }
}
