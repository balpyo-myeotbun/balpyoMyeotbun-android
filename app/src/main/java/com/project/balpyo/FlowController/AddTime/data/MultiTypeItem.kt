package com.project.balpyo.FlowController.AddTime.data

sealed class MultiTypeItem {
    data class TextItem(val text: String): MultiTypeItem()
    object BreathButtonItem : MultiTypeItem()
    object PPTButtonItem : MultiTypeItem()
    //object Divider: EditScriptItem()
}

