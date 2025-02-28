package com.sundayting.wancompose.common.ui.scrolldelete

//@Composable
//fun rememberScrollToDeleteState(): AnchoredDraggableState<DragValue> {
//    val animationSpec = tween<Float>()
//    val velocityThreshold = with(LocalDensity.current) { 100.dp.toPx() }
//    val positionalThreshold = { distance: Float -> distance * 0.2f }
//    return rememberSaveable(
//        LocalDensity.current,
//        saver = AnchoredDraggableState.Saver(
//            animationSpec = animationSpec,
//            positionalThreshold = positionalThreshold,
//            velocityThreshold = { velocityThreshold },
//        )
//    ) {
//        AnchoredDraggableState(
//            initialValue = DragValue.IDLE,
//            positionalThreshold = positionalThreshold,
//            velocityThreshold = { velocityThreshold },
//            animationSpec = animationSpec
//        )
//    }
//}

//@Composable
//fun ScrollToDelete(
//    modifier: Modifier = Modifier,
//    state: AnchoredDraggableState<DragValue> = rememberScrollToDeleteState(),
//    onClickDelete: (() -> Unit)? = null,
//    content: @Composable () -> Unit,
//) {
//
//    var deleteAreaWidth by remember {
//        mutableIntStateOf(0)
//    }
//
//    val anchors = remember(deleteAreaWidth) {
//        DraggableAnchors {
//            DragValue.SHOW at -deleteAreaWidth.toFloat()
//            DragValue.IDLE at 0f
//        }
//    }
//
//    SideEffect {
//        state.updateAnchors(anchors, state.currentValue)
//    }
//
//    ConstraintLayout(
//        modifier.anchoredDraggable(state, Orientation.Horizontal),
//    ) {
//        Box(
//            Modifier
//                .constrainAs(createRef()) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    height = Dimension.fillToConstraints
//                    end.linkTo(parent.end)
//                }
//                .background(WanTheme.colors.errorColor)
//                .onSizeChanged { deleteAreaWidth = it.width }
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = ripple(),
//                    enabled = state.currentValue == DragValue.SHOW
//                ) {
//                    onClickDelete?.invoke()
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            AlwaysDarkModeArea {
//                Text(
//                    text = stringResource(id = R.string.delete),
//                    style = WanTheme.typography.h6.copy(
//                        color = WanTheme.colors.level1TextColor
//                    ),
//                    modifier = Modifier.padding(horizontal = 15.dp)
//                )
//            }
//        }
//
//        Box(
//            Modifier
//                .constrainAs(createRef()) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    width = Dimension.fillToConstraints
//                }
//                .offset {
//                    IntOffset(
//                        x = state
//                            .requireOffset()
//                            .roundToInt(),
//                        y = 0
//                    )
//                }
//                .background(Color.Blue)
//        ) {
//            content()
//        }
//
//    }
//
//}
//
//enum class DragValue { IDLE, SHOW }
//
//@Composable
//@Preview(showBackground = true)
//private fun PreviewScrollToDelete() {
//    ScrollToDelete(Modifier.fillMaxWidth()) {
//        ArticleListSingleBean(
//            modifier = Modifier.fillMaxWidth(),
//            bean = remember {
//                ArticleList.ArticleUiBean(
//                    envelopePic = null,
//                    title = "我是标题我",
//                    date = "1小时之前",
//                    isNew = true,
//                    isStick = true,
//                    chapter = ArticleList.ArticleUiBean.Chapter(
//                        superChapterName = "广场Tab",
//                        chapterName = "自助"
//                    ),
//                    authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(
//                        author = "小茗同学",
//                    ),
//                    id = 50,
//                    isCollect = true,
//                    desc = "我是描述我是",
//                    tags = listOf(
//                        ArticleList.ArticleUiBean.Tag(
//                            name = "哈哈哈",
//                            url = "134"
//                        )
//                    )
//                )
//            },
//        )
//    }
//}