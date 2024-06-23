/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.update;
public interface IAppCheckUpdater extends android.os.IInterface
{
  /** Default implementation for IAppCheckUpdater. */
  public static class Default implements IAppCheckUpdater
  {
    @Override public boolean checkAppUpdate(String packageName, ICheckUpdateCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean hasAvailableVersion(String packageName, IAvailableVersionCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IAppCheckUpdater
  {
    private static final String DESCRIPTOR = "com.zeekrlife.market.update.IAppCheckUpdater";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.update.IAppCheckUpdater interface,
     * generating a proxy if needed.
     */
    public static IAppCheckUpdater asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IAppCheckUpdater))) {
        return ((IAppCheckUpdater)iin);
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
        case TRANSACTION_CHECK_APP_UPDATE:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          ICheckUpdateCallback arg1;
          arg1 = ICheckUpdateCallback.Stub.asInterface(data.readStrongBinder());
          boolean result = this.checkAppUpdate(arg0, arg1);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_HAS_AVAILABLE_VERSION:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          IAvailableVersionCallback arg1;
          arg1 = IAvailableVersionCallback.Stub.asInterface(data.readStrongBinder());
          boolean result = this.hasAvailableVersion(arg0, arg1);
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
    private static class Proxy implements IAppCheckUpdater
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
      @Override public boolean checkAppUpdate(String packageName, ICheckUpdateCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(packageName);
          data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean status = mRemote.transact(Stub.TRANSACTION_CHECK_APP_UPDATE, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().checkAppUpdate(packageName, callback);
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
      @Override public boolean hasAvailableVersion(String packageName, IAvailableVersionCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(packageName);
          data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean status = mRemote.transact(Stub.TRANSACTION_HAS_AVAILABLE_VERSION, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().hasAvailableVersion(packageName, callback);
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
      public static IAppCheckUpdater sDefaultImpl;
    }
    static final int TRANSACTION_CHECK_APP_UPDATE = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_HAS_AVAILABLE_VERSION = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(IAppCheckUpdater impl) {
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
    public static IAppCheckUpdater getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public boolean checkAppUpdate(String packageName, ICheckUpdateCallback callback) throws android.os.RemoteException;
  public boolean hasAvailableVersion(String packageName, IAvailableVersionCallback callback) throws android.os.RemoteException;
}
