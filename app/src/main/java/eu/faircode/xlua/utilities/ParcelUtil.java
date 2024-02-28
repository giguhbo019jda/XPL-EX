package eu.faircode.xlua.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParcelUtil {
    public static void writeBool(Parcel in, Boolean bResult) {
        if(bResult == null) {
            //?
        }else {
            in.writeByte(bResult ? (byte)1 : (byte)0);
        }
    }

    public static Boolean readBool(Parcel in) {
        return in.readByte() == 1;
    }
    /*public final <T extends Parcelable> List<T> readParcelableList(List<T> list, Parcel p) {
        final int N = p.readInt();// readInt();
        if (N == -1) {
            list.clear();
            return list;
        }

        final int M = list.size();
        int i = 0;
        for (; i < M && i < N; i++) {
            //list.set(i, (T) readParcelable(cl));
            list.size(i, (T)p.readParcelable())
        }
        for (; i<N; i++) {
            list.add((T) readParcelable(cl));
        }
        for (; i<M; i++) {
            list.remove(N);
        }
        return list;
    }*/
}
