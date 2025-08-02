package com.example.oriedita_ui.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext

sealed class CanvasTool(val id: Int, val label: String, val resourceName: String?, val fallbackIcon: ImageVector) {
    object DrawCreaseFree : CanvasTool(1, "Свободная линия", "ppp/senbun_nyuryoku", Icons.Default.Create)
    object MoveCreasePattern : CanvasTool(2, "Перемещение узора", "ppp/memori_yoko_idou", Icons.Default.OpenWith)
    object LineSegmentDelete : CanvasTool(3, "Удалить отрезок", "ppp/senbun_sakujyo", Icons.Default.Remove)
    object ChangeCreaseType : CanvasTool(4, "Изменить тип сгиба", "ppp/senbun_henkan", Icons.Default.SwapHoriz)
    object LengthenCrease : CanvasTool(5, "Продлить сгиб", "ppp/senbun_entyou", Icons.Default.ArrowForward)
    object SquareBisector : CanvasTool(7, "Биссектриса квадрата", "ppp/kaku_toubun", Icons.Default.Square)
    object Inward : CanvasTool(8, "Внутрь (Rabbit Ear)", "ppp/orikaesi", Icons.Default.KeyboardArrowDown)
    object PerpendicularDraw : CanvasTool(9, "Перпендикуляр", "ppp/naishin", Icons.Default.Height)
    object SymmetricDraw : CanvasTool(10, "Симметрия", "ppp/hanten", Icons.Default.Flip)
    object DrawCreaseRestricted : CanvasTool(11, "Ограниченный сгиб", "ppp/senbun_nyuryoku11", Icons.Default.Block)
    object DrawCreaseSymmetric : CanvasTool(12, "Симметричный сгиб", "ppp/kyouei", Icons.Default.Flip)
    object DrawCreaseAngleRestricted : CanvasTool(13, "Сгиб по углу", "ppp/jiyuu_kaku_set_a", Icons.Default.Angle)
    object DrawPoint : CanvasTool(14, "Точка", "ppp/kitei", Icons.Default.Circle)
    object DeletePoint : CanvasTool(15, "Удалить точку", "ppp/eda_kesi", Icons.Default.HighlightOff)
    object AngleSystem : CanvasTool(16, "Система углов", "ppp/deg", Icons.Default.ChangeHistory)
    object DrawCreaseAngleRestricted3 : CanvasTool(18, "Сгиб по 3 точкам", "ppp/jiyuu_kaku_set_b", Icons.Default.Timeline)
    object CreaseSelect : CanvasTool(19, "Выделить сгиб", "ppp/select_suitei", Icons.Default.SelectAll)
    object CreaseUnselect : CanvasTool(20, "Снять выделение", "ppp/ckbox_mouse_settei_off", Icons.Default.Deselect)
    object CreaseMove : CanvasTool(21, "Переместить сгиб", "ppp/memori_tate_idou", Icons.Default.OpenWith)
    object CreaseCopy : CanvasTool(22, "Копировать сгиб", "ppp/memori_yoko_idou", Icons.Default.ContentCopy)
    object CreaseMakeMountain : CanvasTool(23, "Сделать гору", "ppp/M_nisuru", Icons.Default.Terrain)
    object CreaseMakeValley : CanvasTool(24, "Сделать долину", "ppp/V_nisuru", Icons.Default.Waves)
    object CreaseMakeEdge : CanvasTool(25, "Сделать край", "ppp/E_nisuru", Icons.Default.BorderAll)
    object BackgroundChangePosition : CanvasTool(26, "Фон: позиция", "ppp/oriagari_sousa", Icons.Default.Photo)
    object LineSegmentDivision : CanvasTool(27, "Деление отрезка", "ppp/senbun_b_nyuryoku", Icons.Default.CallSplit)
    object LineSegmentRatioSet : CanvasTool(28, "Деление по пропорции", "ppp/senbun_n_nyuryoku", Icons.Default.Percent)
    object PolygonSetNoCorners : CanvasTool(29, "Многоугольник без углов", "ppp/sei_takakukei", Icons.Default.Hexagon)
    object CreaseAdvanceType : CanvasTool(30, "Расширенный тип сгиба", "ppp/senbun_yoke_henkan", Icons.Default.Upgrade)
    object CreaseMove4P : CanvasTool(31, "Переместить 4P", "ppp/memori_tate_idou", Icons.Default.OpenWith)
    object CreaseCopy4P : CanvasTool(32, "Копировать 4P", "ppp/memori_yoko_idou", Icons.Default.ContentCopy)
    object FishBoneDraw : CanvasTool(33, "Рыбья кость", "ppp/sakananohone", Icons.Default.Fish)
    object CreaseMakeMV : CanvasTool(34, "Сделать MV", "ppp/HK_nisuru", Icons.Default.SwapVert)
    object DoubleSymmetricDraw : CanvasTool(35, "Двойная симметрия", "ppp/renzoku_orikaesi", Icons.Default.Flip)
    object CreasesAlternateMV : CanvasTool(36, "Чередование MV", "ppp/all_s_step_to_orisen", Icons.Default.SwapCalls)
    object DrawCreaseAngleRestricted5 : CanvasTool(37, "Сгиб по 5 точкам", "ppp/jiyuu_kaku_set_b", Icons.Default.Timeline)
    object VertexMakeAngularlyFlatFoldable : CanvasTool(38, "Плоский угол вершины", "ppp/fuku_orikaesi", Icons.Default.ChangeHistory)
    object FoldableLineInput : CanvasTool(39, "Ввод сгибаемой линии", "ppp/foldable_line_input", Icons.Default.Input)
    object ParallelDraw : CanvasTool(40, "Параллельные линии", "ppp/heikousen", Icons.Default.ViewStream)
    object VertexDeleteOnCrease : CanvasTool(41, "Удалить вершину на сгибе", "ppp/eda_kesi", Icons.Default.HighlightOff)
    object CircleDraw : CanvasTool(42, "Окружность по центру и радиусу", "ppp/en_nyuryoku", Icons.Default.Circle)
    object CircleDrawThreePoint : CanvasTool(43, "Окружность по 3 точкам", "ppp/en_3ten_nyuryoku", Icons.Default.ControlPoint)
    object CircleDrawSeparate : CanvasTool(44, "Окружность отдельно", "ppp/en_bunri_nyuryoku", Icons.Default.Circle)
    object CircleDrawTangentLine : CanvasTool(45, "Окружность по касательной", "ppp/en_en_sessen", Icons.Default.Timeline)
    object CircleDrawInverted : CanvasTool(46, "Инвертированная окружность", "ppp/en_en_dousin_en", Icons.Default.Sync)
    object CircleDrawFree : CanvasTool(47, "Свободная окружность", "ppp/en_nyuryoku_free", Icons.Default.Circle)
    object CircleDrawConcentric : CanvasTool(48, "Концентрическая окружность", "ppp/en_en_dousin_en", Icons.Default.RadioButtonChecked)
    object CircleDrawConcentricSelect : CanvasTool(49, "Выбрать концентрическую окружность", "ppp/en_en_dousin_en", Icons.Default.RadioButtonUnchecked)
    object CircleDrawTwoConcentricSelect : CanvasTool(50, "2 концентрические окружности", "ppp/en_en_dousin_en", Icons.Default.RadioButtonUnchecked)
    object ParallelDrawWidth : CanvasTool(51, "Ширина параллельных", "ppp/heikousen_haba_sitei", Icons.Default.ViewStream)
    object ContinuousSymmetricDraw : CanvasTool(52, "Непрерывная симметрия", "ppp/renzoku_orikaesi", Icons.Default.Flip)
    object DisplayLengthBetweenPoints1 : CanvasTool(53, "Длина между точками 1", "ppp/deg", Icons.Default.Straighten)
    object DisplayLengthBetweenPoints2 : CanvasTool(54, "Длина между точками 2", "ppp/deg2", Icons.Default.Straighten)
    object DisplayAngleBetweenThreePoints1 : CanvasTool(55, "Угол между 3 точками 1", "ppp/deg3", Icons.Default.ChangeHistory)
    object DisplayAngleBetweenThreePoints2 : CanvasTool(56, "Угол между 3 точками 2", "ppp/deg4", Icons.Default.ChangeHistory)
    object DisplayAngleBetweenThreePoints3 : CanvasTool(57, "Угол между 3 точками 3", "ppp/deg", Icons.Default.ChangeHistory)
    object CreaseToggleMV : CanvasTool(58, "Переключить MV", "ppp/HK_nisuru", Icons.Default.SwapVert)
    object CircleChangeColor : CanvasTool(59, "Цвет окружности", "ppp/in_L_col_change", Icons.Default.Palette)
    object CreaseMakeAux : CanvasTool(60, "Сделать вспомогательную", "ppp/A_nisuru", Icons.Default.Assistant)
    object OperationFrameCreate : CanvasTool(61, "Создать рамку операции", "ppp/kaisetu", Icons.Default.Crop)
    object VoronoiCreate : CanvasTool(62, "Создать Вороной", "ppp/Voronoi", Icons.Default.GridOn)
    object FlatFoldableCheck : CanvasTool(63, "Проверка на складываемость", "ppp/oritatami_kanousen", Icons.Default.Done)
    object CreaseDeleteOverlapping : CanvasTool(64, "Удалить перекрывающиеся", "ppp/eda_kesi", Icons.Default.Remove)
    object CreaseDeleteIntersecting : CanvasTool(65, "Удалить пересекающиеся", "ppp/eda_kesi", Icons.Default.Remove)
    object SelectPolygon : CanvasTool(66, "Выделить полигон", "ppp/select_suitei", Icons.Default.SelectAll)
    object UnselectPolygon : CanvasTool(67, "Снять выделение с полигона", "ppp/ckbox_mouse_settei_off", Icons.Default.Deselect)
    object SelectLineIntersecting : CanvasTool(68, "Выделить пересекающиеся линии", "ppp/select_suitei", Icons.Default.SelectAll)
    object UnselectLineIntersecting : CanvasTool(69, "Снять выделение с пересечённых", "ppp/ckbox_mouse_settei_off", Icons.Default.Deselect)
    object LengthenCreaseSameColor : CanvasTool(70, "Продлить сгиб того же цвета", "ppp/senbun_entyou_2", Icons.Default.ArrowForward)
    object FoldableLineDraw : CanvasTool(71, "Рисовать сгибаемую линию", "ppp/senbun_nyuryoku", Icons.Default.Create)
    object ReplaceLineTypeSelect : CanvasTool(72, "Заменить тип линии", "ppp/senbun_henkan2", Icons.Default.SwapHoriz)
    object DeleteLineTypeSelect : CanvasTool(73, "Удалить тип линии", "ppp/senbun_sakujyo", Icons.Default.Remove)
    object SelectLasso : CanvasTool(74, "Лассо выделение", "ppp/select_suitei", Icons.Default.Gesture)
    object UnselectLasso : CanvasTool(75, "Снять лассо выделение", "ppp/ckbox_mouse_settei_off", Icons.Default.Deselect)
    object Text : CanvasTool(81, "Текст", "ppp/text", Icons.Default.TextFields)
    object ModifyCalculatedShape : CanvasTool(101, "Изменить вычисленную форму", "ppp/kaisetu", Icons.Default.Edit)
    object MoveCalculatedShape : CanvasTool(102, "Переместить вычисленную форму", "ppp/memori_yoko_idou", Icons.Default.OpenWith)
    object ChangeStandardFace : CanvasTool(103, "Изменить стандартную грань", "ppp/kaisetu", Icons.Default.Crop)
    object AddFoldingConstraint : CanvasTool(104, "Добавить ограничение сгиба", "ppp/in_L_col_change", Icons.Default.Assistant)
    object Axiom5 : CanvasTool(105, "Аксиома 5", "ppp/deg", Icons.Default.Filter5)
    object Axiom7 : CanvasTool(106, "Аксиома 7", "ppp/deg2", Icons.Default.Filter7)
}

@Composable
fun CanvasTool.iconPainter(): Painter {
    val isDark = MaterialTheme.colorScheme.isLight.not()
    val context = LocalContext.current
    val baseName = resourceName?.substringAfter("/") ?: return painterResource(id = fallbackIcon.hashCode())
    val folder = if (isDark) "ppp_dark" else "ppp"
    val resId = context.resources.getIdentifier(baseName.removeSuffix(".png"), "drawable", context.packageName)
    val darkResId = context.resources.getIdentifier("${folder}/" + baseName.removeSuffix(".png"), "drawable", context.packageName)
    return when {
        isDark && darkResId != 0 -> painterResource(id = darkResId)
        !isDark && resId != 0 -> painterResource(id = resId)
        else -> painterResource(id = fallbackIcon.hashCode())
    }
} 