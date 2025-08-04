package com.example.oriedita_ui.action;

import org.tinylog.Logger;
import com.example.oriedita_data.canvas.CreasePattern_Worker;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.all_s_step_to_orisenAction)
public class All_s_step_to_orisenAction extends AbstractOrieditaAction {
    CreasePattern_Worker mainCreasePatternWorker;

    public All_s_step_to_orisenAction() {
    }

    @Override
    public boolean resetLineStep() {
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.info("lineStep_Size = " + mainCreasePatternWorker.getLineStep().size());
        Logger.info("candidate_size = " + mainCreasePatternWorker.getCandidateSize());

        mainCreasePatternWorker.addPreviewLinesToCp();
        mainCreasePatternWorker.getLineStep().clear();
    }
}
