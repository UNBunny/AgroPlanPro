from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import pandas as pd
from urllib.parse import urljoin
from io import StringIO

# Настройка веб-драйвера для максимальной скорости
options = webdriver.ChromeOptions()

# Headless режим - браузер без интерфейса (ЗНАЧИТЕЛЬНО быстрее!)
options.add_argument('--headless=new')

# Отключаем загрузку изображений, CSS и других ресурсов
prefs = {
    'profile.managed_default_content_settings.images': 2,  # Блокируем изображения
    'profile.default_content_setting_values.notifications': 2,
    'profile.managed_default_content_settings.stylesheets': 2,  # Блокируем CSS
    'profile.managed_default_content_settings.cookies': 2,
    'profile.managed_default_content_settings.javascript': 1,  # JavaScript нужен
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
options.add_argument('--disk-cache-size=0')  # Отключаем кеш
options.add_argument('--aggressive-cache-discard')
options.add_argument('--disable-cache')
options.add_argument('--disable-application-cache')
options.add_argument('--disable-offline-load-stale-cache')
options.add_argument('--disk-cache-size=0')
options.add_argument('--disable-gpu-shader-disk-cache')
options.add_argument('--media-cache-size=0')
options.add_argument('--disable-notifications')
options.add_argument('--disable-popup-blocking')

# Ускоряем загрузку страниц
options.page_load_strategy = 'eager'  # Не ждем полной загрузки всех ресурсов

options.add_experimental_option("excludeSwitches", ["enable-automation", "enable-logging"])
options.add_experimental_option('useAutomationExtension', False)

# ========================================
# НАСТРОЙКА: Ограничение количества страниц
# ========================================
# None = парсить все страницы
# Число = парсить только первые N страниц (для тестирования)
MAX_PAGES = 10  # Измените на None для парсинга всех страниц
# ========================================

# Инициализация драйвера
print("Инициализация Chrome драйвера...")
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=options)
print("Драйвер запущен!")

# Список для хранения всех данных
all_data = []

try:
    # Переход на страницу с календарем
    base_url = "https://www.zerno.ru"
    calendar_url = "https://www.zerno.ru/cerealspricescalendar/2020/wheat/3"
    print(f"Переход на: {calendar_url}")
    driver.get(calendar_url)

    # С eager стратегией страница уже готова!
    print("Страница загружена!")

    # Находим все ссылки в таблице календаря
    print("Поиск ссылок в календаре...")

    # Ищем все ссылки внутри td элементов, которые ведут на cerealspricesdate
    links = driver.find_elements(By.CSS_SELECTOR, "td a[href*='cerealspricesdate']")

    # Извлекаем URLs
    urls = []
    for link in links:
        href = link.get_attribute('href')
        if href and 'cerealspricesdate' in href:
            full_url = urljoin(base_url, href)
            if full_url not in urls:  # Избегаем дубликатов
                urls.append(full_url)

    print(f"Найдено {len(urls)} ссылок для обработки")

    # Применяем ограничение, если указано
    if MAX_PAGES is not None and MAX_PAGES > 0:
        urls = urls[:MAX_PAGES]
        print(f"⚠️  РЕЖИМ ТЕСТИРОВАНИЯ: Будет обработано только {len(urls)} страниц")
    else:
        print(f"📊 ПОЛНЫЙ РЕЖИМ: Будет обработано {len(urls)} страниц")

    if len(urls) == 0:
        print("Ссылки не найдены. Проверяем структуру страницы...")
        # Выводим все ссылки на странице для отладки
        all_links = driver.find_elements(By.TAG_NAME, "a")
        print(f"Всего ссылок на странице: {len(all_links)}")
        for link in all_links[:10]:
            print(f"  - {link.get_attribute('href')}")

    # Обрабатываем каждую ссылку
    for idx, url in enumerate(urls, 1):
        try:
            # Выводим прогресс для каждой страницы
            print(f"[{idx}/{len(urls)}] Обработка: {url}")

            driver.get(url)

            # С eager стратегией таблицы уже загружены, ждать не нужно!
            # Находим все таблицы на странице
            tables_elements = driver.find_elements(By.TAG_NAME, "table")

            # Парсим все таблицы и ищем ту, которая содержит "Класс 3", "Класс 4", "Класс 5"
            found_table = False

            for table_idx, table_elem in enumerate(tables_elements):
                try:
                    # Парсим HTML таблицы напрямую
                    table_html = table_elem.get_attribute('outerHTML')

                    # Проверяем, содержит ли таблица нужные заголовки
                    if 'Класс 3' in table_html or 'класс 3' in table_html.lower():
                        # Используем pandas для парсинга таблицы
                        # decimal=',' указывает, что запятая - разделитель десятичных знаков
                        tables_parsed = pd.read_html(
                            StringIO(table_html),
                            decimal=',',
                            thousands=' '
                        )

                        if not tables_parsed:
                            continue

                        table_df = tables_parsed[0]

                        # Проверяем структуру таблицы
                        # Ищем колонку с валютой
                        rub_rows = None

                        # Пробуем разные варианты поиска строк с руб/т
                        for col_idx in range(min(3, len(table_df.columns))):
                            col_data = table_df.iloc[:, col_idx].astype(str)
                            if any('руб/т' in str(val) or 'руб' in str(val) for val in col_data):
                                # Нашли колонку с валютой
                                rub_rows = table_df[col_data.str.contains('руб/т', na=False)].copy()
                                break

                        if rub_rows is not None and not rub_rows.empty:
                            # Добавляем колонку с датой
                            date_str = url.split('/')[-3]
                            rub_rows['Дата'] = date_str
                            rub_rows['URL'] = url

                            all_data.append(rub_rows)
                            found_table = True
                            break

                except Exception as table_error:
                    continue

            if not found_table:
                # Если не нашли по заголовкам, пробуем найти любую таблицу с руб/т
                for table_idx, table_elem in enumerate(tables_elements):
                    try:
                        table_html = table_elem.get_attribute('outerHTML')
                        if 'руб/т' in table_html or 'руб' in table_html:
                            tables_parsed = pd.read_html(
                                StringIO(table_html),
                                decimal=',',
                                thousands=' '
                            )
                            if tables_parsed:
                                table_df = tables_parsed[0]

                                # Ищем строки с руб/т в любой колонке
                                rub_mask = table_df.apply(lambda row: any('руб/т' in str(val) for val in row), axis=1)
                                rub_rows = table_df[rub_mask].copy()

                                if not rub_rows.empty:
                                    date_str = url.split('/')[-3]
                                    rub_rows['Дата'] = date_str
                                    rub_rows['URL'] = url

                                    all_data.append(rub_rows)
                                    found_table = True
                                    break
                    except:
                        continue

        except Exception as e:
            # Пропускаем ошибки молча
            continue

    # Объединяем все данные
    if all_data:
        combined_df = pd.concat(all_data, ignore_index=True)

        # Сохраняем все данные в CSV
        output_file = 'wheat_prices_rub_only_2020.csv'
        combined_df.to_csv(output_file, index=False, encoding='utf-8-sig')
        print(f"\n✓ Данные успешно сохранены в {output_file}")
        print(f"✓ Всего записей: {len(combined_df)}")

        # Выводим статистику
        print("\nПервые 10 строк данных:")
        print(combined_df.head(10))

        # Если есть колонка с городами, показываем уникальные города
        if len(combined_df.columns) > 0:
            print(f"\nКолонки в таблице: {list(combined_df.columns)}")

            # Вычисляем средние цены по городам для класса 3
            # Предполагаем: колонка 0 - Город, колонка 2 - Цена класс 3
            if len(combined_df.columns) >= 3:
                city_column = combined_df.columns[0]
                price_class3_column = combined_df.columns[2]

                # Преобразуем цены в числовой формат
                combined_df[price_class3_column] = pd.to_numeric(
                    combined_df[price_class3_column],
                    errors='coerce'
                )

                # Вычисляем средние цены по городам
                avg_prices = combined_df.groupby(city_column)[price_class3_column].mean().reset_index()
                avg_prices.columns = ['Город', 'Средняя цена (руб/т, класс 3)']
                avg_prices = avg_prices.sort_values('Средняя цена (руб/т, класс 3)', ascending=False)

                # Сохраняем средние цены
                avg_output_file = 'wheat_avg_prices_rub_class3_2020.csv'
                avg_prices.to_csv(avg_output_file, index=False, encoding='utf-8-sig')
                print(f"\n✓ Средние цены по городам (класс 3) сохранены в {avg_output_file}")

                # Выводим топ-10 городов с самыми высокими средними ценами
                print("\nТоп-10 городов с самыми высокими средними ценами (класс 3, руб/т):")
                print(avg_prices.head(10))

                # Также вычисляем статистику по всем классам
                print("\n--- Статистика по всем классам ---")
                for col_idx in [2, 4, 6]:  # Цены для классов 3, 4, 5
                    if col_idx < len(combined_df.columns):
                        col_name = combined_df.columns[col_idx]
                        combined_df[col_name] = pd.to_numeric(
                            combined_df[col_name],
                            errors='coerce'
                        )
                        avg_price = combined_df[col_name].mean()
                        print(f"Средняя цена {col_name}: {avg_price:.2f} руб/т")
    else:
        print("\nНе удалось извлечь данные")

except Exception as e:
    print(f"Произошла ошибка: {str(e)}")
    import traceback
    traceback.print_exc()

finally:
    # Закрываем браузер
    driver.quit()
    print("\nБраузер закрыт")

