package com.zeekr.car.adaptapi.device;

import android.content.Context;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ecarx.xui.adaptapi.binder.IConnectable;
import com.ecarx.xui.adaptapi.device.Device;
import com.ecarx.xui.adaptapi.tbox.TBox;
import com.zeekr.basic.CommonKt;
import com.zeekr.car.tsp.EnvType;
import com.zeekr.car.util.CarLogUtils;
import com.zeekr.car.util.SystemProperties;
import com.zeekr.car.util.ThreadPoolUtil;
import com.zeekr.sdk.base.ApiReadyCallback;
import com.zeekr.sdk.base.Singleton;
import com.zeekr.sdk.base.annotation.KeepName;
import com.zeekr.sdk.device.ability.IDayNightMode;
import com.zeekr.sdk.device.ability.IDeviceState;
import com.zeekr.sdk.device.ability.ITetheringAPI;
import com.zeekr.sdk.device.impl.DeviceAPI;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author DC1E车型上代替openApi中的DeviceApi
 */
public class DeviceApiImpl extends DeviceAPI {
    private static final String TAG = "DeviceApiImpl";

    private static final Singleton<DeviceApiImpl> DEVICE_API_SINGLETON = new Singleton<DeviceApiImpl>() {
        @KeepName
        protected DeviceApiImpl create() {
            return new DeviceApiImpl();
        }
    };

    public static DeviceApiImpl getInstance() {
        return DEVICE_API_SINGLETON.get();
    }

    private Device mDeviceService;

    private TBox tBox;

    // 云端环境
    private final EnvType envType;
    // 车机的序列号
    private String ihuId;
    // 车架号
    private String vin;
    // 品牌
    private String operatorName;
    // 供应商代码
    private String supplierCode;
    // 项目代码
    private String projectCode;
    // 车系代码
    private String vehicleType;
    // 车型识别码
    private String vehicleModel;

    private String mIccId;

    private String mImSi;

    private String mMsisdn;
    private volatile boolean isSuccess = false;

    @Override
    public void init(Context context, ApiReadyCallback apiReadyCallback) throws IllegalStateException {
        if (isSuccess) {
            apiReadyCallback.onAPIReady(true, "");
        } else {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                ThreadPoolUtil.runOnSubThread(() -> init(apiReadyCallback),0);
            } else {
                init(apiReadyCallback);
            }
        }
    }

    private void init(ApiReadyCallback apiReadyCallback) {
        initApi();
        if (apiReadyCallback != null) {
            apiReadyCallback.onAPIReady(isSuccess, "");
        }
    }

    public DeviceApiImpl() {
        envType = new EnvType(SystemProperties.getString("persist.sys.tsp_env"));
        ThreadPoolUtil.runOnSubThread(this::initApi,0);
    }

    public void startTask(){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        initApi();
                    }
                }
        ).start();
        new Thread().start();
    }

    @Override
    protected String getServiceAlias() {
        return "device";
    }

    private synchronized void initApi() {
        Log.d(TAG, "adaptapi deviceapi start init");
        if (isSuccess && !TextUtils.isEmpty(vin) && !TextUtils.isEmpty(ihuId)) {
            Log.d(TAG, "adaptapi deviceapi  init succes: vin: " + vin + "===ihid: " + ihuId);
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            boxCreate(CommonKt.getAppContext());
            mDeviceService = Device.create(CommonKt.getAppContext());
            Log.d(TAG, "adaptapi deviceapi start init mAdapterService:" + mDeviceService);

            IConnectable iCarConnectable = (IConnectable) mDeviceService;
            iCarConnectable.registerConnectWatcher(new IConnectable.IConnectWatcher() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "adaptapi deviceapi connect");
                    isSuccess = true;
                    getDeviceInfo();
                    countDownLatch.countDown();
                }

                @Override
                public void onDisConnected() {
                    isSuccess = false;
                    Log.d(TAG, "adaptapi deviceapi disconnect");
                }
            });
            iCarConnectable.connect();
            countDownLatch.await(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            isSuccess = false;
            countDownLatch.countDown();
            Log.d(TAG, "INIT CATCH " + e.getMessage());
        }
        if (!isSuccess) {
            Log.d(TAG, "adaptapi deviceapi init timeout");
        } else {
            Log.d(TAG, "adaptapi deviceapi init sucess");
        }
    }

    private void boxCreate(Context context) {
        try {
            tBox = TBox.create(context);
            Log.d(TAG, "adaptapi tBoxCreate");
        } catch (Exception e) {
            CarLogUtils.logStackTrace(e);
            Log.d(TAG, "adaptapi tBoxCreate exception :" + Log.getStackTraceString(e));
        }
    }

    private void getDeviceInfo() {
        try {
            Log.d(TAG, "adaptapi deviceapi getDeviceInfo：1");
            if (mDeviceService == null) {
                mDeviceService = Device.create(CommonKt.getAppContext());
            }
            if (mDeviceService == null) {
                Log.d(TAG, "adaptapi deviceapi mAdapterService: null");
                return;
            }
            Log.d(TAG, "adaptapi deviceapi getDeviceInfo：2");
            vin = mDeviceService.getVin();
            ihuId = mDeviceService.getIhuId();
            operatorName = mDeviceService.getOperatorName() + "";
            supplierCode = mDeviceService.getSupplierCode();
            projectCode = mDeviceService.getProjectCode();
            vehicleType = mDeviceService.getVehicleType();
            Log.d(TAG, "adaptapi deviceapi tbox：" + tBox);
            if (tBox != null) {
                mIccId = tBox.getICCID();
                mMsisdn = tBox.getMSISDN();
                mImSi = tBox.getIMEI();
            }
            vehicleModel = supplierCode + "_" + projectCode + "_" + vehicleType;
            Log.d(TAG, "getDeviceInfo:" + toString());
        }catch (Throwable t) {
            Log.d(TAG, "getDeviceInfo catch " + t.getMessage());
        }
    }

    public EnvType getEnvType() {
        Log.d(TAG, "getEnvType " + envType);
        return envType;
    }

    @Override
    public String getIHUID() {
        Log.d(TAG, "getIhuId " + ihuId);
        try {
            check(TextUtils.isEmpty(ihuId));
        } catch (Exception e) {
            Log.d(TAG, "getIhuid catch " + e.getMessage());
        }
        return ihuId;
    }

    @Override
    public String getVIN() {
        Log.d(TAG, "getVIN " + vin);
        try {
            check(TextUtils.isEmpty(vin));
        } catch (Exception e) {
            Log.d(TAG, "getVIN catch " + e.getMessage());
        }
        return vin;
    }

    @Override
    public String getDVRID() throws RemoteException {
        return null;
    }

    @Override
    public String getXDSN() {
        return null;
    }

    @Override
    public String getICCID() {
        Log.d(TAG, "getIccId " + mIccId);
        try {
            check(TextUtils.isEmpty(mIccId));
        } catch (Exception e) {
            Log.d(TAG, "getIccId catch " + e.getMessage());
        }
        return mIccId;
    }

    public String getIMSI() {
        Log.d(TAG, "getIMSI " + mImSi);
        try {
            check(TextUtils.isEmpty(mImSi));
        } catch (Exception e) {
            Log.d(TAG, "getImSi catch " + e.getMessage());
        }
        return mImSi;
    }

    public String getMsisdn() {
        Log.d(TAG, "getMsisdn " + mMsisdn);
        try {
            check(TextUtils.isEmpty(mMsisdn));
        } catch (Exception e) {
            Log.d(TAG, "getMsisdn catch " + e.getMessage());
        }
        return mMsisdn;
    }

    @Override
    public int getOperatorCode() {
        return 0;
    }

    @Override
    public String getOperatorName() {
        Log.d(TAG, "getOperatorCode " + operatorName);
        try {
            check(TextUtils.isEmpty(operatorName));
        } catch (Exception e) {
            Log.d(TAG, "getOperatorCode catch " + e.getMessage());
        }
        return operatorName;
    }

    @Override
    public String getOpenIHUID() {
        return null;
    }

    @Override
    public String getOpenVIN() {
        return null;
    }

    @Override
    public String getIHUSerialNo() {
        return null;
    }

    @Override
    public String getDeviceServiceIDJson() {
        return null;
    }

    @Override
    public String getVehicleTypeConfig() {
        return null;
    }

    @Override
    public IDayNightMode getDayNightMode() {
        return null;
    }

    @Override
    public IDeviceState getDeviceState() {
        return null;
    }

    @Override
    public String getAppendVehicleType() {
        return null;
    }

    @Override
    public int getDHUType() {
        return 0;
    }

    @Override
    public String getOTAVersionName() {
        return null;
    }

    @Override
    public ITetheringAPI getTethering() {
        return null;
    }

    @Override
    public String getMarketAreaCode() {
        return null;
    }

    @Override
    public String getSupplierCode() {
        Log.d(TAG, "getSupplierCode " + supplierCode);
        try {
            check(TextUtils.isEmpty(supplierCode));
        } catch (Exception e) {
            Log.d(TAG, "getSupplierCode catch " + e.getMessage());
        }
        return supplierCode;
    }

    @Override
    public String getProjectCode() {
        Log.d(TAG, "getProjectCode " + projectCode);
        try {
            check(TextUtils.isEmpty(projectCode));
        } catch (Exception e) {
            Log.d(TAG, "getProjectCode catch " + e.getMessage());
        }
        return projectCode;
    }

    @Override
    public String getVehicleType() {
        Log.d(TAG, "getVehicleType " + vehicleType);
        try {
            check(TextUtils.isEmpty(vehicleType));
        } catch (Exception e) {
            Log.d(TAG, "getVehicleType catch " + e.getMessage());
        }
        return vehicleType;
    }

    public String getVehicleModel() {
        Log.d(TAG, "getVehicleModel " + vehicleModel);
        try {
            check(TextUtils.isEmpty(vehicleModel));
        } catch (Exception e) {
            Log.d(TAG, "getVehicleType catch " + e.getMessage());
        }
        return vehicleModel;
    }

    public void check(boolean s) {
        Log.d(TAG, "check 1" + s);
        if (!isSuccess) {
            Log.d(TAG, "check 2");
            initApi();
            return;
        }
        Log.d(TAG, "check 3");
        if (s) {
            Log.d(TAG, "check 4");
            getDeviceInfo();
        }
    }

    @Override
    public String toString() {
        return "DeviceInfo{"
                + ", envType="
                + envType
                + ", ihuId='"
                + ihuId
                + '\''
                + ", vin='"
                + vin
                + '\''
                + ", operatorCode='"
                + operatorName
                + '\''
                + ", supplierCode='"
                + supplierCode
                + '\''
                + ", projectCode='"
                + projectCode
                + '\''
                + ", vehicleType='"
                + vehicleType
                + '\''
                + ", vehicleModel='"
                + vehicleModel
                + '\''
                + ", iccId='"
                + mIccId
                + '\''
                + ", imsi='"
                + mImSi
                + '\''
                + ", msisdn='"
                + mMsisdn
                + '\''
                + '}';
    }
}