# Лінтери та статичний аналіз коду

## Огляд інструментів

### 1. ktlint

**Опис:** Офіційний лінтер для Kotlin, розроблений JetBrains.

**Переваги:**
- Автоматичне форматування коду
- Підтримка Android Studio
- Інтеграція з Gradle
- Велика кількість правил

### 2. Detekt

**Опис:** Статичний аналізатор коду для Kotlin.

**Переваги:**
- Детальний аналіз коду
- Налаштовувані правила
- Підтримка користувацьких правил
- Інтеграція з CI/CD

### 3. Android Lint

**Опис:** Вбудований інструмент для аналізу Android проектів.

**Переваги:**
- Специфічні правила для Android
- Перевірка ресурсів
- Аналіз манифесту
- Інтеграція з Android Studio

## Вибір лінтера

Для проекту Trimly обрано **ktlint** з наступних причин:
1. Офіційна підтримка від JetBrains
2. Простота налаштування
3. Автоматичне форматування
4. Широке використання в спільноті Kotlin

## Налаштування ktlint

### 1. Додавання залежності в build.gradle.kts

```kotlin
plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

ktlint {
    version.set("1.0.1")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
}
```

### 2. Конфігураційний файл (.editorconfig)

```ini
root = true

[*.{kt,kts}]
indent_size = 4
indent_style = space
max_line_length = 120
trim_trailing_whitespace = true
insert_final_newline = true
charset = utf-8
```

### 3. Правила форматування

Основні правила:
- Відступи: 4 пробіли
- Максимальна довжина рядка: 120 символів
- Фінальний перенос рядка
- Видалення зайвих пробілів
- UTF-8 кодування

### 4. Ігнорування файлів

Файли, які не перевіряються:
- Згенеровані файли
- Тестові файли
- Файли зовнішніх бібліотек

## Запуск лінтера

### 1. Перевірка коду

```bash
./gradlew ktlintCheck
```

### 2. Автоматичне форматування

```bash
./gradlew ktlintFormat
```

### 3. Інтеграція з Git

Додати pre-commit hook:
```bash
./gradlew addKtlintFormatGitPreCommitHook
```

## Важливі аспекти якості коду

1. **Стиль коду:**
   - Консистентність форматування
   - Читабельність
   - Дотримання конвенцій

2. **Безпека:**
   - Перевірка null-безпеки
   - Валідація вхідних даних
   - Безпечна робота з ресурсами

3. **Продуктивність:**
   - Оптимізація циклів
   - Ефективне використання пам'яті
   - Асинхронні операції

## Приклади виправлення проблем

1. **Проблема:** Занадто довгі рядки коду
   ```kotlin
   // До
   val result = someVeryLongFunctionName(withManyParameters, andAnotherParameter, andOneMoreParameter, andFinalParameter)
   
   // Після
   val result = someVeryLongFunctionName(
       withManyParameters,
       andAnotherParameter,
       andOneMoreParameter,
       andFinalParameter
   )
   ```

2. **Проблема:** Відсутність фінального переносу рядка
   ```kotlin
   // До
   class MyClass {
       fun myFunction() {
           // ...
       }
   }
   
   // Після
   class MyClass {
       fun myFunction() {
           // ...
       }
   }
   ```

3. **Проблема:** Зайві пробіли
   ```kotlin
   // До
   val x = 1  
   val y = 2  
   
   // Після
   val x = 1
   val y = 2
   ```

## Рекомендації

1. **Регулярна перевірка:**
   - Запускати лінтер перед кожним комітом
   - Інтегрувати в CI/CD пайплайн
   - Перевіряти новий код

2. **Налаштування правил:**
   - Адаптувати правила під потребу проекту
   - Додавати нові правила при необхідності
   - Оновлювати конфігурацію

3. **Автоматизація:**
   - Використовувати pre-commit hooks
   - Налаштувати автоматичне форматування
   - Інтегрувати з IDE

## Корисні посилання

- [Документація ktlint](https://github.com/pinterest/ktlint)
- [Правила форматування Kotlin](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Code Style](https://source.android.com/setup/contribute/code-style) 