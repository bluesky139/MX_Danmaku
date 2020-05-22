# MX 弹幕 (MX Danmaku)

在 MX Player 上播放弹幕。

## 起因

因为经常用 MX Player 播放 NAS 上的动漫，但有时又想看点弹幕，桌面端倒是有“弹弹 Play”，但它的 Android 版本始终不够理想，因此产生了为已有播放器挂上弹幕的想法。

## 两种实现

* 一种是使用 [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService) 来获取窗口状态，播放进度，播放暂停等操作，进而对弹幕进行控制，弹幕本身则通过 [OverlayWindow](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#TYPE_APPLICATION_OVERLAY) 展现。

* 另一种是配合 [LTweaksSystem](https://github.com/bluesky139/LTweaksSystem) hook 播放器的状态来实现，可以得到更好的效果。

## 其它说明

* 第一种版本的实现主要是为了让普通用户也能使用，AccessibilityService 的能力十分有限，不过基本上也没什么问题，我在原生 Android 和类原生 Android 上测试过，使用请务必查看该 app 内的详细说明。

* 我自己主要在用第二种版本，还是 hook 来得更实在，有兴趣的可以参考[我在 LTweaksSystem 里的实现](https://github.com/bluesky139/LTweaksSystem/blob/master/app/src/main/java/li/lingfeng/ltsystem/tweaks/entertainment/MXPlayerDanmaku.java)，兴许可以出个 Xposed 版本。再配合[高亮最后访问的文件](https://github.com/bluesky139/LTweaksSystem/blob/master/app/src/main/java/li/lingfeng/ltsystem/tweaks/system/SolidExplorerHighlightVisitedFile.java)，这样也知道上次看到哪一集了，真是完美。