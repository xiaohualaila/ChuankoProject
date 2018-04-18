package com.shuli.root.chuankoproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import WedoneBioVein.SdkMain;
import WedoneBioVein.UserData;
import WedoneBioVein.VeinMatchCaller;

import static java.lang.Thread.sleep;

public class BioVeinActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_vein);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        InitOperations();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bio_vein, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int tabIndex = getArguments().getInt(ARG_SECTION_NUMBER);
            if(1 == tabIndex) {
                View rootView = inflater.inflate(R.layout.basic_bio_vein, container, false);
                return rootView;
            }
            View rootView = inflater.inflate(R.layout.process_bio_vein, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "BioVein各接口调用演示";
                case 1:
                    return "采集与验证流程演示";
            }
            return null;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView txtLogMsg = (TextView) findViewById(R.id.bio_vein_msg);
            String textStr = txtLogMsg.getText().toString();
            txtLogMsg.setText((CharSequence) msg.obj);
        }
    };

    /*
     *显示提示消息
     * msg: 消息内容
     * action: 0=清空原来的内容，只显示msg，1=不清空原来的内容，追加显示msg
     */
    public  void DisplayNoticeMsg(String msg, int action){
        Message msgObj = new Message();
        msgObj.obj = msg;
        handler.sendMessage(msgObj);
//        TextView msgTextView = (TextView) findViewById(R.id.bio_vein_msg);
//        if(1 == action) {
//            msgTextView.append(msg + "\r\n");
//        }
//        else if(0 == action){
//            msgTextView.setText(msg + "\r\n");
//        }

//      TextView textView = (TextView) container.findViewById(R.id.bio_vein_global_msg);
//      textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return;
    };

    private void enableAllOperates(boolean enable){
        findViewById(R.id.btn_enum_device).setClickable(enable);
        findViewById(R.id.btn_init_device).setClickable(enable);
        try{
            sleep(1,0);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private SdkMain mSdkMain = null;
    UserData mRegUserData = new UserData(); //用于保存采集(注册)的模板
    UserData mAIUserData = new UserData(); //用于保存验证时通过自动学习生成的模板，在比对时也作为比对模板的一部分
    int mUserCnt = 0;

    int mMaxVeinDeviceNum = 20;
    int mVeinDevIdLen = 64;
    byte[][] mVeinDevIdList = null;
    int mVeinDevCnt = 0;

    private void InitOperations(){
        if(null == mSdkMain){
            mSdkMain = new SdkMain();
            mVeinDevIdList = new byte[mMaxVeinDeviceNum][];
            for(int nCnt = 0; nCnt < mMaxVeinDeviceNum; nCnt++){
                mVeinDevIdList[nCnt] = new byte[mVeinDevIdLen];
            }
            mRegUserData.ClearData();
            mAIUserData.ClearData();
        }
    }

    private  byte[] mergeBytes(byte[] part1, byte[] part2){
        int p1Len = (null == part1)?0:part1.length;
        int p2Len = (null == part2)?0:part2.length;

        if(0 == (p1Len + p2Len)){
            return null;
        }

        byte[] ret = new byte[p1Len + p2Len];
        for(int i = 0; i < p1Len; i++){
            ret[i] = part1[i];
        }
        for(int i = 0; i < p2Len; i++){
            ret[p1Len + i] = part2[i];
        }
        SdkMain.DebugStringPrint("mergeBytes:p1Len=" + p1Len + ",p2Len=" + p2Len);
        return ret;
    }

    //枚举设备按钮
    /**/
    public void OnClickBtnEnumDevice(View v) {
        /**
         * Wedone:
         * 定义：
         *        int FV_EnumDevice(byte[][] devIds)
         * 功能:：
         *       枚举设备Id
         *
         * @参数(OUT)  byte[][] devIds: 用户保存有效设备Id的输出缓冲区，在调用本接口前必须分配好相应的内存空间；
         *                              返回正确时
         * @调用 public
         * @返回 int: 0=正确，其他=错误代码
         */
        int retVal = mSdkMain.FV_EnumDevice(mVeinDevIdList);
        if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
            mVeinDevCnt = 0;
            String devId = null, msg = "";
            for(int devIdx = 0; devIdx < mMaxVeinDeviceNum; devIdx++){
                devId = new String(mVeinDevIdList[devIdx]);
                if(0 == devId.trim().length()) {
                    break;
                }
                mVeinDevCnt++;
                msg += "{Dev" + (devIdx + 1) + ":" + devId + "}";
            }
            DisplayNoticeMsg("枚举设备成功:" + msg, 0);
        }
        else{
            DisplayNoticeMsg("枚举设备失败,err=" + retVal, 0);
        }
        return;
    }

    //初始化设备按钮
    public void OnClickBtnInitDevice(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        int retVal;
        String devId = null, msg = "";
        for(int devIdx = 0; devIdx < mVeinDevCnt; devIdx++){
            devId = new String(mVeinDevIdList[devIdx]);
            if(0 == devId.trim().length()) {
                break;
            }
            /**
             * Wedone:
             * 定义：
             *        int FV_InitDevice(byte[] devId)
             * 功能:：
             *      初始化设备
             *
             * @参数(IN)  byte[] devId: 指定初始化的设备的ID
             * @调用 public
             * @返回 int: 0=正确，其他=错误代码
             */
            retVal = mSdkMain.FV_InitDevice(mVeinDevIdList[devIdx]);
            if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
                msg += "{" + devId + ":成功}";
            }
            else{
                msg += "{" + devId + ":err=" + retVal + "}";
            }
        }
        DisplayNoticeMsg("初始化设备：" + msg, 0);

        return;
    }

    //打开设备按钮
    public void OnClickBtnOpenDevice(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        int retVal;
        String devId = null, msg = "";
        for(int devIdx = 0; devIdx < mVeinDevCnt; devIdx++){
            devId = new String(mVeinDevIdList[devIdx]);
            if(0 == devId.trim().length()) {
                break;
            }
            /**
             * Wedone:
             * 定义：
             *        int FV_OpenDevice(byte[] devId)
             * 功能:：
             *      打开设备
             *
             * @参数(IN)  byte[] devId: 指定要打开的设备的ID
             * @调用 public
             * @返回 int: 0=正确，其他=错误代码
             */
            retVal = mSdkMain.FV_OpenDevice(mVeinDevIdList[devIdx]);
            if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
                msg += "{" + devId + ":成功}";
            }
            else{
                msg += "{" + devId + ":err=" + retVal + "}";
            }
        }
        DisplayNoticeMsg("打开设备：" + msg, 0);

        return;
    }

    //关闭设备按钮
    public void OnClickBtnCloseDevice(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        int retVal;
        String devId = null, msg = "";
        for(int devIdx = 0; devIdx < mVeinDevCnt; devIdx++){
            devId = new String(mVeinDevIdList[devIdx]);
            if(0 == devId.trim().length()) {
                break;
            }
            /**
             * Wedone:
             * 定义：
             *        int FV_CloseDevice(byte[] devId)
             * 功能:：
             *        关闭设备
             *
             * @参数(IN)  byte[] devId: 指定要关闭的设备的ID
             * @调用 public
             * @返回 int: 0=正确，其他=错误代码
             */
            retVal = mSdkMain.FV_CloseDevice(mVeinDevIdList[devIdx]);
            if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
                msg += "{" + devId + ":成功}";
            }
            else{
                msg += "{" + devId + ":err=" + retVal + "}";
            }
        }
        DisplayNoticeMsg("关闭设备：" + msg, 0);

        return;
    }

    //读取序列号按钮
    public void OnClickBtnGetDevSerialNum(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        int retVal;
        String devId = null, msg = "";
        byte[] serialNum = new byte[SdkMain.FV_CONST_DEVSERIAL_LEN];
        for(int devIdx = 0; devIdx < mVeinDevCnt; devIdx++){
            devId = new String(mVeinDevIdList[devIdx]);
            if(0 == devId.trim().length()) {
                break;
            }
            /**
             * Wedone:
             * 定义：
             *        int FV_GetDevSerialNum(byte[] devId, byte[] serialNum)
             * 功能:：
             *        获取设备序列号
             *
             * @参数(IN)  byte[] devId: 指定要操作的设备的ID
             * @参数(OUT)  byte[] serialNum: 获取的设备序列号信息
             * @调用 public
             * @返回 int: 0=正确，其他=错误代码
             */
            retVal = mSdkMain.FV_GetDevSerialNum(mVeinDevIdList[devIdx], serialNum);
            if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
                msg += "{" + devId + ":" + new String(serialNum) + "}";
            }
            else{
                msg += "{" + devId + ":err=" + retVal + "}";
            }
        }
        DisplayNoticeMsg("读取序列号：" + msg, 0);

        return;
    }

    //读取SDK版本号按钮
    public void OnClickBtnGetSdkVersion(View v) {
        int retVal;
        byte[] version = new byte[SdkMain.FV_CONST_SDK_VERSION_LEN];

        /**
         * Wedone:
         * 定义：
         *        int FV_GetSdkVersion(byte[] version)
         * 功能:：
         *        获取SDK版本号
         *
         * @参数(OUT)  byte[] version: 获取的SDK版本号信息
         * @调用 public
         * @返回 int: 0=正确，其他=错误代码
         */
        retVal = mSdkMain.FV_GetSdkVersion(version);
        if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
            DisplayNoticeMsg("读取SDK版本号：" + new String(version), 0);
        }
        else{
            DisplayNoticeMsg("读取SDK版本号：err=" + retVal, 0);
        }

        return;
    }

    //检测手指放置状态按钮
    public void OnClickBtnFingerDetect(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        int retVal;
        String devId = null, msg = "";
        byte[] fingerStatus = new byte[1];
        for(int devIdx = 0; devIdx < mVeinDevCnt; devIdx++){
            devId = new String(mVeinDevIdList[devIdx]);
            if(0 == devId.trim().length()) {
                break;
            }
            /**
             * Wedone:
             * 定义：
             *        int FV_FingerDetect(byte[] devId, byte[] fingerStatus)
             * 功能:：
             *        检测手指的放置状态
             *
             * @参数(IN)  byte[] devId: 指定要操作的设备的ID
             * @参数(OUT)  byte[] fingerStatus: 获取的手指状态，0：没有检测到手指，1、2：检测到手指但没有放好，3：检测到手指并且已经放置好
             * @调用 public
             * @返回 int: 0=正确，其他=错误代码
             */
            retVal = mSdkMain.FV_FingerDetect(mVeinDevIdList[devIdx], fingerStatus);
            if(mSdkMain.FV_ERRCODE_SUCCESS == retVal){
                if(0 == (int)fingerStatus[0]){
                    msg += "{" + devId + ":没放手指}";
                }
                else if(3 == (int)fingerStatus[0]){
                    msg += "{" + devId + ":手指放好}";
                }
                else {
                    msg += "{" + devId + ":手指没放好}";
                }
            }
            else{
                msg += "{" + devId + ":err=" + retVal + "}";
            }
        }
        DisplayNoticeMsg("读取手指放置状态：" + msg, 0);

        return;
    }

    //读取指静脉特征按钮
    public void OnClickBtnGrabFeature(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int retVal;
                String devId = null, msg = "";
                byte[] featureData = new byte[SdkMain.FV_CONST_FEATURE_LEN];
                for (int devIdx = 0; devIdx < mVeinDevCnt; devIdx++) {
                    devId = new String(mVeinDevIdList[devIdx]);
                    DisplayNoticeMsg("正在采集指静脉特征，请在{" + devId + "}上放手指！", 0);
                    if (0 == devId.trim().length()) {
                        break;
                    }
                    /**
                     * Wedone:
                     * 定义：
                     *        int FV_GrabFeature(byte[] devId, byte[] featureData, byte flag){
                     *                   BioVeinDevice bioVeinDevice  = getBioVeinDevice(devId, 2)
                     * 功能:：
                     *        读取一个指静脉特征模板数据
                     *
                     * @参数(IN)  byte[] devId: 指定要操作的设备的ID
                     * @参数(OUT)  byte[] featureData: 用于保存特征模板数据的缓冲区
                     * @参数(IN)  byte flag: 操作标志位，暂时未作定义
                     * @调用 public
                     * @返回 int: 0=正确，其他=错误代码
                     */
                    retVal = mSdkMain.FV_GrabFeature(mVeinDevIdList[devIdx], featureData, (byte) 0);
                    if (mSdkMain.FV_ERRCODE_SUCCESS == retVal) {
                        msg += "{" + devId + ":成功}";
                    } else {
                        msg += "{" + devId + ":err=" + retVal + "}";
                    }
                }
                DisplayNoticeMsg("读取指静脉特征：" + msg, 0);
            }
        }).start();

        return;
    }

    //读取指静脉特征和图片按钮
    public void OnClickBtnGrabFeatureImage(View v) {
        if(0 >= mVeinDevCnt){
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int retVal;
                String devId = null, msg = "";
                byte[] featureData = new byte[SdkMain.FV_CONST_FEATURE_LEN];
                byte[] imageData = new byte[SdkMain.FV_CONST_IMAGE_MAX_SIZE];
                int[] imageSize = new int[1];
                int[] imageWidth = new int[1];
                int[] imageHeight = new int[1];
                for (int devIdx = 0; devIdx < mVeinDevCnt; devIdx++) {
                    devId = new String(mVeinDevIdList[devIdx]);
                    DisplayNoticeMsg("正在采集指静脉特征和图片，请在{" + devId + "}上放手指！", 0);
                    if (0 == devId.trim().length()) {
                        break;
                    }
                    /**
                     * Wedone:
                     * 定义：
                     *        int FV_GrabFeature(byte[] devId, byte[] featureData, byte flag){
                     *                   BioVeinDevice bioVeinDevice  = getBioVeinDevice(devId, 2)
                     * 功能:：
                     *        读取一个指静脉特征模板数据及其对应的指静脉图片（****本接口的图片信息输出尚未封装完成****）
                     *
                     * @参数(IN)  byte[] devId: 指定要操作的设备的ID
                     * @参数(OUT)  byte[] featureData: 用于保存特征模板数据的缓冲区
                     * @参数(OUT)  byte[] imageData: 用于保存特征模板数据的缓冲区
                     * @参数(OUT)  int[] imageSize: 用于保存特征模板数据的缓冲区
                     * @参数(OUT)  int[] imageWidth: 用于保存特征模板数据的缓冲区
                     * @参数(OUT)  int[] imageHeight: 用于保存特征模板数据的缓冲区
                     * @参数(IN)  byte flag: 操作标志位，暂时未作定义
                     * @调用 public
                     * @返回 int: 0=正确，其他=错误代码
                     */
                    retVal = mSdkMain.FV_GrabFeatureAndImage(mVeinDevIdList[devIdx], featureData, imageData, imageSize, imageWidth, imageHeight, (byte) 0);
                    if (mSdkMain.FV_ERRCODE_SUCCESS == retVal) {
                        msg += "{" + devId + ":成功}";
                    } else {
                        msg += "{" + devId + ":err=" + retVal + "}";
                    }
                }
                DisplayNoticeMsg("读取指静脉特征和图片：" + msg, 0);
            }
        }).start();

        return;
    }

    /**
     * Wedone: 等待手指传感器变为某种指定的状态，等待的时间为nInterval*nTimes
     *
     * @参数(IN)  byte bFingerStatus: 等待的状态；0：手指已经移开，3：手指已经放置好。
     * @参数(IN)  int nTimes: 检测的次数，必须大于0。
     * @参数(IN)  int nInterval: 每次检测的间隔，单位为毫秒，建议在500 - 1000毫秒之间。
     * @调用 public
     * @返回 boolean: true=成功的等到了指定的状态：
     *             false=没有等到指定的状态就超时了
     */
    public boolean WaitFingerStatus(byte[] devId, byte bFingerStatus, int nTimes, int nInterval, String msghdr){
        if((0 >= nTimes) || (200 >= nInterval) || (1000 < nInterval)){
            return false;
        }
        String msg = "";
        byte[] fingerStatus = new byte[1];
        int retVal;
        if(null != msghdr){
            msg = msghdr;
        }
        else if(0 == bFingerStatus){
            msg = "请移开手指！";
        }
        else if(0x03 == bFingerStatus){
            msg = "请放手指！";
        }
        for(int nCnt = 0; nCnt < nTimes; nCnt++){
            DisplayNoticeMsg(msg, 0);
            retVal = mSdkMain.FV_FingerDetect(devId, fingerStatus);
            if (mSdkMain.FV_ERRCODE_SUCCESS != retVal) {
                DisplayNoticeMsg("检测手指放置状态失败，错误码=" + retVal + "!\r\n", 0);
                return false;
            }
            if(bFingerStatus == fingerStatus[0]){
                return true;
            }
            try {
                sleep(nInterval, 0);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            if(0 == (nCnt%(1000/nInterval))) {
                msg += "*";
            }
        }
        return false;
    }

    //注册1根手指(3次)按钮
    public void OnClickBtnRegister(View v) {
        if (0 >= mVeinDevCnt) {
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }
        if (mRegUserData.D_USER_TEMPLATE_NUM <= mRegUserData.GetTemplateNum()) {
            DisplayNoticeMsg("单个用户最多只能注册20个模板！\r\n", 0);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int retVal;
                String devId = null, msg = "";
                int readCnt = 0; //读取模板次数的计数器
                int retryCnt = 0;
                byte[] featureData = new byte[SdkMain.FV_CONST_FEATURE_LEN];
                int devIdx = 0; //Wedone:指定在第一台设备上进行操作

                devId = new String(mVeinDevIdList[devIdx]);
                if (0 == devId.trim().length()) {//Wedone:无效的设备ID，则直接返回
                    return;
                }

                //清除上次注册的数据
                mRegUserData.ClearData();
                mAIUserData.ClearData();

                while(true) {
                    if(6 <= retryCnt)break; //已经重试了6次，结束采集
                    retryCnt++;
                    //Wedone:检测手指，直到检测到手指已经放好才进行后续读取静脉特征的操作
                    boolean isWaitSuccess = true;
                    msg = "正在{" + devId + "}上采集第" + (readCnt + 1) + "个静脉特征:";
                    isWaitSuccess = WaitFingerStatus(mVeinDevIdList[devIdx], (byte) 0x03, 20, 500, msg + "请放手指!");
                    if (!isWaitSuccess) {
                        return;
                    }

                    //Wedone:读取指静脉特征模板数据
                    retVal = mSdkMain.FV_GrabFeature(mVeinDevIdList[devIdx], featureData, (byte) 0);
                    if (mSdkMain.FV_ERRCODE_SUCCESS == retVal) {
                        msg += "成功";
                        DisplayNoticeMsg(msg, 0);
                    } else {
                        msg += "err=" + retVal;
                        DisplayNoticeMsg(msg, 0);
                        break; //Wedone: 采集过程中发生错误，则直接退出
                    }

                    //Wedone:检测手指，直到检测到手指已经移开才进行后续读操作，确保每次采集都是重新放置了手指而不是手指一直放着不动
                    msg = "读取完成第" + (readCnt + 1) + "个静脉特征:";
                    isWaitSuccess = WaitFingerStatus(mVeinDevIdList[devIdx], (byte) 0x00, 20, 500, msg + "请移开手指!");
                    if (!isWaitSuccess) {
                        return;
                    }

                    //Wedone:确认采集的指静脉特征数据是否有效
                    retVal = VeinMatchCaller.FvmIsValidFeature(featureData, (byte)0x01);
                    if(SdkMain.FV_ERRCODE_SUCCESS != retVal){
                        DisplayNoticeMsg("错误：指静脉特征数据无效！\r\n", 0);
                        continue;
                    }

                    //Wedone:调用FV_GrabFeature返回成功的话，第二个参数的缓冲区中就保存了所读取的模板数据，
                    if (0 == readCnt) {
                        //采集完成第一个静脉特征，生成对应的用户信息
                        mUserCnt++;
                        byte bUserId[] = new byte[mRegUserData.D_USER_HDR_USERID_LEN];
                        byte bUserName[] = new byte[mRegUserData.D_USER_HDR_USERNAME_LEN];

                        mRegUserData.SetUid((long) mUserCnt);

                        bUserId[0] = 'I';
                        bUserId[1] = 'D';
                        bUserId[2] = (byte) (0x30 + (mUserCnt % 10000) / 1000);
                        bUserId[3] = (byte) (0x30 + (mUserCnt % 1000) / 100);
                        bUserId[4] = (byte) (0x30 + (mUserCnt % 100) / 10);
                        bUserId[5] = (byte) (0x30 + (mUserCnt % 10));
                        mRegUserData.SetUserId(bUserId, (short) 6);

                        bUserName[0] = 'U';
                        bUserName[1] = 'S';
                        bUserName[2] = 'E';
                        bUserName[3] = 'R';
                        bUserName[4] = (byte) (0x30 + (mUserCnt % 10000) / 1000);
                        bUserName[5] = (byte) (0x30 + (mUserCnt % 1000) / 100);
                        bUserName[6] = (byte) (0x30 + (mUserCnt % 100) / 10);
                        bUserName[7] = (byte) (0x30 + (mUserCnt % 10));
                        mRegUserData.SetUserName(bUserName, (short) 8);
                    }
                    if(0 < readCnt){ //Wedone:之前已经有采集的特征，把当前采集的静脉特征与之前采集的进行验证是否属于同一根手指
                        byte[] regTemplateData = mRegUserData.TemplateData();
                        //Wedone: 注册过程中，检测采集的静脉特征是否属于同一根手指
                        retVal = VeinMatchCaller.FvmIsSameFinger(featureData,//本次采集的指静脉特征值
                                regTemplateData,  //包含之前采集的指静脉特征值数据
                                (byte)readCnt, //第二个参数中包含的指静脉特征值的个数
                                (byte)0x03); //加密方式，当前请固定为3
                        if(SdkMain.FV_ERRCODE_SUCCESS != retVal){
                            DisplayNoticeMsg("错误：注册过程中采集的特征必须属于同一根手指！\r\n", 0);
                            continue;
                        }
                    }
                    //Wedone：采集的特征值符合要求，保存到本地缓冲区中
                    mRegUserData.AddTemplateData(featureData, (short) featureData.length);
                    readCnt++;
                    DisplayNoticeMsg("采集指静脉特征成功！已采集" + mRegUserData.GetTemplateNum() + "个特征模板\r\n", 0);
                    if(3 <= readCnt)break; //采集完成3个有效模板，则结束采集
                }
            }
        }).start();
        return;
    }

    //验证手指按钮
    public void OnClickBtnIdentify(View v) {
        if (0 >= mVeinDevCnt) {
            DisplayNoticeMsg("不存在有效的指静脉设备，请先进行枚举设备操作！", 0);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int retVal;
                String devId = null, msg = "";
                byte[] featureData = new byte[SdkMain.FV_CONST_FEATURE_LEN];
                int devIdx = 0; //Wedone:指定在第一台设备上进行操作

                devId = new String(mVeinDevIdList[devIdx]);
                if (0 == devId.trim().length()) {//Wedone:无效的设备ID，则直接返回
                    return;
                }

                //Wedone:检测手指，直到检测到手指已经放好才进行后续读取静脉特征的操作
                boolean isWaitSuccess = true;
                msg = "正在{" + devId + "}上进行验证：";
                isWaitSuccess = WaitFingerStatus(mVeinDevIdList[devIdx], (byte) 0x03, 20, 500, msg + "请放手指!");
                if (!isWaitSuccess) {
                    return;
                }

                //Wedone:读取指静脉特征模板数据
                retVal = mSdkMain.FV_GrabFeature(mVeinDevIdList[devIdx], featureData, (byte) 0);
                if (mSdkMain.FV_ERRCODE_SUCCESS == retVal) {
                    msg += "成功";
                    DisplayNoticeMsg(msg, 0);
                } else {
                    msg += "err=" + retVal;
                    DisplayNoticeMsg(msg, 0);
                    return; //Wedone: 采集过程中发生错误，则直接退出
                }

                //Wedone:检测手指，直到检测到手指已经移开才进行后续读操作，确保每次采集都是重新放置了手指而不是手指一直放着不动
                msg = "采集验证模板成功:";
                isWaitSuccess = WaitFingerStatus(mVeinDevIdList[devIdx], (byte) 0x00, 20, 500, msg + "请移开手指!");
                if (!isWaitSuccess) {
                    return;
                }

                //Wedone:确认采集的指静脉特征数据是否有效
                retVal = VeinMatchCaller.FvmIsValidFeature(featureData, (byte)0x01);
                if(SdkMain.FV_ERRCODE_SUCCESS != retVal){
                    DisplayNoticeMsg("错误：指静脉特征数据无效！\r\n", 0);
                    return;
                }

                int regTemplateCnt = mRegUserData.GetTemplateNum();
                int aiTemplateCnt = mAIUserData.GetTemplateNum();
                byte[] regTemplateData = mRegUserData.TemplateData(); //获取注册采集的特征数据
                byte[] aiTemplateData = mAIUserData.TemplateData(); //获取AI自学习的特征数据
                byte[] aiTemplateBuff = new byte[UserData.D_USER_TEMPLATE_SIZE*3]; //准备3个模板大小的缓冲区用于自动学习
                byte[] mergeTemplateData = mergeBytes(regTemplateData, aiTemplateData); //把注册时采集的特征数据和AI自学习的数据合并起来验证
                byte securityLevel = 4;
                int[] diff = new int[1];
                int[] AIDataLen = new int[1];
                diff[0] = 10000;
                AIDataLen[0] = UserData.D_USER_TEMPLATE_SIZE*3;
                SdkMain.DebugStringPrint("指静脉比对:regTemplateCnt=" + regTemplateCnt + ",aiTemplateCnt=" + aiTemplateCnt);
                //Wedone: 调用静脉特征值比对接口进行比对，各个参数意义如下说明
                retVal = VeinMatchCaller.FvmMatchFeature(featureData, //采集的用于验证的指静脉特征值
                        mergeTemplateData, //包含注册的特征值+AI自学习的特征值数据
                        (byte) (regTemplateCnt + aiTemplateCnt), //第二个参数的比对特征值的个数，包含注册+AI自学习的特征值个数
                        (byte) 0x03, //加密方式，当前请固定为3
                        securityLevel, //比对时使用的安全级别， 1:1场景：范围[6-10],建议值为6，1:N场景：范围[1-5],建议值为4
                        diff, //用于返回比对结果的差异度值
                        aiTemplateBuff, //用于输出比对时自动学习生成的特征数据
                        AIDataLen); //输入时初始化值为学习缓冲区的大小，验证通过返回时为学习成功的数据长度
                if(SdkMain.FV_ERRCODE_SUCCESS != retVal){
                    DisplayNoticeMsg("验证失败！！！", 0);
                    return;
                }
                //Wedone：验证通过，并且返回的AI学习缓冲区的数据长度大于0，保存AI学习的数据
                if(0 < AIDataLen[0]){
                    mAIUserData.ClearData();
                    mAIUserData.SetTemplateData(aiTemplateBuff, (short)AIDataLen[0]);
                }
                DisplayNoticeMsg("验证通过！差异度=" + diff[0] + "，学习数据长度=" + AIDataLen[0], 0);
            }
        }).start();
        return;
    }
}
