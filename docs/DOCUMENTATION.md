# Документація ключових компонентів Trimly

## 1. LocationManager

```kotlin
/**
 * Менеджер для роботи з геолокацією користувача.
 *
 * Надає функціональність для отримання поточної локації користувача,
 * відстеження змін позиції та перевірки дозволів на доступ до локації.
 * Використовує Google Location Services для точного визначення позиції.
 *
 * @property currentLocation Поточна локація користувача
 * @property locationCallback Обробник змін локації
 * @constructor Створює новий екземпляр менеджера з контекстом
 * @throws SecurityException Якщо немає дозволу на доступ до локації
 * @sample
 * ```kotlin
 * val locationManager = LocationManager(context)
 * val location = locationManager.getCurrentLocation()
 * ```
 */
class LocationManager(private val context: Context) {
    // ...
}
```

## 2. BusinessRepository

```kotlin
/**
 * Репозиторій для роботи з даними закладів.
 *
 * Надає функціональність для отримання списку закладів,
 * пошуку закладів за різними критеріями та кешування результатів.
 * Використовує корутини для асинхронних операцій.
 *
 * @property apiService Сервіс для мережевих запитів
 * @property cache Кеш для зберігання даних
 * @constructor Створює новий екземпляр репозиторію
 * @sample
 * ```kotlin
 * val repository = BusinessRepository(apiService, cache)
 * val businesses = repository.getBusinessesNearby(location)
 * ```
 */
class BusinessRepository(
    private val apiService: BusinessApiService,
    private val cache: BusinessCache
) {
    /**
     * Отримує список закладів поблизу вказаної локації.
     *
     * Спочатку перевіряє кеш на наявність даних,
     * якщо даних немає - виконує мережевий запит.
     *
     * @param location Локація для пошуку закладів
     * @param radius Радіус пошуку в метрах
     * @return Список закладів
     * @throws NetworkException Якщо помилка мережі
     * @sample
     * ```kotlin
     * val businesses = repository.getBusinessesNearby(
     *     location = LatLng(50.4501, 30.5234),
     *     radius = 1000
     * )
     * ```
     */
    suspend fun getBusinessesNearby(
        location: LatLng,
        radius: Int = 1000
    ): List<Business> {
        // ...
    }
}
```

## 3. BookingViewModel

```kotlin
/**
 * ViewModel для роботи з бронюванням послуг.
 *
 * Керує станом бронювання, валідацією даних та взаємодією з репозиторієм.
 * Використовує LiveData для спостереження за змінами стану.
 *
 * @property repository Репозиторій для роботи з бронюваннями
 * @property validator Валідатор даних бронювання
 * @constructor Створює новий екземпляр ViewModel
 * @sample
 * ```kotlin
 * val viewModel = BookingViewModel(repository, validator)
 * viewModel.bookService(serviceId, date, time)
 * ```
 */
class BookingViewModel(
    private val repository: BookingRepository,
    private val validator: BookingValidator
) : ViewModel() {
    /**
     * Стан бронювання.
     *
     * @property isLoading Чи виконується зараз операція
     * @property error Повідомлення про помилку, якщо є
     * @property booking Дані поточного бронювання
     */
    data class BookingState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val booking: Booking? = null
    )

    /**
     * Бронює послугу на вказаний час.
     *
     * Валідує дані, перевіряє доступність часу
     * та створює нове бронювання.
     *
     * @param serviceId Ідентифікатор послуги
     * @param date Дата бронювання
     * @param time Час бронювання
     * @throws ValidationException Якщо дані не валідні
     * @throws TimeSlotNotAvailableException Якщо час зайнятий
     * @sample
     * ```kotlin
     * viewModel.bookService(
     *     serviceId = "123",
     *     date = LocalDate.now(),
     *     time = LocalTime.of(14, 30)
     * )
     * ```
     */
    fun bookService(serviceId: String, date: LocalDate, time: LocalTime) {
        // ...
    }
}
```

## Примітки щодо документації

1. **Формат документації:**
   - Використовується KDoc формат
   - Документація включає опис, параметри, винятки та приклади
   - Приклади показують типове використання

2. **Структура документації:**
   - Загальний опис класу/функції
   - Опис властивостей та параметрів
   - Опис винятків
   - Приклади використання

3. **Рекомендації:**
   - Документуйте всі публічні API
   - Надавайте приклади для складних методів
   - Описуйте обмеження та винятки 