package com.example.oriedita_common.editor;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Абстрактная модель для Android приложений
 * Предоставляет базовую функциональность для работы с PropertyChangeSupport
 * и привязки данных к Android компонентам
 */
public class AbstractModel {
    
    /**
     * Поддержка изменения свойств для уведомления слушателей
     */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Удаляет слушателя изменений свойств
     * @param listener слушатель для удаления
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * Добавляет слушателя изменений свойств для всех свойств
     * @param listener слушатель для добавления
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Добавляет слушателя изменений свойств для конкретного свойства
     * @param propertyName имя свойства для отслеживания
     * @param listener слушатель для добавления
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Привязывает Android EditText компонент к полю в этой модели
     * Обеспечивает двустороннюю привязку данных между UI и моделью
     * 
     * @param editText компонент EditText для привязки
     * @param property имя свойства в модели для привязки
     */
    public void bind(@NonNull EditText editText, String property) {
        try {
            // Получаем геттер и сеттер для свойства
            Method getter = findGetter(property);
            Method setter = findSetter(property);
            
            if (getter == null || setter == null) {
                throw new RuntimeException("Не удалось найти геттер или сеттер для свойства: " + property);
            }
            
            // Устанавливаем начальное значение из модели в EditText
            Object currentValue = getter.invoke(this);
            if (currentValue != null) {
                editText.setText(currentValue.toString());
            }

            // Отслеживаем изменения в EditText
            AtomicReference<String> value = new AtomicReference<>(editText.getText().toString());

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Не требуется действие перед изменением текста
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Не требуется действие во время изменения текста
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String newText = s.toString();
                        if (!value.get().equals(newText)) {
                            value.set(newText);
                            // Обновляем значение в модели
                            setter.invoke(AbstractModel.this, newText);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Отслеживаем изменения в модели и обновляем EditText
            addPropertyChangeListener(property, e -> {
                String newValue = e.getNewValue() != null ? e.getNewValue().toString() : "";
                if (!newValue.equals(editText.getText().toString())) {
                    editText.setText(newValue);
                    // Устанавливаем курсор в конец текста
                    editText.setSelection(newValue.length());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при привязке EditText к свойству: " + property, e);
        }
    }

    /**
     * Привязывает Android EditText компонент к полю в этой модели с валидацией
     * 
     * @param editText компонент EditText для привязки
     * @param property имя свойства в модели для привязки
     * @param validator валидатор для проверки введенных данных
     */
    public void bind(@NonNull EditText editText, String property, @NonNull TextValidator validator) {
        try {
            // Получаем геттер и сеттер для свойства
            Method getter = findGetter(property);
            Method setter = findSetter(property);
            
            if (getter == null || setter == null) {
                throw new RuntimeException("Не удалось найти геттер или сеттер для свойства: " + property);
            }
            
            // Устанавливаем начальное значение
            Object currentValue = getter.invoke(this);
            if (currentValue != null) {
                editText.setText(currentValue.toString());
            }

            AtomicReference<String> value = new AtomicReference<>(editText.getText().toString());

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Не требуется действие
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Не требуется действие
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String newText = s.toString();
                        if (!value.get().equals(newText)) {
                            // Проверяем валидность
                            if (validator.isValid(newText)) {
                                value.set(newText);
                                setter.invoke(AbstractModel.this, newText);
                                editText.setError(null); // Убираем ошибку
                            } else {
                                editText.setError(validator.getErrorMessage(newText));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Отслеживаем изменения в модели
            addPropertyChangeListener(property, e -> {
                String newValue = e.getNewValue() != null ? e.getNewValue().toString() : "";
                if (!newValue.equals(editText.getText().toString())) {
                    editText.setText(newValue);
                    editText.setSelection(newValue.length());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при привязке EditText к свойству с валидацией: " + property, e);
        }
    }

    /**
     * Уведомляет всех слушателей об изменении свойства
     * 
     * @param propertyName имя измененного свойства
     * @param oldValue старое значение
     * @param newValue новое значение
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Уведомляет всех слушателей об изменении свойства (для примитивных типов)
     * 
     * @param propertyName имя измененного свойства
     * @param oldValue старое значение
     * @param newValue новое значение
     */
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Уведомляет всех слушателей об изменении свойства (для boolean)
     * 
     * @param propertyName имя измененного свойства
     * @param oldValue старое значение
     * @param newValue новое значение
     */
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Находит геттер для указанного свойства
     * 
     * @param propertyName имя свойства
     * @return метод геттера или null, если не найден
     */
    private Method findGetter(String propertyName) {
        try {
            // Пробуем стандартный геттер (get + PropertyName)
            String getterName = "get" + capitalize(propertyName);
            return getClass().getMethod(getterName);
        } catch (NoSuchMethodException e1) {
            try {
                // Пробуем boolean геттер (is + PropertyName)
                String getterName = "is" + capitalize(propertyName);
                return getClass().getMethod(getterName);
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
    }

    /**
     * Находит сеттер для указанного свойства
     * 
     * @param propertyName имя свойства
     * @return метод сеттера или null, если не найден
     */
    private Method findSetter(String propertyName) {
        try {
            // Пробуем найти сеттер (set + PropertyName)
            String setterName = "set" + capitalize(propertyName);
            Method[] methods = getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    return method;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Делает первую букву строки заглавной
     * 
     * @param str исходная строка
     * @return строка с заглавной первой буквой
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Интерфейс для валидации текста
     */
    public interface TextValidator {
        /**
         * Проверяет валидность текста
         * @param text текст для проверки
         * @return true, если текст валиден
         */
        boolean isValid(String text);

        /**
         * Возвращает сообщение об ошибке для невалидного текста
         * @param text невалидный текст
         * @return сообщение об ошибке
         */
        String getErrorMessage(String text);
    }
}
