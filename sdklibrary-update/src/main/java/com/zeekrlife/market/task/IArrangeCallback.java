/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.zeekrlife.market.task;
public interface IArrangeCallback extends android.os.IInterface
{
  /** Default implementation for IArrangeCallback. */
  public static class Default implements IArrangeCallback
  {
    @Override public void onDownloadPending(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadStarted(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadCompleted(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadPaused(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onDownloadError(String taskId, int errorCode) throws android.os.RemoteException
    {
    }
    @Override public void onInstallPending(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onInstallStarted(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onInstallProgress(String taskId, float progress) throws android.os.RemoteException
    {
    }
    @Override public void onInstallCompleted(String taskId) throws android.os.RemoteException
    {
    }
    @Override public void onInstallError(String taskId, int errorCode) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IArrangeCallback
  {
    private static final String DESCRIPTOR = "com.zeekrlife.market.task.IArrangeCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.zeekrlife.market.task.IArrangeCallback interface,
     * generating a proxy if needed.
     */
    public static IArrangeCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IArrangeCallback))) {
        return ((IArrangeCallback)iin);
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
        case TRANSACTION_ON_DOWNLOAD_PENDING:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onDownloadPending(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_DOWNLOAD_STARTED:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onDownloadStarted(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_DOWNLOAD_CONNECTED:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          long arg1;
          arg1 = data.readLong();
          long arg2;
          arg2 = data.readLong();
          this.onDownloadConnected(arg0, arg1, arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_DOWNLOAD_PROGRESS:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          long arg1;
          arg1 = data.readLong();
          long arg2;
          arg2 = data.readLong();
          this.onDownloadProgress(arg0, arg1, arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_DOWNLOAD_COMPLETED:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onDownloadCompleted(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_DOWNLOAD_PAUSED:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onDownloadPaused(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_DOWNLOAD_ERROR:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          int arg1;
          arg1 = data.readInt();
          this.onDownloadError(arg0, arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_INSTALL_PENDING:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onInstallPending(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_INSTALL_STARTED:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onInstallStarted(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_INSTALL_PROGRESS:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          float arg1;
          arg1 = data.readFloat();
          this.onInstallProgress(arg0, arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_INSTALL_COMPLETED:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          this.onInstallCompleted(arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_ON_INSTALL_ERROR:
        {
          data.enforceInterface(descriptor);
          String arg0;
          arg0 = data.readString();
          int arg1;
          arg1 = data.readInt();
          this.onInstallError(arg0, arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IArrangeCallback
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
      @Override public void onDownloadPending(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_PENDING, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadPending(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onDownloadStarted(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_STARTED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadStarted(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          data.writeLong(soFarBytes);
          data.writeLong(totalBytes);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_CONNECTED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadConnected(taskId, soFarBytes, totalBytes);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          data.writeLong(soFarBytes);
          data.writeLong(totalBytes);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_PROGRESS, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadProgress(taskId, soFarBytes, totalBytes);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onDownloadCompleted(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_COMPLETED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadCompleted(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onDownloadPaused(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_PAUSED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadPaused(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onDownloadError(String taskId, int errorCode) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          data.writeInt(errorCode);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_DOWNLOAD_ERROR, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onDownloadError(taskId, errorCode);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onInstallPending(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_INSTALL_PENDING, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallPending(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onInstallStarted(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_INSTALL_STARTED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallStarted(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onInstallProgress(String taskId, float progress) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          data.writeFloat(progress);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_INSTALL_PROGRESS, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallProgress(taskId, progress);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onInstallCompleted(String taskId) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_INSTALL_COMPLETED, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallCompleted(taskId);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      @Override public void onInstallError(String taskId, int errorCode) throws android.os.RemoteException
      {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        try {
          data.writeInterfaceToken(DESCRIPTOR);
          data.writeString(taskId);
          data.writeInt(errorCode);
          boolean status = mRemote.transact(Stub.TRANSACTION_ON_INSTALL_ERROR, data, reply, 0);
          if (!status && getDefaultImpl() != null) {
            getDefaultImpl().onInstallError(taskId, errorCode);
            return;
          }
          reply.readException();
        }
        finally {
          reply.recycle();
          data.recycle();
        }
      }
      public static IArrangeCallback sDefaultImpl;
    }
    static final int TRANSACTION_ON_DOWNLOAD_PENDING = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_ON_DOWNLOAD_STARTED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_ON_DOWNLOAD_CONNECTED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_ON_DOWNLOAD_PROGRESS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_ON_DOWNLOAD_COMPLETED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_ON_DOWNLOAD_PAUSED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_ON_DOWNLOAD_ERROR = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_ON_INSTALL_PENDING = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_ON_INSTALL_STARTED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_ON_INSTALL_PROGRESS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_ON_INSTALL_COMPLETED = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_ON_INSTALL_ERROR = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    public static boolean setDefaultImpl(IArrangeCallback impl) {
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
    public static IArrangeCallback getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public void onDownloadPending(String taskId) throws android.os.RemoteException;
  public void onDownloadStarted(String taskId) throws android.os.RemoteException;
  public void onDownloadConnected(String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException;
  public void onDownloadProgress(String taskId, long soFarBytes, long totalBytes) throws android.os.RemoteException;
  public void onDownloadCompleted(String taskId) throws android.os.RemoteException;
  public void onDownloadPaused(String taskId) throws android.os.RemoteException;
  public void onDownloadError(String taskId, int errorCode) throws android.os.RemoteException;
  public void onInstallPending(String taskId) throws android.os.RemoteException;
  public void onInstallStarted(String taskId) throws android.os.RemoteException;
  public void onInstallProgress(String taskId, float progress) throws android.os.RemoteException;
  public void onInstallCompleted(String taskId) throws android.os.RemoteException;
  public void onInstallError(String taskId, int errorCode) throws android.os.RemoteException;
}
