# Адаптация FrameProvider для Android

## Обзор

`FrameProvider` был адаптирован для работы в Android вместо Swing. Основные изменения:

### 🔄 Замены:

1. **`javax.swing.JFrame`** → **`android.app.Activity`** + **`android.content.Context`**
2. **`JOptionPane`** → **`AndroidDialogHelper`**
3. **`JColorChooser`** → **`AndroidColorHelper`**

## Структура адаптации

### 1. FrameProvider Interface

```java
public interface FrameProvider {
    Context getContext();           // Основной контекст приложения
    Activity getActivity();         // Основная активность (если доступна)
    boolean isActivityAvailable();  // Проверка доступности активности
}
```

### 2. FrameProviderImpl

```java
public class FrameProviderImpl implements FrameProvider {
    private volatile Context context;
    private volatile Activity activity;
    
    public void setContext(Context context) { ... }
    public void setActivity(Activity activity) { ... }
    // Реализация методов интерфейса
}
```

### 3. Вспомогательные классы

#### AndroidDialogHelper
- `showConfirmDialog()` - диалог подтверждения
- `showThreeButtonDialog()` - диалог с тремя кнопками
- `showInputDialog()` - диалог ввода текста
- `showMessage()` - простое сообщение
- `showWarning()` - предупреждение
- `showError()` - ошибка

#### AndroidColorHelper
- `showColorDialog()` - выбор цвета
- `showRGBColorDialog()` - выбор цвета с RGB слайдерами
- Утилиты для работы с цветами

## Использование

### Инициализация в Activity

```java
public class MainActivity extends Activity {
    private FrameProviderImpl frameProvider;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Инициализация
        frameProvider = new FrameProviderImpl();
        frameProvider.setActivity(this);
        frameProvider.setContext(this);
    }
}
```

### Использование диалогов

```java
// Диалог подтверждения
AndroidDialogHelper.showConfirmDialog(
    frameProvider.getContext(),
    "Заголовок",
    "Сообщение",
    "Да",
    "Нет",
    confirmed -> {
        // Обработка результата
    }
);

// Выбор цвета
AndroidColorHelper.showColorDialog(
    frameProvider.getContext(),
    "Выберите цвет",
    Color.BLACK,
    color -> {
        // Обработка выбранного цвета
    }
);
```

## Миграция существующего кода

### Замена вызовов JFrame

**Было:**
```java
JFrame frame = frameProvider.get();
frame.setTitle("Заголовок");
```

**Стало:**
```java
Activity activity = frameProvider.getActivity();
if (activity != null) {
    activity.setTitle("Заголовок");
}
```

### Замена JOptionPane

**Было:**
```java
int choice = JOptionPane.showConfirmDialog(
    frameProvider.get(), 
    "Сообщение", 
    "Заголовок", 
    JOptionPane.YES_NO_OPTION
);
```

**Стало:**
```java
AndroidDialogHelper.showConfirmDialog(
    frameProvider.getContext(),
    "Заголовок",
    "Сообщение",
    "Да",
    "Нет",
    confirmed -> {
        // Обработка результата
    }
);
```

### Замена JColorChooser

**Было:**
```java
Color color = JColorChooser.showDialog(
    frameProvider.get(), 
    "Выберите цвет", 
    Color.BLACK
);
```

**Стало:**
```java
AndroidColorHelper.showColorDialog(
    frameProvider.getContext(),
    "Выберите цвет",
    Color.BLACK,
    color -> {
        // Обработка выбранного цвета
    }
);
```

## Совместимость

### Минимальная версия Android
- **API 16+** для базовой функциональности
- **API 24+** для лямбда-выражений (рекомендуется)

### Зависимости
- `android.app.Activity`
- `android.content.Context`
- `android.app.AlertDialog`
- `android.graphics.Color`

## Рекомендации

1. **Инициализация**: Всегда вызывайте `setContext()` или `setActivity()` перед использованием
2. **Проверка доступности**: Используйте `isActivityAvailable()` перед вызовом `getActivity()`
3. **Обработка ошибок**: Обрабатывайте случаи, когда активность недоступна
4. **Асинхронность**: Диалоги в Android асинхронные, используйте callback'и

## Примеры полной миграции

См. файл `MainActivityExample.java` для полного примера использования.

## Проблемы и решения

### Проблема: "Context not initialized"
**Решение**: Убедитесь, что вызвали `setContext()` или `setActivity()` перед использованием

### Проблема: Activity недоступна
**Решение**: Используйте `getContext()` вместо `getActivity()` или проверьте `isActivityAvailable()`

### Проблема: Диалоги не показываются
**Решение**: Убедитесь, что вызываете диалоги из UI потока (Activity.runOnUiThread()) 