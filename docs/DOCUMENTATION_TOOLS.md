# Інструменти для автоматичної генерації документації

## 1. Dokka

**Опис:** Офіційний інструмент для генерації документації Kotlin коду.

**Переваги:**
- Підтримує KDoc формат
- Генерує документацію в різних форматах (HTML, Markdown, Javadoc)
- Інтегрується з Gradle
- Підтримує перехресні посилання між документацією

**Використання:**
```kotlin
plugins {
    id("org.jetbrains.dokka") version "1.9.10"
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}
```

## 2. KDoc

**Опис:** Стандартний формат документації для Kotlin.

**Переваги:**
- Синтаксис, схожий на Javadoc
- Підтримує Markdown
- Інтегрується з IDE
- Автоматична валідація документації

**Приклад використання:**
```kotlin
/**
 * Опис класу або функції
 *
 * @param name Опис параметра
 * @return Опис значення, що повертається
 * @throws ExceptionType Опис винятку
 */
```

## 3. Dokka + Dokka Plugin

**Опис:** Розширення для Dokka, що додає додаткові можливості.

**Переваги:**
- Генерація документації для Android проекту
- Підтримка Android специфічних компонентів
- Інтеграція з Android Studio
- Автоматична документація ресурсів

## 4. Dokka + Dokka Gradle Plugin

**Опис:** Gradle плагін для автоматизації генерації документації.

**Переваги:**
- Автоматична генерація при збірці
- Налаштування через Gradle
- Підтримка різних форматів виводу
- Інтеграція з CI/CD

## 5. Dokka + Dokka Android Plugin

**Опис:** Спеціалізований плагін для Android проектів.

**Переваги:**
- Автоматична документація Android компонентів
- Підтримка Android специфічних анотацій
- Документація манифесту
- Інтеграція з Android ресурсами

## Рекомендації щодо вибору інструментів

1. **Для простих проектів:**
   - Використовуйте базовий Dokka
   - Документуйте код за допомогою KDoc
   - Генеруйте документацію в HTML форматі

2. **Для Android проектів:**
   - Використовуйте Dokka Android Plugin
   - Документуйте Android специфічні компоненти
   - Налаштуйте автоматичну генерацію через Gradle

3. **Для CI/CD:**
   - Інтегруйте генерацію документації в пайплайн
   - Використовуйте Dokka Gradle Plugin
   - Зберігайте згенеровану документацію в артефактах

## Налаштування в build.gradle.kts

```kotlin
plugins {
    id("org.jetbrains.dokka") version "1.9.10"
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
    dokkaSourceSets {
        named("main") {
            moduleName.set("Trimly")
            includes.from("README.md")
            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl.set(uri("https://github.com/yourusername/trimly/tree/main/app/src/main/java").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}
```
