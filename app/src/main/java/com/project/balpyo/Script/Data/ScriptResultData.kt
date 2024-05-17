package com.project.balpyo.Script.Data

import java.io.Serializable

data class ScriptResultData(val uid: String, val title : String, val secTime : Long, val script : String, val gptId : String) : Serializable