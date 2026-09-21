# Jazyky Trainer (Android, Kotlin + Jetpack Compose)

Appka na výuku jazyků. Zatím **angličtina** a **němčina**, každá se dvěma
začátečnickými lekcemi: dětská ("Moje rodina a zvířata") a dospělá firemní
("První den v kanceláři" – meeting, telefonát, e-mail, prezentace).

Appka umí:
- **mluvit nahlas** (Android TextToSpeech) – slovíčka i celé konverzace
- **poslouchat výslovnost** (Android SpeechRecognizer) u cvičení typu "zopakuj větu"
- 4 typy cvičení: výběr z možností po poslechu, doplňovačka, párování slovíček, mluvení
- funguje **offline** (kromě rozpoznávání řeči, které na některých telefonech potřebuje
  internet, podle toho, jak má výrobce telefonu nastavený hlasový engine)

## Jak appku otevřít a spustit

1. Nainstaluj [Android Studio](https://developer.android.com/studio) (zdarma).
2. Otevři složku `JazykyApp` jako projekt (File → Open).
3. Android Studio si sám stáhne Gradle wrapper a závislosti (chce to internet
   při prvním otevření, pak už appka běží offline).
4. Připoj telefon (nebo spusť emulátor) a klikni na Run (zelený trojúhelník).

Appka je zatím jen pro Android (jak jsme se domluvili), minimální podporovaná
verze je Android 7.0 (API 24) – tím pokryješ prakticky všechny telefony v provozu.

## Jak appka funguje uvnitř (proč je to takhle levné na rozšiřování)

**Jazyk a obsah appky = data, ne kód.** Veškerý učební obsah je v souborech JSON
ve složce `app/src/main/assets/languages/`:

```
languages/
  index.json                              <- seznam dostupných jazyků
  en/
    index.json                            <- seznam lekcí pro angličtinu
    lesson_kids_beginner_01.json
    lesson_adult_corporate_beginner_01.json
  de/
    index.json
    lesson_kids_beginner_01.json
    lesson_adult_corporate_beginner_01.json
```

Appka (Kotlin kód) tahle data jen čte a zobrazuje. Neobsahuje nikde natvrdo
"angličtina" nebo "němčina" – seznam jazyků i lekcí appka staví za běhu podle
těchto souborů.

### Jak přidat úplně nový jazyk (např. francouzštinu)

1. Vytvoř složku `app/src/main/assets/languages/fr/`
2. Zkopíruj do ní `index.json` a lekce (stejná struktura jako `en/` nebo `de/`)
3. Přidej řádek do `app/src/main/assets/languages/index.json`:
   ```json
   {"code": "fr", "name": "Français", "name_cz": "Francouzština"}
   ```
4. Appku nemusíš nijak přeprogramovávat, francouzština se objeví na úvodní obrazovce.

### Jak přidat novou lekci do existujícího jazyka

1. Vytvoř nový soubor lekce, např. `lesson_kids_beginner_02.json` (stejná struktura
   jako existující lekce – viz níže).
2. Přidej záznam do `app/src/main/assets/languages/<jazyk>/index.json`.

### Struktura jedné lekce (JSON)

```json
{
  "id": "en_kids_beginner_02",
  "language": "en",
  "audience": "kids",              // "kids" nebo "adult_corporate"
  "level": "beginner",
  "title": "...",
  "title_cz": "...",
  "vocabulary": [
    { "word": "...", "translation_cz": "...", "example": "...", "example_cz": "...", "tts_text": "..." }
  ],
  "conversations": [                // nepovinné, hodí se hlavně pro firemní lekce
    {
      "id": "...", "title": "...", "title_cz": "...",
      "lines": [{ "speaker": "A", "text": "...", "text_cz": "..." }]
    }
  ],
  "exercises": [
    { "type": "listen_and_choose", "prompt_tts": "...", "options": ["..."], "correct": "..." },
    { "type": "fill_gap", "sentence": "... ___ ...", "answer": "...", "hint_cz": "..." },
    { "type": "match_pairs", "pairs": [{ "target": "...", "cz": "..." }] },
    { "type": "speak_and_check", "target_phrase": "...", "tts_text": "..." }
  ]
}
```

## Proč tahle technologie (a proč to bylo levné)

- **Kotlin + Jetpack Compose** – jeden jazyk, moderní a rychlý na psaní UI,
  doporučovaný přímo Googlem, nic navíc se neplatí.
- **TextToSpeech a SpeechRecognizer jsou součástí Androidu zdarma** – appka
  nepotřebuje žádné externí API ani měsíční poplatky za hlas. Kvalita hlasu
  závisí na tom, jaký hlasový balíček má uživatel v telefonu nastavený
  (na většině telefonů je to od Google, zvuk je slušný).
- **kotlinx.serialization** na čtení JSONu – lehká, oficiální knihovna, žádné
  poplatky.
- Appka **neběží na žádném serveru** – všechno je v telefonu, takže žádné
  provozní náklady na hosting ani API volání.

## Co appka zatím neumí (další rozšíření, o kterých jsme mluvili)

- **Spaced repetition** (chytré opakování slovíček v rostoucích intervalech) –
  zatím appka jede lekce lineárně, opakování bude potřeba dodat jako další
  krok (ukládání pokroku do malé databáze v telefonu, tzv. Room).
- **Streaky a body** (denní série, gamifikace) – zatím appka po dokončení
  cvičení jen ukáže skóre, bez ukládání historie mezi dny.
- **Víc lekcí a úrovní** (pokročilí, různá témata) – přidávání je popsané výše,
  je to jen otázka napsání dalšího obsahu.
- **iOS verze** – pokud časem budeš chtít i iPhone appku, datová vrstva
  (JSON lekce) zůstane stejná, přepisovala by se jen obrazovka appky.

Klidně navrhni, co z tohohle seznamu chceš udělat příště.
