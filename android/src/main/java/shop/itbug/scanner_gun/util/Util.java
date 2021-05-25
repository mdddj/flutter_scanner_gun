package shop.itbug.scanner_gun.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Util {
    public static final Executor EXECUTOR = Executors.newCachedThreadPool();
}
