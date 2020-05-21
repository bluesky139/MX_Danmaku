# MX 弹幕 (MX Danmaku)

在 MX Player 上播放弹幕。

## 起因

因为经常用 MX Player 播放 NAS 上的动漫，但有时又想看点弹幕，桌面端倒是有“弹弹 Play”，但它的 Android 版本始终不够理想，因此产生了为已有播放器挂上弹幕的想法。

## 两种实现

* 一种是使用 [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService) 来获取窗口状态，播放进度，播放暂停等操作，进而对弹幕进行控制，弹幕本身则通过 [OverlayWindow](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#TYPE_APPLICATION_OVERLAY) 展现。

* 另一种是配合 [LTweaksSystem](https://github.com/bluesky139/LTweaksSystem) hook 播放器的状态来实现，可以得到更好的效果。

## 已知问题

* 