/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.task;
public interface ITaskService extends android.os.IInterface
{
  /** Default implementation for ITaskService. */
  public static class Default implements com.zeekrlife.market.task.ITaskService
  {
    @Override public java.util.List<com.zeekrlife.market.task.ITaskInfo> getTaskList() throws android.os.RemoteException
    {
      return null;
    }
    @Override public com.zeekrlife.market.task.ITaskInfo getTask(java.lang.String taskId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean addTask(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean removeTask(java.lang.String taskId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean registerTaskCallback(com.zeekrlife.market.task.ITaskCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean unregisterTaskCallback(com.zeekrlife.market.task.ITaskCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean pauseDownload(java.lang.String taskId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean resumeDownload(java.lang.String taskId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean registerArrangeCallback(com.zeekrlife.market.task.IArrangeCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean unregisterArrangeCallback(com.zeekrlife.market.task.IArrangeCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.zeekrlife.market.task.ITaskService
  {
    private static final java.lang.String DESCRIPTOR = "com.zeekrlife.market.task.ITaskService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.task.ITaskService interface,
     * generating a proxy if needed.
     */
    public static com.zeekrlife.market.task.ITaskService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.zeekrlife.market.task.ITaskService))) {
        return ((com.zeekrlife.market.task.ITaskService)iin);
      }
      return new com.zeekrlife.market.task.ITaskService.Stub.Proxy(obj);
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
        case TRANSACTION_getTaskList:
        {
          data.enforceInterface(descriptor);
          java.util.List<com.zeekrlife.market.task.ITaskInfo> _result = this.getTaskList();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_getTask:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          com.zeekrlife.market.task.ITaskInfo _result = this.getTask(_arg0);
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_addTask:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.ITaskInfo _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.zeekrlife.market.task.ITaskInfo.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          boolean _result = this.addTask(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_removeTask:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.removeTask(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_registerTaskCallback:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.ITaskCallback _arg0;
          _arg0 = com.zeekrlife.market.task.ITaskCallback.Stub.asInterface(data.readStrongBinder());
          boolean _result = this.registerTaskCallback(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_unregisterTaskCallback:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.ITaskCallback _arg0;
          _arg0 = com.zeekrlife.market.task.ITaskCallback.Stub.asInterface(data.readStrongBinder());
          boolean _result = this.unregisterTaskCallback(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_pauseDownload:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.pauseDownload(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_resumeDownload:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.resumeDownload(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_registerArrangeCallback:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.IArrangeCallback _arg0;
          _arg0 = com.zeekrlife.market.task.IArrangeCallback.Stub.asInterface(data.readStrongBinder());
          boolean _result = this.registerArrangeCallback(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_unregisterArrangeCallback:
        {
          data.enforceInterface(descriptor);
          com.zeekrlife.market.task.IArrangeCallback _arg0;
          _arg0 = com.zeekrlife.market.task.IArrangeCallback.Stub.asInterface(data.readStrongBinder());
          boolean _result = this.unregisterArrangeCallback(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.zeekrlife.market.task.ITaskService
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
      @Override public java.util.List<com.zeekrlife.market.task.ITaskInfo> getTaskList() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<com.zeekrlife.market.task.ITaskInfo> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTaskList, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getTaskList();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(com.zeekrlife.market.task.ITaskInfo.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public com.zeekrlife.market.task.ITaskInfo getTask(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        com.zeekrlife.market.task.ITaskInfo _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTask, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getTask(taskId);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = com.zeekrlife.market.task.ITaskInfo.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean addTask(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((taskInfo!=null)) {
            _data.writeInt(1);
            taskInfo.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addTask, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().addTask(taskInfo);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean removeTask(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_removeTask, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().removeTask(taskId);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean registerTaskCallback(com.zeekrlife.market.task.ITaskCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerTaskCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().registerTaskCallback(callback);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean unregisterTaskCallback(com.zeekrlife.market.task.ITaskCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterTaskCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().unregisterTaskCallback(callback);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean pauseDownload(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_pauseDownload, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().pauseDownload(taskId);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean resumeDownload(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_resumeDownload, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().resumeDownload(taskId);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean registerArrangeCallback(com.zeekrlife.market.task.IArrangeCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerArrangeCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().registerArrangeCallback(callback);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean unregisterArrangeCallback(com.zeekrlife.market.task.IArrangeCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterArrangeCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().unregisterArrangeCallback(callback);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static com.zeekrlife.market.task.ITaskService sDefaultImpl;
    }
    static final int TRANSACTION_getTaskList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_addTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_removeTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_registerTaskCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_unregisterTaskCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_pauseDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_resumeDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_registerArrangeCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_unregisterArrangeCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    public static boolean setDefaultImpl(com.zeekrlife.market.task.ITaskService impl) {
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
    public static com.zeekrlife.market.task.ITaskService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public java.util.List<com.zeekrlife.market.task.ITaskInfo> getTaskList() throws android.os.RemoteException;
  public com.zeekrlife.market.task.ITaskInfo getTask(java.lang.String taskId) throws android.os.RemoteException;
  public boolean addTask(com.zeekrlife.market.task.ITaskInfo taskInfo) throws android.os.RemoteException;
  public boolean removeTask(java.lang.String taskId) throws android.os.RemoteException;
  public boolean registerTaskCallback(com.zeekrlife.market.task.ITaskCallback callback) throws android.os.RemoteException;
  public boolean unregisterTaskCallback(com.zeekrlife.market.task.ITaskCallback callback) throws android.os.RemoteException;
  public boolean pauseDownload(java.lang.String taskId) throws android.os.RemoteException;
  public boolean resumeDownload(java.lang.String taskId) throws android.os.RemoteException;
  public boolean registerArrangeCallback(com.zeekrlife.market.task.IArrangeCallback callback) throws android.os.RemoteException;
  public boolean unregisterArrangeCallback(com.zeekrlife.market.task.IArrangeCallback callback) throws android.os.RemoteException;
}
