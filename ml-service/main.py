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



import os
from typing import Optional

_yield_model = None
_yield_le_region = None
_yield_le_crop = None

if os.path.exists('yield_model.pkl'):
    _yield_model = joblib.load('yield_model.pkl')
    _yield_le_region = joblib.load('yield_region_encoder.pkl')
    _yield_le_crop = joblib.load('yield_crop_encoder.pkl')


class YieldPredictionRequest(BaseModel):
    region: str
    crop: str
    precip_oct_mar: Optional[float] = None
    min_temp_winter: Optional[float] = None
    precip_apr_may: Optional[float] = None
    temp_sum_apr_may: Optional[float] = None
    frost_risk_spring: Optional[bool] = None
    precip_jun_jul: Optional[float] = None
    temp_sum_jun_jul: Optional[float] = None
    heat_stress_jun_jul: Optional[int] = None
    gtk_jun_jul: Optional[float] = None
    precip_aug_sep: Optional[float] = None
    temp_sum_aug_sep: Optional[float] = None
    heat_stress_aug_sep: Optional[int] = None
    gtk_apr_sep: Optional[float] = None
    temp_sum_apr_sep: Optional[float] = None
    total_heat_stress_days: Optional[int] = None
    min_temp_vegetation: Optional[float] = None


@app.post("/predict/yield")
def predict_yield(request: YieldPredictionRequest):
    if _yield_model is None:
        return {"error": "Yield model not loaded. Run train_yield.py first."}
    try:
        region_code = _yield_le_region.transform([request.region])[0]
        crop_code = _yield_le_crop.transform([request.crop])[0]
    except ValueError as e:
        return {"error": f"Unknown region or crop: {e}"}

    features = pd.DataFrame([[
        region_code, crop_code,
        request.precip_oct_mar, request.min_temp_winter,
        request.precip_apr_may, request.temp_sum_apr_may,
        int(request.frost_risk_spring) if request.frost_risk_spring is not None else 0,
        request.precip_jun_jul, request.temp_sum_jun_jul,
        request.heat_stress_jun_jul, request.gtk_jun_jul,
        request.precip_aug_sep, request.temp_sum_aug_sep,
        request.heat_stress_aug_sep,
        request.gtk_apr_sep, request.temp_sum_apr_sep,
        request.total_heat_stress_days, request.min_temp_vegetation
    ]], columns=[
        "region_encoded", "crop_encoded",
        "precip_oct_mar", "min_temp_winter",
        "precip_apr_may", "temp_sum_apr_may", "frost_risk_spring",
        "precip_jun_jul", "temp_sum_jun_jul", "heat_stress_jun_jul", "gtk_jun_jul",
        "precip_aug_sep", "temp_sum_aug_sep", "heat_stress_aug_sep",
        "gtk_apr_sep", "temp_sum_apr_sep", "total_heat_stress_days",
        "min_temp_vegetation"
    ])

    prediction = _yield_model.predict(features)[0]
    return {"predicted_yield": round(float(prediction), 2)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)