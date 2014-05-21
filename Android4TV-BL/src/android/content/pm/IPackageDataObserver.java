package android.content.pm;

public interface IPackageDataObserver extends android.os.IInterface {
    public abstract static class Stub extends android.os.Binder implements
            android.content.pm.IPackageDataObserver {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static android.content.pm.IPackageDataObserver asInterface(
                android.os.IBinder obj) {
            throw new RuntimeException("Stub!");
        }

        public android.os.IBinder asBinder() {
            throw new RuntimeException("Stub!");
        }

        public boolean onTransact(int code, android.os.Parcel data,
                android.os.Parcel reply, int flags)
                throws android.os.RemoteException {
            throw new RuntimeException("Stub!");
        }
    }

    public abstract void onRemoveCompleted(final String packageName,
            final boolean succeeded) throws android.os.RemoteException;
}