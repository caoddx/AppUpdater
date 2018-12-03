# AppUpdater

[![jitpack](https://jitpack.io/v/caoddx/AppUpdater.svg)](https://jitpack.io/#caoddx/AppUpdater)

android app updater

## Gradle Dependency

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. Add the dependency

```gradle
dependencies {
    // core
    implementation 'com.github.caoddx.AppUpdater:appupdater:0.2.0'

    // update source : fir.im
    implementation 'com.github.caoddx.AppUpdater:firimupdatesource:0.2.0'
}
```

## Usage

```kotlin
// see https://fir.im/docs
val source = FirImSource("your app id", "your api token")

val updater = Updater(
                this,
                UpdateUI(this), // implement by yourself
                source,
                checkIntervalInSecond = 12 * 3600,
                downloadMode = DownloadMode.AllAllowAndAsk
        )

updater.start()
```