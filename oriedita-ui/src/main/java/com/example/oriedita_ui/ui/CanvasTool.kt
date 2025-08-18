package com.example.oriedita_ui.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext

sealed class CanvasTool(val id: Int, val label: String, val resourceName: String) {
    object DrawCreaseFree : CanvasTool(1, "Свободная линия", "ppp/senbun_nyuryoku")
    object MoveCreasePattern : CanvasTool(2, "Перемещение узора", "ppp/memori_yoko_idou")
    object LineSegmentDelete : CanvasTool(3, "Удалить отрезок", "ppp/senbun_sakujyo")
    object ChangeCreaseType : CanvasTool(4, "Изменить тип сгиба", "ppp/senbun_henkan")
    object LengthenCrease : CanvasTool(5, "Продлить сгиб", "ppp/senbun_entyou")
    object SquareBisector : CanvasTool(7, "Биссектриса квадрата", "ppp/kaku_toubun")
    object Inward : CanvasTool(8, "Внутрь (Rabbit Ear)", "ppp/orikaesi")
    object PerpendicularDraw : CanvasTool(9, "Перпендикуляр", "ppp/naishin")
    object SymmetricDraw : CanvasTool(10, "Симметрия", "ppp/hanten")
    object DrawCreaseRestricted : CanvasTool(11, "Ограниченный сгиб", "ppp/senbun_nyuryoku11")
    object DrawCreaseSymmetric : CanvasTool(12, "Симметричный сгиб", "ppp/kyouei")
    object DrawCreaseAngleRestricted : CanvasTool(13, "Сгиб по углу", "ppp/jiyuu_kaku_set_a")
    object DrawPoint : CanvasTool(14, "Точка", "ppp/kitei")
    object DeletePoint : CanvasTool(15, "Удалить точку", "ppp/eda_kesi")
    object AngleSystem : CanvasTool(16, "Система углов", "ppp/deg")
    object DrawCreaseAngleRestricted3 : CanvasTool(18, "Сгиб по 3 точкам", "ppp/jiyuu_kaku_set_b")
    object CreaseSelect : CanvasTool(19, "Выделить сгиб", "ppp/select_suitei")
    object CreaseUnselect : CanvasTool(20, "Снять выделение", "ppp/ckbox_mouse_settei_off")
    object CreaseMove : CanvasTool(21, "Переместить сгиб", "ppp/memori_tate_idou")
    object CreaseCopy : CanvasTool(22, "Копировать сгиб", "ppp/memori_yoko_idou")
    object CreaseMakeMountain : CanvasTool(23, "Сделать гору", "ppp/M_nisuru")
    object CreaseMakeValley : CanvasTool(24, "Сделать долину", "ppp/V_nisuru")
    object CreaseMakeEdge : CanvasTool(25, "Сделать край", "ppp/E_nisuru")
    object BackgroundChangePosition : CanvasTool(26, "Фон: позиция", "ppp/oriagari_sousa")
    object LineSegmentDivision : CanvasTool(27, "Деление отрезка", "ppp/senbun_b_nyuryoku")
    object LineSegmentRatioSet : CanvasTool(28, "Деление по пропорции", "ppp/senbun_n_nyuryoku")
    object PolygonSetNoCorners : CanvasTool(29, "Многоугольник без углов", "ppp/sei_takakukei")
    object CreaseAdvanceType : CanvasTool(30, "Расширенный тип сгиба", "ppp/senbun_yoke_henkan")
    object CreaseMove4P : CanvasTool(31, "Переместить 4P", "ppp/memori_tate_idou")
    object CreaseCopy4P : CanvasTool(32, "Копировать 4P", "ppp/memori_yoko_idou")
    object FishBoneDraw : CanvasTool(33, "Рыбья кость", "ppp/sakananohone")
    object CreaseMakeMV : CanvasTool(34, "Сделать MV", "ppp/HK_nisuru")
    object DoubleSymmetricDraw : CanvasTool(35, "Двойная симметрия", "ppp/renzoku_orikaesi")
    object CreasesAlternateMV : CanvasTool(36, "Чередование MV", "ppp/all_s_step_to_orisen")
    object DrawCreaseAngleRestricted5 : CanvasTool(37, "Сгиб по 5 точкам", "ppp/jiyuu_kaku_set_b")
    object VertexMakeAngularlyFlatFoldable : CanvasTool(38, "Плоский угол вершины", "ppp/fuku_orikaesi")
    object FoldableLineInput : CanvasTool(39, "Ввод сгибаемой линии", "ppp/foldable_line_input")
    object ParallelDraw : CanvasTool(40, "Параллельные линии", "ppp/heikousen")
    object VertexDeleteOnCrease : CanvasTool(41, "Удалить вершину на сгибе", "ppp/eda_kesi")
    object CircleDraw : CanvasTool(42, "Окружность по центру и радиусу", "ppp/en_nyuryoku")
    object CircleDrawThreePoint : CanvasTool(43, "Окружность по 3 точкам", "ppp/en_3ten_nyuryoku")
    object CircleDrawSeparate : CanvasTool(44, "Окружность отдельно", "ppp/en_bunri_nyuryoku")
    object CircleDrawTangentLine : CanvasTool(45, "Окружность по касательной", "ppp/en_en_sessen")
    object CircleDrawInverted : CanvasTool(46, "Инвертированная окружность", "ppp/en_en_dousin_en")
    object CircleDrawFree : CanvasTool(47, "Свободная окружность", "ppp/en_nyuryoku_free")
    object CircleDrawConcentric : CanvasTool(48, "Концентрическая окружность", "ppp/en_en_dousin_en")
    object CircleDrawConcentricSelect : CanvasTool(49, "Выбрать концентрическую окружность", "ppp/en_en_dousin_en")
    object CircleDrawTwoConcentricSelect : CanvasTool(50, "2 концентрические окружности", "ppp/en_en_dousin_en")
    object ParallelDrawWidth : CanvasTool(51, "Ширина параллельных", "ppp/heikousen_haba_sitei")
    object ContinuousSymmetricDraw : CanvasTool(52, "Непрерывная симметрия", "ppp/renzoku_orikaesi")
    object DisplayLengthBetweenPoints1 : CanvasTool(53, "Длина между точками 1", "ppp/deg")
    object DisplayLengthBetweenPoints2 : CanvasTool(54, "Длина между точками 2", "ppp/deg2")
    object DisplayAngleBetweenThreePoints1 : CanvasTool(55, "Угол между 3 точками 1", "ppp/deg3")
    object DisplayAngleBetweenThreePoints2 : CanvasTool(56, "Угол между 3 точками 2", "ppp/deg4")
    object DisplayAngleBetweenThreePoints3 : CanvasTool(57, "Угол между 3 точками 3", "ppp/deg")
    object CreaseToggleMV : CanvasTool(58, "Переключить MV", "ppp/HK_nisuru")
    object CircleChangeColor : CanvasTool(59, "Цвет окружности", "ppp/in_L_col_change")
    object CreaseMakeAux : CanvasTool(60, "Сделать вспомогательную", "ppp/A_nisuru")
    object OperationFrameCreate : CanvasTool(61, "Создать рамку операции", "ppp/kaisetu")
    object VoronoiCreate : CanvasTool(62, "Создать Вороной", "ppp/Voronoi")
    object FlatFoldableCheck : CanvasTool(63, "Проверка на складываемость", "ppp/oritatami_kanousen")
    object CreaseDeleteOverlapping : CanvasTool(64, "Удалить перекрывающиеся", "ppp/eda_kesi")
    object CreaseDeleteIntersecting : CanvasTool(65, "Удалить пересекающиеся", "ppp/eda_kesi")
    object SelectPolygon : CanvasTool(66, "Выделить полигон", "ppp/select_suitei")
    object UnselectPolygon : CanvasTool(67, "Снять выделение с полигона", "ppp/ckbox_mouse_settei_off")
    object SelectLineIntersecting : CanvasTool(68, "Выделить пересекающиеся линии", "ppp/select_suitei")
    object UnselectLineIntersecting : CanvasTool(69, "Снять выделение с пересечённых", "ppp/ckbox_mouse_settei_off")
    object LengthenCreaseSameColor : CanvasTool(70, "Продлить сгиб того же цвета", "ppp/senbun_entyou_2")
    object FoldableLineDraw : CanvasTool(71, "Рисовать сгибаемую линию", "ppp/senbun_nyuryoku")
    object ReplaceLineTypeSelect : CanvasTool(72, "Заменить тип линии", "ppp/senbun_henkan2")
    object DeleteLineTypeSelect : CanvasTool(73, "Удалить тип линии", "ppp/senbun_sakujyo")
    object SelectLasso : CanvasTool(74, "Лассо выделение", "ppp/select_suitei")
    object UnselectLasso : CanvasTool(75, "Снять лассо выделение", "ppp/ckbox_mouse_settei_off")
    object Text : CanvasTool(81, "Текст", "ppp/text")
    object ModifyCalculatedShape : CanvasTool(101, "Изменить вычисленную форму", "ppp/kaisetu")
    object MoveCalculatedShape : CanvasTool(102, "Переместить вычисленную форму", "ppp/memori_yoko_idou")
    object ChangeStandardFace : CanvasTool(103, "Изменить стандартную грань", "ppp/kaisetu")
    object AddFoldingConstraint : CanvasTool(104, "Добавить ограничение сгиба", "ppp/in_L_col_change")
    object Axiom5 : CanvasTool(105, "Аксиома 5", "ppp/deg")
    object Axiom7 : CanvasTool(106, "Аксиома 7", "ppp/deg2")
}

@Composable
fun CanvasTool.iconPainter(): Painter {
    val isDark = false // Пока используем только светлую тему
    val context = LocalContext.current
    val baseName = resourceName.substringAfter("/")
    val folder = if (isDark) "ppp_dark" else "ppp"
    val resId = context.resources.getIdentifier(baseName.removeSuffix(".png"), "drawable", context.packageName)
    val darkResId = context.resources.getIdentifier("${folder}/" + baseName.removeSuffix(".png"), "drawable", context.packageName)
    
    return when {
        isDark && darkResId != 0 -> painterResource(id = darkResId)
        !isDark && resId != 0 -> painterResource(id = resId)
        else -> painterResource(id = resId) // Fallback to light theme icon
    }
} 