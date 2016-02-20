package cn.openwatch.internal.watchface;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

//反编译自wearable ui lib 1.2
@TargetApi(21)
public class WatchFaceStyle implements Parcelable {
    public static final int PEEK_MODE_VARIABLE = 0;
    public static final int PEEK_MODE_SHORT = 1;
    public static final int PROGRESS_MODE_NONE = 0;
    public static final int PROGRESS_MODE_DISPLAY = 1;
    public static final int PEEK_OPACITY_MODE_OPAQUE = 0;
    public static final int PEEK_OPACITY_MODE_TRANSLUCENT = 1;
    public static final int BACKGROUND_VISIBILITY_INTERRUPTIVE = 0;
    public static final int BACKGROUND_VISIBILITY_PERSISTENT = 1;
    public static final int AMBIENT_PEEK_MODE_VISIBLE = 0;
    public static final int AMBIENT_PEEK_MODE_HIDDEN = 1;
    public static final int PROTECT_STATUS_BAR = 1;
    public static final int PROTECT_HOTWORD_INDICATOR = 2;
    public static final int PROTECT_WHOLE_SCREEN = 4;
    public static final String KEY_COMPONENT = "component";
    public static final String KEY_CARD_PEEK_MODE = "cardPeekMode";
    public static final String KEY_CARD_PROGRESS_MODE = "cardProgressMode";
    public static final String KEY_BACKGROUND_VISIBILITY = "backgroundVisibility";
    public static final String KEY_SHOW_SYSTEM_UI_TIME = "showSystemUiTime";
    public static final String KEY_AMBIENT_PEEK_MODE = "ambientPeekMode";
    public static final String KEY_PEEK_CARD_OPACITY = "peekOpacityMode";
    public static final String KEY_VIEW_PROTECTION_MODE = "viewProtectionMode";
    public static final String KEY_STATUS_BAR_GRAVITY = "statusBarGravity";
    public static final String KEY_HOTWORD_INDICATOR_GRAVITY = "hotwordIndicatorGravity";
    public static final String KEY_SHOW_UNREAD_INDICATOR = "showUnreadIndicator";
    private final ComponentName component;
    private final int cardPeekMode;
    private final int cardProgressMode;
    private final int peekOpacityMode;
    private final int viewProtectionMode;
    private final int statusBarGravity;
    private final int hotwordIndicatorGravity;
    private final int backgroundVisibility;
    private final boolean showSystemUiTime;
    private final int ambientPeekMode;
    private final boolean showUnreadCountIndicator;

    private WatchFaceStyle(ComponentName component, int cardPeekMode, int cardProgressMode, int backgroundVisibility,
                           boolean showSystemUiTime, int ambientPeekMode, int peekOpacityMode, int viewProtectionMode,
                           int statusBarGravity, int hotwordIndicatorGravity, boolean showUnreadCountIndicator) {
        this.component = component;

        this.ambientPeekMode = ambientPeekMode;
        this.backgroundVisibility = backgroundVisibility;
        this.cardPeekMode = cardPeekMode;
        this.cardProgressMode = cardProgressMode;
        this.hotwordIndicatorGravity = hotwordIndicatorGravity;
        this.peekOpacityMode = peekOpacityMode;
        this.showSystemUiTime = showSystemUiTime;
        this.showUnreadCountIndicator = showUnreadCountIndicator;
        this.statusBarGravity = statusBarGravity;
        this.viewProtectionMode = viewProtectionMode;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBundle(toBundle());
    }

    public WatchFaceStyle(Bundle bundle) {
        this.component = bundle.getParcelable("component");

        this.ambientPeekMode = bundle.getInt("ambientPeekMode", 0);
        this.backgroundVisibility = bundle.getInt("backgroundVisibility", 1);

        this.cardPeekMode = bundle.getInt("cardPeekMode", 0);
        this.cardProgressMode = bundle.getInt("cardProgressMode", 0);
        this.hotwordIndicatorGravity = bundle.getInt("hotwordIndicatorGravity");
        this.peekOpacityMode = bundle.getInt("peekOpacityMode", 0);
        this.showSystemUiTime = bundle.getBoolean("showSystemUiTime");
        this.showUnreadCountIndicator = bundle.getBoolean("showUnreadIndicator");
        this.statusBarGravity = bundle.getInt("statusBarGravity");
        this.viewProtectionMode = bundle.getInt("viewProtectionMode");
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("component", this.component);

        bundle.putInt("ambientPeekMode", this.ambientPeekMode);
        bundle.putInt("backgroundVisibility", this.backgroundVisibility);
        bundle.putInt("cardPeekMode", this.cardPeekMode);
        bundle.putInt("cardProgressMode", this.cardProgressMode);
        bundle.putInt("hotwordIndicatorGravity", this.hotwordIndicatorGravity);
        bundle.putInt("peekOpacityMode", this.peekOpacityMode);
        bundle.putBoolean("showSystemUiTime", this.showSystemUiTime);
        bundle.putBoolean("showUnreadIndicator", this.showUnreadCountIndicator);
        bundle.putInt("statusBarGravity", this.statusBarGravity);
        bundle.putInt("viewProtectionMode", this.viewProtectionMode);
        return bundle;
    }

    public boolean equals(Object otherObj) {
        if ((otherObj == null) || (!(otherObj instanceof WatchFaceStyle))) {
            return false;
        }
        WatchFaceStyle other = (WatchFaceStyle) otherObj;
        return (this.component.equals(other.component)) && (this.cardPeekMode == other.cardPeekMode)
                && (this.cardProgressMode == other.cardProgressMode)
                && (this.backgroundVisibility == other.backgroundVisibility)
                && (this.showSystemUiTime == other.showSystemUiTime) && (this.ambientPeekMode == other.ambientPeekMode)
                && (this.peekOpacityMode == other.peekOpacityMode)
                && (this.viewProtectionMode == other.viewProtectionMode)
                && (this.statusBarGravity == other.statusBarGravity)
                && (this.hotwordIndicatorGravity == other.hotwordIndicatorGravity)
                && (this.showUnreadCountIndicator == other.showUnreadCountIndicator);
    }

    public int hashCode() {
        int[] values = {this.component.hashCode(), this.cardPeekMode, this.cardProgressMode, this.backgroundVisibility,
                this.showSystemUiTime ? 1 : 0, this.ambientPeekMode, this.peekOpacityMode, this.viewProtectionMode,
                this.statusBarGravity, this.hotwordIndicatorGravity, this.showUnreadCountIndicator ? 1 : 0};

        return Arrays.hashCode(values);
    }

    public String toString() {
        return String.format(
                "watch face %s (card %d/%d bg %d time %s ambientPeek %d peekOpacityMode %d viewProtectionMode %d  statusBarGravity %d hotwordIndicatorGravity %d showUnreadCountIndicator %s)",
                this.component == null ? "default" : this.component.getShortClassName(),
                Integer.valueOf(this.cardPeekMode), Integer.valueOf(this.cardProgressMode),
                Integer.valueOf(this.backgroundVisibility), Boolean.valueOf(this.showSystemUiTime),
                Integer.valueOf(this.ambientPeekMode), Integer.valueOf(this.peekOpacityMode),
                Integer.valueOf(this.viewProtectionMode), Integer.valueOf(this.statusBarGravity),
                Integer.valueOf(this.hotwordIndicatorGravity),
                Boolean.valueOf(this.showUnreadCountIndicator));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final Creator<WatchFaceStyle> CREATOR = new Creator() {
        public WatchFaceStyle createFromParcel(Parcel p) {
            return new WatchFaceStyle(p.readBundle());
        }

        public WatchFaceStyle[] newArray(int size) {
            return new WatchFaceStyle[size];
        }
    };

    public ComponentName getComponent() {
        return this.component;
    }

    public int getCardPeekMode() {
        return this.cardPeekMode;
    }

    public int getCardProgressMode() {
        return this.cardProgressMode;
    }

    public int getPeekOpacityMode() {
        return this.peekOpacityMode;
    }

    public int getViewProtectionMode() {
        return this.viewProtectionMode;
    }

    public int getStatusBarGravity() {
        return this.statusBarGravity;
    }

    public int getHotwordIndicatorGravity() {
        return this.hotwordIndicatorGravity;
    }

    public int getBackgroundVisibility() {
        return this.backgroundVisibility;
    }

    public boolean getShowSystemUiTime() {
        return this.showSystemUiTime;
    }

    public int getAmbientPeekMode() {
        return this.ambientPeekMode;
    }

    public boolean getShowUnreadCountIndicator() {
        return this.showUnreadCountIndicator;
    }

    public static class Builder {
        private final ComponentName mComponent;
        private int mCardPeekMode = 0;
        private int mCardProgressMode = 0;
        private int mBackgroundVisibility = 0;
        private boolean mShowSystemUiTime = false;
        private int mAmbientPeekMode = 0;
        private int mPeekOpacityMode = 0;
        private int mViewProtectionMode = 0;
        private int mStatusBarGravity = 0;
        private int mHotwordIndicatorGravity = 0;
        private boolean mShowUnreadCountIndicator = false;

        public static Builder forComponentName(ComponentName component) {
            if (component == null) {
                throw new IllegalArgumentException("component must not be null.");
            }
            return new Builder(component);
        }

        public static Builder forActivity(Activity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("activity must not be null.");
            }
            return new Builder(new ComponentName(activity, activity.getClass()));
        }

        public Builder(Service service) {
            this(new ComponentName(service, service.getClass()));
        }

        public static Builder forDefault() {
            return new Builder((ComponentName) null);
        }

        private Builder(ComponentName component) {
            this.mComponent = component;
        }

        public Builder setCardPeekMode(int peekMode) {
            switch (peekMode) {
                case 0:
                case 1:
                    this.mCardPeekMode = peekMode;
                    return this;
            }
            throw new IllegalArgumentException("peekMode must be PEEK_MODE_VARIABLE or PEEK_MODE_SHORT");
        }

        public Builder setCardProgressMode(int progressMode) {
            switch (progressMode) {
                case 0:
                case 1:
                    this.mCardProgressMode = progressMode;
                    return this;
            }
            throw new IllegalArgumentException("progressMode must be PROGRESS_MODE_NONE or PROGRESS_MODE_DISPLAY");
        }

        public Builder setBackgroundVisibility(int backgroundVisibility) {
            switch (backgroundVisibility) {
                case 0:
                case 1:
                    this.mBackgroundVisibility = backgroundVisibility;
                    return this;
            }
            throw new IllegalArgumentException(
                    "backgroundVisibility must be BACKGROUND_VISIBILITY_INTERRUPTIVE or BACKGROUND_VISIBILITY_PERSISTENT");
        }

        public Builder setShowSystemUiTime(boolean showSystemUiTime) {
            this.mShowSystemUiTime = showSystemUiTime;
            return this;
        }

        public Builder setAmbientPeekMode(int ambientPeekMode) {
            switch (ambientPeekMode) {
                case 0:
                case 1:
                    this.mAmbientPeekMode = ambientPeekMode;
                    return this;
            }
            throw new IllegalArgumentException(
                    "Ambient peek mode must be AMBIENT_PEEK_MODE_VISIBLE or AMBIENT_PEEK_MODE_HIDDEN");
        }

        public Builder setPeekOpacityMode(int peekOpacityMode) {
            switch (peekOpacityMode) {
                case 0:
                case 1:
                    this.mPeekOpacityMode = peekOpacityMode;
                    return this;
            }
            throw new IllegalArgumentException(
                    "Peek card opacity must be PEEK_OPACITY_MODE_OPAQUE or PEEK_OPACITY_MODE_TRANSLUCENT");
        }

        @Deprecated
        public Builder setViewProtection(int viewProtection) {
            return setViewProtectionMode(viewProtection);
        }

        public Builder setViewProtectionMode(int viewProtectionMode) {
            if ((viewProtectionMode < 0) || (viewProtectionMode > 7)) {
                throw new IllegalArgumentException(
                        "View protection must be combination PROTECT_STATUS_BAR, PROTECT_HOTWORD_INDICATOR or PROTECT_WHOLE_SCREEN");
            }
            this.mViewProtectionMode = viewProtectionMode;
            return this;
        }

        public Builder setStatusBarGravity(int statusBarGravity) {
            this.mStatusBarGravity = statusBarGravity;
            return this;
        }

        public Builder setHotwordIndicatorGravity(int hotwordIndicatorGravity) {
            this.mHotwordIndicatorGravity = hotwordIndicatorGravity;
            return this;
        }

        public Builder setShowUnreadCountIndicator(boolean show) {
            this.mShowUnreadCountIndicator = show;
            return this;
        }

        public WatchFaceStyle build() {
            return new WatchFaceStyle(this.mComponent, this.mCardPeekMode, this.mCardProgressMode,
                    this.mBackgroundVisibility, this.mShowSystemUiTime, this.mAmbientPeekMode, this.mPeekOpacityMode,
                    this.mViewProtectionMode, this.mStatusBarGravity, this.mHotwordIndicatorGravity,
                    this.mShowUnreadCountIndicator);
        }
    }
}
