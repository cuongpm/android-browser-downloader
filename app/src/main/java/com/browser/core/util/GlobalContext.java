package com.browser.core.util;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

public class GlobalContext {

    private static Context sContext;

    protected GlobalContext() {
        // disallow public access
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        if (sContext == null) {
            sContext = CurrentApplicationHolder.INSTANCE;
        }
        return sContext;
    }

    static class CurrentApplicationHolder {

        static final Application INSTANCE;

        static {
            try {
                Class<?> clazz = Class.forName("android.app.ActivityThread");
                Method method = ReflectUtil.getMethod(clazz, "currentApplication");
                INSTANCE = (Application) ReflectUtil.invokeStaticMethod(method);
            } catch (Throwable ex) {
                throw new AssertionError(ex);
            }
        }
    }

}
