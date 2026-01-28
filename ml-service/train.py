import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import LabelEncoder
import joblib

# 1. Загрузка данных
df = pd.read_csv('all_crops_prices_2020_2025_20251204_212239.csv')
df = df.dropna(subset=['Цена_руб_т'])
df['Дата'] = pd.to_datetime(df['Дата'])

# 2. Словарь Город -> Область
city_to_region = {
    'Волгоград': 'Волгоградская область',
    'Воронеж': 'Воронежская область',
    'Краснодар': 'Краснодарский край',
    'Новороссийск': 'Краснодарский край',
    'Тамань': 'Краснодарский край',
    'Курск': 'Курская область',
    'Липецк': 'Липецкая область',
    'Орел': 'Орловская область',
    'Ростов-на-Дону': 'Ростовская область',
    'Азов': 'Ростовская область',
    'Таганрог': 'Ростовская область',
    'Ставрополь': 'Ставропольский край'
}

# Мапим города на области. Если города нет в словаре, будет 'Другой регион'
df['Область'] = df['Город'].map(city_to_region).fillna('Другой регион')

# 3. Признаки
df['Month'] = df['Дата'].dt.month
df['DayOfYear'] = df['Дата'].dt.dayofyear
df['Year'] = df['Дата'].dt.year

# Кодируем текст в числа для модели
le_city = LabelEncoder()
le_region = LabelEncoder()
le_crop = LabelEncoder()

df['City_Code'] = le_city.fit_transform(df['Город'])
df['Region_Code'] = le_region.fit_transform(df['Область'])
df['Crop_Code'] = le_crop.fit_transform(df['Культура'])

# 4. Обучение (теперь с Region_Code!)
X = df[['City_Code', 'Region_Code', 'Crop_Code', 'Month', 'DayOfYear', 'Year']]
y = df['Цена_руб_т']

model = RandomForestRegressor(n_estimators=100, random_state=42)
model.fit(X, y)

# 5. Сохранение
joblib.dump(model, 'price_model.pkl')
joblib.dump(le_city, 'city_encoder.pkl')
joblib.dump(le_region, 'region_encoder.pkl') # Не забудь сохранить и этот энкодер!
joblib.dump(le_crop, 'crop_encoder.pkl')

print("Success! Модель обучена с учетом регионов.")