# SyncMarket
You can manage your application's version update or get your market information more simply.<br>
This Library used [Jsoup](https://jsoup.org/).

![kakaotalk_20170202_213907914](https://cloud.githubusercontent.com/assets/24237865/22550481/9fd478da-e993-11e6-9ca5-cfcff0e2dd26.jpg)

 
## Including in your project
#### build.gradle
```java
repositories {
  mavenCentral() // or jcenter() works as well
}

dependencies {
  compile 'com.skydoves.syncmarket:syncmarket:1.0.7'
}
```

#### or Maven
```xml
<dependency>
  <groupId>com.skydoves.syncmarket</groupId>
  <artifactId>syncmarket</artifactId>
  <version>1.0.6</version>
</dependency>
```
    
## Usage
#### Permission
Need below permissions in AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

#### Initialize
Next you have to call initialize method as below as follows
```java
SyncMarket.init(this);
```

#### Useage
and then use like this using rx
```java
TextView tv_ver = (TextView)findViewById(R.id.tv_ver);
SyncMarket.getVersionObservable()
	  .subscribe(ver -> tv_ver.setText(ver));
```

#### Note
If you call "get...()" Method, then return your published app's information from Google Play Store.<br>
From : **https://play.google.com/store/apps/details?id= + (packageName)**

If your application is not published yet or not connected internet, then return string **none** or do nothing.

#### Method list
```java
SyncMarket.getVersionObservable(); // emit the last published version from Google Play Store
```
```java
SyncMarket.getDownloadsObservable(); // emit downloads range from Google Play Store
```
```java
SyncMarket.getRecentChangesObservable(); // emit the last published date from Google Play Store as string array
```
```java
SyncMarket.getDownloadsObservable(); // emit require minimum device api level from Google Play Store
```
```java
SyncMarket.getPublishedDateObservable(); // emit the last published date from Google Play Store
```
```java
SyncMarket.getMarketUrl(); // get application's google playstore url
```
```java
SyncMarket.gotoMarket(); // intent your application's Google Play Store Page
```
```java
SyncMarket.isNetworkAvailable(); // check is network is available? 
```
```java
// you don't have to call this method if you called init method. but you can set and testing.
SyncMarket.setPackageName(String s); 
```
```java
SyncMarket.setTimeout(); // you can set networking timeout.
```

#### Proguard rules for release
```java
-keep public class org.jsoup.** {
	public *;
}
```

# License
```xml
The MIT License (MIT)

Copyright (c) 2017 skydoves

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
