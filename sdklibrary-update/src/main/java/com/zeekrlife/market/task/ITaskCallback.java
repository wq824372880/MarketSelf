/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.task;
public interface ITaskCallback extends android.os.IInterface
{
  /** Default implementation for ITaskCallback. */
  public static class Default implements ITaskCallback
  {
    @Override public void onTaskAdded(ITaskInfo taskInfo) throws android.os.RemoteException
    {
    }
    @Override public void onTaskRemoved(ITaskInfo taskInfo) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements ITaskCallback
  {
    private static final String DESCRIPTOR = "com.zeekrlife.market.task.ITaskCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.task.ITaskCallback interface,
     * generating a proxy if needed.
     */
    public static ITaskCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof ITaskCallback))) {
        return ((ITaskCallback)iin);
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
        case TRANSACTION_ON_TASK_ADDED:
        {
          data.enforceInterface(descriptor);
          ITaskInfo arg0;
          if ((0!=data.readInt())) {
            arg0 = ITaskInfo.CREATOR.createFromParcel(data);
          }
          else {
            arg0 = null;
          }
          this.onTaskAdded(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_TASK_REMOVED:
        {
          data.enforceInterface(descriptor);
          ITaskInfo arg0;
          if ((0!=data.readInt())) {
            arg0 = ITaskInfo.CREATOR.createFromParcel(data);
          }
          else {
            arg0 = null;
          }
          this.onTaskRemoved(arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements ITaskCallback
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
      @Override public void onTaskAdded(ITaskInfo taskInfo) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          if ((taskInfo!=null)) {
            data.writeInt(1);
            taskInfo.writeToParcel(data, 0);
          }
          else {
            data.writeInt(0);
          }
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_TASK_ADDED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onTaskAdded(taskInfo);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onTaskRemoved(ITaskInfo taskInfo) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          if ((taskInfo!=null)) {
            data.writeInt(1);
            taskInfo.writeToParcel(data, 0);
          }
          else {
            data.writeInt(0);
          }
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_TASK_REMOVED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onTaskRemoved(taskInfo);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      public static ITaskCallback sDefaultImpl;
    }
    static final int TRANSACTION_ON_TASK_ADDED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_ON_TASK_REMOVED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(ITaskCallback impl) {
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
    public static ITaskCallback getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public void onTaskAdded(ITaskInfo taskInfo) throws android.os.RemoteException;
  public void onTaskRemoved(ITaskInfo taskInfo) throws android.os.RemoteException;
}
