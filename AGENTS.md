# ZN Sklo – závazný vzhled pro všechny ZN aplikace

Verze 1.0.0 (2026-11-11). Autor projektů: Zdeněk Novotný.

Tento soubor je určen pro AI nástroje (Claude, ChatGPT, Codex a další), které pomáhají stavět nebo upravovat ZN aplikace: ZN Foto, ZN-maps, ZN Viewer, ZN Monitor, kalkulačky a další.

## Pokyny pro AI

1. Každé uživatelské rozhraní pro ZN aplikace stav podle tohoto dokumentu. Nevymýšlej vlastní vzhled, barvy, písmo ani tvary.
2. Do aplikace vlož oba bloky CSS z konce tohoto souboru beze změny: nejdřív „tokens.css“, pod něj „zn-sklo.css“.
3. Rozhraní skládej z tříd `zn-*`. Vlastní CSS piš jen pro to, co třídy nepokrývají, a používej v něm proměnné (`var(--glass-1)`, `var(--radius-lg)`, `var(--space-4)`), ne pevné hodnoty.
4. Žádné rámečky (`border`) kolem panelů, karet a tlačítek. Žádné emoji v rozhraní. Žádné další barvy mimo zde uvedené.
5. Rozložení vyber z tří šablon níže. Když uživatel řekne jiný odstín, změň jen `--zn-hue`.
6. Texty rozhraní piš česky.
7. Po větší změně souboru zvyš číslo verze aplikace a přidej záznam do seznamu změn.
8. V nativní aplikaci (například Android v Kotlinu) se třídy CSS nepoužijí. Převeď stejné hodnoty barev, zaoblení, rozestupů a písma do prostředků té platformy a drž se zásad a šablon rozložení.
9. Když něco v tomto dokumentu chybí (nový druh prvku), navrhni ho ze stejného materiálu a tvarů a upozorni na to uživatele.

## Zásady

1. **Obsah je hlavní, ovládání ustupuje.** Fotka, mapa nebo data zabírají co nejvíc plochy. Ovládání nad nimi pluje a nezakrývá víc, než musí.
2. **Sklo místo rámečků.** Plochy se neoddělují čarou. Odděluje je rozmazané pozadí, odlesk na horní hraně (`glass-edge`) a stín (`shadow-float`).
3. **Jedna barva na aplikaci.** Rozhraní je bílé nebo tmavé podle motivu. Barevná je jen jedna zvýrazňující (`accent`) a stavové tečky.
4. **Čitelnost je podmínka.** Text má vždy kontrast aspoň 4,5:1. Když je pozadí pod sklem neklidné, použije se silnější sklo (`glass-2`) nebo plná plocha (`glass-solid`).
5. **Jedna věc najednou.** Zobrazuje se ovladač, se kterým člověk právě pracuje. Ostatní jsou o jedno klepnutí dál.

## Materiál

| Vrstva | Token | Kde |
|---|---|---|
| Pozadí | `bg-base`, `bg-glow-1`, `bg-glow-2` | Celá plocha aplikace (třída `zn-bg`). V prohlížeči obsahu může pozadí nahradit rozmazaná verze samotného obsahu. |
| Klidné sklo | `glass-1` | Velké panely, boční menu, karty. |
| Zvednuté sklo | `glass-2` | Tlačítka, štítky, aktivní položka, plovoucí kapsle. |
| Plná plocha | `glass-solid` | Vstupní pole a vybraná záložka. |

Sklo se nevrství víc než dvakrát na sebe. Na plnou plochu už další sklo nepatří.

## Odstín

Celý vzhled se ladí jednou hodnotou. Prvek s třídou `zn-tint` dostane `--zn-hue` (0–360) a přepočítá se pozadí, zvýraznění i tmavé tóny, ve světlém i tmavém motivu:

```html
<body class="zn-root zn-bg zn-tint" style="--zn-hue: 150">
```

Výchozí odstín je 220 (modrá). Stavové barvy `ok`, `warn` a `danger` se s odstínem nemění.

## Motivy

Tmavý motiv je výchozí pro práci s obrazem (ZN Foto, ZN Viewer). Světlý motiv se hodí pro přehledy a formuláře (ZN Monitor, kalkulačky). Přepíná se atributem `data-theme="dark"` nebo `data-theme="light"` na `<html>`.

## Rozložení

Tři šablony, podle druhu aplikace:

- **Nástroj na mobilu** – obsah přes celou plochu, nahoře malé kapsle, dole ovládací panel a přepínač nástrojů.
- **Přehled na počítači** – boční menu, uprostřed karty, vpravo stavový panel (třída `zn-shell`). Pod 900 px se sloupce skládají pod sebe.
- **Pracovní plocha** – velké plátno, nad ním plovoucí palety: nástroje vlevo, vlastnosti vpravo, měřítko dole.

Ukázky jsou ve skupině Rozložení.

## Tvary a rozestupy

- Panely a karty `radius-lg`, vnořené plochy `radius-md`, pole `radius-sm`, tlačítka a štítky `radius-pill`.
- Vnořený tvar má menší zaoblení než jeho obal, aby rohy působily souose.
- Okraj obrazovky a mezera mezi panely `space-4`, vnitřek karty `space-6`.
- Všechno, na co se klepe, má výšku aspoň 44 px.

## Písmo

Inter ve třech řezech (400, 500, 600). Název obrazovky `zn-display`, název karty `zn-title`, text `zn-body`, tlačítka `zn-label`, nadpisky verzálkami `zn-caption`. Adresy a kód písmem `zn-mono`.

Texty jsou česky, krátké, v rozkazovacím způsobu u tlačítek („Uložit nastavení“, „Spustit“). Stav se píše jedním slovem („Běží“, „Zastaveno“).

## Ikony

Jednoduché čárové ikony 20 px, tloušťka čáry 1,8, zaoblené konce (třída `zn-ic`). Žádné emoji v rozhraní. Tlačítko jen s ikonou má vždy `aria-label`.

## Styly písma

| Třída | Velikost | Použití |
|---|---|---|
| `zn-display` | 28px / 34px, řez 600 | Název obrazovky. Jednou na obrazovku. |
| `zn-title` | 20px / 26px, řez 600 | Název karty nebo panelu. |
| `zn-body` | 15px / 22px, řez 400 | Běžný text. |
| `zn-label` | 14px / 20px, řez 600 | Tlačítka, položky menu, hodnoty v polích. |
| `zn-caption` | 12px / 16px, řez 600 | Nadpisek sekce a popisek pole, psaný verzálkami. |
| `zn-value` | 22px / 26px, řez 600 | Právě upravovaná číselná hodnota. |
| `zn-mono` | 13px / 20px, řez 400 | Adresy, cesty k souborům, kód. |

## Kostra aplikace

```html
<!doctype html>
<html lang="cs" data-theme="dark">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>/* sem tokens.css a pod něj zn-sklo.css */</style>
</head>
<body class="zn-root zn-bg zn-tint" style="--zn-hue: 220; margin: 0; min-height: 100vh">
  <!-- obsah -->
</body>
</html>
```

Světlý motiv: `data-theme="light"`. Jiný odstín: jiná hodnota `--zn-hue` (0–360; 40 jantarová, 150 zelená, 220 modrá, 300 fialová).

## Třídy

| Třída | Co dělá |
|---|---|
| `zn-root` | Písmo, barva textu a základní nastavení. Patří na `<body>`. |
| `zn-bg` | Pozadí se dvěma měkkými barevnými skvrnami. |
| `zn-tint` | Zapne ladění odstínu přes `--zn-hue`. |
| `zn-glass` | Klidné sklo. S `zn-glass--raised` zvednuté, s `zn-glass--pill` kapsle. |
| `zn-btn` | Tlačítko. Varianty `--primary` (nejvýš jedno na kartu), `--solid`, `--quiet`, `--icon` (vždy s `aria-label`). |
| `zn-chip` | Stavový štítek: tečka a slovo. Varianty `--ok`, `--warn`, `--danger`. |
| `zn-field`, `zn-input` | Pole s popiskem nad sebou. Popisek má třídu `zn-eyebrow`. |
| `zn-card`, `zn-card__head`, `zn-card__title` | Karta. Používá se spolu s `zn-glass`. |
| `zn-seg`, `zn-seg__item` | Přepínač nástrojů. Vybraná položka má `aria-pressed="true"`. Svislá varianta `zn-seg--vertical`. |
| `zn-nav` | Položka bočního menu. Aktivní má `aria-current="page"`. |
| `zn-shell`, `zn-shell__side`, `zn-shell__aside` | Kostra přehledu: menu, obsah, stavový panel. |
| `zn-cards` | Mřížka karet. |
| `zn-stack`, `zn-row`, `zn-grow` | Sloupec, řada a roztažení prvku. |
| `zn-eyebrow`, `zn-muted` | Nadpisek verzálkami a tlumený text. |
| `zn-ic` | Čárová ikona 20 px (inline SVG, `viewBox="0 0 24 24"`). |

## Příklad: karta

```html
<div class="zn-glass zn-card">
  <div class="zn-card__head">
    <div>
      <p class="zn-eyebrow">01 · Zdroj dat</p>
      <h3 class="zn-card__title">Název služby</h3>
    </div>
    <span class="zn-chip zn-chip--ok">Běží</span>
  </div>
  <label class="zn-field">
    <span class="zn-eyebrow">Port</span>
    <input class="zn-input" type="number" value="5001">
  </label>
  <div class="zn-row">
    <button class="zn-btn zn-btn--primary">Spustit</button>
    <button class="zn-btn">Uložit nastavení</button>
  </div>
</div>
```

## tokens.css

```css
:root, [data-theme="dark"] {
  --bg-base: oklch(0.2 0.035 220);
  --bg-glow-1: oklch(0.42 0.11 220);
  --bg-glow-2: oklch(0.36 0.09 180);
  --glass-1: rgba(255,255,255,0.08);
  --glass-2: rgba(255,255,255,0.14);
  --glass-solid: rgba(255,255,255,0.92);
  --glass-edge: rgba(255,255,255,0.22);
  --ink: #ffffff;
  --ink-muted: rgba(255,255,255,0.74);
  --ink-on-solid: oklch(0.22 0.035 220);
  --accent: oklch(0.8 0.12 220);
  --on-accent: oklch(0.2 0.035 220);
  --ok: oklch(0.8 0.15 155);
  --warn: oklch(0.86 0.14 85);
  --danger: oklch(0.7 0.17 25);
  --shadow-float: 0 12px 36px rgba(0,0,0,0.28);
}
[data-theme="light"] {
  --bg-base: oklch(0.86 0.05 220);
  --bg-glow-1: oklch(0.95 0.03 200);
  --bg-glow-2: oklch(0.74 0.1 250);
  --glass-1: rgba(255,255,255,0.38);
  --glass-2: rgba(255,255,255,0.58);
  --glass-solid: rgba(255,255,255,0.94);
  --glass-edge: rgba(255,255,255,0.8);
  --ink: oklch(0.25 0.04 220);
  --ink-muted: oklch(0.4 0.04 220);
  --ink-on-solid: oklch(0.25 0.04 220);
  --accent: oklch(0.5 0.14 220);
  --on-accent: #ffffff;
  --ok: oklch(0.52 0.13 155);
  --warn: oklch(0.62 0.14 70);
  --danger: oklch(0.5 0.18 25);
  --shadow-float: 0 12px 36px rgba(30,60,110,0.16);
}
:root {
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-6: 24px;
  --space-8: 32px;
  --radius-sm: 12px;
  --radius-md: 20px;
  --radius-lg: 28px;
  --radius-pill: 999px;
  --blur-glass: 28px;
  --font-sans: Inter, system-ui, sans-serif;
  --font-mono: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
}
```

## zn-sklo.css

```css
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap");
/* ZN Sklo 1.0.0 – společný vzhled pro ZN aplikace. Hodnoty bere z tokens.css. */

.zn-root{font-family:var(--font-sans);color:var(--ink);font-size:15px;line-height:22px;-webkit-font-smoothing:antialiased;box-sizing:border-box}
.zn-root *,.zn-root *::before,.zn-root *::after{box-sizing:border-box}
.zn-root button,.zn-root input,.zn-root select{font-family:inherit}
.zn-root h1,.zn-root h2,.zn-root h3,.zn-root p{margin:0}

/* Odstín: na prvek s třídou zn-tint nastav --zn-hue (0–360) a celý vzhled se přeladí. */
.zn-tint{--bg-base:oklch(0.2 0.035 var(--zn-hue));--bg-glow-1:oklch(0.42 0.11 var(--zn-hue));--bg-glow-2:oklch(0.36 0.09 calc(var(--zn-hue) - 40));--accent:oklch(0.8 0.12 var(--zn-hue));--on-accent:oklch(0.2 0.035 var(--zn-hue));--ink-on-solid:oklch(0.22 0.035 var(--zn-hue))}
[data-theme="light"] .zn-tint,.zn-tint[data-theme="light"]{--bg-base:oklch(0.86 0.05 var(--zn-hue));--bg-glow-1:oklch(0.95 0.03 calc(var(--zn-hue) - 20));--bg-glow-2:oklch(0.74 0.1 calc(var(--zn-hue) + 30));--accent:oklch(0.5 0.14 var(--zn-hue));--on-accent:#ffffff;--ink:oklch(0.25 0.04 var(--zn-hue));--ink-muted:oklch(0.4 0.04 var(--zn-hue));--ink-on-solid:oklch(0.25 0.04 var(--zn-hue))}

/* Pozadí: dvě měkké barevné skvrny, přes které je sklo vidět. */
.zn-bg{background:radial-gradient(70% 60% at 10% 0%,var(--bg-glow-1),transparent 72%),radial-gradient(60% 55% at 100% 100%,var(--bg-glow-2),transparent 72%),var(--bg-base);color:var(--ink)}

/* Sklo: bez rámečku, jen odlesk nahoře a stín. */
.zn-glass{background:var(--glass-1);-webkit-backdrop-filter:blur(var(--blur-glass)) saturate(170%);backdrop-filter:blur(var(--blur-glass)) saturate(170%);box-shadow:inset 0 1px 0 var(--glass-edge),var(--shadow-float);border-radius:var(--radius-lg)}
.zn-glass--raised{background:var(--glass-2)}
.zn-glass--pill{border-radius:var(--radius-pill)}

.zn-eyebrow{font-size:12px;line-height:16px;font-weight:600;letter-spacing:.07em;text-transform:uppercase;color:var(--ink-muted)}
.zn-muted{color:var(--ink-muted)}
.zn-ic{width:20px;height:20px;fill:none;stroke:currentColor;stroke-width:1.8;stroke-linecap:round;stroke-linejoin:round;flex:none}

/* Tlačítka */
.zn-btn{display:inline-flex;align-items:center;justify-content:center;gap:8px;height:44px;padding:0 18px;border:0;border-radius:var(--radius-pill);background:var(--glass-2);color:var(--ink);font-size:14px;line-height:20px;font-weight:600;white-space:nowrap;cursor:pointer;box-shadow:inset 0 1px 0 var(--glass-edge);transition:transform .12s ease,filter .12s ease}
.zn-btn:hover{filter:brightness(1.08)}
.zn-btn:active{transform:scale(.97)}
.zn-btn:focus-visible,.zn-input:focus-visible,.zn-nav:focus-visible,.zn-seg__item:focus-visible{outline:2px solid var(--accent);outline-offset:2px}
.zn-btn:disabled{opacity:.45;cursor:default}
.zn-btn--primary{background:var(--accent);color:var(--on-accent);box-shadow:none}
.zn-btn--solid{background:var(--glass-solid);color:var(--ink-on-solid);box-shadow:none}
.zn-btn--quiet{background:transparent;box-shadow:none}
.zn-btn--icon{width:44px;padding:0}

/* Stavový štítek: tečka + slovo */
.zn-chip{display:inline-flex;align-items:center;gap:6px;height:26px;padding:0 10px;border-radius:var(--radius-pill);background:var(--glass-2);color:var(--ink);font-size:11px;line-height:1;font-weight:600;letter-spacing:.06em;text-transform:uppercase;white-space:nowrap}
.zn-chip::before{content:"";width:8px;height:8px;border-radius:50%;background:var(--ink-muted)}
.zn-chip--ok::before{background:var(--ok)}
.zn-chip--warn::before{background:var(--warn)}
.zn-chip--danger::before{background:var(--danger)}

/* Pole */
.zn-field{display:flex;flex-direction:column;gap:6px;min-width:0}
.zn-input{height:44px;width:100%;border:0;border-radius:var(--radius-sm);background:var(--glass-solid);color:var(--ink-on-solid);padding:0 14px;font-size:14px;font-weight:500}

/* Karta */
.zn-card{padding:var(--space-6);display:flex;flex-direction:column;gap:var(--space-4)}
.zn-card__head{display:flex;align-items:flex-start;justify-content:space-between;gap:var(--space-3)}
.zn-card__title{font-size:20px;line-height:26px;font-weight:600}

/* Přepínač (kapsle se záložkami) */
.zn-seg{display:inline-flex;align-items:center;gap:4px;padding:8px}
.zn-seg__item{height:48px;min-width:46px;padding:0 12px;border:0;border-radius:var(--radius-pill);background:transparent;color:var(--ink);display:inline-flex;align-items:center;justify-content:center;gap:8px;font-size:14px;font-weight:600;cursor:pointer}
.zn-seg__item[aria-pressed="true"]{background:var(--glass-solid);color:var(--ink-on-solid);padding:0 18px 0 14px}
.zn-seg--vertical{flex-direction:column}
.zn-seg--vertical .zn-seg__item{width:48px;min-width:0;padding:0}

/* Položka bočního menu */
.zn-nav{display:flex;align-items:center;gap:12px;height:44px;padding:0 14px;border-radius:var(--radius-md);color:var(--ink-muted);text-decoration:none;font-size:14px;font-weight:500}
.zn-nav[aria-current="page"]{background:var(--glass-2);color:var(--ink);font-weight:600;box-shadow:inset 0 1px 0 var(--glass-edge)}

/* Rozložení */
.zn-stack{display:flex;flex-direction:column;gap:var(--space-4)}
.zn-row{display:flex;align-items:center;gap:var(--space-3);flex-wrap:wrap}
.zn-grow{flex:1 1 0;min-width:0}
.zn-shell{display:grid;grid-template-columns:240px minmax(0,1fr) 300px;gap:var(--space-4);padding:var(--space-4);align-items:start}
.zn-shell__side,.zn-shell__aside{padding:var(--space-4)}
.zn-cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(320px,100%),1fr));gap:var(--space-4)}
@media (max-width:900px){.zn-shell{grid-template-columns:minmax(0,1fr)}}
@media (prefers-reduced-motion:reduce){.zn-btn{transition:none}}
```

## Změny

- **1.0.0** (2026-11-11) – první verze.
