package com.example.oriedita_common.editor.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Утилиты для работы с ресурсами в Android приложении
 * Предоставляет методы для работы с файлами конфигурации, версиями и настройками
 */
public class ResourceUtil {

    /**
     * Получает директорию приложения для хранения данных
     * В Android используется внутренняя директория приложения
     * 
     * @param context контекст приложения
     * @return директория для хранения данных приложения
     */
    public static File getAppDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * Получает директорию приложения (устаревший метод для совместимости)
     * 
     * @return директория для хранения данных приложения
     * @deprecated используйте getAppDir(Context context)
     */
    @Deprecated
    public static File getAppDir() {
        // Возвращаем временную директорию как fallback
        return new File(System.getProperty("java.io.tmpdir"), "oriedita");
    }

    /**
     * Получает временную директорию приложения
     * 
     * @param context контекст приложения
     * @return временная директория приложения
     */
    public static File getTempDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * Получает временную директорию (устаревший метод для совместимости)
     * 
     * @return временная директория
     * @deprecated используйте getTempDir(Context context)
     */
    @Deprecated
    public static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"), "oriedita");
    }

    /**
     * Получает версию приложения из Android манифеста
     * 
     * @param context контекст приложения
     * @return версия приложения или "dev" при ошибке
     */
    public static String getVersionFromManifest(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("ResourceUtil", "Не удалось получить версию приложения", e);
        }
        return "dev";
    }

    /**
     * Получает версию приложения (устаревший метод для совместимости)
     * 
     * @return версия приложения или "dev" при ошибке
     * @deprecated используйте getVersionFromManifest(Context context)
     */
    @Deprecated
    public static String getVersionFromManifest() {
        return "dev";
    }

    /**
     * Пустой ResourceBundle для случаев, когда файл не найден
     */
    private static ResourceBundle emptyResourceBundle = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[0][];
        }
    };
    
    /**
     * Кэш пользовательских ResourceBundle
     */
    private static Map<String, ResourceBundle> userBundleCache = new ConcurrentHashMap<>();
    
    /**
     * Кэш локальных ResourceBundle
     */
    private static Map<String, ResourceBundle> localBundleCache = new ConcurrentHashMap<>();


    /**
     * Читает строку из одного из трех источников:
     * 1. Пытается найти properties файл с ключом в локальной директории
     * 2. Пытается найти properties файл с ключом в директории приложения
     * 3. Пытается прочитать ключ из properties файла в ресурсах приложения
     *
     * @param context контекст приложения
     * @param bundle имя properties файла для загрузки
     * @param key ключ для чтения в properties файле
     * @return значение ключа или null, если не найдено
     */
    public static String getBundleString(Context context, String bundle, String key) {
        // Пытаемся загрузить из локальной директории
        ResourceBundle localBundle = localBundleCache.computeIfAbsent(bundle, (b) -> {
            try {
                File localFile = new File(bundle + ".properties");
                if (localFile.exists()) {
                    return new PropertyResourceBundle(new FileInputStream(localFile));
                }
            } catch (IOException e) {
                Log.w("ResourceUtil", "Не удалось загрузить локальный bundle: " + bundle, e);
            }
            return emptyResourceBundle;
        });

        if (localBundle.containsKey(key)) {
            return localBundle.getString(key);
        }

        // Пытаемся загрузить из директории приложения
        ResourceBundle userBundle = userBundleCache.computeIfAbsent(bundle, (b) -> {
            try {
                File appFile = new File(getAppDir(context), bundle + ".properties");
                if (appFile.exists()) {
                    return new PropertyResourceBundle(new FileInputStream(appFile));
                }
            } catch (IOException e) {
                Log.w("ResourceUtil", "Не удалось загрузить пользовательский bundle: " + bundle, e);
            }
            return emptyResourceBundle;
        });

        if (userBundle.containsKey(key)) {
            return userBundle.getString(key);
        }

        // Пытаемся загрузить из ресурсов приложения
        try {
            ResourceBundle jarBundle = ResourceBundle.getBundle(bundle);
            if (jarBundle.containsKey(key)) {
                return jarBundle.getString(key);
            }
        } catch (MissingResourceException ignored) {
            // Игнорируем, если ресурс не найден
        }

        Log.d("ResourceUtil", bundle + "." + key + " не существует");
        return null;
    }

    /**
     * Читает строку из bundle (устаревший метод для совместимости)
     *
     * @param bundle имя properties файла для загрузки
     * @param key ключ для чтения в properties файле
     * @return значение ключа или null, если не найдено
     * @deprecated используйте getBundleString(Context context, String bundle, String key)
     */
    @Deprecated
    public static String getBundleString(String bundle, String key) {
        Log.w("ResourceUtil", "Используется устаревший метод getBundleString без Context");
        return null;
    }

    /**
     * Получает значение горячих клавиш по умолчанию из ресурсов приложения
     * 
     * @param key ключ для чтения в hotkey bundle
     * @return значение ключа или null, если не найдено
     */
    public static String getDefaultHotkeyBundleString(String key) {
        ResourceBundle jarBundle = null;
        try {
            jarBundle = ResourceBundle.getBundle("hotkey");
        } catch (MissingResourceException ignored) {
            // Игнорируем, если ресурс не найден
        }

        if (jarBundle != null && jarBundle.containsKey(key)) {
            return jarBundle.getString(key);
        }

        Log.d("ResourceUtil", key + " не существует (ПО УМОЛЧАНИЮ)");
        return null;
    }

    /**
     * Обновляет ключ в bundle файле
     * 
     * @param context контекст приложения
     * @param bundleName имя bundle файла
     * @param key ключ для обновления
     * @param value новое значение (null для удаления)
     */
    public static void updateBundleKey(Context context, String bundleName, String key, String value) {
        try {
            localBundleCache.remove(bundleName);
            userBundleCache.remove(bundleName);
            ResourceBundle.clearCache();

            File bundleLocation = new File(getAppDir(context), bundleName + ".properties");
            if (!bundleLocation.exists() && !bundleLocation.createNewFile()) {
                throw new IOException("Не удалось создать файл");
            }
            
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(bundleLocation)) {
                properties.load(fis);
            }

            if (value == null) {
                properties.remove(key);
            } else {
                properties.setProperty(key, value);
            }

            try (FileOutputStream fos = new FileOutputStream(bundleLocation)) {
                properties.store(fos, null);
            }
        } catch (IOException e) {
            Log.e("ResourceUtil", "Ошибка при записи ключа bundle", e);
        }
    }

    /**
     * Обновляет ключ в bundle файле (устаревший метод для совместимости)
     * 
     * @param bundleName имя bundle файла
     * @param key ключ для обновления
     * @param value новое значение (null для удаления)
     * @deprecated используйте updateBundleKey(Context context, String bundleName, String key, String value)
     */
    @Deprecated
    public static void updateBundleKey(String bundleName, String key, String value) {
        Log.w("ResourceUtil", "Используется устаревший метод updateBundleKey без Context");
    }

    /**
     * Очищает bundle файл
     * 
     * @param context контекст приложения
     * @param bundleName имя bundle файла для очистки
     */
    public static void clearBundle(Context context, String bundleName) {
        try {
            localBundleCache.remove(bundleName);
            userBundleCache.remove(bundleName);
            ResourceBundle.clearCache();

            File bundleLocation = new File(getAppDir(context), bundleName + ".properties");
            if (!bundleLocation.exists() && !bundleLocation.createNewFile()) {
                throw new IOException("Не удалось создать файл");
            }
            
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(bundleLocation)) {
                properties.load(fis);
            }

            properties.clear();

            try (FileOutputStream fos = new FileOutputStream(bundleLocation)) {
                properties.store(fos, null);
            }
        } catch (IOException e) {
            Log.e("ResourceUtil", "Ошибка при очистке bundle", e);
        }
    }

    /**
     * Очищает bundle файл (устаревший метод для совместимости)
     * 
     * @param bundleName имя bundle файла для очистки
     * @deprecated используйте clearBundle(Context context, String bundleName)
     */
    @Deprecated
    public static void clearBundle(String bundleName) {
        Log.w("ResourceUtil", "Используется устаревший метод clearBundle без Context");
    }
}
