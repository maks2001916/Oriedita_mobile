package com.example.oriedita_ui.action;

import org.tinylog.Logger;
import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.save.Save;
import oriedita.editor.service.FileSaveService;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.io.File;

@ActionHandler(ActionType.IMPORT_ADD)
public class ImportAddAction extends AbstractOrieditaAction {
    FileSaveService fileSaveService;

    CreasePattern_Worker mainCreasePatternWorker;

    public ImportAddAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.info("readFile2Memo() 開始");
        File file = fileSaveService.selectImportFile();
        Save save = null;
        try {
            save = fileSaveService.readImportFile(file);
        } catch (FileReadingException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
        }
        Logger.info("readFile2Memo() 終了");

        if (save != null) {
            mainCreasePatternWorker.setSave_for_reading_tuika(save);
            mainCreasePatternWorker.record();
        }
    }
}
