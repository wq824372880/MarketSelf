# ZeekrlifeMarket

应用商城

### 1、Task模块：

模块：task-lib、task-aidl、task-core： 

（1）提供下载、安装、卸载功能以及具有外部应用调用的下载安装服务； 

（2）提供下载、安装状态的按钮控件；

接口使用：

```
//初始化
TaskProxy.init(context,callback);
//服务是否可用
TaskProxy.ensureServiceAvailable();
//添加任务
TaskProxy.addTask(taskInfo);
//移除任务
TaskProxy.removeTask(taskId);
//暂停下载任务
TaskProxy.pauseDownload(taskId);
//恢复下载任务
TaskProxy.resumeDownload(taskInfo);
```

控件：具有下载、安装状态的按钮

```xml

<com.zeekrlife.task.base.widget.TaskLayout 
    android:id="@+id/layout_task"
    android:layout_width="180dp" 
    android:layout_height="68dp" 
/>
```
```
//初始化
layoutTask.init(taskInfo)
```

### 2、自动更新服务：

模块：service-thirdupdate; 

功能：开机时检测商城内已安装应用是否需要自动更新;

骨架屏
//骨架 skeleton
implementation 'com.ethanhua:skeleton:1.1.2'
implementation 'io.supercharge:shimmerlayout:2.1.0'
Recycleview
bind //绑定的RecycleView，不要给RecycleView设置adapter
adapter //设置Skeleton消失时要给RecycleView设置的 adapter，内部会自动绑定
load //预览加载的RecycleView item 布局文件
shimmer(true) // 是否显示shimmer动画，默认显示
count(10) // 设置recycleView 默认预览加载条目数，默认10
color(color) // shimmer 动画颜色，默认 #a2878787
angle(20) //shimmer 动画角度，默认 20度
duration(1000) // shimmer动画持续时间 ，默认1000ms;
frozen(true) // skeleton 显示时是否RecycleView 可滑动，默认不可滑动
hide // 关闭Skeleton，就会自动绑定Adapter，显示真正数据

view
bind 绑定view
load 预览加载的View 布局，会替换绑定view内的view
shimmer(true) // 是否显示shimmer动画，默认显示
color(color) // shimmer 动画颜色，默认 #a2878787
angle(20) //shimmer 动画角度，默认 20度
duration(1000) // shimmer动画持续时间 ，默认1000ms;
hide // 关闭Skeleton 加载预览，显示真正View

fun recycleview(view: RecyclerView, adapter: AppListAdapter, layout: Int, color: Int): RecyclerViewSkeletonScreen? {
return Skeleton.bind(view)
.adapter(adapter)
.shimmer(true)
.angle(20)
.frozen(false)
.duration(1000)
.color(color)
.count(10)//default count is 10
.load(layout)
.show();
}
fun view(view: View, layout: Int, color: Int): ViewSkeletonScreen? {
return Skeleton.bind(view)
.load(layout)
.shimmer(true)
.angle(20)
.duration(2000)
.color(color)
.show();
}

//骨架 自动
implementation ('com.github.rasoulmiri:Skeleton:v1.0.9'){
exclude group: 'com.android.support'
}

在xml文件里用SkeletonGroup和SkeletonView包裹一层
然后在代码中调用SkeletonGroup的id进行finishAnimation方法调用进行停止