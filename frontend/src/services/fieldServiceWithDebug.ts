import axios from 'axios'
import { Field, FieldCreateRequest } from '../types/Field'

const API_BASE_URL = 'http://localhost:8080/api'

// Создаем экземпляр axios с настроенными заголовками
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Добавляем перехватчики для логирования запросов и ответов
api.interceptors.request.use(
  (config) => {
    console.log(`[API] Отправка ${config.method?.toUpperCase()} запроса на: ${config.url}`, config.data);
    return config;
  },
  (error) => {
    console.error('[API] Ошибка при настройке запроса:', error);
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    console.log(`[API] Получен успешный ответ ${response.status} от: ${response.config.url}`, response.data);
    return response;
  },
  (error) => {
    if (error.response) {
      console.error(`[API] Ошибка ${error.response.status} от сервера:`, error.response.data);
      console.error(`[API] Полный ответ:`, error.response);
    } else if (error.request) {
      console.error('[API] Нет ответа от сервера:', error.request);
    } else {
      console.error('[API] Ошибка запроса:', error.message);
    }
    return Promise.reject(error);
  }
);

export const fieldService = {
  async getAllFields(): Promise<Field[]> {
    console.log('[FieldService] Запрашиваем список полей');
    try {
      const response = await api.get<Field[]>('/fields');
      console.log('[FieldService] Получен список полей:', response.data);
      return response.data;
    } catch (error) {
      console.error('[FieldService] Ошибка при получении списка полей:', error);
      throw error;
    }
  },

  async createField(field: FieldCreateRequest): Promise<Field> {
    console.log('[FieldService] Создаем новое поле:', field);
    try {
      // Проверяем данные перед отправкой
      if (!field.coordinates || field.coordinates.length < 3) {
        throw new Error('Недостаточно координат для создания поля');
      }
      
      // Проверяем формат координат
      field.coordinates.forEach((coord, idx) => {
        if (coord.length !== 2) {
          throw new Error(`Неверный формат координат в точке ${idx}: ${JSON.stringify(coord)}`);
        }
      });

      const response = await api.post<Field>('/fields', field);
      console.log('[FieldService] Поле успешно создано:', response.data);
      return response.data;
    } catch (error) {
      console.error('[FieldService] Ошибка при создании поля:', error);
      throw error;
    }
  },
}

// Экспортируем для отладки
export default fieldService;
