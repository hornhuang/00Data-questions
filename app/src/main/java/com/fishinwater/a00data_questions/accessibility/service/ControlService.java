package com.fishinwater.a00data_questions.accessibility.service;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fishinwater.a00data_questions.accessibility.utils.PinYinUtil;
import com.fishinwater.a00data_questions.accessibility.utils.WechatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fishinwater-1999
 * @date :2020/3/31 10:11
 */
public class ControlService extends AccessibilityService {

    /**
     * 一些基本包名.类名
     */
    public final String TAG = getClass().getName();
    //微信包名
    private final static String WeChat_PNAME = "com.tencent.mm";
    //微信布局ID前缀
    private static final String BaseLayoutId = "com.tencent.mm:id/";
    //微信首页
    public static final String WECHAT_CLASS_LAUNCHUI = "com.tencent.mm.ui.LauncherUI";
    //微信聊天页面
    public static final String WECHAT_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";

    public static boolean isSendSuccess; //true 发送表示完成

    /**
     * 支支持最新的微信版本
     * <p>
     * 由于各个版本 id 都不一样，一个个搞太可怕了
     * 就用最新的了
     */
    private String searchedittextid = "bem";
    private String searchlistviewid = "f13";
    private String backimageviewid = "dn";

    /**
     * 聊天界面各个 id
     * 都是基于最新的
     */
    private String chatuiedittextid = "ajs";
    private String chatuiusernameid = "g1r";
    private String chatuiswitchid = "alt";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 拿下，后面拿去匹配
        String className = event.getClassName().toString();
        // 更具包名+类名判断当前在那个界面
        switch (className) {
            case WECHAT_CLASS_LAUNCHUI:
                handleFlow_clickSearch();
                break;
            case WECHAT_CLASS_CHATUI:
                if (WechatUtils.ACTION.equals("text")) {
                    Log.d(TAG, 2 + " text");
                    handleFlow_ChatUI2();
                } else if (WechatUtils.ACTION.equals("picture")) {
                    Log.d(TAG, 2 + " picture");
                    handleFlow_ChatUI_PIC();
                }
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 微信界面，点击title的搜索按钮
     */
    private void handleFlow_clickSearch() {
        try {
            // 如果没有名字，说明不是主动发送的，就没有必要搜索了
            if (TextUtils.isEmpty(WechatUtils.NAME)) {
                return;
            }

            // 调起微信之后，不管在什么页面，先查找返回键并点击：防止在其他页面查找不到搜索按钮
            Thread.sleep(100);
            // 博客太旧了，旧版才有‘返回’按钮，现在应该换
            // WechatUtils.findTextAndClick(this, "返回");
            WechatUtils.findViewIdAndClick(this, BaseLayoutId + backimageviewid);

            Thread.sleep(500);

            WechatUtils.findTextAndClick(this, "搜索");

            Thread.sleep(500);

            handleFlow_past();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索界面粘贴要搜索的内容
     */
    private void handleFlow_past() {
        // 这个对象是拿来获得界面上的控件
        // 就类似 fragment 里面用的 view.findViewById 的 view
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            // 还是老样子获得匹配到的列表
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + searchedittextid);
            if (list != null && list.size() > 0) {
                for (final AccessibilityNodeInfo node : list) {
                    if (node.getClassName().equals("android.widget.EditText") && node.isEnabled()) {
                        try {
                            Thread.sleep(350);
                            // 把咱们要找的人，从中文换成拼音，粘贴上去
                            WechatUtils.pastContent(this, node, PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME));

                            Thread.sleep(500);

                            clickSearchResult();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 点击搜索到的结果
     */
    private void clickSearchResult() {
        // 都是一样的套路，获得 Root
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            // 获得 list 上面的控件
            List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + searchlistviewid);
            if (list1 != null && list1.size() > 0) {
                // 虽然比配到的可能很多，但是咱们只要找最匹配的那个，也就是第一个
                AccessibilityNodeInfo listInfo = list1.get(0);
                for (int i = 0; i < listInfo.getChildCount(); i++) {
                    // 循环遍历找名字
                    AccessibilityNodeInfo itemNodeInfo = listInfo.getChild(i);
                    for (int j = 0; j < itemNodeInfo.getChildCount(); j++) {
                        CharSequence name = itemNodeInfo.getChild(j).getText();
                        Log.i(TAG, "childName:" + name);
                        // 这个很重要，
                        if (!TextUtils.isEmpty(name)
                                && TextUtils.equals(PinYinUtil.getPinYinUtil().getStringPinYin(name.toString()),
                                PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME))) {
                            itemNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            try {
                                Thread.sleep(350);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (WechatUtils.ACTION.equals("text")) {
                                Log.d(TAG, 2 + " text");
                                handleFlow_ChatUI2();
                            } else if (WechatUtils.ACTION.equals("picture")) {
                                Log.d(TAG, 2 + " picture");
                                handleFlow_ChatUI_PIC();
                            }
                            return;
                        }
                    }
                }
            }
        }
        WechatUtils.NAME = "";
        WechatUtils.CONTENT = "";
        isSendSuccess = true;
        Log.i(TAG, "没有找到联系人");
        try {
            // 没找到联系人，一定是在搜索页面，这时候要先点一次返回 退出搜索页面，然后在退出微信
            // 防止直接退出微信，下一次发微信直接调起微信显示搜索页面，这时候粘贴内容就跟上一次的内容追加了，结果就不是想要的了
            Thread.sleep(100);
            WechatUtils.findTextAndClick(this, "返回");
            Thread.sleep(200);
            sendBroadcast(new Intent("FIND_CONTANCT_RESULT"));
            resetAndReturnApp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleFlow_ChatUI2() {
        //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
        String curUserName = WechatUtils.findTextById(this, BaseLayoutId + chatuiusernameid);

        WechatUtils.NAME = "";
        if (TextUtils.isEmpty(WechatUtils.CONTENT)) {
            if (WechatUtils.findViewId(this, BaseLayoutId + chatuiedittextid)) {
                //当前页面可能处于发送文字状态，需要切换成发送文本状态
                WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);
            }
            isSendSuccess = true;
            return;
        }
        if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + chatuiedittextid, WechatUtils.CONTENT)) {
            sendContent();
        } else {
            //当前页面可能处于发送语音状态，需要切换成发送文本状态
            WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendContent();
        }
    }

    private AccessibilityNodeInfo accessbiltyContent(AccessibilityNodeInfo parentView, String strAddress) throws Exception {
        int childCount = parentView.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            AccessibilityNodeInfo child = parentView.getChild(i);
            child = null;
            if (child != null) {
                String strToAddress = strAddress + " > " + child.getClassName().toString();
                String nodeText = "";
                if (child.getText() != null) nodeText = child.getText().toString();
                accessbiltyContent(child, strToAddress);
            } else {
                return null;
            }
        }
        return null;
    }


    /**
     * 点击搜索到的结果
     */
    private void clickPicResult() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 都是一样的套路，获得 Root
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dgf");
        List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByText("发送");
        Log.d(TAG, list1.size() + " +++ " + nodeInfo.getChildCount());
        Log.d(TAG, list2.size() + " +++ " + nodeInfo.getChildCount());

//        while ( nodeInfo.getChildCount() != 0) {
//            list1 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b5x");
//            Log.d(TAG,  " +++ " + nodeInfo.getChildCount());
//            if (list1.size() != 0) {
//                break;
//            }
//            nodeInfo = nodeInfo.getChild(0);
//        }
//        Log.d(TAG, list1.size() + " +++ " + nodeInfo.getChildCount());

//        while (nodeInfo.getChildCount() != 0) {//BaseLayoutId + "dgf"
//            list1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/content");
//            Log.d(TAG, 2222222 + "====" + list1.size());
//            Log.d(TAG, 1111111 + "++++" + nodeInfo.getChildCount());
//            if (list1.size() == 0) {
//                nodeInfo = nodeInfo.getChild(nodeInfo.getChildCount()-1);
//            }else {
//                nodeInfo = list1.get(0);
//                Log.d(TAG, 7777777 + "" + nodeInfo.getChildCount());
//                break;
//            }
//        }
//
//
        Log.d(TAG, 66666 + "" + list1.size());

        if (list1 != null && list1.size() > 0) {
            // 虽然比配到的可能很多，但是咱们只要找最匹配的那个，也就是第一个
            AccessibilityNodeInfo listInfo = list1.get(0);
            list1 = listInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + "dg_");
            Log.d(TAG, 666666 + "" + listInfo);
            for (int i = 0; i < listInfo.getChildCount(); i++) {
                // 循环遍历找名字
                list1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list2.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d(TAG, 666666 + "" + list2.size());
            }
        }
    }

    private void handleFlow_ChatUI_Text() {
        //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
        String curUserName = WechatUtils.findTextById(this, BaseLayoutId + chatuiusernameid);
        if (!TextUtils.isEmpty(curUserName)
                && TextUtils.equals(PinYinUtil.getPinYinUtil().getStringPinYin(curUserName),
                PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME))) {
            WechatUtils.NAME = "";
            if (TextUtils.isEmpty(WechatUtils.CONTENT)) {
                if (WechatUtils.findViewId(this, BaseLayoutId + "aja")) {
                    //当前页面可能处于发送文字状态，需要切换成发送文本状态
                    WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);
                }
                isSendSuccess = true;
                return;
            }

            if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + "aja", WechatUtils.CONTENT)) {
                sendContent();
            } else {
                //当前页面可能处于发送语音状态，需要切换成发送文本状态
                WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + chatuiedittextid, WechatUtils.CONTENT))
                    sendContent();
            }
        } else {
            //回到主界面
            sendContent();
            WechatUtils.findTextAndClick(this, "返回");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WechatUtils.findTextAndClick(this, "返回");//再次点击返回，目的是防止上一次返回到搜索页面，那样就阻塞住了
        }
    }

    private void handleFlow_ChatUI_PIC() {
        //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
        String curUserName = WechatUtils.findTextById(this, BaseLayoutId + chatuiusernameid);
        if (!TextUtils.isEmpty(curUserName)
                && TextUtils.equals(PinYinUtil.getPinYinUtil().getStringPinYin(curUserName),
                PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME))) {
            WechatUtils.NAME = "";
            if (TextUtils.isEmpty(WechatUtils.CONTENT)) {
                if (WechatUtils.findViewId(this, BaseLayoutId + "aja")) {
                    //当前页面可能处于发送文字状态，需要切换成发送文本状态
                    WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);
                }
                isSendSuccess = true;
                return;
            }
            WechatUtils.findViewIdAndClick(this, BaseLayoutId + "aja");
            WechatUtils.findViewIdAndClick(this, BaseLayoutId + "p5");
            clickPicResult();
//
//            if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + "aja", WechatUtils.CONTENT)) {
//                sendContent();
//            } else {
//                //当前页面可能处于发送语音状态，需要切换成发送文本状态
//                WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + chatuiedittextid, WechatUtils.CONTENT))
//                    sendContent();
//            }
        } else {
            //回到主界面
            sendContent();
            WechatUtils.findTextAndClick(this, "返回");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WechatUtils.findTextAndClick(this, "返回");//再次点击返回，目的是防止上一次返回到搜索页面，那样就阻塞住了
        }
    }

    private void sendContent() {
        //发送成功   能执行这一步，基本上就是发出去了
        WechatUtils.findTextAndClick(this, "发送");
        WechatUtils.NAME = "";
        WechatUtils.CONTENT = "";
        isSendSuccess = true;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resetAndReturnApp() {
        isSendSuccess = true;
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    /**
     * 拉起微信界面
     *
     * @param event 服务事件
     */
    private void sendNotifacationReply(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            String content = notification.tickerText.toString();
            String[] cc = content.split(":");

            String receiveName = cc[0].trim();
            String receciveScontent = cc[1].trim();

            PendingIntent pendingIntent = notification.contentIntent;
            try {
                isSendSuccess = true;
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, content, Toast.LENGTH_LONG).show();
        }
    }

}
