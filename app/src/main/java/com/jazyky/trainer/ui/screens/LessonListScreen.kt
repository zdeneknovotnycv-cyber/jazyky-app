package com.jazyky.trainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jazyky.trainer.data.LessonRepository
import com.jazyky.trainer.data.model.LessonMeta

private fun audienceLabel(audience: String): String = when (audience) {
    "kids" -> "Pro děti"
    "adult_corporate" -> "Pro dospělé – firemní komunikace"
    else -> audience
}

/**
 * Seznam lekcí pro daný jazyk. Čte se z assets/languages/<lang>/index.json,
 * takže nová lekce = nový JSON soubor + řádek v tomto indexu, žádný kód navíc.
 */
@Composable
fun LessonListScreen(
    languageCode: String,
    onBack: () -> Unit,
    onLessonSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { LessonRepository(context) }
    var lessons by remember { mutableStateOf<List<LessonMeta>>(emptyList()) }

    LaunchedEffect(languageCode) {
        lessons = repository.getLessonsForLanguage(languageCode)
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Zpět")
            }
            Text(text = "Lekce", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(lessons) { lesson ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onLessonSelected(lesson.file) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = lesson.title_cz, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${audienceLabel(lesson.audience)} · ${lesson.level}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
