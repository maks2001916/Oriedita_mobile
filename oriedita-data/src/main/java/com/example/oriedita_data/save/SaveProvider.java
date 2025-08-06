package com.example.oriedita_data.save;

/**
 * Провайдер для создания экземпляров Save
 * Обеспечивает создание сохранений различных версий
 */
public class SaveProvider {
    
    /**
     * Создает экземпляр Save последней версии
     * @return новый экземпляр SaveV1_1
     */
    public static Save createInstance() {
        return new SaveV1_1();
    }
    
    /**
     * Создает экземпляр Save указанной версии
     * @param version версия для создания ("v1", "v1.1")
     * @return новый экземпляр Save указанной версии
     * @throws IllegalArgumentException если версия не поддерживается
     */
    public static Save createInstance(String version) {
        switch (version) {
            case "v1":
                return new SaveV1_0();
            case "v1.1":
                return new SaveV1_1();
            default:
                throw new IllegalArgumentException("Неподдерживаемая версия: " + version);
        }
    }
    
    /**
     * Создает экземпляр Save версии 1.0
     * @return новый экземпляр SaveV1_0
     */
    public static Save createV1_0() {
        return new SaveV1_0();
    }
    
    /**
     * Создает экземпляр Save версии 1.1
     * @return новый экземпляр SaveV1_1
     */
    public static Save createV1_1() {
        return new SaveV1_1();
    }
    
    /**
     * Получает последнюю поддерживаемую версию
     * @return строку с последней версией
     */
    public static String getLatestVersion() {
        return "v1.1";
    }
    
    /**
     * Проверяет, поддерживается ли указанная версия
     * @param version версия для проверки
     * @return true если версия поддерживается, false в противном случае
     */
    public static boolean isVersionSupported(String version) {
        return "v1".equals(version) || "v1.1".equals(version);
    }
}
