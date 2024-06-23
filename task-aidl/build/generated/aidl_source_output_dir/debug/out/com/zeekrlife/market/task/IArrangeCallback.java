/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.task;
public interface IArrangeCallback extends android.os.IInterface
{
  /** Default implementation for IArrangeCallback. */
  public static class Default implements com.zeekrlife.market.task.IArrangeCallback
  {
    @Override public void onDownloadPending(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadStarted(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadConnected(java.lang.String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadProgress(java.lang.String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadCompleted(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadPaused(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadError(java.lang.String taskId, int errorCode) throws android.os.RemoteException
    {
    }
    @Override public void onInstallPending(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onInstallStarted(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onInstallProgress(java.lang.String taskId, float progress) throws android.os.RemoteException
    {
    }
    @Override public void onInstallCompleted(java.lang.String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onInstallError(java.lang.String taskId, int errorCode) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.zeekrlife.market.task.IArrangeCallback
  {
    private static final java.lang.String DESCRIPTOR = "com.zeekrlife.market.task.IArrangeCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.task.IArrangeCallback interface,
     * generating a proxy if needed.
     */
    public static com.zeekrlife.market.task.IArrangeCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.zeekrlife.market.task.IArrangeCallback))) {
        return ((com.zeekrlife.market.task.IArrangeCallback)iin);
      }
      return new com.zeekrlife.market.task.IArrangeCallback.Stub.Proxy(obj);
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
        case TRANSACTION_onDownloadPending:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onDownloadPending(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onDownloadStarted:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onDownloadStarted(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onDownloadConnected:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _arg1;
          _arg1 = data.readLong();
          long _arg2;
          _arg2 = data.readLong();
          this.onDownloadConnected(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onDownloadProgress:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          long _arg1;
          _arg1 = data.readLong();
          long _arg2;
          _arg2 = data.readLong();
          this.onDownloadProgress(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onDownloadCompleted:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onDownloadCompleted(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onDownloadPaused:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onDownloadPaused(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onDownloadError:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          this.onDownloadError(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onInstallPending:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onInstallPending(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onInstallStarted:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onInstallStarted(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onInstallProgress:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          float _arg1;
          _arg1 = data.readFloat();
          this.onInstallProgress(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onInstallCompleted:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onInstallCompleted(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onInstallError:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          this.onInstallError(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.zeekrlife.market.task.IArrangeCallback
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
      @Override public void onDownloadPending(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadPending, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadPending(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onDownloadStarted(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadStarted, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadStarted(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onDownloadConnected(java.lang.String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          _data.writeLong(soFarBytes);
          _data.writeLong(totalBytes);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadConnected, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadConnected(taskId, soFarBytes, totalBytes);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onDownloadProgress(java.lang.String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          _data.writeLong(soFarBytes);
          _data.writeLong(totalBytes);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadProgress, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadProgress(taskId, soFarBytes, totalBytes);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onDownloadCompleted(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadCompleted, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadCompleted(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onDownloadPaused(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadPaused, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadPaused(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onDownloadError(java.lang.String taskId, int errorCode) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          _data.writeInt(errorCode);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDownloadError, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadError(taskId, errorCode);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onInstallPending(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onInstallPending, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallPending(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onInstallStarted(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onInstallStarted, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallStarted(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onInstallProgress(java.lang.String taskId, float progress) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          _data.writeFloat(progress);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onInstallProgress, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallProgress(taskId, progress);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onInstallCompleted(java.lang.String taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onInstallCompleted, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallCompleted(taskId);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void onInstallError(java.lang.String taskId, int errorCode) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(taskId);
          _data.writeInt(errorCode);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onInstallError, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallError(taskId, errorCode);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.zeekrlife.market.task.IArrangeCallback sDefaultImpl;
    }
    static final int TRANSACTION_onDownloadPending = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onDownloadStarted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_onDownloadConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_onDownloadProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_onDownloadCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_onDownloadPaused = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_onDownloadError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_onInstallPending = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_onInstallStarted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_onInstallProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_onInstallCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_onInstallError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    public static boolean setDefaultImpl(com.zeekrlife.market.task.IArrangeCallback impl) {
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
    public static com.zeekrlife.market.task.IArrangeCallback getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void onDownloadPending(java.lang.String taskId) throws android.os.RemoteException;
  public void onDownloadStarted(java.lang.String taskId) throws android.os.RemoteException;
  public void onDownloadConnected(java.lang.String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException;
  public void onDownloadProgress(java.lang.String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException;
  public void onDownloadCompleted(java.lang.String taskId) throws android.os.RemoteException;
  public void onDownloadPaused(java.lang.String taskId) throws android.os.RemoteException;
  public void onDownloadError(java.lang.String taskId, int errorCode) throws android.os.RemoteException;
  public void onInstallPending(java.lang.String taskId) throws android.os.RemoteException;
  public void onInstallStarted(java.lang.String taskId) throws android.os.RemoteException;
  public void onInstallProgress(java.lang.String taskId, float progress) throws android.os.RemoteException;
  public void onInstallCompleted(java.lang.String taskId) throws android.os.RemoteException;
  public void onInstallError(java.lang.String taskId, int errorCode) throws android.os.RemoteException;
}
