package cn.openwatch.internal.communication;

import android.util.SparseArray;

public final class SupportClient {

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_ANDROID_WEAR = 0;
    public static final int TYPE_TICWEAR = 1;
    public static final int TYPE_DUWEAR = 2;
    public static final int TYPE_ANDROID_WEAR_CHINA = 3;

    public static final String TYPENAME_ANDROID_WEAR = "android_wear";
    public static final String TYPENAME_ANDROID_WEAR_CHINA = "android_wear_china";
    public static final String TYPENAME_TICWEAR = "ticwear";
    public static final String TYPENAME_DUWEAR = "duwear";

    protected String className;
    protected String systemCharacter;

    private SupportClient(String className, String systemCharacter) {
        this.className = className;
        this.systemCharacter = systemCharacter;
    }

    private static SupportClient createAwClient() {
        return new SupportClient("cn.openwatch.internal.communication.os.google.AwApiClient", null);
    }

    private static SupportClient createAwChinaClient() {
        return new SupportClient("cn.openwatch.internal.communication.os.google.china.AwApiClient", null);
    }

    private static SupportClient createTicWearClient() {
        return new SupportClient("cn.openwatch.internal.communication.os.ticwear.TicwearApiClient",
                "ticwear.version.name");
    }

    private static SupportClient createDuWearClient() {
        return new SupportClient("cn.openwatch.internal.communication.os.duwear.DuwearApiClient",
                "ro.baidu.build.software");
    }

    public static SparseArray<SupportClient> getSupportClientMap() {
        SparseArray<SupportClient> supportClientMap = new SparseArray<SupportClient>();

        supportClientMap.put(SupportClient.TYPE_ANDROID_WEAR, createAwClient());
        supportClientMap.put(SupportClient.TYPE_TICWEAR, createTicWearClient());
        supportClientMap.put(SupportClient.TYPE_DUWEAR, createDuWearClient());
        supportClientMap.put(SupportClient.TYPE_ANDROID_WEAR_CHINA, createAwChinaClient());

        return supportClientMap;
    }
}