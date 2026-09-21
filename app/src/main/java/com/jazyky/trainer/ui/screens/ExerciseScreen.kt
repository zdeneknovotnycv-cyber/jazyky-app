package com.jazyky.trainer.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jazyky.trainer.data.LessonRepository
import com.jazyky.trainer.data.model.Exercise
import com.jazyky.trainer.data.model.Lesson
import com.jazyky.trainer.tts.TtsManager
import java.util.Locale

/**
 * Prochází cvičení jedné lekce jedno po druhém.
 * Podporované typy: listen_and_choose, fill_gap, match_pairs, speak_and_check.
 *
 * Přidání nového typu cvičení:
 *   1) doplň potřebná pole do data class Exercise (pokud chybí)
 *   2) přidej nový "when" case sem
 */
@Composable
fun ExerciseScreen(
    languageCode: String,
    fileName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { LessonRepository(context) }
    val tts = remember { TtsManager(context) }
    var lesson by remember { mutableStateOf<Lesson?>(null) }
    var index by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }

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
            Text(text = "Cvičení", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (finished || current.exercises.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hotovo! Skóre: $correctCount / ${current.exercises.size}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Zpět na lekci") }
            }
        } else {
            LinearProgressIndicator(
                progress = { (index).toFloat() / current.exercises.size },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExerciseItem(
                exercise = current.exercises[index],
                languageCode = languageCode,
                tts = tts,
                onAnswered = { wasCorrect ->
                    if (wasCorrect) correctCount++
                    if (index + 1 >= current.exercises.size) {
                        finished = true
                    } else {
                        index++
                    }
                }
            )
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: Exercise,
    languageCode: String,
    tts: TtsManager,
    onAnswered: (Boolean) -> Unit
) {
    when (exercise.type) {
        "listen_and_choose" -> ListenAndChooseExercise(exercise, languageCode, tts, onAnswered)
        "fill_gap" -> FillGapExercise(exercise, onAnswered)
        "match_pairs" -> MatchPairsExercise(exercise, onAnswered)
        "speak_and_check" -> SpeakAndCheckExercise(exercise, languageCode, tts, onAnswered)
        else -> {
            Text("Neznámý typ cvičení: ${exercise.type}")
            Button(onClick = { onAnswered(false) }) { Text("Přeskočit") }
        }
    }
}

@Composable
private fun ListenAndChooseExercise(
    exercise: Exercise,
    languageCode: String,
    tts: TtsManager,
    onAnswered: (Boolean) -> Unit
) {
    var selected by remember(exercise) { mutableStateOf<String?>(null) }

    Column {
        Text("Poslechni si slovo a vyber správný český překlad:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { tts.speak(exercise.prompt_tts ?: "", languageCode) }) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Přehrát")
        }
        Spacer(modifier = Modifier.height(16.dp))
        (exercise.options ?: emptyList()).forEach { option ->
            val isSelected = selected == option
            OutlinedButton(
                onClick = { if (selected == null) selected = option },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(option)
            }
            if (isSelected) {
                val correct = option == exercise.correct
                Text(
                    text = if (correct) "Správně!" else "Špatně, správně je: ${exercise.correct}",
                    color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAnswered(selected == exercise.correct) },
            enabled = selected != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pokračovat")
        }
    }
}

@Composable
private fun FillGapExercise(exercise: Exercise, onAnswered: (Boolean) -> Unit) {
    var input by remember(exercise) { mutableStateOf("") }
    var checked by remember(exercise) { mutableStateOf(false) }

    Column {
        Text("Doplň chybějící slovo:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(exercise.sentence ?: "", style = MaterialTheme.typography.titleMedium)
        Text("Nápověda (CZ): ${exercise.hint_cz ?: ""}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
            enabled = !checked
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (checked) {
            val correct = input.trim().equals(exercise.answer, ignoreCase = true)
            Text(
                text = if (correct) "Správně!" else "Správná odpověď je: ${exercise.answer}",
                color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { onAnswered(correct) }, modifier = Modifier.fillMaxWidth()) {
                Text("Pokračovat")
            }
        } else {
            Button(onClick = { checked = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Zkontrolovat")
            }
        }
    }
}

@Composable
private fun MatchPairsExercise(exercise: Exercise, onAnswered: (Boolean) -> Unit) {
    val pairs = exercise.pairs ?: emptyList()
    var selectedTarget by remember(exercise) { mutableStateOf<String?>(null) }
    var matched by remember(exercise) { mutableStateOf(setOf<String>()) }
    var mistakes by remember(exercise) { mutableStateOf(0) }

    Column {
        Text("Přiřaď dvojice (cizí slovo ↔ český překlad):", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                pairs.forEach { pair ->
                    val isMatched = matched.contains(pair.target)
                    OutlinedButton(
                        onClick = { if (!isMatched) selectedTarget = pair.target },
                        enabled = !isMatched,
                        modifier = Modifier.fillMaxWidth().padding(2.dp)
                    ) {
                        Text(pair.target)
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                pairs.shuffled(java.util.Random(exercise.hashCode().toLong())).forEach { pair ->
                    val isMatched = matched.contains(pair.target)
                    OutlinedButton(
                        onClick = {
                            val target = selectedTarget
                            if (target != null && !isMatched) {
                                if (target == pair.target) {
                                    matched = matched + pair.target
                                } else {
                                    mistakes++
                                }
                                selectedTarget = null
                            }
                        },
                        enabled = !isMatched,
                        modifier = Modifier.fillMaxWidth().padding(2.dp)
                    ) {
                        Text(pair.cz)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (matched.size == pairs.size && pairs.isNotEmpty()) {
            Button(onClick = { onAnswered(mistakes == 0) }, modifier = Modifier.fillMaxWidth()) {
                Text("Pokračovat")
            }
        } else {
            Text("Spárováno: ${matched.size} / ${pairs.size}")
        }
    }
}

@Composable
private fun SpeakAndCheckExercise(
    exercise: Exercise,
    languageCode: String,
    tts: TtsManager,
    onAnswered: (Boolean) -> Unit
) {
    var recognized by remember(exercise) { mutableStateOf<String?>(null) }
    var checked by remember(exercise) { mutableStateOf(false) }

    val speechLocale = if (languageCode == "de") "de-DE" else "en-US"

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            recognized = results?.firstOrNull()
        }
    }

    Column {
        Text("Poslechni si větu a zkus ji nahlas zopakovat:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(exercise.target_phrase ?: "", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Button(onClick = { tts.speak(exercise.tts_text ?: exercise.target_phrase ?: "", languageCode) }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Přehrát")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLocale)
                }
                launcher.launch(intent)
            }) {
                Icon(Icons.Filled.Mic, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mluvit")
            }
        }

        recognized?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Rozpoznáno: $it", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (!checked) {
            Button(
                onClick = { checked = true },
                enabled = recognized != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zkontrolovat")
            }
        } else {
            val normalizedTarget = normalize(exercise.target_phrase ?: "")
            val normalizedRecognized = normalize(recognized ?: "")
            val correct = normalizedRecognized == normalizedTarget
            Text(
                text = if (correct) "Výborně, výslovnost sedí!" else "Zkus to znovu příště, nebylo to úplně přesné.",
                color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { onAnswered(correct) }, modifier = Modifier.fillMaxWidth()) {
                Text("Pokračovat")
            }
        }
    }
}

private fun normalize(text: String): String =
    text.lowercase(Locale.getDefault()).trim().trim('.', '?', '!', ',')
