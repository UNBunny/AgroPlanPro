import axios from 'axios'
import { Field, FieldCreateRequest } from '../types/Field'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const fieldService = {
  async getAllFields(): Promise<Field[]> {
    const response = await api.get<Field[]>('/fields')
    return response.data
  },

  async createField(field: FieldCreateRequest): Promise<Field> {
    const response = await api.post<Field>('/fields', field)
    return response.data
  },
}