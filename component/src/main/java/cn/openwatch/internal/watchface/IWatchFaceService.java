package cn.openwatch.internal.watchface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

//反编译自wearable ui lib 1.2
public interface IWatchFaceService extends IInterface {
    void setStyle(WatchFaceStyle paramWatchFaceStyle) throws RemoteException;

    abstract class Stub extends Binder implements IWatchFaceService {
        static final int TRANSACTION_setStyle = 1;

        public Stub() {
            attachInterface(this, "android.support.wearable.watchface.IWatchFaceService");
        }

        public static IWatchFaceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("android.support.wearable.watchface.IWatchFaceService");
            if ((iin != null) && ((iin instanceof IWatchFaceService))) {
                return (IWatchFaceService) iin;
            }
            return new Proxy(obj);
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1598968902:
                    reply.writeString("android.support.wearable.watchface.IWatchFaceService");
                    return true;
                case 1:
                    data.enforceInterface("android.support.wearable.watchface.IWatchFaceService");
                    WatchFaceStyle _arg0;
                    if (0 != data.readInt()) {
                        _arg0 = WatchFaceStyle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setStyle(_arg0);
                    reply.writeNoException();
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IWatchFaceService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @SuppressWarnings("unused")
            public String getInterfaceDescriptor() {
                return "android.support.wearable.watchface.IWatchFaceService";
            }

            public void setStyle(WatchFaceStyle style) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.wearable.watchface.IWatchFaceService");
                    if (style != null) {
                        _data.writeInt(1);
                        style.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
