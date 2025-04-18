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

# Приклади виправлення проблем

## 1. Проблема з форматуванням коду

### Проблема
```kotlin
fun processData(data:List<String>){
    for(item in data){
        println(item)
    }
}
```

### Рішення
```kotlin
fun processData(data: List<String>) {
    for (item in data) {
        println(item)
    }
}
```

## 2. Проблема з невикористаними імпортами

### До виправлення
```kotlin
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.R
import java.util.* // Невикористаний імпорт

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

### Після виправлення
```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

## 3. Проблема з небезпечними операціями з null

### До виправлення
```kotlin
fun processUserData(user: User?) {
    val name = user.name // Можливий NPE
    val email = user.email // Можливий NPE
    
    println("Name: $name, Email: $email")
}
```

### Після виправлення
```kotlin
fun processUserData(user: User?) {
    user?.let { safeUser ->
        val name = safeUser.name
        val email = safeUser.email
        
        println("Name: $name, Email: $email")
    } ?: run {
        println("User is null")
    }
}
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
