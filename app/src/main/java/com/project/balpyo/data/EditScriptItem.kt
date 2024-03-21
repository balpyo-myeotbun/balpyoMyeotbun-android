package com.project.balpyo.data

sealed class EditScriptItem {
    data class TextItem(val text: String): EditScriptItem()
    data class ButtonItem(val text: String) : EditScriptItem()
    object Divider: EditScriptItem()
}
