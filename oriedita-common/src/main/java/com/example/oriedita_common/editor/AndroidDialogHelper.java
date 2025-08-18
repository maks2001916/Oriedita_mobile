package com.example.oriedita_common.editor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Вспомогательный класс для работы с диалогами в Android
 * Заменяет Swing диалоги (JOptionPane, JColorChooser и т.д.)
 */
public class AndroidDialogHelper {

    /**
     * Показывает диалог подтверждения
     * @param context контекст приложения
     * @param title заголовок диалога
     * @param message сообщение
     * @param positiveButton текст кнопки "Да"
     * @param negativeButton текст кнопки "Нет"
     * @param listener слушатель результата
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                       String positiveButton, String negativeButton,
                                       OnDialogResultListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(true);
                    }
                })
                .setNegativeButton(negativeButton, (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(false);
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Показывает диалог с тремя кнопками (Да/Нет/Отмена)
     * @param context контекст приложения
     * @param title заголовок диалога
     * @param message сообщение
     * @param positiveButton текст кнопки "Да"
     * @param negativeButton текст кнопки "Нет"
     * @param neutralButton текст кнопки "Отмена"
     * @param listener слушатель результата
     */
    public static void showThreeButtonDialog(Context context, String title, String message,
                                           String positiveButton, String negativeButton, String neutralButton,
                                           OnThreeButtonDialogResultListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(ThreeButtonResult.YES);
                    }
                })
                .setNegativeButton(negativeButton, (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(ThreeButtonResult.NO);
                    }
                })
                .setNeutralButton(neutralButton, (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(ThreeButtonResult.CANCEL);
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Показывает диалог ввода текста
     * @param context контекст приложения
     * @param title заголовок диалога
     * @param hint подсказка для поля ввода
     * @param initialValue начальное значение
     * @param listener слушатель результата
     */
    public static void showInputDialog(Context context, String title, String hint, String initialValue,
                                     OnInputDialogResultListener listener) {
        EditText input = new EditText(context);
        input.setHint(hint);
        if (initialValue != null) {
            input.setText(initialValue);
        }

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(input.getText().toString());
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    if (listener != null) {
                        listener.onResult(null);
                    }
                })
                .show();
    }

    /**
     * Показывает простое сообщение
     * @param context контекст приложения
     * @param message сообщение
     */
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Показывает предупреждение
     * @param context контекст приложения
     * @param title заголовок
     * @param message сообщение
     */
    public static void showWarning(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Показывает ошибку
     * @param context контекст приложения
     * @param title заголовок
     * @param message сообщение
     */
    public static void showError(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Интерфейс для слушателя результата диалога подтверждения
     */
    public interface OnDialogResultListener {
        void onResult(boolean confirmed);
    }

    /**
     * Интерфейс для слушателя результата диалога с тремя кнопками
     */
    public interface OnThreeButtonDialogResultListener {
        void onResult(ThreeButtonResult result);
    }

    /**
     * Интерфейс для слушателя результата диалога ввода
     */
    public interface OnInputDialogResultListener {
        void onResult(String input);
    }

    /**
     * Результат диалога с тремя кнопками
     */
    public enum ThreeButtonResult {
        YES, NO, CANCEL
    }
} 