from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import pandas as pd

app = FastAPI()

# Загружаем модель и энкодеры, которые ты только что создал
model = joblib.load('price_model.pkl')
le_city = joblib.load('city_encoder.pkl')
le_region = joblib.load('region_encoder.pkl')
le_crop = joblib.load('crop_encoder.pkl')

# Описываем, какие данные мы ждем от Java
class PredictionRequest(BaseModel):
    city: str
    region: str
    crop: str
    month: int
    day_of_year: int
    year: int

@app.post("/predict")
def predict_price(request: PredictionRequest):
    # Превращаем текст в коды, которые понимает модель
    try:
        city_code = le_city.transform([request.city])[0]
        region_code = le_region.transform([request.region])[0]
        crop_code = le_crop.transform([request.crop])[0]
    except ValueError:
        return {"error": "Unknown city, region or crop"}

    # Формируем вектор для предсказания
    features = pd.DataFrame([[city_code, region_code, crop_code, request.month, request.day_of_year, request.year]],
                            columns=['City_Code', 'Region_Code', 'Crop_Code', 'Month', 'DayOfYear', 'Year'])

    prediction = model.predict(features)[0]

    return {"predicted_price": round(float(prediction), 2)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)