-dontskipnonpubliclibraryclassmembers  # �����Էǹ����Ŀ���
-dontoptimize                          # �Ż����Ż���������ļ�
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*     # ����ʱ�����õ��㷨
-optimizationpasses 5                  # ָ�������ѹ������
-dontusemixedcaseclassnames            # �Ƿ�ʹ�ô�Сд���
-keepattributes *Annotation*           # ����ע��
-dontpreverify                         # ����ʱ�Ƿ���ԤУ��
-verbose                               # ����ʱ�Ƿ��¼��־
-ignorewarnings                        # ���Ծ���


#������Щ�಻������
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.DialogFragment
-keep public class * extends android.app.Application

-keep public class * extends android.support.**   #���������v4����v7���������
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