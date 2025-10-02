export interface Field {
  id: number
  name: string
  area: number
  coordinates: number[][]
  holes?: number[][][]  // массив отверстий (дырок) в поле
  createdAt: string
  updatedAt: string
}

export interface FieldCreateRequest {
  name: string
  area: number
  coordinates: number[][]
  holes?: number[][][]  // опциональные отверстия
}