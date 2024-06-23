package com.zeekr.car.adaptapi;

import android.content.Context;
import android.util.Log;
import com.ecarx.xui.adaptapi.FunctionStatus;
import com.ecarx.xui.adaptapi.binder.IConnectable;
import com.ecarx.xui.adaptapi.car.Car;
import com.ecarx.xui.adaptapi.car.ICar;
import com.ecarx.xui.adaptapi.car.base.ICarFunction;
import com.ecarx.xui.adaptapi.car.vehicle.IVehicle;
import com.zeekr.car.util.CarLogUtils;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AdaptApi代理类
 *
 * @author Lei.Chen29
 */
public class CarApiProxy {

    private static final String TAG = "CarApiProxy";

    private ICar mICar;

    private CopyOnWriteArraySet<ConnectListener> mConnListenerList = new CopyOnWriteArraySet<>();

    private CopyOnWriteArraySet<SlideCsdPosnListener> mCsdPosnListener = new CopyOnWriteArraySet<>();

    private AtomicBoolean mConnected = new AtomicBoolean(false);

    private ICarFunction mCarFunc;

    private static volatile CarApiProxy sInstance = null;

    public interface ConnectListener {
        /**
         * onConnected
         */
        void onConnected();

        /**
         * onDisconnected
         */
        void onDisconnected();
    }

    public static CarApiProxy getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CarApiProxy.class) {
                if (sInstance == null) {
                    sInstance = new CarApiProxy(context);
                }
            }
        }
        return sInstance;
    }

    private CarApiProxy(Context context) {
        initCar(context);
    }

    public boolean isConnected() {
        return mConnected.get();
    }

    public ICar getICar() {
        return mICar;
    }

    /**
     * 连接监听
     *
     * @param connectListener connectListener
     */
    public void addConnListener(ConnectListener connectListener) {
        if (mConnected.get()) {
            connectListener.onConnected();
        }
        mConnListenerList.add(connectListener);
    }

    /**
     * 移除连接监听
     *
     * @param connectListener connectListener
     */
    public void removeConnListener(ConnectListener connectListener) {
        mConnListenerList.remove(connectListener);
    }

    /**
     * 添加Csd位置移动监听
     */
    public void addSlideCsdPosnListener(SlideCsdPosnListener listener) {
        mCsdPosnListener.add(listener);
    }

    /**
     * 移除Csd位置移动监听
     */
    public void removeSlideCsdPosnListener(SlideCsdPosnListener listener) {
        mCsdPosnListener.remove(listener);
    }

    private void initCar(Context context) {
        try {
            Log.e(TAG, "Car create");
            mICar = Car.create(context.getApplicationContext());
            if (mICar != null) {
                if (mICar instanceof IConnectable) {
                    IConnectable iCarConnectable = (IConnectable) mICar;
                    iCarConnectable.registerConnectWatcher(new IConnectable.IConnectWatcher() {
                        @Override
                        public void onConnected() {
                            Log.d(TAG, "Car onConnected ");
                            mConnected.set(true);
                            if (mICar != null) {
                                mCarFunc = mICar.getICarFunction();
                            }
                            registerCarFunc();
                            notifyConnected();
                        }

                        @Override
                        public void onDisConnected() {
                            Log.d(TAG, "Car onDisConnected");
                            mConnected.set(false);
                            notifyDisconnected();
                        }
                    });
                    iCarConnectable.connect();
                } else {
                    mConnected.set(true);
                    notifyConnected();
                }
            } else {
                Log.e(TAG, "ICar is null");
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "CarAPIProxy init ex", throwable);
            CarLogUtils.logStackTrace(throwable);
        }
    }

    private void notifyConnected() {
        for (ConnectListener listener : mConnListenerList) {
            listener.onConnected();
        }
    }

    private void notifyDisconnected() {
        for (ConnectListener listener : mConnListenerList) {
            listener.onDisconnected();
        }
    }

    private final ICarFunction.IFunctionValueWatcher mCallback = new ICarFunction.IFunctionValueWatcher() {
        @Override
        public void onFunctionChanged(int function) {

        }

        @Override
        public void onFunctionValueChanged(int function, int zone, int value) {
            Log.e(TAG, "onFunctionValueChanged value=" + function + " value=" + value);
            FunctionStatus status = mCarFunc.isFunctionSupported(IVehicle.SETTING_FUNC_SLDG_CSD_POSN_CMD);
            if (status != FunctionStatus.active) {
                Log.w(TAG, function + " not active");
                return;
            }

            try {
                if (function == IVehicle.SETTING_FUNC_SLDG_CSD_POSN_CMD) {
                    Log.e(TAG, "functionValue:SETTING_FUNC_SLDG_CSD_POSN_CMD");
                    if (mCsdPosnListener.size() > 0) {
                        for (SlideCsdPosnListener listener : mCsdPosnListener) {
                            listener.onCsdSlide(value);
                        }
                    }
                }
            } catch (Exception e) {
               CarLogUtils.logStackTrace(e);
            }
        }

        @Override
        public void onCustomizeFunctionValueChanged(int i, int i1, float v) {

        }

        @Override
        public void onSupportedFunctionStatusChanged(int i, int i1, FunctionStatus functionStatus) {

        }

        @Override
        public void onSupportedFunctionValueChanged(int i, int[] ints) {

        }
    };

    public void stop() {
        if (null != mCarFunc) {
            mCarFunc.unregisterFunctionValueWatcher(mCallback);
        }
    }

    private void registerCarFunc() {
        if (null == mCarFunc) {
            Log.e(TAG, " mCarFunc is null");
            return;
        }
        mCarFunc.registerFunctionValueWatcher(IVehicle.SETTING_FUNC_SLDG_CSD_POSN_CMD, mCallback);
    }
}
