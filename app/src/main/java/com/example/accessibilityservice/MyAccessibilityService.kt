package com.example.accessibilityservice



import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    private val TAG = "MyAccessibilityService"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventLog = StringBuilder()
        eventLog.append("onAccessibilityEvent called | ")

        event?.let {
            eventLog.append("Event received: ${it.eventType} | ")

            val packageName = it.packageName.toString()
            eventLog.append("Package: $packageName | ")

            var eventProcessed = false

            when (it.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    eventLog.append("View Clicked: ${it.source?.className} | ")
                    eventLog.append("View Text: ${it.source?.text} | ")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    eventLog.append("Text Changed: ${it.text} | ")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    eventLog.append("View Scrolled: ${it.source?.className} | ")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    eventLog.append("Window State Changed: ${it.source?.className} | ")
                    eventLog.append("Current Package: $packageName | ")
                    eventProcessed = true
                }
            }

            // Если обработано событие, добавляем разделитель
            if (eventProcessed) {
                eventLog.append("-------------------------------------------------- | ")
            }

            // Рекурсивно обрабатываем все дочерние узлы
            it.source?.let { node ->
                traverseNodeTree(node, eventLog)
            }
        }

        // Выводим собранный лог
        Log.d(TAG, eventLog.toString())
    }

    private fun traverseNodeTree(node: AccessibilityNodeInfo, eventLog: StringBuilder) {
        eventLog.append("Node Info: ${node.className} - ${node.text} | ")
        if (node.childCount > 0) {
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { childNode ->
                    traverseNodeTree(childNode, eventLog)
                }
            }
        }
    }

    override fun onInterrupt() {
        // Обработка прерывания сервиса
        Log.d(TAG, "Service Interrupted")
    }
}
