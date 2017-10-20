# 安卓开发框架（MVP+主流框架+基类+工具类）

## 简介

使用**MVP模式+Retrofit+Rxjava+GreenDAO+Fresco+EventBus+ButterKnife+基类+工具类**搭建的一个安卓开发框架，<br>
代码中已加入较详细的注释，下方有各个模块的介绍传送门。

- 可以在里面选取一些觉得有用的模块去用（比如一些工具类，封装类，基类等）
- 也可以直接在它基础上进行调整和新项目开发（比如修改包名项目名，替换部分框架等）

## 传送门

各个模块的博客传送门：

[安卓开发框架（MVP+主流框架+基类+工具类）--- 开篇](http://blog.csdn.net/ljy_programmer/article/details/78180445)<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- MVP模式](http://blog.csdn.net/ljy_programmer/article/details/78187499)<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- Retrofit+RxJava](http://blog.csdn.net/ljy_programmer/article/details/78187761)<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- GreenDAO](http://blog.csdn.net/ljy_programmer/article/details/78257528)<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- Fresco](http://blog.csdn.net/ljy_programmer/article/details/78273267)<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- EventBus](http://blog.csdn.net/ljy_programmer/article/details/78298043)<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- ButterKnife](http:sdf)（未完成）<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- 基类](http:sdf)（未完成）<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- 工具类](http:sdf)（未完成）<br>
[安卓开发框架（MVP+主流框架+基类+工具类）--- 其他](http:sdf)（未完成）<br>


## demo
>demo大致的流程如下：
>1. 使用Retrofit+Rxjava请求豆瓣电影API，获取“正在上映”和“即将上映”的电影数据。
>2. 将数据通过两个Fragment以列表的形式进行展示，其中图片的显示使用Fresco进行加载。
>3. 点击列表项，使用GreenDAO将该电影插入到本地数据库中的“我的收藏”数据表，并且刷新Toolbar右侧收藏的数量。
>4. 点击toolbar右侧的收藏，进入收藏页面，使用GreenDAO从“我的收藏”数据表中获取数据并进行展示。
>5. 点击列表项，则将该电影从数据表中移除并刷新列表展示，同时使用EventBus通知上个页面刷新Toolbar右侧的收藏数量。
>6. 以上过程中，使用ButterKnife进行视图绑定，按照MVP模式进行开发，穿插使用了各种基类、工具类。


**demo运行图:**

![demo演示效果](http://img.blog.csdn.net/20171009141853011?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGp5X3Byb2dyYW1tZXI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 包结构

项目代码整体分为5个包，如下图所示：

![包结构](http://img.blog.csdn.net/20171010094014110?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGp5X3Byb2dyYW1tZXI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


其中，<br>
**app包**：存放全局性文件。如Application类，常量类等。<br>
**model包**：存放数据模型/处理相关的文件。如实体类，数据库相关文件，网络请求相关文件等。<br>
**presenter包**：存放业务逻辑服务相关的文件。<br>
**util包**：存放工具类。<br>
**view包**：存放视图相关的文件。如activity，fragment，adapter，自定义控件等。<br>

可以建多一个**other**包，用来存放Service，BroadcastReceiver，蓝牙，友盟等其他内容模块。<br>





<br>
如果觉得不错，欢迎 **star** 给我鼓励与支持，谢谢~
