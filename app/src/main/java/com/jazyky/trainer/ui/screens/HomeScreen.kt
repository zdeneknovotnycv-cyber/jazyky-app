package com.jazyky.trainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jazyky.trainer.data.LessonRepository
import com.jazyky.trainer.data.model.LanguageInfo

/**
 * Úvodní obrazovka: výběr jazyka.
 * Seznam jazyků appka nečte z kódu, ale z assets/languages/index.json,
 * takže přidání nového jazyka nevyžaduje zásah do téhle obrazovky.
 */
@Composable
fun HomeScreen(onLanguageSelected: (String) -> Unit) {
    val context = LocalContext.current
    val repository = remember { LessonRepository(context) }
    var languages by remember { mutableStateOf<List<LanguageInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        languages = repository.getLanguages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Jazyky Trainer",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vyber si jazyk, který se chceš učit",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(languages) { lang ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onLanguageSelected(lang.code) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = lang.name_cz, style = MaterialTheme.typography.titleLarge)
                        Text(text = lang.name, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
