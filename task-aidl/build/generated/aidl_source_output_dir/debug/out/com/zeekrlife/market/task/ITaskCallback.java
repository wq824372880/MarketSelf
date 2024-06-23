/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.task;
public interface ITaskCallback extends android.os.IInterface
{
  /** Default implementation for ITaskCallback. */
  public static class Default implements com.zeekrlife.market.task.ITaskCallback
  {
    @Override public void onTaskAdded(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException
    {
    }
    @Override public void onTaskRemoved(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.zeekrlife.market.task.ITaskCallback
  {
    private static final java.lang.String DESCRIPTOR = "com.zeekrlife.market.task.ITaskCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.task.ITaskCallback interface,
     * generating a proxy if needed.
     */
    public static com.zeekrlife.market.task.ITaskCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.zeekrlife.market.task.ITaskCallback))) {
        return ((com.zeekrlife.market.task.ITaskCallback)iin);
      }
      return new com.zeekrlife.market.task.ITaskCallback.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_onTaskAdded:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.ITaskInfo _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.zeekrlife.market.task.ITaskInfo.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.onTaskAdded(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onTaskRemoved:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.ITaskInfo _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.zeekrlife.market.task.ITaskInfo.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.onTaskRemoved(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.zeekrlife.market.task.ITaskCallback
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
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void onTaskAdded(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((taskInfo!=null)) {
            _data.writeInt(1);
            taskInfo.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_onTaskAdded, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onTaskAdded(taskInfo);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onTaskRemoved(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((taskInfo!=null)) {
            _data.writeInt(1);
            taskInfo.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_onTaskRemoved, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onTaskRemoved(taskInfo);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.zeekrlife.market.task.ITaskCallback sDefaultImpl;
    }
    static final int TRANSACTION_onTaskAdded = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onTaskRemoved = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(com.zeekrlife.market.task.ITaskCallback impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.zeekrlife.market.task.ITaskCallback getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void onTaskAdded(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException;
  public void onTaskRemoved(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException;
}
