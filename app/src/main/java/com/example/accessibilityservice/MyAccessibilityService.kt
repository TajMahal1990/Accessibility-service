package com.example.accessibilityservice


import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    private val TAG = "MyAccessibilityService"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventLog = StringBuilder()
        eventLog.append("onAccessibilityEvent called\n")

        event?.let {
            eventLog.append("Event received: ${it.eventType}\n")

            val packageName = it.packageName?.toString() ?: "unknown"
            if (packageName.contains("unknown")) return // пропускаем если пакет не известен

            eventLog.append("Package: $packageName\n")

            var eventProcessed = false

            // Обработка обработка различных типов
            when (it.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    val text = it.source?.text?.toString() ?: "null"
                    if (text.contains("null")) return // пропускаем если текст не известен
                    eventLog.append("Event: View Clicked\n")
                    eventLog.append("Class: ${it.source?.className}\n")
                    eventLog.append("Text: $text\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    val text = it.text?.joinToString(",") ?: "null"
                    if (text.contains("null")) return // пропускаем если текст не известен
                    eventLog.append("Event: Text Changed\n")
                    eventLog.append("Text: $text\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    eventLog.append("Event: View Scrolled\n")
                    eventLog.append("Class: ${it.source?.className}\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val className = it.source?.className?.toString() ?: "unknown"
                    if (className.contains("unknown")) return // пропускаем если класс не известен
                    eventLog.append("Event: Window State Changed\n")
                    eventLog.append("Class: $className\n")
                    eventLog.append("Current Package: $packageName\n")
                    eventProcessed = true
                }
            }


            if (eventProcessed) {
                eventLog.append("--------------------------------------------------\n")
            }

            // обход по дочерним классам
            it.source?.let { node ->
                traverseNodeTree(node, eventLog)
            }
        }

        // проверка на null и known
        val logString = eventLog.toString()
        if (logString.contains("null") || logString.contains("unknown")) return

        // Выводим собранный лог
        Log.d(TAG, logString)
    }

    // метод для рекусировки
    private fun traverseNodeTree(node: AccessibilityNodeInfo, eventLog: StringBuilder) {
        val className = node.className?.toString() ?: "unknown"
        val text = node.text?.toString() ?: "null"

        // пропускаем углы если в них unknown или null
        if (className.contains("unknown") || text.contains("null")) return

        eventLog.append("Node Info: $className - $text\n")
        if (node.childCount > 0) {
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { childNode ->
                    traverseNodeTree(childNode, eventLog)
                }
            }
        }
    }

    override fun onInterrupt() {
        // обработка прерывания сервиса
        Log.d(TAG, "Service Interrupted")
    }
}
