from datetime import datetime
from io import StringIO
from urllib.parse import urljoin

import pandas as pd
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager

# Настройка веб-драйвера для максимальной скорости
options = webdriver.ChromeOptions()

# Headless режим - браузер без интерфейса (ЗНАЧИТЕЛЬНО быстрее!)
options.add_argument('--headless=new')

# Отключаем загрузку изображений, CSS и других ресурсов
prefs = {
    'profile.managed_default_content_settings.images': 2,
    'profile.default_content_setting_values.notifications': 2,
    'profile.managed_default_content_settings.stylesheets': 2,
    'profile.managed_default_content_settings.cookies': 2,
    'profile.managed_default_content_settings.javascript': 1,
    'profile.managed_default_content_settings.plugins': 2,
    'profile.managed_default_content_settings.popups': 2,
    'profile.managed_default_content_settings.geolocation': 2,
    'profile.managed_default_content_settings.media_stream': 2,
}
options.add_experimental_option('prefs', prefs)

# Оптимизация производительности
options.add_argument('--disable-gpu')
options.add_argument('--no-sandbox')
options.add_argument('--disable-dev-shm-usage')
options.add_argument('--disable-extensions')
options.add_argument('--disable-logging')
options.add_argument('--disable-software-rasterizer')
options.add_argument('--disable-web-security')
options.add_argument('--allow-running-insecure-content')
options.add_argument('--ignore-certificate-errors')
options.add_argument('--window-size=1920,1080')
options.add_argument('--disable-blink-features=AutomationControlled')
options.add_argument('--disable-infobars')
options.add_argument('--disable-browser-side-navigation')
options.add_argument('--disable-features=VizDisplayCompositor')
options.add_argument('--disk-cache-size=0')
options.add_argument('--aggressive-cache-discard')
options.add_argument('--disable-cache')
options.add_argument('--disable-application-cache')
options.add_argument('--disable-offline-load-stale-cache')
options.add_argument('--disable-gpu-shader-disk-cache')
options.add_argument('--media-cache-size=0')
options.add_argument('--disable-notifications')
options.add_argument('--disable-popup-blocking')

# Ускоряем загрузку страниц
options.page_load_strategy = 'eager'

options.add_experimental_option("excludeSwitches", ["enable-automation", "enable-logging"])
options.add_experimental_option('useAutomationExtension', False)

# ========================================
# НАСТРОЙКА
# ========================================
START_YEAR = 2020
END_YEAR = datetime.now().year  # Текущий год (2025)

# Список культур для парсинга (название на английском как в URL)
# Можно расширить этот список по мере необходимости
CROPS = {
    'wheat': 'Пшеница',
    'barley': 'Ячмень',
    'corn': 'Кукуруза',
    # 'rye': 'Рожь',
    # 'oats': 'Овес',
    # 'sunflower': 'Подсолнечник',
    # 'soybean': 'Соя',
    # 'rapeseed': 'Рапс'
}

# Классы для культур (если применимо)
# Для пшеницы обычно: 1, 2, 3, 4, 5
# Для других культур может быть пусто или другие классы
CROP_CLASSES = {
    'wheat': [3, 4, 5],
    'barley': [],
    'corn': [],  # Кукуруза обычно без классов
    # 'rye': [1, 2, 3],
    # 'oats': [],
    # 'sunflower': [],
    # 'soybean': [],
    # 'rapeseed': []
}

# Максимум страниц для тестирования (None = все страницы)
# Для теста: установите небольшое число (например, 5)
# Для полного парсинга: установите None
MAX_PAGES_PER_YEAR = None  # Измените на число для тестирования (например, 5)

# ========================================

print("=" * 80)
print("ПАРСЕР ЦЕН НА ЗЕРНОВЫЕ КУЛЬТУРЫ")
print("=" * 80)
print(f"Период парсинга: {START_YEAR} - {END_YEAR}")
print(f"Культуры: {', '.join([f'{k} ({v})' for k, v in CROPS.items()])}")
print("=" * 80)

# Инициализация драйвера
print("\n[1/3] Инициализация Chrome драйвера...")
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)
print("✓ Драйвер запущен!")

# Список для хранения всех данных
all_data = []
base_url = "https://www.zerno.ru"

try:
    print("\n[2/3] Сбор данных...")

    # Проходим по всем годам
    for year in range(START_YEAR, END_YEAR + 1):
        print(f"\n{'=' * 80}")
        print(f"ГОД: {year}")
        print(f"{'=' * 80}")

        # Проходим по всем культурам
        for crop_code, crop_name in CROPS.items():
            print(f"\n  Культура: {crop_name} ({crop_code})")

            # Получаем список классов для данной культуры
            crop_classes = CROP_CLASSES.get(crop_code, [])

            # Если классов нет, парсим без указания класса
            if not crop_classes:
                crop_classes = [None]

            for crop_class in crop_classes:
                try:
                    # Формируем URL
                    if crop_class is not None:
                        calendar_url = f"{base_url}/cerealspricescalendar/{year}/{crop_code}/{crop_class}"
                        class_label = f"класс {crop_class}"
                    else:
                        calendar_url = f"{base_url}/cerealspricescalendar/{year}/{crop_code}"
                        class_label = "без класса"

                    print(f"    → {class_label}: {calendar_url}")

                    driver.get(calendar_url)

                    # Проверяем, что страница существует (нет ошибки 404)
                    if "404" in driver.title or "не найден" in driver.page_source.lower():
                        print(f"      ⚠️  Страница не найдена, пропускаем")
                        continue

                    # Ищем все ссылки на даты
                    links = driver.find_elements(By.CSS_SELECTOR, "td a[href*='cerealspricesdate']")

                    # Извлекаем URLs
                    urls = []
                    for link in links:
                        href = link.get_attribute('href')
                        if href and 'cerealspricesdate' in href:
                            full_url = urljoin(base_url, href)
                            if full_url not in urls:
                                urls.append(full_url)

                    if not urls:
                        print(f"      ⚠️  Ссылки не найдены")
                        continue

                    # Применяем ограничение для тестирования
                    if MAX_PAGES_PER_YEAR is not None:
                        urls = urls[:MAX_PAGES_PER_YEAR]
                        print(f"      ℹ️  Режим тестирования: обработка {len(urls)} страниц")

                    print(f"      ✓ Найдено {len(urls)} дат для обработки")

                    # Фильтруем даты - берем только 2 даты в месяц: до 15 и после 15 числа
                    # Извлекаем даты из URLs и группируем по месяцам
                    from collections import defaultdict
                    dates_by_month = defaultdict(list)

                    # Отладка: показываем первые несколько URL
                    if urls:
                        print(f"      🔍 Пример URL для парсинга: {urls[0]}")

                    for url in urls:
                        try:
                            # URL формат: .../cerealspricesdate/YYYY-MM-DD/crop_code/[class]
                            # Извлекаем компоненты пути
                            parts = url.split('/')

                            # Проверяем, есть ли 'cerealspricesdate' в URL
                            if 'cerealspricesdate' not in parts:
                                continue

                            # Ищем индекс 'cerealspricesdate'
                            idx = parts.index('cerealspricesdate')

                            # Проверяем, что после 'cerealspricesdate' есть минимум 1 элемент (дата)
                            if len(parts) <= idx + 1:
                                continue

                            # Дата идет одним сегментом: YYYY-MM-DD
                            date_part = parts[idx + 1]

                            # Парсим дату
                            date_components = date_part.split('-')
                            if len(date_components) != 3:
                                continue

                            year_part = date_components[0]
                            month_part = date_components[1]
                            day_part = date_components[2]

                            # Валидация: проверяем, что это числа
                            year_int = int(year_part)
                            month_int = int(month_part)
                            day_int = int(day_part)

                            year_month = f"{year_part}-{month_part.zfill(2)}"
                            dates_by_month[year_month].append((day_int, url))
                        except (ValueError, IndexError) as e:
                            # Отладка: показываем проблемные URL
                            # print(f"        ⚠️  Ошибка парсинга URL: {url} - {e}")
                            continue

                    print(f"      📊 Найдено месяцев с данными: {len(dates_by_month)}")

                    # Выбираем 2 даты из каждого месяца: до 15 числа и после 15 числа
                    filtered_urls = []
                    for year_month, dates in sorted(dates_by_month.items()):
                        dates.sort()  # Сортируем по дню

                        # До 15 числа (включительно)
                        before_15 = [url for day, url in dates if day <= 15]
                        if before_15:
                            filtered_urls.append(before_15[-1])  # Берем последнюю дату до 15

                        # После 15 числа
                        after_15 = [url for day, url in dates if day > 15]
                        if after_15:
                            filtered_urls.append(after_15[-1])  # Берем последнюю дату после 15

                    urls = filtered_urls
                    print(f"      ℹ️  После фильтрации (2 даты: до 15 и после 15): {len(urls)} дат")

                    # Обрабатываем каждую дату
                    for idx, url in enumerate(urls, 1):
                        try:
                            driver.get(url)

                            # Извлекаем дату из URL
                            # URL формат: .../cerealspricesdate/YYYY-MM-DD/crop_code/[class]
                            try:
                                parts = url.split('/')
                                idx_csp = parts.index('cerealspricesdate')
                                date_str = parts[idx_csp + 1]  # Дата уже в формате YYYY-MM-DD
                            except:
                                # Fallback: пытаемся найти строку, похожую на дату
                                import re
                                date_match = re.search(r'\d{4}-\d{2}-\d{2}', url)
                                date_str = date_match.group(0) if date_match else "Unknown"

                            # Ищем все таблицы на странице
                            tables_elements = driver.find_elements(By.TAG_NAME, "table")

                            for table_elem in tables_elements:
                                try:
                                    table_html = table_elem.get_attribute('outerHTML')

                                    # Проверяем, что таблица содержит данные о ценах
                                    if 'руб/т' not in table_html:
                                        continue

                                    # Парсим таблицу
                                    tables_parsed = pd.read_html(
                                        StringIO(table_html),
                                        decimal=',',
                                        thousands=' '
                                    )

                                    if not tables_parsed:
                                        continue

                                    table_df = tables_parsed[0]

                                    # Фильтруем только строки с руб/т (строка с валютой)
                                    rub_mask = table_df.apply(lambda row: any('руб/т' in str(val) for val in row),
                                                              axis=1)
                                    table_df = table_df[rub_mask].copy()

                                    if table_df.empty or len(table_df.columns) < 3:
                                        continue

                                    # Определяем индекс колонки с ценой в зависимости от класса
                                    # Структура таблицы: Город, Валюта, Класс3(цена,изм,%,тренд), Класс4(...), Класс5(...)
                                    # Колонки: 0=Город, 1=Валюта, 2=Класс3_цена, 6=Класс4_цена, 10=Класс5_цена
                                    price_col_index = 2  # По умолчанию класс 3

                                    if crop_class is not None:
                                        if crop_class == 3:
                                            price_col_index = 2
                                        elif crop_class == 4:
                                            price_col_index = 6
                                        elif crop_class == 5:
                                            price_col_index = 10

                                    # Проверяем, что нужная колонка существует
                                    if len(table_df.columns) <= price_col_index:
                                        continue

                                    # Извлекаем только нужные колон��и: Город (col 0), Цена (зависит от класса)
                                    result_df = pd.DataFrame({
                                        'Город': table_df.iloc[:, 0],
                                        'Цена_руб_т': pd.to_numeric(table_df.iloc[:, price_col_index], errors='coerce'),
                                        'Дата': date_str,
                                        'Год': year,
                                        'Культура': crop_name,
                                        'Культура_код': crop_code
                                    })

                                    if crop_class is not None:
                                        result_df['Класс'] = crop_class

                                    result_df['URL'] = url

                                    all_data.append(result_df)

                                    # Показываем прогресс каждые 10 страниц
                                    if idx % 10 == 0:
                                        print(f"        [{idx}/{len(urls)}]", end='\r')

                                    break  # Нашли нужную таблицу, переходим к следующей дате

                                except Exception as table_error:
                                    continue

                        except Exception as date_error:
                            continue

                    print(f"        ✓ Обработано {len(urls)} дат")

                except Exception as class_error:
                    print(f"      ✗ Ошибка: {str(class_error)}")
                    continue

    # Объединяем все данные
    print(f"\n[3/3] Сохранение данных...")

    if all_data:
        combined_df = pd.concat(all_data, ignore_index=True)

        # Сохраняем все данные
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        output_file = f'all_crops_prices_{START_YEAR}_{END_YEAR}_{timestamp}.csv'
        combined_df.to_csv(output_file, index=False, encoding='utf-8-sig')

        print(f"\n{'=' * 80}")
        print(f"✓ УСПЕШНО!")
        print(f"{'=' * 80}")
        print(f"Файл: {output_file}")
        print(f"Всего записей: {len(combined_df):,}")
        print(f"Колонки: {list(combined_df.columns)}")

        # Статистика по культурам
        if 'Культура' in combined_df.columns:
            print(f"\nСтатистика по культурам:")
            crop_stats = combined_df['Культура'].value_counts()
            for crop, count in crop_stats.items():
                print(f"  • {crop}: {count:,} записей")

        # Статистика по годам
        if 'Год' in combined_df.columns:
            print(f"\nСтатистика по годам:")
            year_stats = combined_df['Год'].value_counts().sort_index()
            for year, count in year_stats.items():
                print(f"  • {year}: {count:,} записей")

        # Показываем первые строки
        print(f"\nПервые 5 строк данных:")
        print(combined_df.head(5))

        print(f"\n{'=' * 80}")

    else:
        print("\n✗ Не удалось извлечь данные")

except Exception as e:
    print(f"\n✗ Произошла ошибка: {str(e)}")
    import traceback

    traceback.print_exc()

finally:
    driver.quit()
    print("\n✓ Браузер закрыт")
