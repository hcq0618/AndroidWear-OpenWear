##主要功能介绍##

1、精简的库大小

2、方便简洁、易拓展、定制性强的API - 更快速开发

3、支持兼容多个手表系统 - android wear谷歌版和中国版、ticwear、duwear

4、连接及通信自适应适配 - 无需关心手表系统 只需关心业务逻辑

5、生命周期内部管理 - 无需关心生命周期 只需关心业务逻辑

6、声明一个监听服务即可兼容多个手表系统

7、丰富的工具类 - 更快速开发

8、手表无需联网 也可在手表端直接发起网络

9、可以以布局文件的形式 构建自定义表盘

10、支持多种数据形式的通信 - 字节流、字符串、图片、文件、键值对

11、支持全双工双向通信



## 设备通信初始化 ##

涉及设备通信相关包含数据发送、数据监听、统计分析、自定义事件、错误日志、独立网络请求、云更新。


**Mobile端uses-sdk配置**

```xml
    <uses-sdk
        android:minSdkVersion="9"
        android:targetSdkVersion="22" />
```

**Wearable端uses-sdk配置**

    <uses-sdk
        android:minSdkVersion="18"
        android:targetSdkVersion="22" />


**Mobile端及Wearable端的平台兼容性配置**
创建res/values/wearable_service_version.xml文件

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
	<!-- google play service版本号-->
    <integer name="google_play_services_version">6587000</integer>
	<!-- duwear连接服务版本号-->
    <integer name="open_wearable_service_version">1000000</integer>
	<!-- android wear中国版连接服务版本号-->
	<integer name="android_wear_china_services_version">7887000</integer>
    </resources>

**Mobile端及Wearable端初始化代码**

	OpenWatchCommunication.init(context);


声明所需要兼容的Android Wear谷歌版、Android Wear中国版、DuWear连接服务的版本号（TicWear不需要）

兼容Android Wear谷歌版

        <meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />

兼容Android Wear中国版

		<meta-data
            android:name="com.google.android.wearable.version"
            android:value="@integer/android_wear_china_services_version" />

兼容Duwear

        <meta-data
            android:name="org.owa.wear.ows.sdk.version"
            android:value="@integer/open_wearable_service_version" />


**可以自定义内部线程池**

例如

    OpenWatchCommunication.setTheadPool(Executors.newCachedThreadPool());

若不设置，则内部默认使用newCachedThreadPool创建的线程池，线程优先级为Process.THREAD_PRIORITY_BACKGROUND的非守护线程。

## 数据发送 ##

需要初始化设备通信相关，详见设备通信相关初始化一节。

提供多种手机与手表间的数据发送方式，不用手动管理连接，内部会在必要时自动断开或打开连接，不需要再关心设备连接、通信、数据层，且所发送的数据均没有大小限制，无论是手机端还是在手表端，代码写法及逻辑相同。

无需再导入其他第三方系统平台的开发jar包。


每种数据发送行为都由path和data组成：


- path：自定义的任意字符串，用于标识该数据发送行为的唯一性。
- data：所发送的任意类型数据，可以是基本数据类型、字节流、图片或者map类型，详见Demo。


**1、数据存储和手机与手表间的自动同步，当配对设备未连接，数据并不会被丢失，会在下次连接上配对设备时接收到数据：**

    OpenWatchSender.sendData(context, "/send_data", "hello openwatch", new SendListener() {
    				
    				@Override
    				public void onSuccess() {
    					// TODO Auto-generated method stub
    					//发送成功
    				}
    				
    				@Override
    				public void onError(ErrorStatus error) {
    					// TODO Auto-generated method stub
    					//发送失败及失败原因
    				}
    			});


**2、数据以消息的形式发送，当配对设备未连接，数据会被丢失，用于发送临时性或时效性数据：**

    OpenWatchSender.sendMsg(this, "/send_msg", "hello openwatch", new SendListener() {
    				
    				@Override
    				public void onSuccess() {
    					// TODO Auto-generated method stub
    					//发送成功
    				}
    				
    				@Override
    				public void onError(ErrorStatus error) {
    					// TODO Auto-generated method stub
    					//发送失败及失败原因
    				}
    			});

**3、有时你的需求场景会需要类似网络请求，数据发送为请求/响应交流模型，可以等待配对设备的响应直到超时。当配对设备未连接，数据会被丢失：**

    OpenWatchBothWay.request(this, "/send_bothway", "hello openwatch", new BothWayCallback() {
    
    				@Override
    				public void onResponsed(byte[] rawData) {
    					// TODO Auto-generated method stub
    					//配对设备响应回来的数据
    				}
    
    				@Override
    				public void onError(ErrorStatus error) {
    					// TODO Auto-generated method stub
    					//数据请求错误及原因
    				}
    			});

配对设备接收到对应的请求并响应，同时传入接收到的path

    if (path.equals("/send_bothway")) {
    	OpenWatchBothWay.response(this, path, "response bothway");
    }


可设置数据发送的超时时间（默认10秒）：

    OpenWatchSender.setTimeOutMills(15000);

可设置请求响应的超时时间（默认10秒）：

    OpenWatchBothWay.setTimeOutMills(15000);



## 数据接收与监听 ##

需要初始化设备通信相关，详见设备通信相关初始化一节。

数据接收与监听有两种方式，可任选其一或者两者结合使用，无论是手机端还是在手表端，代码写法及逻辑相同（详见Demo）:

**1、设置Listener，一般用于activity中，可根据应用具体业务和功能需求，添加不同层面的监听，并在监听的回调函数中接收到配对设备发送的数据：**

    	// 添加设备连接的监听
		OpenWatchRegister.addConnectListener(this);
		// 添加接收数据的监听
		OpenWatchRegister.addDataListener(this);
		OpenWatchRegister.addMessageListener(this);
		// 添加接收图片、map等特殊类型数据的监听
		OpenWatchRegister.addSpecialTypeListener(this);

取消监听

		OpenWatchRegister.removeDataListener(this);
		OpenWatchRegister.removeConnectListener(this);
		OpenWatchRegister.removeMessageListener(this);
		OpenWatchRegister.removeSpecialTypeListener(this);

**2、设置监听服务：**

在配置文件注册监听服务
根据应用想要兼容的智能手表系统，可选择性配置不同的action，action name不可修改

    <!-- 继承自OpenWatchListenerService的子类service 下面service name需要修改成自己的类名 -->
        <!-- 根据自身业务需求和兼容性需求 可选择性添加不同的intent-filter 其中action name不可修改-->

        <service android:name="cn.openwatch.mobile.demo.ListenerService" >
            <intent-filter>

                <!-- 兼容android wear谷歌版和中国版 -->
                <action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
                <!-- 兼容duwear-->
                <action android:name="org.owa.wear.ows.BIND_LISTENER" />
                <!-- 兼容ticwear -->
                <action android:name="com.mobvoi.android.wearable.BIND_LISTENER" />
            </intent-filter>
        </service>


继承OpenWatchListenerService，内部会管理OpenWatchListenerService的生命周期，当有数据接收到时会启动service，当不需要再工作时销毁service，无需手动管理。

    public class ListenerService extends OpenWatchListenerService {

	@Override
	public void onMessageReceived(String path, byte[] rawData) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + ":配对设备发来临时性数据", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDataReceived(String path, byte[] rawData) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + "：配对设备发来数据", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDataDeleted(String path, byte[] rawData) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + "：配对设备删除了一条数据", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDataMapReceived(String path, DataMap dataMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + "：配对设备发来键值对", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBitmapReceived(String path, Bitmap bitmap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + "：配对设备发来图片", Toast.LENGTH_SHORT).show();
	}

	  @Override
    public void onFileReceived(SpecialData data) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：配对设备发来文件：" + new String(data.getData()), Toast.LENGTH_SHORT)
                .show();

        //保存成文件
        data.receiveFile(getExternalCacheDir() + File.separator + "file.txt");
    }

    @Override
    public void onStreamReceived(SpecialData data) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：配对设备发来数据流：" + new String(data.getData()), Toast.LENGTH_SHORT).show();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getExternalCacheDir(), "file.txt"));
            //写入到输出流
            data.receiveStream(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onInputClosed(String path) {
 		//在调用SpecialData的receiveStream或receiveFile后回调
        Toast.makeText(this, getClass().getSimpleName() + "：保存配对设备发来的数据完成", Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onPeerConnected(String displayName, String nodeId) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + "：和配对设备连接上了  设备名：" + displayName + " 设备id：" + nodeId,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPeerDisconnected(String displayName, String nodeId) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getClass().getSimpleName() + "：和配对设备断开了连接  设备名：" + displayName + " 设备id：" + nodeId,
				Toast.LENGTH_SHORT).show();
	}
    }

## 手表端独立网络请求 ##

需要初始化设备通信相关，详见设备通信相关初始化一节。

大部分智能手表不可以独立联网，OpenWatch提供了在手表端应用中，可独立发起网络请求的方式，支持get和post请求。

    OpenWatchHttp http = new OpenWatchHttp(context);
    http.get("http://www.baidu.com", new HttpCallback() {
    
    				@Override
    				public void onResponse(String response) {
    					// TODO Auto-generated method stub
    					Log.d(WearListAdapter.class.getName(), response);
    					Toast.makeText(context, "网络请求响应成功", Toast.LENGTH_SHORT).show();
    				}
    
    				@Override
    				public void onError(byte[] data, int statusCode, Map<String, String> headers) {
    					// TODO Auto-generated method stub
    					Toast.makeText(context, "网络请求响应失败:" + statusCode, Toast.LENGTH_SHORT).show();
    				}
    			});

可以自定义网络请求的实现方式

	OpenWatchHttp.setCustomHttpCaller(new IHttpCaller() {

			@Override
			public void setTimeOutMills(int timeOut) {
				// TODO Auto-generated method stub

			}

			@Override
			public void getWithHeaders(String url, Map<String, String> params, Map<String, String> headers,
					HttpCallback callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postWithHeaders(String url, byte[] body, Map<String, String> headers, HttpCallback callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public void cancelAll() {
				// TODO Auto-generated method stub

			}
		});

##  创建自定义表盘  ##

OpenWatch提供自定义View或者布局文件的方式创建表盘，并提供时间变化与时区变化回调等（详见Demo）。


    public class DigitalWatchFace extends OpenWatchFace {
    
    	private TextView timeTextView, dateTextView;
    
    	// 构建表盘布局时回调
    	@Override
    	public View onCreateView() {
    		// TODO Auto-generated method stub

			//设置表盘样式 指针样式等 此处略
			......
    
    		View watchface = View.inflate(this, R.layout.digital_watchface_layout, null);
    		timeTextView = (TextView) watchface.findViewById(R.id.watchface_time_tv);
    		dateTextView = (TextView) watchface.findViewById(R.id.watchface_date_tv);
    
    		// 返回自定义view或者布局文件生成的view
    		return watchface;
    	}


		// 时区发生改变时回调
		@Override
		public void onTimeZoneChanged() {
			// TODO Auto-generated method stub
			super.onTimeZoneChanged();
		}

		// 时间发生改变时回调
		@Override
		public void onTimeUpdate(Time time) {
			// TODO Auto-generated method stub
			super.onTimeUpdate(time);

			// 刷新表盘
			invalidate();
		}

    
    }


改变表盘上的日期和时间显示

        @Override
    	protected void onWatchFaceDraw(Canvas canvas, Rect bounds) {
    		// TODO Auto-generated method stub
    
    		Time time = getTime();
    		setDate(time);
    		setTime(time);
    
    		super.onWatchFaceDraw(canvas, bounds);
    	}
    
    	private void setDate(Time time) {
    		String dateStr = (time.month + 1) + "." + time.monthDay + "  " + (time.hour > 12 ? "下午" : "上午");
    		dateTextView.setText(dateStr);
    	}
    
    	private void setTime(Time time) {
    		String minStr = time.minute < 10 ? "0" + time.minute : String.valueOf(time.minute);
    		String timeStr = (time.hour > 12 ? time.hour - 12 : time.hour) + ":" + minStr;
    
    		timeTextView.setText(timeStr);
    	}

可设置秒针及相关样式，可设置长宽、纯色指针、图片指针等。

    @Override
	public View onCreateView() {
		// TODO Auto-generated method stub

		OpenWatchFaceStyle style = new OpenWatchFaceStyle(this);

		// 如果需要的话 构建表盘秒针 否则不会绘制秒针
		OpenWatchFaceHand secondHand = new OpenWatchFaceHand();
		secondHand.setLength(DisplayUtil.dip2px(this, 10));
		secondHand.setWidth(DisplayUtil.dip2px(this, 3));
		// 秒针在表盘边界显示
		secondHand.setDrawGravity(OpenWatchFaceHand.DRAW_GRAVITY_BORDER);

		style.setSecondHand(secondHand);

		setStyle(style);

		//设置表盘样式 创建布局等 此处略
		......
    }

可设置onTimeUpdate函数的回调频率模式

	//这里设置的模式 表现为交互模式下每秒回调一次 省电模式下每分钟回调一次
    setTimeUpdateMode(TIME_UPDATE_PER_SECOND_MODE);

可设置表盘样式

    @Override
	public View onCreateView() {
		// TODO Auto-generated method stub

		OpenWatchFaceStyle style = new OpenWatchFaceStyle(this);

		// 构建秒针及设置秒针样式等 此处略
		......

		// 表盘上的通知卡片以单行高度显示
		style.setCardPeekMode(OpenWatchFaceStyle.PEEK_MODE_SHORT);

		setStyle(style);

		//创建布局等 此处略
		......
    }

声明权限

    <uses-permission android:name="com.google.android.permission.PROVIDE_BACKGROUND" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

在工程中创建res/xml目录，并创建watch_face.xml文件，文件内容为

    <?xml version="1.0" encoding="utf-8"?>
    <wallpaper xmlns:android="http://schemas.android.com/apk/res/android" />

注册表盘Service，注意要替换name为自定义类，其中label属性为表盘名称。

    <service
    android:name="cn.openwatch.wearable.demo.DigitalWatchFace"
    android:allowEmbedded="true"
    android:label="@string/digital_watch_face_name"
    android:permission="android.permission.BIND_WALLPAPER" >
    <intent-filter>
    <action android:name="android.service.wallpaper.WallpaperService" />
    
    <category android:name="com.google.android.wearable.watchface.category.WATCH_FACE" />
    </intent-filter>
    
    <meta-data
    android:name="android.service.wallpaper"
    android:resource="@xml/watch_face" />
    
    <!-- 表盘预览图 自行替换android:resource 对于圆形设备 预览图会被裁剪成圆形 -->
    <meta-data
    android:name="com.google.android.wearable.watchface.preview"
    android:resource="@drawable/ic_launcher" />
    </service>

若应用想构建多个表盘 则可以自定义多个OpenWatchFace子类，并分别注册多个表盘Service。

## Proguard混淆配置 ##

Eclipse或其他IDE打包

    -keep class cn.openwatch.** {*; }
	-dontwarn cn.openwatch.**

Android Studio或Gradle打包

	-keep class cn.openwatch.** {*; }
	-dontwarn cn.openwatch.**

## 打包应用 ##


当发布应用的时候，需要将手表端应用嵌入进手机端应用，因为用户不能直接在手表设备上查看或安装应用。

手机端与手表端应用包名、签名要保持一致，手机端应用声明的权限需要包含手表端应用的所有声明权限。

如果打包正确，当用户安装手机端应用后，系统会自动推送手表端应用到配对的手表设备上。如果应用正在开发或者是用debug签名，这个功能就没效果。

手机端应用模块中的权限声明，需要包含手表端应用模块中的所有权限，否则这个功能也会没有效果。

在开发的时候，需要用adb install命令或者直接用相应的IDE（比如Android studio）来安装。

创建一个res/xml/wearable_app_desc.xml文件，里面包含Android Wear应用的版本和路径信息。例如：


    <wearableApp package="wearable.app.package.name">
      <versionCode>1</versionCode>
      <versionName>1.0</versionName>
      <rawPathResId>android_wear_micro_apk</rawPathResId> 
    </wearableApp>

package, versionCode, 和versionName的值要和手表端应用的AndroidManifest.xml文件中的一样，rawPathResId的值不要改变。

**用Android Studio打包**

在手机端应用的AndroidManifest.xml文件中，在manifest根标签下添加

	xmlns:tools="http://schemas.android.com/tools"

在application标签下添加meta-data 用于引用wearable_app_desc.xml描述文件（针对android wear谷歌版和中国版不需要手动添加手表端App的描述文件，打包的时候，studio会自动添加）：


兼容DuWear

    <!-- 引用同一个wearable_app_desc文件 需要添加tools:replace="android:resource" 否则打包会报错-->
    <meta-data
            android:name="org.owa.wear.app"
            android:resource="@xml/wearable_app_desc"
            tools:replace="android:resource" />

兼容TicWear

     <meta-data
            android:name="com.mobvoi.ticwear.app"
            android:resource="@xml/wearable_app_desc" />


**用Eclipse或其他IDE打包**


如果你使用的是其他编译方式或者IDE，还可以手动打包。

1、拷贝已签名的手表端应用apk到手机端工程的res/raw目录下，apk重命名为android_wear_micro_apk.apk，文件名不要改变。


3、在手机端应用的AndroidManifest.xml文件中，在application标签下添加一个meta-data 用于引用wearable_app_desc.xml描述文件：

兼容Android Wear谷歌版及中国版

    <meta-data android:name="com.google.android.wearable.beta.app"
     android:resource="@xml/wearable_app_desc"/>

兼容DuWear

    <meta-data
            android:name="org.owa.wear.app"
            android:resource="@xml/wearable_app_desc" />

兼容TicWear

     <meta-data
            android:name="com.mobvoi.ticwear.app"
            android:resource="@xml/wearable_app_desc" />



##其他##

1、将原生gms谷歌版jar包从几兆精简到了300k

2、删除了原生gms谷歌版jar包中大量的log输出

3、gms谷歌版jar包基于版本6.5 以免用户手机端安装的版本过低 会弹出升级提示

4、gms中国版基于版本7.8

5、为避免gms谷歌版和中国版类名的冲突 用jarjar修改了各自的包名


## License

MIT License

Copyright (c) 2017 Hcq

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
