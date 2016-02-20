package cn.openwatch.internal.watchface;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

//反编译自wearable ui lib 1.2
@TargetApi(21)
public abstract class WatchFaceService extends WallpaperService {
    public static final String COMMAND_AMBIENT_UPDATE = "com.google.android.wearable.action.AMBIENT_UPDATE";
    public static final String COMMAND_BACKGROUND_ACTION = "com.google.android.wearable.action.BACKGROUND_ACTION";
    public static final String COMMAND_SET_PROPERTIES = "com.google.android.wearable.action.SET_PROPERTIES";
    public static final String COMMAND_SET_BINDER = "com.google.android.wearable.action.SET_BINDER";
    public static final String COMMAND_REQUEST_STYLE = "com.google.android.wearable.action.REQUEST_STYLE";
    public static final String ACTION_REQUEST_STATE = "com.google.android.wearable.watchfaces.action.REQUEST_STATE";
    public static final String EXTRA_CARD_LOCATION = "card_location";
    public static final String EXTRA_AMBIENT_MODE = "ambient_mode";
    public static final String EXTRA_INTERRUPTION_FILTER = "interruption_filter";
    public static final String EXTRA_UNREAD_COUNT = "unread_count";
    public static final String EXTRA_BINDER = "binder";
    public static final String PROPERTY_BURN_IN_PROTECTION = "burn_in_protection";
    public static final String PROPERTY_LOW_BIT_AMBIENT = "low_bit_ambient";
    public static final int INTERRUPTION_FILTER_ALL = 1;
    public static final int INTERRUPTION_FILTER_PRIORITY = 2;
    public static final int INTERRUPTION_FILTER_NONE = 3;

    public abstract Engine onCreateEngine();

    public abstract class Engine extends WallpaperService.Engine {
        private final IntentFilter mAmbientTimeTickFilter;
        private final IntentFilter mInteractiveTimeTickFilter;
        private final BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (Log.isLoggable("WatchFaceService", 3)) {
                    Log.d("WatchFaceService", "Received intent that triggers onTimeTick for: " + intent);
                }
                WatchFaceService.Engine.this.onTimeTick();
            }
        };
        private boolean mTimeTickRegistered = false;
        private IWatchFaceService mWatchFaceService;
        private WatchFaceStyle mWatchFaceStyle;
        private WatchFaceStyle mLastWatchFaceStyle;
        private boolean mInAmbientMode;
        private int mInterruptionFilter;
        private int mUnreadCount;

        public Engine() {
            super();
            this.mAmbientTimeTickFilter = new IntentFilter();
            this.mAmbientTimeTickFilter.addAction("android.intent.action.DATE_CHANGED");
            this.mAmbientTimeTickFilter.addAction("android.intent.action.TIME_SET");
            this.mAmbientTimeTickFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            this.mInteractiveTimeTickFilter = new IntentFilter(this.mAmbientTimeTickFilter);
            this.mInteractiveTimeTickFilter.addAction("android.intent.action.TIME_TICK");
        }

        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            if (Log.isLoggable("WatchFaceService", 3)) {
                Log.d("WatchFaceService", "received command: " + action);
            }
            if ("com.google.android.wearable.action.BACKGROUND_ACTION".equals(action)) {
                maybeUpdateAmbientMode(extras);
                maybeUpdateInterruptionFilter(extras);
                maybeUpdatePeekCardPosition(extras);
                maybeUpdateUnreadCount(extras);
            } else if ("com.google.android.wearable.action.AMBIENT_UPDATE".equals(action)) {
                if (this.mInAmbientMode) {
                    if (Log.isLoggable("WatchFaceService", 3)) {
                        Log.d("WatchFaceService", "ambient mode update");
                    }
                    this.mWakeLock.acquire();
                    onTimeTick();
                    this.mWakeLock.acquire(100L);
                }
            } else if ("com.google.android.wearable.action.SET_PROPERTIES".equals(action)) {
                onPropertiesChanged(extras);
            } else if ("com.google.android.wearable.action.SET_BINDER".equals(action)) {
                onSetBinder(extras);
            } else if ("com.google.android.wearable.action.REQUEST_STYLE".equals(action)) {
                if (this.mLastWatchFaceStyle != null) {
                    setWatchFaceStyle(this.mLastWatchFaceStyle);
                } else if (Log.isLoggable("WatchFaceService", 3)) {
                    Log.d("WatchFaceService", "Last watch face style is null.");
                }
            }
            return null;
        }

        private void onSetBinder(Bundle extras) {
            IBinder binder = extras.getBinder("binder");
            if (binder != null) {
                this.mWatchFaceService = IWatchFaceService.Stub.asInterface(binder);
                if (this.mWatchFaceStyle != null) {
                    try {
                        this.mWatchFaceService.setStyle(this.mWatchFaceStyle);
                        this.mWatchFaceStyle = null;
                    } catch (RemoteException e) {
                        Log.w("WatchFaceService", "Failed to set WatchFaceStyle", e);
                    }
                }
            } else {
                Log.w("WatchFaceService", "Binder is null.");
            }
        }

        private final Rect mPeekCardPosition = new Rect(0, 0, 0, 0);
        private PowerManager.WakeLock mWakeLock;

        public void setWatchFaceStyle(WatchFaceStyle watchFaceStyle) {
            if (Log.isLoggable("WatchFaceService", 3)) {
                Log.d("WatchFaceService", "setWatchFaceStyle " + watchFaceStyle);
            }
            this.mWatchFaceStyle = watchFaceStyle;
            this.mLastWatchFaceStyle = watchFaceStyle;
            if (this.mWatchFaceService != null) {
                try {
                    this.mWatchFaceService.setStyle(watchFaceStyle);
                    this.mWatchFaceStyle = null;
                } catch (RemoteException e) {
                    Log.e("WatchFaceService", "Failed to set WatchFaceStyle: ", e);
                }
            }
        }

        public void onAmbientModeChanged(boolean inAmbientMode) {
        }

        public void onInterruptionFilterChanged(int interruptionFilter) {
        }

        public void onPeekCardPositionUpdate(Rect rect) {
        }

        public void onUnreadCountChanged(int count) {
        }

        public void onPropertiesChanged(Bundle properties) {
        }

        public void onTimeTick() {
        }

        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            this.mWatchFaceStyle = new WatchFaceStyle.Builder(WatchFaceService.this).build();
            this.mWakeLock = ((PowerManager) WatchFaceService.this.getSystemService("power")).newWakeLock(1,
                    "WatchFaceService");

            this.mWakeLock.setReferenceCounted(false);
        }

        public void onDestroy() {
            if (this.mTimeTickRegistered) {
                this.mTimeTickRegistered = false;
                WatchFaceService.this.unregisterReceiver(this.mTimeTickReceiver);
            }
            super.onDestroy();
        }

        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (Log.isLoggable("WatchFaceService", 3)) {
                Log.d("WatchFaceService", "onVisibilityChanged: " + visible);
            }
            Intent intent = new Intent("com.google.android.wearable.watchfaces.action.REQUEST_STATE");
            WatchFaceService.this.sendBroadcast(intent);

            updateTimeTickReceiver();
        }

        public final boolean isInAmbientMode() {
            return this.mInAmbientMode;
        }

        public final int getInterruptionFilter() {
            return this.mInterruptionFilter;
        }

        public final int getUnreadCount() {
            return this.mUnreadCount;
        }

        public final Rect getPeekCardPosition() {
            return this.mPeekCardPosition;
        }

        private void maybeUpdateInterruptionFilter(Bundle bundle) {
            if (bundle.containsKey("interruption_filter")) {
                int interruptionFilter = bundle.getInt("interruption_filter", 1);
                if (interruptionFilter != this.mInterruptionFilter) {
                    this.mInterruptionFilter = interruptionFilter;
                    onInterruptionFilterChanged(interruptionFilter);
                }
            }
        }

        private void maybeUpdatePeekCardPosition(Bundle bundle) {
            if (bundle.containsKey("card_location")) {
                Rect rect = Rect.unflattenFromString(bundle.getString("card_location"));
                if (!rect.equals(this.mPeekCardPosition)) {
                    this.mPeekCardPosition.set(rect);
                    onPeekCardPositionUpdate(rect);
                }
            }
        }

        private void maybeUpdateAmbientMode(Bundle bundle) {
            if (bundle.containsKey("ambient_mode")) {
                boolean inAmbientMode = bundle.getBoolean("ambient_mode", false);
                if (this.mInAmbientMode != inAmbientMode) {
                    this.mInAmbientMode = inAmbientMode;
                    dispatchAmbientModeChanged();
                }
            }
        }

        private void dispatchAmbientModeChanged() {
            if (Log.isLoggable("WatchFaceService", 3)) {
                Log.d("WatchFaceService", "dispatchAmbientModeChanged: " + this.mInAmbientMode);
            }
            onAmbientModeChanged(this.mInAmbientMode);
            updateTimeTickReceiver();
        }

        private void maybeUpdateUnreadCount(Bundle bundle) {
            if (bundle.containsKey("unread_count")) {
                int unreadCount = bundle.getInt("unread_count", 0);
                if (unreadCount != this.mUnreadCount) {
                    this.mUnreadCount = unreadCount;
                    onUnreadCountChanged(this.mUnreadCount);
                }
            }
        }

        private void updateTimeTickReceiver() {
            if (Log.isLoggable("WatchFaceService", 3)) {
                Log.d("WatchFaceService", "updateTimeTickReceiver: " + this.mTimeTickRegistered + " -> (" + isVisible()
                        + ", " + this.mInAmbientMode + ")");
            }
            if (this.mTimeTickRegistered) {
                WatchFaceService.this.unregisterReceiver(this.mTimeTickReceiver);
                this.mTimeTickRegistered = false;
            }
            if (isVisible()) {
                if (this.mInAmbientMode) {
                    WatchFaceService.this.registerReceiver(this.mTimeTickReceiver, this.mAmbientTimeTickFilter);
                } else {
                    WatchFaceService.this.registerReceiver(this.mTimeTickReceiver, this.mInteractiveTimeTickFilter);
                }
                this.mTimeTickRegistered = true;

                onTimeTick();
            }
        }
    }
}
