
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

    # Ожидание загрузки страницы
    print("Ожидание загрузки страницы...")
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.TAG_NAME, "table"))
    )
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
            print(f"\n[{idx}/{len(urls)}] Обработка: {url}")
            driver.get(url)

            # Ожидание загрузки таблицы
            WebDriverWait(driver, 5).until(
                EC.presence_of_element_located((By.CLASS_NAME, "nit_calendar_month"))
            )


            # Находим таблицу nit_calendar_month
            table = driver.find_element(By.CLASS_NAME, "nit_calendar_month")

            # Парсим HTML таблицы напрямую
            table_html = table.get_attribute('outerHTML')

            # Используем pandas для парсинга таблицы
            # decimal=',' - запятая как разделитель десятичных знаков
            table_df = pd.read_html(
                StringIO(table_html),
                decimal=',',
                thousands=' '
            )[0]

            # Добавляем колонку с датой (извлекаем из URL)
            date_str = url.split('/')[-3]  # Извлекаем дату из URL (формат: 2020-01-09)
            table_df['Дата'] = date_str
            table_df['URL'] = url

            all_data.append(table_df)
            print(f"  Извлечено {len(table_df)} записей из таблицы с {len(table_df.columns)} колонками")

        except Exception as e:
            print(f"  Ошибка при обработке {url}: {str(e)}")
            continue

    # Объединяем все данные
    if all_data:
        combined_df = pd.concat(all_data, ignore_index=True)

        # Сохраняем все данные в CSV
        output_file = 'wheat_prices_by_city_2020.csv'
        combined_df.to_csv(output_file, index=False, encoding='utf-8-sig')
        print(f"\n✓ Данные успешно сохранены в {output_file}")
        print(f"✓ Всего записей: {len(combined_df)}")

        # Выводим статистику
        print("\nПервые 10 строк данных:")
        print(combined_df.head(10))

        # Если есть колонка с городами, показываем уникальные города
        if len(combined_df.columns) > 0:
            print(f"\nКолонки в таблице: {list(combined_df.columns)}")

            # Вычисляем средние цены по городам
            # Предполагаем, что первая колонка - это город, а вторая - цена
            if len(combined_df.columns) >= 2:
                city_column = combined_df.columns[0]
                price_column = combined_df.columns[1]

                # Преобразуем цены в числовой формат (если они строковые)
                combined_df[price_column] = pd.to_numeric(combined_df[price_column], errors='coerce')

                # Вычисляем средние цены по городам
                avg_prices = combined_df.groupby(city_column)[price_column].mean().reset_index()
                avg_prices.columns = ['Город', 'Средняя цена']
                avg_prices = avg_prices.sort_values('Средняя цена', ascending=False)

                # Сохраняем средние цены
                avg_output_file = 'wheat_avg_prices_by_city_2020.csv'
                avg_prices.to_csv(avg_output_file, index=False, encoding='utf-8-sig')
                print(f"\n✓ Средние цены по городам сохранены в {avg_output_file}")

                # Выводим топ-10 городов с самыми высокими средними ценами
                print("\nТоп-10 городов с самыми высокими средними ценами:")
                print(avg_prices.head(10))
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
