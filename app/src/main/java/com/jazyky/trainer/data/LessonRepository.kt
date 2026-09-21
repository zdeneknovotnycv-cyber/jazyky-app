package com.jazyky.trainer.data

import android.content.Context
import com.jazyky.trainer.data.model.LanguagesIndex
import com.jazyky.trainer.data.model.Lesson
import com.jazyky.trainer.data.model.LessonIndex
import kotlinx.serialization.json.Json

/**
 * Čte veškerý obsah appky (jazyky, lekce) z assets/languages/.
 *
 * Jak přidat nový jazyk (např. francouzštinu):
 *   1) vytvoř složku assets/languages/fr/
 *   2) přidej tam index.json + soubory lekcí (stejná struktura jako en/de)
 *   3) přidej řádek do assets/languages/index.json
 *   Appka nepotřebuje žádnou změnu kódu.
 *
 * Jak přidat novou lekci do existujícího jazyka:
 *   1) přidej JSON soubor lekce do assets/languages/<kod>/
 *   2) přidej záznam do assets/languages/<kod>/index.json
 */
class LessonRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun getLanguages(): List<com.jazyky.trainer.data.model.LanguageInfo> {
        val text = context.assets.open("languages/index.json").bufferedReader().use { it.readText() }
        return json.decodeFromString<LanguagesIndex>(text).languages
    }

    fun getLessonsForLanguage(languageCode: String): List<com.jazyky.trainer.data.model.LessonMeta> {
        val text = context.assets
            .open("languages/$languageCode/index.json")
            .bufferedReader()
            .use { it.readText() }
        return json.decodeFromString<LessonIndex>(text).lessons
    }

    fun getLesson(languageCode: String, fileName: String): Lesson {
        val text = context.assets
            .open("languages/$languageCode/$fileName")
            .bufferedReader()
            .use { it.readText() }
        return json.decodeFromString<Lesson>(text)
    }
}
