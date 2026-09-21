package com.jazyky.trainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jazyky.trainer.data.LessonRepository
import com.jazyky.trainer.data.model.Lesson
import com.jazyky.trainer.tts.TtsManager

/**
 * Zobrazení jedné lekce: slovní zásoba (s tlačítkem "přehrát nahlas")
 * a u firemní lekce i vzorové konverzace. Na konci tlačítko na cvičení.
 */
@Composable
fun LessonScreen(
    languageCode: String,
    fileName: String,
    onBack: () -> Unit,
    onStartExercises: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { LessonRepository(context) }
    val tts = remember { TtsManager(context) }
    var lesson by remember { mutableStateOf<Lesson?>(null) }

    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    LaunchedEffect(languageCode, fileName) {
        lesson = repository.getLesson(languageCode, fileName)
    }

    val current = lesson ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Zpět")
            }
            Text(text = current.title_cz, style = MaterialTheme.typography.headlineSmall)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(text = "Slovní zásoba", style = MaterialTheme.typography.titleMedium)
            }
            items(current.vocabulary) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.word, style = MaterialTheme.typography.titleSmall)
                            Text(text = item.translation_cz, style = MaterialTheme.typography.bodySmall)
                            Text(text = item.example, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { tts.speak(item.tts_text, languageCode) }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Přehrát")
                        }
                    }
                }
            }

            if (current.conversations.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Vzorové konverzace", style = MaterialTheme.typography.titleMedium)
                }
                items(current.conversations) { conversation ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = conversation.title_cz, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            conversation.lines.forEach { line ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "${line.speaker}: ${line.text}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = line.text_cz, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { tts.speak(line.text, languageCode) }) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "Přehrát")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onStartExercises,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spustit cvičení")
        }
    }
}
