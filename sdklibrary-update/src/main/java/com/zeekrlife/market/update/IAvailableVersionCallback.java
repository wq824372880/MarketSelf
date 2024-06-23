/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.update;
public interface IAvailableVersionCallback extends android.os.IInterface
{
  /** Default implementation for IAvailableVersionCallback. */
  public static class Default implements IAvailableVersionCallback
  {
    @Override public boolean onAppAvailableVersion(boolean hasAvailableVersion, IAppInfo appInfo) throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IAvailableVersionCallback
  {
    private static final String DESCRIPTOR = "com.zeekrlife.market.update.IAvailableVersionCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.update.IAvailableVersionCallback interface,
     * generating a proxy if needed.
     */
    public static IAvailableVersionCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IAvailableVersionCallback))) {
        return ((IAvailableVersionCallback)iin);
      }
      return new Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_ON_APP_AVAILABLE_VERSION:
        {
          data.enforceInterface(descriptor);
          boolean arg0;
          arg0 = (0!=data.readInt());
          IAppInfo arg1;
          if ((0!=data.readInt())) {
            arg1 = IAppInfo.CREATOR.createFromParcel(data);
          }
          else {
            arg1 = null;
          }
          boolean result = this.onAppAvailableVersion(arg0, arg1);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IAvailableVersionCallback
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public boolean onAppAvailableVersion(boolean hasAvailableVersion, IAppInfo appInfo) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeInt(((hasAvailableVersion)?(1):(0)));
          if ((appInfo!=null)) {
            data.writeInt(1);
            appInfo.writeToParcel(data, 0);
          }
          else {
            data.writeInt(0);
          }
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_APP_AVAILABLE_VERSION, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().onAppAvailableVersion(hasAvailableVersion, appInfo);
          }
          reply.readException();
          result = (0!=reply.readInt());
        }
        finally {
          reply.recycle();
          data.recycle();
        }
        return result;
      }
      public static IAvailableVersionCallback sDefaultImpl;
    }
    static final int TRANSACTION_ON_APP_AVAILABLE_VERSION = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(IAvailableVersionCallback impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static IAvailableVersionCallback getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public boolean onAppAvailableVersion(boolean hasAvailableVersion, IAppInfo appInfo) throws android.os.RemoteException;
}
