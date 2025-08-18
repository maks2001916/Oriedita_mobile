# Соответствие классов обработчиков мыши

## Таблица соответствий между Android проектом и oriedita-master

| Android проект | oriedita-master | Статус | Примечания |
|---|---|---|---|
| **Базовые классы** |
| `BaseMouseHandler.kt` | `BaseMouseHandler.java` | ✅ Соответствует | Базовый класс для всех обработчиков |
| `BaseMouseHandlerBoxSelect.kt` | `BaseMouseHandlerBoxSelect.java` | ✅ Соответствует | Выбор в прямоугольнике |
| `BaseMouseHandlerInputRestricted.kt` | `BaseMouseHandlerInputRestricted.java` | ✅ Соответствует | Ограниченный ввод |
| `BaseMouseHandlerLasso.kt` | `BaseMouseHandlerLasso.java` | ✅ Соответствует | Выбор лассо |
| `BaseMouseHandlerLineSelect.kt` | `BaseMouseHandlerLineSelect.java` | ✅ Соответствует | Выбор линий |
| `BaseMouseHandlerLineTransform.kt` | `BaseMouseHandlerLineTransform.java` | ✅ Соответствует | Трансформация линий |
| `BaseMouseHandlerPolygon.kt` | `BaseMouseHandlerPolygon.java` | ✅ Соответствует | Работа с многоугольниками |
| `StepMouseHandler.kt` | `StepMouseHandler.java` | ✅ Соответствует | Пошаговые обработчики |

| **Обработчики углов и измерений** |
| `MouseHandlerAngleSystem.kt` | `MouseHandlerAngleSystem.java` | ✅ Соответствует | Угловая система |
| `MouseHandlerAngleMeasure.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerAngleSnap.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerAngleBisector.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики аксиом** |
| `MouseHandlerAxiom5.kt` | `MouseHandlerAxiom5.java` | ✅ Соответствует | Аксиома 5 |
| `MouseHandlerAxiom7.kt` | `MouseHandlerAxiom7.java` | ✅ Соответствует | Аксиома 7 |

| **Обработчики окружностей** |
| `MouseHandlerCircleDraw.kt` | `MouseHandlerCircleDraw.java` | ✅ Соответствует | Рисование окружностей |
| `MouseHandlerCircleDrawFree.kt` | `MouseHandlerCircleDrawFree.java` | ✅ Соответствует | Свободное рисование окружностей |
| `MouseHandlerCircleDrawThreePoint.kt` | `MouseHandlerCircleDrawThreePoint.java` | ✅ Соответствует | Окружность по трем точкам |
| `MouseHandlerCircleDrawConcentric.kt` | `MouseHandlerCircleDrawConcentric.java` | ✅ Соответствует | Концентрические окружности |
| `MouseHandlerCircleDrawConcentricSelect.kt` | `MouseHandlerCircleDrawConcentricSelect.java` | ✅ Соответствует | Выбор концентрических окружностей |
| `MouseHandlerCircleDrawConcentricTwoCircleSelect.kt` | `MouseHandlerCircleDrawConcentricTwoCircleSelect.java` | ✅ Соответствует | Выбор двух концентрических окружностей |
| `MouseHandlerCircleDrawInverted.kt` | `MouseHandlerCircleDrawInverted.java` | ✅ Соответствует | Инвертированные окружности |
| `MouseHandlerCircleDrawSeparate.kt` | `MouseHandlerCircleDrawSeparate.java` | ✅ Соответствует | Раздельные окружности |
| `MouseHandlerCircleDrawTangentLine.kt` | `MouseHandlerCircleDrawTangentLine.java` | ✅ Соответствует | Касательные линии к окружностям |
| `MouseHandlerCircleInscribed.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCircleChangeColor.kt` | `MouseHandlerCircleChangeColor.java` | ✅ Соответствует | Изменение цвета окружностей |

| **Обработчики линий сгиба** |
| `MouseHandlerDrawCreaseAngleRestricted5.kt` | `MouseHandlerDrawCreaseAngleRestricted5.java` | ✅ Соответствует | Рисование линий с ограничением угла 5 |
| `MouseHandlerDrawCreaseAngleRestricted3_2.kt` | `MouseHandlerDrawCreaseAngleRestricted3_2.java` | ✅ Соответствует | Рисование линий с ограничением угла 3_2 |
| - | `MouseHandlerDrawCreaseAngleRestricted.java` | ❌ Отсутствует | Рисование линий с ограничением угла (общий) |
| - | `MouseHandlerDrawCreaseFree.java` | ❌ Отсутствует | Свободное рисование линий сгиба |
| - | `MouseHandlerDrawCreaseRestricted.java` | ❌ Отсутствует | Ограниченное рисование линий сгиба |
| - | `MouseHandlerDrawCreaseSymmetric.java` | ❌ Отсутствует | Симметричное рисование линий сгиба |
| `MouseHandlerCreaseMakeAux.kt` | `MouseHandlerCreaseMakeAux.java` | ✅ Соответствует | Создание вспомогательных линий |
| `MouseHandlerCreaseMakeAuxEdge.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCreaseMakeAuxLive.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCreaseMakeAuxLiveEdge.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCreaseMakeAuxLiveMountain.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCreaseMakeAuxMountain.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCreaseMakeAuxValley.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerCreaseMakeEdge.kt` | `MouseHandlerCreaseMakeEdge.java` | ✅ Соответствует | Создание краевых линий |
| `MouseHandlerCreaseMakeMountain.kt` | `MouseHandlerCreaseMakeMountain.java` | ✅ Соответствует | Создание горных линий |
| `MouseHandlerCreaseMakeValley.kt` | `MouseHandlerCreaseMakeValley.java` | ✅ Соответствует | Создание долинных линий |
| `MouseHandlerCreaseMakeMV.kt` | `MouseHandlerCreaseMakeMV.java` | ✅ Соответствует | Создание горных/долинных линий |
| `MouseHandlerCreaseAdvanceType.kt` | `MouseHandlerCreaseAdvanceType.java` | ✅ Соответствует | Продвижение типа линии |
| `MouseHandlerCreaseToggleMV.kt` | `MouseHandlerCreaseToggleMV.java` | ✅ Соответствует | Переключение горная/долинная |
| `MouseHandlerCreasesAlternateMV.kt` | `MouseHandlerCreasesAlternateMV.java` | ✅ Соответствует | Чередование горных/долинных |
| `MouseHandlerCreaseSelect.kt` | `MouseHandlerCreaseSelect.java` | ✅ Соответствует | Выбор линий сгиба |
| `MouseHandlerCreaseUnselect.kt` | `MouseHandlerCreaseUnselect.java` | ✅ Соответствует | Снятие выбора линий |
| `MouseHandlerCreaseCopy.kt` | `MouseHandlerCreaseCopy.java` | ✅ Соответствует | Копирование линий |
| `MouseHandlerCreaseCopy4p.kt` | `MouseHandlerCreaseCopy4p.java` | ✅ Соответствует | Копирование линий 4 точки |
| `MouseHandlerCreaseMove.kt` | `MouseHandlerCreaseMove.java` | ✅ Соответствует | Перемещение линий |
| `MouseHandlerCreaseMove4p.kt` | `MouseHandlerCreaseMove4p.java` | ✅ Соответствует | Перемещение линий 4 точки |
| `MouseHandlerCreaseDeleteIntersecting.kt` | `MouseHandlerCreaseDeleteIntersecting.java` | ✅ Соответствует | Удаление пересекающихся линий |
| `MouseHandlerCreaseDeleteOverlapping.kt` | `MouseHandlerCreaseDeleteOverlapping.java` | ✅ Соответствует | Удаление перекрывающихся линий |
| `MouseHandlerLengthenCrease.kt` | `MouseHandlerLengthenCrease.java` | ✅ Соответствует | Удлинение линий |
| - | `MouseHandlerLengthenCreaseSameColor.java` | ❌ Отсутствует | Удлинение линий того же цвета |
| `MouseHandlerLineSegmentDelete.kt` | `MouseHandlerLineSegmentDelete.java` | ✅ Соответствует | Удаление сегментов линий |
| `MouseHandlerLineSegmentDivision.kt` | `MouseHandlerLineSegmentDivision.java` | ✅ Соответствует | Деление сегментов линий |
| `MouseHandlerLineSegmentRatioSet.kt` | `MouseHandlerLineSegmentRatioSet.java` | ✅ Соответствует | Установка соотношений сегментов |

| **Обработчики симметрии** |
| `MouseHandlerSymmetricLine.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerSymmetricPoint.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerDoubleSymmetricDraw.kt` | `MouseHandlerDoubleSymmetricDraw.java` | ✅ Соответствует | Двойная симметричная отрисовка |
| `MouseHandlerContinuousSymmetricDraw.kt` | `MouseHandlerContinuousSymmetricDraw.java` | ✅ Соответствует | Непрерывная симметричная отрисовка |
| - | `MouseHandlerSymmetricDraw.java` | ❌ Отсутствует | Симметричная отрисовка |

| **Обработчики геометрических построений** |
| `MouseHandlerParallel.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerPerpendicular.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerTangent.kt` | - | ❌ Отсутствует | Новый класс для Android |
| - | `MouseHandlerParallelDraw.java` | ❌ Отсутствует | Параллельная отрисовка |
| - | `MouseHandlerParallelDrawWidth.java` | ❌ Отсутствует | Параллельная отрисовка с шириной |
| - | `MouseHandlerPerpendicularDraw.java` | ❌ Отсутствует | Перпендикулярная отрисовка |
| `MouseHandlerSquareBisector.kt` | `MouseHandlerSquareBisector.java` | ✅ Соответствует | Биссектриса квадрата |
| `MouseHandlerFishBoneDraw.kt` | `MouseHandlerFishBoneDraw.java` | ✅ Соответствует | Рисование рыбьей кости |

| **Обработчики выбора** |
| `MouseHandlerSelectAll.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerUnselectAll.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerSelectRectangle.kt` | - | ❌ Отсутствует | Новый класс для Android |
| - | `MouseHandlerSelectLasso.java` | ❌ Отсутствует | Выбор лассо |
| - | `MouseHandlerUnselectLasso.java` | ❌ Отсутствует | Снятие выбора лассо |
| - | `MouseHandlerSelectLineIntersecting.java` | ❌ Отсутствует | Выбор пересекающихся линий |
| - | `MouseHandlerUnselectLineIntersecting.java` | ❌ Отсутствует | Снятие выбора пересекающихся линий |
| - | `MouseHandlerSelectPolygon.java` | ❌ Отсутствует | Выбор многоугольника |
| - | `MouseHandlerUnselectPolygon.java` | ❌ Отсутствует | Снятие выбора многоугольника |

| **Обработчики навигации** |
| `MouseHandlerPan.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerZoom.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerRotate.kt` | - | ❌ Отсутствует | Новый класс для Android |
| - | `MouseHandlerMoveCreasePattern.java` | ❌ Отсутствует | Перемещение паттерна сгибов |

| **Обработчики отображения** |
| `MouseHandlerDisplayLengthBetweenPoints1.kt` | `MouseHandlerDisplayLengthBetweenPoints1.java` | ✅ Соответствует | Отображение длины между точками 1 |
| `MouseHandlerDisplayLengthBetweenPoints2.kt` | `MouseHandlerDisplayLengthBetweenPoints2.java` | ✅ Соответствует | Отображение длины между точками 2 |
| `MouseHandlerDisplayAngleBetweenThreePoints1.kt` | `MouseHandlerDisplayAngleBetweenThreePoints1.java` | ✅ Соответствует | Отображение угла между тремя точками 1 |
| `MouseHandlerDisplayAngleBetweenThreePoints2.kt` | `MouseHandlerDisplayAngleBetweenThreePoints2.java` | ✅ Соответствует | Отображение угла между тремя точками 2 |
| `MouseHandlerDisplayAngleBetweenThreePoints3.kt` | `MouseHandlerDisplayAngleBetweenThreePoints3.java` | ✅ Соответствует | Отображение угла между тремя точками 3 |

| **Обработчики точек** |
| `MouseHandlerDrawPoint.kt` | `MouseHandlerDrawPoint.java` | ✅ Соответствует | Рисование точек |
| `MouseHandlerDeletePoint.kt` | `MouseHandlerDeletePoint.java` | ✅ Соответствует | Удаление точек |
| - | `MouseHandlerVertexDeleteOnCrease.java` | ❌ Отсутствует | Удаление вершины на линии сгиба |
| - | `MouseHandlerVertexMakeAngularlyFlatFoldable.java` | ❌ Отсутствует | Создание углово плоскоскладываемой вершины |

| **Обработчики текста и комментариев** |
| `MouseHandlerText.kt` | `MouseHandlerText.java` | ✅ Соответствует | Работа с текстом |
| `MouseHandlerComment.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики сетки и привязки** |
| `MouseHandlerGridSnap.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики слоев и групп** |
| `MouseHandlerLayer.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerGroup.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики видимости** |
| `MouseHandlerVisibility.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики истории** |
| `MouseHandlerUndo.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerRedo.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики поиска и фильтрации** |
| `MouseHandlerSearch.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerFilter.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Обработчики специальных операций** |
| `MouseHandlerModifyCalculatedShape.kt` | `MouseHandlerModifyCalculatedShape.java` | ✅ Соответствует | Модификация вычисленной формы |
| - | `MouseHandlerMoveCalculatedShape.java` | ❌ Отсутствует | Перемещение вычисленной формы |
| `MouseHandlerVoronoiCreate.kt` | `MouseHandlerVoronoiCreate.java` | ✅ Соответствует | Создание диаграммы Вороного |
| `MouseHandlerFlatFoldableCheck.kt` | `MouseHandlerFlatFoldableCheck.java` | ✅ Соответствует | Проверка плоской складываемости |
| `MouseHandlerFoldableLineDraw.kt` | `MouseHandlerFoldableLineDraw.java` | ✅ Соответствует | Рисование складываемых линий |
| - | `MouseHandlerFoldableLineInput.java` | ❌ Отсутствует | Ввод складываемых линий |
| `MouseHandlerChangeCreaseType.kt` | `MouseHandlerChangeCreaseType.java` | ✅ Соответствует | Изменение типа линии сгиба |
| - | `MouseHandlerReplaceTypeSelect.java` | ❌ Отсутствует | Выбор типа для замены |
| `MouseHandlerChangeStandardFace.kt` | `MouseHandlerChangeStandardFace.java` | ✅ Соответствует | Изменение стандартной грани |
| `MouseHandlerBackgroundChangePosition.kt` | `MouseHandlerBackgroundChangePosition.java` | ✅ Соответствует | Изменение позиции фона |
| `MouseHandlerAddFoldingConstraints.kt` | `MouseHandlerAddFoldingConstraints.java` | ✅ Соответствует | Добавление ограничений складывания |
| `MouseHandlerDeleteTypeSelect.kt` | `MouseHandlerDeleteTypeSelect.java` | ✅ Соответствует | Выбор типа для удаления |
| - | `MouseHandlerPolygonSetNoCorners.java` | ❌ Отсутствует | Установка многоугольника без углов |
| - | `MouseHandlerInward.java` | ❌ Отсутствует | Внутренние операции |
| - | `MouseHandlerOperationFrameCreate.java` | ❌ Отсутствует | Создание рамки операции |
| `MouseHandlerCopy.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerMove.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerMeasure.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerFold.kt` | - | ❌ Отсутствует | Новый класс для Android |
| `MouseHandlerUnfold.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Менеджер обработчиков** |
| `MouseHandlerManager.kt` | - | ❌ Отсутствует | Новый класс для Android |

| **Дополнительные классы оригинального проекта** |
| - | `StepGraph.java` | ❌ Отсутствует | Граф шагов |
| - | `StepNode.java` | ❌ Отсутствует | Узел шага |

## Статистика

### Общее количество классов:
- **Android проект:** 89 классов
- **oriedita-master:** 67 классов

### Соответствия:
- **✅ Соответствует:** 45 классов (50.6%)
- **❌ Отсутствует в оригинале:** 44 класса (49.4%)
- **❌ Отсутствует в Android:** 22 класса (24.7%)

### Категории новых классов в Android:

1. **Навигация и управление камерой** (4 класса):
   - `MouseHandlerPan.kt`
   - `MouseHandlerZoom.kt`
   - `MouseHandlerRotate.kt`
   - `MouseHandlerGridSnap.kt`

2. **Выбор и выделение** (3 класса):
   - `MouseHandlerSelectAll.kt`
   - `MouseHandlerUnselectAll.kt`
   - `MouseHandlerSelectRectangle.kt`

3. **Геометрические построения** (3 класса):
   - `MouseHandlerParallel.kt`
   - `MouseHandlerPerpendicular.kt`
   - `MouseHandlerTangent.kt`

4. **Симметрия** (2 класса):
   - `MouseHandlerSymmetricLine.kt`
   - `MouseHandlerSymmetricPoint.kt`

5. **Углы и измерения** (3 класса):
   - `MouseHandlerAngleMeasure.kt`
   - `MouseHandlerAngleSnap.kt`
   - `MouseHandlerAngleBisector.kt`

6. **Вспомогательные линии** (6 классов):
   - `MouseHandlerCreaseMakeAuxEdge.kt`
   - `MouseHandlerCreaseMakeAuxLive.kt`
   - `MouseHandlerCreaseMakeAuxLiveEdge.kt`
   - `MouseHandlerCreaseMakeAuxLiveMountain.kt`
   - `MouseHandlerCreaseMakeAuxMountain.kt`
   - `MouseHandlerCreaseMakeAuxValley.kt`

7. **Управление проектом** (6 классов):
   - `MouseHandlerUndo.kt`
   - `MouseHandlerRedo.kt`
   - `MouseHandlerLayer.kt`
   - `MouseHandlerGroup.kt`
   - `MouseHandlerVisibility.kt`
   - `MouseHandlerManager.kt`

8. **Поиск и фильтрация** (2 класса):
   - `MouseHandlerSearch.kt`
   - `MouseHandlerFilter.kt`

9. **Дополнительные операции** (6 классов):
   - `MouseHandlerCopy.kt`
   - `MouseHandlerMove.kt`
   - `MouseHandlerMeasure.kt`
   - `MouseHandlerFold.kt`
   - `MouseHandlerUnfold.kt`
   - `MouseHandlerComment.kt`

10. **Специализированные окружности** (1 класс):
    - `MouseHandlerCircleInscribed.kt`

### Категории отсутствующих в Android классов:

1. **Рисование линий сгиба** (4 класса):
   - `MouseHandlerDrawCreaseAngleRestricted.java`
   - `MouseHandlerDrawCreaseFree.java`
   - `MouseHandlerDrawCreaseRestricted.java`
   - `MouseHandlerDrawCreaseSymmetric.java`

2. **Геометрические построения** (3 класса):
   - `MouseHandlerParallelDraw.java`
   - `MouseHandlerParallelDrawWidth.java`
   - `MouseHandlerPerpendicularDraw.java`

3. **Выбор элементов** (6 классов):
   - `MouseHandlerSelectLasso.java`
   - `MouseHandlerUnselectLasso.java`
   - `MouseHandlerSelectLineIntersecting.java`
   - `MouseHandlerUnselectLineIntersecting.java`
   - `MouseHandlerSelectPolygon.java`
   - `MouseHandlerUnselectPolygon.java`

4. **Работа с вершинами** (2 класса):
   - `MouseHandlerVertexDeleteOnCrease.java`
   - `MouseHandlerVertexMakeAngularlyFlatFoldable.java`

5. **Специальные операции** (7 классов):
   - `MouseHandlerLengthenCreaseSameColor.java`
   - `MouseHandlerMoveCalculatedShape.java`
   - `MouseHandlerMoveCreasePattern.java`
   - `MouseHandlerReplaceTypeSelect.java`
   - `MouseHandlerPolygonSetNoCorners.java`
   - `MouseHandlerInward.java`
   - `MouseHandlerOperationFrameCreate.java`

6. **Дополнительные классы** (2 класса):
   - `StepGraph.java`
   - `StepNode.java`

## Выводы

1. **Высокая совместимость:** 50.6% классов имеют прямые аналоги в оригинальном проекте
2. **Расширенная функциональность:** Android версия добавляет множество новых возможностей
3. **Адаптация под мобильные устройства:** Новые классы для навигации, выбора и управления
4. **Улучшенный UX:** Дополнительные обработчики для лучшего пользовательского опыта
5. **Модульность:** Новый `MouseHandlerManager.kt` для централизованного управления обработчиками
6. **Некоторые потери:** 22 класса из оригинального проекта не реализованы в Android версии

Android версия значительно расширяет функциональность оригинального проекта, добавляя возможности, специфичные для мобильных устройств и современных требований к пользовательскому интерфейсу, но при этом некоторые специализированные функции оригинального проекта остались нереализованными. 