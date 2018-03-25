# DevRing  
[![label1](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/LJYcoder/DevRing)
[![label2](https://img.shields.io/badge/License-Apache%202.0-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![label3](https://img.shields.io/badge/API-14%2B-yellow.svg)](https://github.com/LJYcoder/DevRing)
[![label4](https://img.shields.io/badge/Blog-%E7%AE%80%E4%B9%A6-orange.svg)](https://www.jianshu.com/u/2ebe42698573)  

## 介绍
**DevRing**是一个提供了日常开发常用模块的**安卓基础开发库**。  
包含了**网络请求**、**图片加载**、**数据库**、**事件总线**、**缓存**、**权限管理**、**资源绑定**、**工具类**等模块。具有可配置，可替换，使用简单的特点。  

其中，网络请求模块使用Retrofit+RxJava实现，图片加载使用Glide实现（可替换），数据库使用GreenDao实现（可替换），事件总线使用EventBus实现（可替换），权限管理使用RxPermission实现，资源绑定使用ButterKnife实现。  

Demo使用MVP+Dagger2进行开发，对以上各框架不熟悉的建议先进行学习（尤其是Dagger2），不然代码看起来可能比较难懂。底部有相关的博客链接。

## 使用
DevRing详细的使用说明文档，过几天会补上，下面做粗略的说明：
### 1.添加依赖
在项目module下的gradle中添加以下依赖：（暂时无法依赖，Jecnter处理申请中..）
``` 
compile 'com.ljy.devring:devring:1.0.0' 
```
>由于其中的数据库模块、图片加载模块、事件总线模块支持替换掉默认实现的框架，所以库中对GreenDao，Glide，EventBus的依赖是使用compileOnly（仅在编译时依赖），这么做是避免被替换的框架依然加入到apk中（增加apk大小）。  
也就是说，当你需要使用相关框架时，还需添加相关依赖。  

如果要使用Devring库的图片加载模块（默认使用Glide）,那么需要添加Glide依赖
```
compile 'com.github.bumptech.glide:glide:4.4.0'
```
如果要使用Devring库的数据库模块（默认使用GreenDao）,那么需要添加GreenDao依赖
```
compile 'org.greenrobot:greendao:3.2.0'
```
如果要使用Devring库的事件总线模块（默认使用EventBus）,那么需要添加EventBus依赖
```
compile 'org.greenrobot:eventbus:3.0.0'
```
### 2.初始化、配置
在Application的onCreate中进行初始化与配置。
```
//务必按顺序执行"初始化"、"配置"、"构建"这三步

//1.初始化
DevRing.init(this);


//2.根据你的需求进行相关模块的全局配置

//配置网络请求模块，如BaseUrl,连接超时时长，Log，全局Header，缓存，失败重试等
DevRing.configureHttp().setXXX()...  

//配置/替换图片加载模块，如加载中图片，加载失败图片，过渡动画，缓存等
DevRing.configureImage().setXXX()...

//配置/替换事件总线模块，如EventBus的index加速
DevRing.configureBus().setXXX()... 

//配置/替换数据库模块
DevRing.configureDB(dbManager);   

//配置缓存模块，如磁盘缓存的地址、大小等
DevRing.configureCache().setXXX()... 

//配置其他模块，如是否显示RingLog，是否启用崩溃日志等
DevRing.configureOther().setXXX()...


//3.构建
DevRing.create();

```
### 3.开始调用
通过DevRing.XXXManager()得到相关模块的管理者，然后进行具体操作。
```
//网络请求模块：
//普通请求、上传请求、下载请求、监听上传下载进度、生命周期控制等。
DevRing.httpManager().xxx();

//图片加载模块:
//各类型图片的加载，可定制加载要求（圆形、圆角、模糊、灰白），下载图片，获取Bitmap等
DevRing.imageManager().xxx();

//事件总线模块：
//订阅/解除订阅，发送普通事件，发送粘性事件
DevRing.busManager().xxx();

//数据库模块：
//数据的增删改查等
DevRing.tableManager(key).xxx();

//缓存模块：
//提供内存缓存，磁盘缓存，SharedPreference缓存
DevRing.cacheManager().xxxCache().xxx();

//其他模块：权限管理、Activity栈管理
DevRing.permissionManager().xxx();
DevRing.activityStackManager().xxx();

...
```
### 注意事项
1. DevRing库中已添加了网络请求权限，所以不必重复添加。  
2. DevRing库中已添加了Dagger2，Retrofit2，RxJava2，RxAndroid2，RxLifeCycle2，RxPermission2，ButterKnife的依赖，所以不必重复添加。  
3. 配置参数为File时，请自行确保对传入的file具有可读写权限，如果没有需先进行权限申请。  
4. 部分框架需忽略混淆，具体的混淆配置请参考Demo中app下的proguard-rules.pro文件。

## Demo
### 内容
1. 使用MVP+Dagger2进行开发。
2. 演示了DevRing的网络请求，图片加载，事件总线，数据库，权限管理等模块的使用。
3. 演示了使用Fresco替换图片加载模块中默认的Glide。
4. 演示了使用RxBus替换事件总线模块中默认的EventBus。
5. 演示了使用原生数据库替换数据库模块中默认的GreenDao。  
....

### 运行图
![screen1](https://github.com/LJYcoder/DevRing/blob/master/screenshot/screen1.gif)&nbsp;&nbsp;&nbsp;![screen2](https://github.com/LJYcoder/DevRing/blob/master/screenshot/screen2.gif)
<br>
<br>
![screen3](https://github.com/LJYcoder/DevRing/blob/master/screenshot/screen3.gif)&nbsp;&nbsp;&nbsp;![screen4](https://github.com/LJYcoder/DevRing/blob/master/screenshot/screen4.gif)

### demo apk下载
[点这里](https://github.com/LJYcoder/DevRing/blob/master/screenshot/DevRingDemo.apk)

## 版本信息
- v1.0.0 (2018/3/25)
  - 提供了网络请求、图片加载、事件总线、数据库、缓存、权限管理、Activity栈管理等模块
  - 提供LifeCallBack以实现Activity/Fragment基类功能
  - 提供部分工具类
  
  
## 相关博客
（最近会对以前的文章进行优化，以及发布DevRing使用文档，Dagger2介绍，Glide介绍）  
如果觉得对你有帮助，欢迎关注点赞~  

[安卓开发框架 开篇](http://www.jianshu.com/p/b714630bdf75)<br>
[MVP开发模式](http://www.jianshu.com/p/1f91cfd68d48)<br>
[网络请求框架 Retrofit+RxJava](http://www.jianshu.com/p/092452f287db)<br>
[图片加载框架 Fresco](http://www.jianshu.com/p/5b5625612f56)<br>
[事件总线框架 EventBus](http://www.jianshu.com/p/6fb4d78db19b)<br>
[资源绑定框架 ButterKnife](http://www.jianshu.com/p/5f89e3bd7fca)<br>
[数据库框架 GreenDAO](http://www.jianshu.com/p/11bdd9d761e6)<br>
[基类](http://www.jianshu.com/p/3d9ee98a9570)<br>
[工具类](http://www.jianshu.com/p/d1361c3ea743)<br>

---
最后，**感谢**本项目中所涉及的开源框架的作者们。  
有什么问题或建议，可以提issue或者简书通知我。  
如果觉得不错，不妨点个**star**。你的支持，是我继续开源的**动力**~
