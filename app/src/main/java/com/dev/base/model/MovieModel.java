package com.dev.base.model;

import com.dev.base.model.db.MovieCollectDao;
import com.dev.base.model.db.helper.DaoManager;
import com.dev.base.model.entity.FileEntity;
import com.dev.base.model.entity.res.MovieRes;
import com.dev.base.model.entity.table.MovieCollect;
import com.dev.base.model.net.HttpFileObserver;
import com.dev.base.model.net.HttpObserver;
import com.dev.base.model.net.LifeCycleEvent;
import com.dev.base.model.net.RetrofitUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author:  ljy
 * date:    2017/9/27
 * description:  电影相关的数据处理/提供层。
 * 包含相关的网络请求、数据库操作、sharePreferrence等操作
 */

public class MovieModel {

    private MovieCollectDao mMovieCollectDao;//电影收藏表操作类

    public static MovieModel getInstance() {
        return MovieModel.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final MovieModel instance = new MovieModel();
    }

    private MovieModel() {
        mMovieCollectDao = DaoManager.getInstance().getDaoSession().getMovieCollectDao();
    }

    /**
     * 获取正在上映的电影
     *
     * @param start            请求的起始点
     * @param count            获取的电影数量
     * @param observer         请求后的回调
     * @param lifecycleSubject 生命周期触发器
     */
    public void getPlayingMovie(int start, int count, HttpObserver<List<MovieRes>> observer, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        Observable observable = RetrofitUtil.getApiService().getPlayingMovie(start, count);//如果需要嵌套请求的话，则在后面加入flatMap进行处理
        RetrofitUtil.composeToSubscribe(observable, observer, lifecycleSubject);
    }

    /**
     * 获取即将上映的电影
     *
     * @param start            请求的起始点
     * @param count            获取的电影数量
     * @param observer         请求后的回调
     * @param lifecycleSubject 生命周期触发器
     */
    public void getCommingMovie(int start, int count, HttpObserver<List<MovieRes>> observer, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        Observable observable = RetrofitUtil.getApiService().getCommingMovie(start, count);
        RetrofitUtil.composeToSubscribe(observable, observer, lifecycleSubject);
    }

    //加入某部电影到“电影收藏”表
    public void addToMyCollect(MovieCollect movieCollect) {
        mMovieCollectDao.insertOrReplace(movieCollect);
    }

    //从“电影收藏”表中删除某部电影
    public void deleteFromMyCollect(MovieCollect movieCollect) {
        mMovieCollectDao.delete(movieCollect);
    }

    //从“电影收藏”表中获取收藏的数量
    public int getCollectCount() {
        return (int) mMovieCollectDao.count();
    }

    //从“电影收藏”表中获取所有收藏的电影
    public List<MovieCollect> getAllCollect() {
        mMovieCollectDao.detachAll();//清空电影收藏表的缓存，使得取出来的数据为最新数据
        return mMovieCollectDao.queryBuilder().list();

//        Query query = mMovieCollectDao.queryBuilder().where(new WhereCondition.StringCondition("TITLE = ?", "羞羞的铁拳")).build();
//        return query.list();

    }



    /**
     * greendao 增删改查的示例以及更多用法，
     * 请到我的博客 http://www.jianshu.com/p/11bdd9d761e6 进行查看
     */




    /**
     *   以下方法demo中并没实际运行，仅供参考
     */

    /**
     * 上传文本和单个文件
     *
     * @param text             文本
     * @param fileEntity       文件实体
     * @param observer         请求后的回调
     * @param lifecycleSubject 生命周期触发器
     */
    public void upLoadTextAndFile(String text, FileEntity fileEntity, HttpObserver<List<MovieRes>> observer, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        RequestBody requestBody = RetrofitUtil.fileToPart(fileEntity, MediaType.parse("image/png"));//这里文件类型以png图片为例
        Observable observable = RetrofitUtil.getApiService().upLoadTextAndFile(text, requestBody);
        RetrofitUtil.composeToSubscribe(observable, observer, lifecycleSubject);
    }

    /**
     * 上传文本和多个文件
     *
     * @param textMap          文本参数集合<key,value>
     * @param listFileEntities 文件实体列表
     * @param observer         请求后的回调
     * @param lifecycleSubject 生命周期触发器
     */
    public void upLoadTextsAndFiles(Map<String,String> textMap, List<FileEntity> listFileEntities, HttpObserver<List<MovieRes>> observer, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        Map<String, RequestBody> bodyMap = RetrofitUtil.filesToPartMap(listFileEntities, MediaType.parse("image/png"));//这里文件类型以png图片为例
        Observable observable = RetrofitUtil.getApiService().upLoadTextsAndFiles(textMap, bodyMap);
        RetrofitUtil.composeToSubscribe(observable, observer, lifecycleSubject);
    }

    /**
     * 下载电影
     *
     * @param observer         请求后的回调
     * @param lifecycleSubject 生命周期触发器
     * @param file             目标文件，下载的电影将保存到该文件中
     */
    public void downLoadFile(HttpFileObserver observer, PublishSubject<LifeCycleEvent> lifecycleSubject, File file) {
        Observable observable = RetrofitUtil.getApiService().downloadFile();
        RetrofitUtil.composeToSubscribeForDownload(observable, observer, lifecycleSubject, file);
    }



}
