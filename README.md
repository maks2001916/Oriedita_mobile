## Oriedita Android (порт)

README для Android-порта приложения Oriedita. Здесь описаны модули, требования, сборка/запуск, предпросмотр Compose и известные нюансы.

### Требования
- JDK 17
- Android Studio (Arctic Fox и новее, рекомендовано последнее)
- Android SDK: compile/target 35, minSdk 24
- Gradle wrapper поставляется в репозитории

### Структура (Android)
```
Oriedita android/
  oriedita/         # Приложение (application)
  oriedita-ui/      # UI на Jetpack Compose
  oriedita-data/    # Данные/ресурсы/примеровые наборы
  oriedita-common/  # Общие ресурсы/строки/темы
  origami/          # Библиотека геометрии/ядро
  test_files/       # Примеры: test.cp, test.fold, test.ori
```

Зависимости по крупному:
- `oriedita` зависит от `oriedita-ui`, `oriedita-common`, `oriedita-data`, `origami`.

### Быстрый старт (Android)
1) Откройте папку `Oriedita source/Oriedita android` в Android Studio.
2) Дождитесь синхронизации Gradle и индексации.
3) Соберите и запустите модуль `oriedita` на устройстве/эмуляторе:
```bash
./gradlew :oriedita:assembleDebug
./gradlew :oriedita:installDebug
```
Либо кнопкой Run ▶ в Android Studio (выбрав конфигурацию `oriedita`).

### Настройки сборки (основное)
- `compileSdk = 35`, `targetSdk = 35`, `minSdk = 24` заданы в `oriedita/build.gradle.kts` и `oriedita-ui/build.gradle.kts`.
- Kotlin JVM target 17.
- Включён Jetpack Compose (Material3, BOM).

### Предпросмотр Jetpack Compose (Preview)
- В модулях с UI (`oriedita-ui`) доступны `@Preview` для быстрой вёрстки.
- Если предпросмотр падает/не отображает корректно на последних API, укажите более низкий API в аннотации, например:
```kotlin
@Preview(showBackground = true, apiLevel = 34)
@Composable
fun MyPreview() { /* ... */ }
```
- В Preview могут быть недоступны некоторые ресурсы (например, пользовательские шрифты). Для стабильности используйте заглушки или условную загрузку ресурсов в режиме дизайнера.

### Известные проблемы
- Предупреждение Layout Editor: «Текущий рендеринг поддерживает только API до 35. Возможны сбои на более высоких API». 
  - Решение: для Preview используйте `apiLevel = 34` или выберите более низкий API в выпадающем списке предпросмотра.

- NPE при загрузке иконок/шрифтов в Preview (например, через `FontIconManager`):
  - Причина: в среде LayoutLib некоторые ресурсы/`Context` могут отсутствовать, `open(...)` возвращает null.
  - Решение: в режимах Preview подменять иконки заглушками или пропускать загрузку кастомных шрифтов.

- Отличия рендеринга между Preview и реальным устройством: Preview работает через LayoutLib и не всегда идентичен рантайму. Проверяйте UI на эмуляторе/устройстве.

### Тестовые файлы
Примеры расположены в `Oriedita android/test_files/` (`test.cp`, `test.fold`, `test.ori`). Удобно для ручной проверки.

### Стиль кода
- Kotlin/Compose: говорящие имена, явные типы в публичных API, ранние возвраты, обработка ошибок без лишних try/catch, не более 2–3 уровней вложенности.
- Не добавляйте комментарии для очевидного кода; документируйте «почему», а не «как».

### Сборка всего Android-проекта
```bash
./gradlew clean build
```

### Лицензия
См. файлы `LICENSE.md` в соответствующих подпроектах (для desktop — в `oriedita-master/`).

