package com.jazyky.trainer.data.model

import kotlinx.serialization.Serializable

/**
 * Datové modely appky.
 *
 * DŮLEŽITÉ: tyto třídy musí odpovídat struktuře JSON souborů v assets/languages/.
 * Když se mění schéma lekcí, mění se na dvou místech: tady a v JSON datech.
 */

@Serializable
data class LanguagesIndex(
    val languages: List<LanguageInfo>
)

@Serializable
data class LanguageInfo(
    val code: String,      // "en", "de", ...
    val name: String,      // "English"
    val name_cz: String    // "Angličtina"
)

@Serializable
data class LessonIndex(
    val language: String,
    val lessons: List<LessonMeta>
)

@Serializable
data class LessonMeta(
    val id: String,
    val file: String,       // jméno souboru s lekcí, např. lesson_kids_beginner_01.json
    val audience: String,   // "kids" nebo "adult_corporate"
    val level: String,      // "beginner", "intermediate", ...
    val title_cz: String
)

@Serializable
data class Lesson(
    val id: String,
    val language: String,
    val audience: String,
    val level: String,
    val title: String,
    val title_cz: String,
    val vocabulary: List<VocabularyItem> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
    val exercises: List<Exercise> = emptyList()
)

@Serializable
data class VocabularyItem(
    val word: String,
    val translation_cz: String,
    val example: String,
    val example_cz: String,
    val tts_text: String
)

@Serializable
data class Conversation(
    val id: String,
    val title: String,
    val title_cz: String,
    val lines: List<ConversationLine>
)

@Serializable
data class ConversationLine(
    val speaker: String,
    val text: String,
    val text_cz: String
)

/**
 * Jedno cvičení. Pole jsou nepovinná, protože každý "type" používá jen některá z nich.
 * Typy podporované appkou zatím: listen_and_choose, fill_gap, match_pairs, speak_and_check.
 * Nový typ cvičení = přidat pole sem (pokud potřeba) a nový "case" v ExerciseScreen.
 */
@Serializable
data class Exercise(
    val type: String,
    val prompt_tts: String? = null,
    val options: List<String>? = null,
    val correct: String? = null,
    val sentence: String? = null,
    val answer: String? = null,
    val hint_cz: String? = null,
    val pairs: List<MatchPair>? = null,
    val target_phrase: String? = null,
    val tts_text: String? = null
)

@Serializable
data class MatchPair(
    val target: String,
    val cz: String
)
