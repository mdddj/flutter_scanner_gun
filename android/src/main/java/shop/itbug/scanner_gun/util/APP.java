package shop.itbug.scanner_gun.util;

import android.app.Application;
import android.os.Handler;

public class APP extends Application {
    private static final Handler sHandler = new Handler();


    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
}
