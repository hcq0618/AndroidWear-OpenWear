package cn.openwatch.internal.watchface;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.Choreographer;
import android.view.SurfaceHolder;

//反编译自wearable ui lib 1.2
@TargetApi(21)
public abstract class CanvasWatchFaceService extends WatchFaceService {

    public Engine onCreateEngine() {
        return new Engine();
    }

    public class Engine extends WatchFaceService.Engine {
        private boolean mDrawRequested;
        private boolean mDestroyed;

        public Engine() {
            super();
        }

        private Choreographer mChoreographer = Choreographer.getInstance();
        private final Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
            public void doFrame(long frameTimeNs) {
                if (CanvasWatchFaceService.Engine.this.mDestroyed) {
                    return;
                }
                if (CanvasWatchFaceService.Engine.this.mDrawRequested) {
                    CanvasWatchFaceService.Engine.this.draw(CanvasWatchFaceService.Engine.this.getSurfaceHolder());
                }
            }
        };
        private final Handler mHandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        CanvasWatchFaceService.Engine.this.invalidate();
                }
            }
        };

        public void onDestroy() {
            this.mDestroyed = true;
            this.mHandler.removeMessages(0);
            this.mChoreographer.removeFrameCallback(this.mFrameCallback);
            super.onDestroy();
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            invalidate();
        }

        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
            draw(holder);
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            invalidate();
        }

        public void invalidate() {
            if (!this.mDrawRequested) {
                this.mDrawRequested = true;
                this.mChoreographer.postFrameCallback(this.mFrameCallback);
            }
        }

        public void postInvalidate() {
            this.mHandler.sendEmptyMessage(0);
        }

        public void onDraw(Canvas canvas, Rect bounds) {
        }

        private void draw(SurfaceHolder holder) {
            this.mDrawRequested = false;
            Canvas canvas = holder.lockCanvas();
            if (canvas == null) {
                return;
            }
            try {
                onDraw(canvas, holder.getSurfaceFrame());
            } finally {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
