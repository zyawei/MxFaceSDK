# 中正人脸识别SDK
## How to
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency
```java
dependencies {
    implementation 'com.github.zyawei:MxImage:0.0.4'
}
```