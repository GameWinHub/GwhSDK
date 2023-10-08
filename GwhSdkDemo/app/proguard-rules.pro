-dontskipnonpubliclibraryclassmembers  # 不忽略非公共的库类
-dontoptimize                          # 优化不优化输入的类文件
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*     # 混淆时所采用的算法
-optimizationpasses 5                  # 指定代码的压缩级别
-dontusemixedcaseclassnames            # 是否使用大小写混合
-keepattributes *Annotation*           # 保持注解
-dontpreverify                         # 混淆时是否做预校验
-verbose                               # 混淆时是否记录日志
-ignorewarnings                        # 忽略警告


#保持哪些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.DialogFragment
-keep public class * extends android.app.Application

-keep public class * extends android.support.**   #如果有引用v4或者v7包，需添加
-keep public class android.app.** {*; }


## gamewinhub sdk
-keep class com.gamewinhub.** {*;}
## payssion
-keep class com.payssion.android.sdk.** { *; }
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
