package com.example.oriedita_ui.action;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.service.FoldingService;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

public class SuiteiAction extends AbstractOrieditaAction {
    private final FoldingService foldingService;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFigure.EstimationOrder estimationOrder;

    public SuiteiAction(FoldingService foldingService,
                        FoldedFigure.EstimationOrder estimationOrder) {
        this.foldingService = foldingService;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.estimationOrder = estimationOrder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        foldingService.fold(estimationOrder);//引数の意味は(i_fold_type , i_suitei_meirei);
        mainCreasePatternWorker.unselect_all(false);
    }
}
