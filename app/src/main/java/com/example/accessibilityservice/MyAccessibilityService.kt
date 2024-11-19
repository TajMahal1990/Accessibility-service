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

            val packageName = it.packageName.toString()
            eventLog.append("Package: $packageName\n")

            var eventProcessed = false

            // Обработка различных типов событий
            when (it.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    eventLog.append("Event: View Clicked\n")
                    eventLog.append("Class: ${it.source?.className}\n")
                    eventLog.append("Text: ${it.source?.text}\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    eventLog.append("Event: Text Changed\n")
                    eventLog.append("Text: ${it.text}\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    eventLog.append("Event: View Scrolled\n")
                    eventLog.append("Class: ${it.source?.className}\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    eventLog.append("Event: Window State Changed\n")
                    eventLog.append("Class: ${it.source?.className}\n")
                    eventLog.append("Current Package: $packageName\n")
                    eventProcessed = true
                }
            }

            // Если событие было обработано, добавляем разделитель
            if (eventProcessed) {
                eventLog.append("--------------------------------------------------\n")
            }

            // Рекурсивный обход всех дочерних узлов
            it.source?.let { node ->
                traverseNodeTree(node, eventLog)
            }
        }

        // Выводим собранный лог
        Log.d(TAG, eventLog.toString())
    }

    // Метод для рекурсивного обхода дерева узлов
    private fun traverseNodeTree(node: AccessibilityNodeInfo, eventLog: StringBuilder) {
        eventLog.append("Node Info: ${node.className} - ${node.text}\n")
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
