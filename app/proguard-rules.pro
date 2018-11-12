# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lixin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 包名不混合大小写
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
# 混淆时记录日志
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
# 不优化输入的类文件
-dontoptimize
# 关闭预校验
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.
# 保护注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }
-keep class com.busap.myvideo.livenew.nearby.widget.menu.base.anotation.**{*;}
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
# 所有native方法不要被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
# 枚举类不要混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# R文件的静态成员
-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

# 以下为自定义内容
# 指定压缩级别
-optimizationpasses 5

# 不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

# 匿名类
-keepattributes EnclosingMethod

# 忽略警告
-ignorewarnings

# 混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 把混淆类中的方法名也混淆了
-useuniqueclassmembernames

# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

# 将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
# 保留行号
-keepattributes SourceFile,LineNumberTable
# icntvplayer
-dontwarn tv.icntv.icntvplayersdk.**
-keep class tv.icntv.icntvplayersdk.** { *; }

-dontwarn com.google.common.**
-keep class com.google.common.**{*;}

#rxjava
-dontwarn com.squareup.**
-keep class com.squareup.** {*;}

-dontwarn com.google.auto.**
-keep class com.google.auto.**{*;}

#butterknife
-dontwarn butterknife.compiler.**
-keep class butterknife.compiler.**{*;}


#adsdk
-dontwarn tv.icntv.**
-keep class tv.icntv.**{*;}

#amlogic
-dontwarn com.droidlogic.**
-keep class com.droidlogic.**{*;}

#service
-dontwarn tv.newtv.**
-keep class tv.newtv.logservice.**{*;}

#imageloader
-dontwarn com.nostra13.**
-keep class com.nostra13.**{*;}

#vds
-dontwarn tv.icntv.vds
-keep class tv.icntv.**{*;}

#bean
-keep class tv.newtv.tvlauncher.mainPage.model.**{*;}
-keep class tv.newtv.tvlauncher.listPage.model.**{*;}
-keep class tv.newtv.tvlauncher.DetailsPage.model.**{*;}
-keep class tv.newtv.tvlauncher.specialpage.bean.**{*;}
-keep class tv.newtv.tvlauncher.statusbar.model.**{*;}
-keep class tv.newtv.tvlauncher.superscript.model.**{*;}
-keep class tv.newtv.tvlauncher.SearchPage.bean.**{*;}
-keep class tv.newtv.tvlauncher.adutil.model.**{*;}

-keep class tv.newtv.cboxtv.bean.**{*;}
-keep class tv.newtv.cboxtv.cms.ad.model.**{*;}
-keep class tv.newtv.cboxtv.cms.mainPage.model.**{*;}
-keep class tv.newtv.cboxtv.cms.details.model.**{*;}
-keep class tv.newtv.cboxtv.cms.listPage.model.**{*;}
-keep class tv.newtv.cboxtv.cms.search.model.**{*;}
-keep class tv.newtv.cboxtv.cms.search.bean.**{*;}
-keep class tv.newtv.cboxtv.cms.special.bean.**{*;}
-keep class tv.newtv.cboxtv.cms.superscripi.model.**{*;}
-keep class tv.newtv.cboxtv.player.model.**{*;}
-keep class tv.newtv.cboxtv.uc.bean.**{*;}
-keep class tv.newtv.cboxtv.cms.special.doubleList.bean.**{*;}

-keep class tv.newtv.cboxtv.menu.**{*;}

-keep class com.letv.**{*;}
-keep class com.android.letvmanager.**{*;}

#bully 避免混淆
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#okhttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-dontwarn okhttp3.**
-keep class okio.**{*;}
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

-keep class com.newtv.libs.util.**{*;}