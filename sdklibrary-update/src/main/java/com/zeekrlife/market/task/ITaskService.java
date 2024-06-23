/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.task;
public interface ITaskService extends android.os.IInterface
{
  /** Default implementation for ITaskService. */
  public static class Default implements ITaskService
  {
    @Override public java.util.List<ITaskInfo> getTaskList() throws android.os.RemoteException
    {
      return null;
    }
    @Override public ITaskInfo getTask(String taskId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean addTask(ITaskInfo taskInfo) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean removeTask(String taskId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean registerTaskCallback(ITaskCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean unregisterTaskCallback(ITaskCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean pauseDownload(String taskId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean resumeDownload(String taskId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean registerArrangeCallback(IArrangeCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean unregisterArrangeCallback(IArrangeCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements ITaskService
  {
    private static final String DESCRIPTOR = "com.zeekrlife.market.task.ITaskService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.task.ITaskService interface,
     * generating a proxy if needed.
     */
    public static ITaskService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof ITaskService))) {
        return ((ITaskService)iin);
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
        case TRANSACTION_GET_TASK_LIST:
        {
          data.enforceInterface(descriptor);
          java.util.List<ITaskInfo> result = this.getTaskList();
          reply.writeNoException();
          reply.writeTypedList(result);
          return true;
        }
        case TRANSACTION_GET_TASK:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          ITaskInfo result = this.getTask(arg0);
          reply.writeNoException();
          if ((result!=null)) {
            reply.writeInt(1);
            result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_ADD_TASK:
        {
          data.enforceInterface(descriptor);
          ITaskInfo arg0;
          if ((0!=data.readInt())) {
            arg0 = ITaskInfo.CREATOR.createFromParcel(data);
          }
          else {
            arg0 = null;
          }
          boolean result = this.addTask(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_REMOVE_TASK:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          boolean result = this.removeTask(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_REGISTER_TASK_CALLBACK:
        {
          data.enforceInterface(descriptor);
          ITaskCallback arg0;
          arg0 = ITaskCallback.Stub.asInterface(data.readStrongBinder());
          boolean result = this.registerTaskCallback(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_UNREGISTER_TASK_CALLBACK:
        {
          data.enforceInterface(descriptor);
          ITaskCallback arg0;
          arg0 = ITaskCallback.Stub.asInterface(data.readStrongBinder());
          boolean result = this.unregisterTaskCallback(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_PAUSE_DOWNLOAD:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          boolean result = this.pauseDownload(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_RESUME_DOWNLOAD:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          boolean result = this.resumeDownload(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_REGISTER_ARRANGE_CALLBACK:
        {
          data.enforceInterface(descriptor);
          IArrangeCallback arg0;
          arg0 = IArrangeCallback.Stub.asInterface(data.readStrongBinder());
          boolean result = this.registerArrangeCallback(arg0);
          reply.writeNoException();
          reply.writeInt(((result)?(1):(0)));
          return true;
        }
        case TRANSACTION_UNREGISTER_ARRANGE_CALLBACK:
        {
          data.enforceInterface(descriptor);
          IArrangeCallback arg0;
          arg0 = IArrangeCallback.Stub.asInterface(data.readStrongBinder());
          boolean result = this.unregisterArrangeCallback(arg0);
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
    private static class Proxy implements ITaskService
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
      @Override public java.util.List<ITaskInfo> getTaskList() throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        java.util.List<ITaskInfo> result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          boolean status = mRemote.transact(Stub.TRANSACTION_GET_TASK_LIST, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().getTaskList();
          }
          reply.readException();
          result = reply.createTypedArrayList(ITaskInfo.CREATOR);
        }
        finally {
          reply.recycle();
          data.recycle();
        }
        return result;
      }
      @Override public ITaskInfo getTask(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        ITaskInfo result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_GET_TASK, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().getTask(taskId);
          }
          reply.readException();
          if ((0!=reply.readInt())) {
            result = ITaskInfo.CREATOR.createFromParcel(reply);
          }
          else {
            result = null;
          }
        }
        finally {
          reply.recycle();
          data.recycle();
        }
        return result;
      }
      @Override public boolean addTask(ITaskInfo taskInfo) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          if ((taskInfo!=null)) {
            data.writeInt(1);
            taskInfo.writeToParcel(data, 0);
          }
          else {
            data.writeInt(0);
          }
          boolean status = mRemote.transact(Stub.TRANSACTION_ADD_TASK, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().addTask(taskInfo);
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
      @Override public boolean removeTask(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_REMOVE_TASK, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().removeTask(taskId);
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
      @Override public boolean registerTaskCallback(ITaskCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean status = mRemote.transact(Stub.TRANSACTION_REGISTER_TASK_CALLBACK, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().registerTaskCallback(callback);
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
      @Override public boolean unregisterTaskCallback(ITaskCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          if(callback != null){
            data.writeStrongBinder(callback.asBinder());
          }
          boolean status = mRemote.transact(Stub.TRANSACTION_UNREGISTER_TASK_CALLBACK, data, reply, 0);
          if (!status && getDefaultImpl() != null && callback != null) {
            return getDefaultImpl().unregisterTaskCallback(callback);
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
      @Override public boolean pauseDownload(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_PAUSE_DOWNLOAD, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().pauseDownload(taskId);
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
      @Override public boolean resumeDownload(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_RESUME_DOWNLOAD, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().resumeDownload(taskId);
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
      @Override public boolean registerArrangeCallback(IArrangeCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean status = mRemote.transact(Stub.TRANSACTION_REGISTER_ARRANGE_CALLBACK, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().registerArrangeCallback(callback);
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
      @Override public boolean unregisterArrangeCallback(IArrangeCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        boolean result;
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean status = mRemote.transact(Stub.TRANSACTION_UNREGISTER_ARRANGE_CALLBACK, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            return getDefaultImpl().unregisterArrangeCallback(callback);
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
      public static ITaskService sDefaultImpl;
    }
    static final int TRANSACTION_GET_TASK_LIST = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_GET_TASK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_ADD_TASK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_REMOVE_TASK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_REGISTER_TASK_CALLBACK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_UNREGISTER_TASK_CALLBACK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_PAUSE_DOWNLOAD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_RESUME_DOWNLOAD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_REGISTER_ARRANGE_CALLBACK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_UNREGISTER_ARRANGE_CALLBACK = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    public static boolean setDefaultImpl(ITaskService impl) {
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
    public static ITaskService getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public java.util.List<ITaskInfo> getTaskList() throws android.os.RemoteException;
  public ITaskInfo getTask(String taskId) throws android.os.RemoteException;
  public boolean addTask(ITaskInfo taskInfo) throws android.os.RemoteException;
  public boolean removeTask(String taskId) throws android.os.RemoteException;
  public boolean registerTaskCallback(ITaskCallback callback) throws android.os.RemoteException;
  public boolean unregisterTaskCallback(ITaskCallback callback) throws android.os.RemoteException;
  public boolean pauseDownload(String taskId) throws android.os.RemoteException;
  public boolean resumeDownload(String taskId) throws android.os.RemoteException;
  public boolean registerArrangeCallback(IArrangeCallback callback) throws android.os.RemoteException;
  public boolean unregisterArrangeCallback(IArrangeCallback callback) throws android.os.RemoteException;
}
