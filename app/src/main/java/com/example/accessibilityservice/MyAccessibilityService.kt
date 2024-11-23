package com.example.accessibilityservice

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo



class MyAccessibilityService : AccessibilityService() {

    private val TAG = "MyAccessibilityService"
    private val loggedData = mutableSetOf<String>()  // Множество для хранения уникальных данных

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventLog = StringBuilder()
        eventLog.append("onAccessibilityEvent called\n")

        event?.let {
            val packageName = it.packageName.toString()
            eventLog.append("Event received: ${it.eventType}\n")
            eventLog.append("Package: $packageName\n")

            var eventProcessed = false

            // Добавление проверки на уникальность события по времени
            val eventTime = it.eventTime // Время события
            val eventKey = "$packageName-${it.eventType}-$eventTime"

            // Если событие уже обработано, пропустить его
            if (loggedData.contains(eventKey)) {
                return
            }

            // Добавляем уникальный ключ в лог
            loggedData.add(eventKey)

            // Обработка различных типов событий
            when (it.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    eventLog.append("Event: View Clicked\n")
                    addToLogIfValid(eventLog, "Class: ${it.source?.className}")
                    addToLogIfValid(eventLog, "Text: ${it.source?.text}")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    eventLog.append("Event: Text Changed\n")
                    addToLogIfValid(eventLog, "Text: ${it.text}")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    eventLog.append("Event: View Scrolled\n")
                    addToLogIfValid(eventLog, "Class: ${it.source?.className}")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    eventLog.append("Event: Window State Changed\n")
                    addToLogIfValid(eventLog, "Class: ${it.source?.className}")
                    eventLog.append("Current Package: $packageName\n")
                    eventProcessed = true
                }
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    eventLog.append("Пришло уведомление от: $packageName\n")

                    // Получаем текст уведомления, если он есть
                    val notificationText = event?.text?.joinToString(" ") ?: "Нет текста уведомления"
                    addToLogIfValid(eventLog, "Текст: $notificationText")

                    eventLog.append("--------------------------------------------------\n")
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
        addToLogIfValid(eventLog, "Node Info: ${node.className} - ${node.text}")
        if (node.childCount > 0) {
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { childNode ->
                    traverseNodeTree(childNode, eventLog)
                }
            }
        }
    }

    // Метод для добавления в лог, если текст не равен "null", "unknown" или не содержит их
    private fun addToLogIfValid(eventLog: StringBuilder, text: String?) {
        if (!text.isNullOrBlank() && !text.contains("null", ignoreCase = true) && !text.contains("unknown", ignoreCase = true)) {
            eventLog.append(text).append("\n")
        }
    }

    override fun onInterrupt() {
        // Обработка прерывания сервиса
        Log.d(TAG, "Service Interrupted")
    }
}