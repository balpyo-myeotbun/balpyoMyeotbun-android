package com.project.balpyo.FlowController.data

sealed class EditScriptItem {
    data class TextItem(val text: String): EditScriptItem()
    data class ButtonItem(val text: String, val sText: String) : EditScriptItem()
    object Divider: EditScriptItem()
}
