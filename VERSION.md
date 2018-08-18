## 版本信息
 - v1.0.14、1.0.15  （2018/8/18）
   - 修复网络模块配置OkHttpClientBuilder和RetrofitBuilder无效的问题
   - 新增SystemTypeUtil工具类

 - v1.0.12、1.0.13  （2018/7/20）
   - 新增工具类：ConfigUtil（设备配置相关工具类）、FontTypeUtil（修改应用字体工具类）、KeyboardUtil（软键盘工具类）
   - 调整网络请求异常的提示信息

 - v1.0.10、1.0.11  （2018/6/6）
   - 图片加载模块的LoadOption新增边框颜色、边框粗细选项(目前仅适用于圆形模式)
   - 网络模块支持根据最新设置的config刷新管理者(DevRing.httpManager().refreshInstance();)
   - 图片模块支持根据最新设置的config来加载图片

 - v1.0.8、1.0.9  （2018/5/20）
   - ActivityStackManager改为ActivityListManager
   - 修复CacheManager中SpCache的小问题
   - 优化http访问异常的处理

 - v1.0.7  （2018/5/15）
   - 优化部分工具类
   - 加入安卓7.0 File适配
   - 调整http访问异常处理

 - v1.0.6  （2018/4/20）
   - 调整对glide,eventbus,greendao的依赖方式(从compileOnly调整为api)，可通过exclude移除不需要的库依赖。

 - v1.0.5  （2018/4/1）
   - 修复网络配置获取Builder为null的bug

 - v1.0.4  （2018/3/30）
   - 移除ButterKnife依赖
   - 优化注释

 - v1.0.3  （2018/3/28）
   - 优化权限管理判断逻辑
   - 优化ColorBar工具类

 - v1.0.2  （2018/3/27）
   - 网络请求模块允许传入的LifecycleTransformer为null，即不进行生命周期控制
   - 修复已知bug

- v1.0.0  （2018/3/25）
  - 提供了网络请求、图片加载、事件总线、数据库、缓存、权限管理、Activity栈管理等模块
  - 提供LifeCallBack以实现Activity/Fragment基类功能
  - 提供部分工具类

