package com.example.oriedita_common.editor.service;

import android.view.KeyEvent;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.text.input.TextFieldValue;
import java.util.Map;

/**
 * Сервис для управления кнопками и обработки событий клавиатуры в Android приложении
 * Адаптирован для работы с Jetpack Compose компонентами
 */
public interface ButtonService {

    /**
     * Устанавливает иконку для текстового элемента
     * 
     * @param iconState состояние иконки для обновления
     * @param key ключ ресурса иконки
     */
    void setIcon(MutableState<String> iconState, String key);

    /**
     * Регистрирует кнопку с указанным ключом действия
     * 
     * @param onClickState состояние обработчика нажатия кнопки
     * @param key ключ действия
     */
    void registerButton(MutableState<Runnable> onClickState, String key);

    /**
     * Регистрирует кнопку с указанным ключом действия и опцией замены подчеркиваний
     * 
     * @param onClickState состояние обработчика нажатия кнопки
     * @param key ключ действия
     * @param replaceUnderscoresInMenus заменять ли подчеркивания в меню
     */
    void registerButton(MutableState<Runnable> onClickState, String key, boolean replaceUnderscoresInMenus);

    /**
     * Загружает все комбинации клавиш из конфигурации
     */
    void loadAllKeyStrokes();

    /**
     * Выполняет общую операцию для кнопок
     * 
     * @param resetLineStep сбросить ли шаг линии
     */
    void Button_shared_operation(boolean resetLineStep);

    /**
     * Выполняет общую операцию для кнопок с сбросом шага линии
     */
    default void Button_shared_operation() {
        Button_shared_operation(true);
    }

    /**
     * Получает карту помощи для комбинаций клавиш
     * 
     * @return карта комбинаций клавиш и соответствующих кнопок
     */
    Map<KeyEvent, MutableState<Runnable>> getHelpInputMap();

    /**
     * Получает действие по комбинации клавиш
     * 
     * @param event событие клавиши
     * @return ключ действия
     */
    String getActionFromKeystroke(KeyEvent event);

    /**
     * Добавляет слушатель по умолчанию для корневого контейнера
     * 
     * @param rootState состояние корневого контейнера
     */
    void addDefaultListener(MutableState<Object> rootState);

    /**
     * Добавляет слушатель по умолчанию для корневого контейнера с опцией замены подчеркиваний
     * 
     * @param rootState состояние корневого контейнера
     * @param replaceUnderscoresInMenus заменять ли подчеркивания в меню
     */
    void addDefaultListener(MutableState<Object> rootState, boolean replaceUnderscoresInMenus);

    /**
     * Устанавливает комбинацию клавиш для действия
     * 
     * @param event событие клавиши
     * @param key ключ действия
     */
    void setKeyStroke(KeyEvent event, String key);

    /**
     * Добавляет слушатель изменений комбинаций клавиш
     * 
     * @param listener слушатель изменений
     */
    void addKeystrokeChangeListener(Runnable listener);

    /**
     * Удаляет слушатель изменений комбинаций клавиш
     * 
     * @param listener слушатель изменений
     */
    void removeKeystrokeChangeListener(Runnable listener);

    /**
     * Регистрирует текстовое поле с указанным ключом
     * 
     * @param textFieldState состояние текстового поля
     * @param key ключ действия
     */
    void registerTextField(MutableState<TextFieldValue> textFieldState, String key);

    /**
     * Удаляет все привязки клавиш
     */
    void removeAllKeyBinds();
}
