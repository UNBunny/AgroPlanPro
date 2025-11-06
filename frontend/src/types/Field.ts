export interface Field {
  id: number
  fieldName: string
  crop_type: string
  status: string
  coordinates: number[][]
  holes?: number[][][]
  areaHectares: number
}

export interface FieldCreateRequest {
  fieldName: string
  crop_type: string
  status: string
  coordinates: number[][]
  holes?: number[][][]
  areaHectares: number
}