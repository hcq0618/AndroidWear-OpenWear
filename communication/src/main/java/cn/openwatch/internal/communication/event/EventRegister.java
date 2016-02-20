package cn.openwatch.internal.communication.event;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.concurrent.CopyOnWriteArraySet;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.listener.ConnectListener;
import cn.openwatch.communication.listener.DataListener;
import cn.openwatch.communication.listener.MessageListener;
import cn.openwatch.communication.listener.SpecialTypeListener;
import cn.openwatch.internal.communication.ClientManager;

/**
 * 配对设备间设备连接和数据通信的的状态监听
 */
public final class EventRegister {

    private CopyOnWriteArraySet<Object> listeners = new CopyOnWriteArraySet<Object>();

    private static EventRegister instance;

    private AbsEventHandler userEventHandler, internalEventHandler;

    private EventRegister() {
    }

    public static EventRegister getInstance() {
        if (instance == null) {
            synchronized (EventRegister.class) {
                if (instance == null)
                    instance = new EventRegister();

            }
        }

        return instance;
    }

    public void initInternalEventHandlerIfNeed(Context cx) {
        // TODO Auto-generated method stub
        if (internalEventHandler == null) {
            synchronized (EventRegister.class) {
                if (internalEventHandler == null) {
                    internalEventHandler = new InternalEventHandler().setContext(cx);
                    putExtraEventHandler(cx, internalEventHandler);
                }
            }
        }
    }

    public void putExtraEventHandler(Context cx, AbsEventHandler otherEventHandler) {
        // TODO Auto-generated method stub
        if (otherEventHandler != null)
            ClientManager.getInstance().putEventHandler(cx, otherEventHandler);
    }

    private void initUserHandlerIfNeed(Context cx) {
        // TODO Auto-generated method stub
        if (userEventHandler == null) {
            synchronized (EventRegister.class) {
                if (userEventHandler == null) {
                    userEventHandler = new UserEventHandler().setEventCallback(new EventCallbackProxy());
                    userEventHandler.setContext(cx);
                    putExtraEventHandler(cx, userEventHandler);
                }
            }
        }
    }

    public void addListener(Context context, Object listener) {
        if (listener != null) {

            listeners.add(listener);
            initUserHandlerIfNeed(context);

        }
    }

    public void removeListener(Object listener) {
        if (listener != null) {
            listeners.remove(listener);

            if (listeners.size() == 0) {
                if (userEventHandler != null) {
                    synchronized (EventRegister.class) {
                        if (userEventHandler != null) {
                            ClientManager.getInstance().removeEventHandler(userEventHandler);
                            userEventHandler = null;
                        }
                    }
                }
            }

            ClientManager.getInstance().tryDisconnect();
        }
    }

    public void addDataListener(Context context) {

        if (context != null) {
            if (context instanceof DataListener) {
                addListener(context, context);
            } else {
                throw new IllegalArgumentException("context must impl DataListener interface");
            }
        }
    }

    public void addConnectListener(Context context) {

        if (context != null) {
            if (context instanceof ConnectListener)
                addListener(context, context);
            else
                throw new IllegalArgumentException("context must impl ConnectListener interface");
        }
    }

    public void addMessageListener(Context context) {

        if (context != null) {
            if (context instanceof MessageListener)
                addListener(context, context);
            else
                throw new IllegalArgumentException("context must impl MessageListener interface");
        }
    }

    public void addSpecialTypeListener(Context context) {

        if (context != null) {
            if (context instanceof SpecialTypeListener)
                addListener(context, context);
            else
                throw new IllegalArgumentException("context must impl SpecialDataListener interface");
        }
    }

    private final class EventCallbackProxy extends EventCallback {

        @Override
        public void callbackBitmap(String path, Bitmap bitmap) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof SpecialTypeListener) {
                    ((SpecialTypeListener) listener).onBitmapReceived(path, bitmap);
                }
            }
        }

        @Override
        public void callbackFile(SpecialData data) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof SpecialTypeListener) {
                    SpecialTypeListener specialTypeListener = ((SpecialTypeListener) listener);
                    data.setListener(specialTypeListener);
                    specialTypeListener.onFileReceived(data);
                }
            }
        }

        @Override
        public void callbackStream(SpecialData data) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof SpecialTypeListener) {
                    SpecialTypeListener specialTypeListener = ((SpecialTypeListener) listener);
                    data.setListener(specialTypeListener);
                    specialTypeListener.onStreamReceived(data);
                }
            }
        }

        @Override
        public void callInputClosed(String path) {
            for (Object listener : listeners) {
                if (listener instanceof SpecialTypeListener) {
                    ((SpecialTypeListener) listener).onInputClosed(path);
                }
            }
        }

        @Override
        public void callbackDataMap(String path, DataMap dataMap) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof DataListener) {
                    ((DataListener) listener).onDataMapReceived(path, dataMap);
                }
            }
        }

        @Override
        public void callbackData(String path, byte[] rawData) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof DataListener) {
                    ((DataListener) listener).onDataReceived(path, rawData);
                }
            }
        }

        @Override
        public void callbackDataDeleted(String path) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof DataListener) {
                    ((DataListener) listener).onDataDeleted(path);
                }
            }
        }

        @Override
        public void callbackMessage(String path, byte[] rawData) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof MessageListener) {
                    ((MessageListener) listener).onMessageReceived(path, rawData);
                }
            }
        }

        @Override
        public void callbackPeerConnected(String displayName, String nodeId) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof ConnectListener) {
                    ((ConnectListener) listener).onPeerConnected(displayName, nodeId);
                }
            }
        }

        @Override
        public void callbackPeerDisconnected(String displayName, String nodeId) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof ConnectListener) {
                    ((ConnectListener) listener).onPeerDisconnected(displayName, nodeId);
                }
            }
        }

        @Override
        public void callbackServiceConnectionSuspended(int cause) {
            // TODO Auto-generated method stub
            for (Object listener : listeners) {
                if (listener instanceof ConnectListener) {
                    ((ConnectListener) listener).onServiceConnectionSuspended(cause);
                }
            }
        }

        @Override
        public void callbackServiceConnected() {
            for (Object listener : listeners) {
                if (listener instanceof ConnectListener) {
                    ((ConnectListener) listener).onServiceConnected();
                }
            }
        }
    }

}
