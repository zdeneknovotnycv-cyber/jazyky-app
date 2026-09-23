package com.jazyky.trainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Shuffle
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
 * Odvodí "slug" kategorie z názvu souboru lekce, protože datový model
 * (kvůli zpětné kompatibilitě se stovkami existujících JSON souborů)
 * kategorii jako pole nemá – je zakódovaná jen v jméně souboru, např.:
 *   lesson_kids_family_people_03.json          -> family_people
 *   lesson_corporate_business_travel_01.json   -> business_travel
 *   lesson_kids_beginner_01.json                -> beginner
 *   lesson_adult_corporate_beginner_01.json     -> beginner
 */
private fun categorySlug(fileName: String): String {
    val noExt = fileName.removeSuffix(".json")
    val withoutPrefix = noExt
        .removePrefix("lesson_")
        .removePrefix("adult_corporate_")
        .removePrefix("corporate_")
        .removePrefix("kids_")
    // odstraň koncové číslo lekce, např. "_03"
    return withoutPrefix.replace(Regex("_\\d+$"), "")
}

private val kidsCategoryLabels = mapOf(
    "beginner" to ("🎈" to "První krůčky"),
    "family_people" to ("👨‍👩‍👧" to "Rodina a lidé"),
    "animals" to ("🐶" to "Zvířata"),
    "body_health" to ("🩹" to "Tělo a zdraví"),
    "birthday_celebrations" to ("🎂" to "Narozeniny a oslavy"),
    "clothes" to ("👕" to "Oblečení"),
    "daily_routine" to ("⏰" to "Denní režim"),
    "feelings_emotions" to ("😊" to "Pocity a emoce"),
    "food_drinks" to ("🍎" to "Jídlo a pití"),
    "home_rooms" to ("🏠" to "Doma a pokoje"),
    "imagination_stories" to ("📚" to "Pohádky a fantazie"),
    "nature_outdoors" to ("🌳" to "Příroda a venku"),
    "numbers_colors" to ("🔢" to "Čísla a barvy"),
    "school_classroom" to ("🏫" to "Škola a třída"),
    "shopping_kids" to ("🛒" to "Nakupování"),
    "sports_hobbies" to ("⚽" to "Sport a koníčky"),
    "time_days" to ("📅" to "Čas a dny"),
    "town_places" to ("🏙️" to "Město a místa"),
    "toys_games" to ("🧸" to "Hračky a hry"),
    "transport" to ("🚗" to "Doprava"),
    "weather_seasons" to ("☀️" to "Počasí a roční období")
)

private val corporateCategoryLabels = mapOf(
    "beginner" to ("💼" to "První den v kanceláři"),
    "business_travel" to ("✈️" to "Pracovní cesty"),
    "career_development" to ("📈" to "Kariérní rozvoj"),
    "customer_service" to ("🎧" to "Zákaznický servis"),
    "email" to ("📧" to "E-maily"),
    "finance_budgets" to ("💰" to "Finance a rozpočty"),
    "hr_recruitment" to ("🧑‍💼" to "HR a nábor"),
    "it_support" to ("💻" to "IT podpora"),
    "legal_contracts" to ("📄" to "Právo a smlouvy"),
    "marketing" to ("📣" to "Marketing"),
    "meetings_intros" to ("🤝" to "Schůzky a představování"),
    "negotiation_sales" to ("🤑" to "Vyjednávání a prodej"),
    "office_life" to ("🏢" to "Život v kanceláři"),
    "performance_feedback" to ("⭐" to "Hodnocení výkonu"),
    "phone_video_calls" to ("📞" to "Telefonáty a videohovory"),
    "problem_solving" to ("🧩" to "Řešení problémů"),
    "project_management" to ("📋" to "Řízení projektů"),
    "reports_presentations" to ("📊" to "Reporty a prezentace"),
    "scheduling_calendars" to ("🗓️" to "Plánování a kalendáře"),
    "smalltalk_networking" to ("☕" to "Smalltalk a networking"),
    "teamwork" to ("👥" to "Týmová práce")
)

private fun categoryIconAndLabel(audience: String, slug: String): Pair<String, String> {
    val map = if (audience == "kids") kidsCategoryLabels else corporateCategoryLabels
    return map[slug] ?: ("📘" to slug.replace('_', ' ').replaceFirstChar { it.uppercase() })
}

/**
 * Seznam lekcí pro daný jazyk. Čte se z assets/languages/<lang>/index.json,
 * takže nová lekce = nový JSON soubor + řádek v tomto indexu, žádný kód navíc.
 *
 * Lekce jsou rozdělené do dvou záložek (Děti / Dospělí) a v rámci záložky
 * seskupené podle tématu, aby uživatel nemusel rolovat přes stovky položek.
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
    var selectedAudience by remember { mutableStateOf("kids") }

    LaunchedEffect(languageCode) {
        lessons = repository.getLessonsForLanguage(languageCode)
    }

    val filtered = lessons.filter { it.audience == selectedAudience }
    val grouped = filtered
        .groupBy { categorySlug(it.file) }
        .toSortedMap()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Zpět")
            }
            Text(text = "Lekce", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Přepínač Děti / Dospělí
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = selectedAudience == "kids",
                onClick = { selectedAudience = "kids" },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("🧒 Děti")
            }
            SegmentedButton(
                selected = selectedAudience == "adult_corporate",
                onClick = { selectedAudience = "adult_corporate" },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("💼 Dospělí")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (filtered.isNotEmpty()) {
                    onLessonSelected(filtered.random().file)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = filtered.isNotEmpty()
        ) {
            Icon(Icons.Filled.Shuffle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Náhodné téma")
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            grouped.forEach { (slug, lessonsInCategory) ->
                val (icon, label) = categoryIconAndLabel(selectedAudience, slug)
                item(key = "header_$slug") {
                    Text(
                        text = "$icon  $label",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(lessonsInCategory, key = { it.file }) { lesson ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onLessonSelected(lesson.file) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = icon, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
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
    }
}
