# Kongzue 的消息总线

Kongzue Runner 是一个独立的消息事件传递总线，不依赖 Intent，可以独立传递数据、执行事件，亦可以对尚未运行的 Activity 预设需要执行的事件，或者跨界面预设接下来要执行的事件。

## 优势

- 操作简单易上手；
  
- 不需要重写任何接口，无需繁琐的设置，不需要你做任何继承或者重写接口；

- 可以对一个不存在，还没启动的 Activity 也能生效；

- 跨类操作直接对内部成员赋值；

- 直接丢就完事了，这货就是个挂；

## 引入

<div>
<b>最新版本：</b>
<a href="https://jitpack.io/#kongzue/Runner">
<img src="https://jitpack.io/v/kongzue/Runner.svg" alt="Jitpack.io">
</a> 
</div>

1) 在 project 的 build.gradle 文件中找到 `allprojects{}` 代码块添加以下代码：

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }      //增加 jitPack Maven 仓库
    }
}
```

⚠️请注意，使用 Android Studio 北极狐版本（Arctic Fox）创建的项目，需要您前往 settings.gradle 添加上述 jitpack 仓库配置。

2) 在 app 的 build.gradle 文件中找到 `dependencies{}` 代码块，并在其中加入以下语句：

```
implementation 'com.github.kongzue:Runner:0.0.1'
```


## 怎么丢？

首先你得初始化，建议在 Application#onCreate 里进行：
```java
Runner.init(this);
```

然后就可以愉快的丢东西了！

### 丢事件：

在已经实例化的 Activity 上执行操作：
```java
//MainActivity.getInstance() 指向 MainActivity 的实例化对象，此处只做演示用，不建议这样用有内存泄漏的风险
Runner.runOnActivity(MainActivity.getInstance(), new ActivityRunnable() {
    @Override
    public void run(Activity activity) {
        MessageDialog.build()
                .setTitle("提示")
                .setMessage("这个事件来自 Activity2 创建。")
                .setOkButton("OK")
                .show(activity);
    }
});
```

不确定，或尚未实例化的情况下，在指定 Activity 上执行操作（会在实例化之后执行）：
```java
Runner.runOnActivity(Activity2.class, new ActivityRunnable() {
    @Override
    public void run(Activity activity) {
        MessageDialog.build()
                .setTitle("提示")
                .setMessage("这个事件来自 MainActivity 创建。")
                .setOkButton("OK")
                .show(activity);
    }
});
```

甚至不知道 class，只有个 Activity 的名字，在指定名字的 Activity 上执行操作（会在实例化之后执行）：
```java
Runner.runOnActivity("Activity2", new ActivityRunnable() {
    @Override
    public void run(Activity activity) {
        MessageDialog.build()
                .setTitle("提示")
                .setMessage("这个事件来自 MainActivity 创建。")
                .setOkButton("OK")
                .show(activity);
    }
});
```

额外说明，ActivityRunnable 具有泛型，你可以直接指定泛型为你的目标 Activity，这样就可以直接操作其内部的 public 修饰的成员或方法了：
```java
Runner.runOnActivity("Activity2", new ActivityRunnable<Activity2>() {
    @Override
    public void run(Activity2 activity2) {
        activity2.execPublicFunction();
    }
});
```

### 丢内容：

首先，你需要在目标 Activity 上编写一个成员，例如：
```java
Bitmap bitmapResult;
```

对已经实例化的 Activity 中的成员直接赋值：
```java
//activity2 为已经实例化的 Activity2
Runner.sendToActivity(activity2, "bitmapResult", BitmapFactory.decodeResource(getResources(),R.mipmap.img_bug));
```

不确定，或尚未实例化的情况下，在指定 Activity 中的成员直接赋值（会在实例化之后执行）：
```java
Runner.sendToActivity(Activity2.class, "bitmapResult", BitmapFactory.decodeResource(getResources(),R.mipmap.img_bug));
```

至不知道 class，只有个 Activity 的名字，在指定 Activity 中的成员直接赋值（会在实例化之后执行）：
```java
Runner.sendToActivity("Activity2", "bitmapResult", BitmapFactory.decodeResource(getResources(),R.mipmap.img_bug));
```

要是担心混淆导致成员名称发生变化，可以使用注解，在 Activity2 中对成员进行注解标注其接收的 key：
```java
@SenderTarget("bitmapResult")
Bitmap bitmap;
```

## 开源协议
```
Copyright Kongzue DialogX

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```