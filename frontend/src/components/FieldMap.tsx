import { useRef } from 'react'
import { MapContainer, TileLayer, Polygon, useMapEvents, Polyline, Marker } from 'react-leaflet'
import L from 'leaflet'
import { Field } from '../types/Field'

interface FieldMapProps {
  fields: Field[]
  isDrawing: boolean
  currentPolygon: number[][]
  onPolygonUpdate: (coordinates: number[][]) => void
  currentHoles?: number[][][]  // массив дырок в поле
  onHolesUpdate?: (holes: number[][][]) => void
  isCreatingHole?: boolean
  currentHole?: number[][]
  onHoleUpdate?: (hole: number[][]) => void
}

// Простой компонент для редактируемой точки
function EditablePoint({ position, index, onDrag, onDelete, color = '#3498db', size = 8, isDeletable = true }: {
  position: [number, number]
  index: number
  onDrag: (index: number, newPosition: [number, number]) => void
  onDelete?: (index: number) => void
  color?: string
  size?: number
  isDeletable?: boolean
}) {
  const markerRef = useRef<L.Marker>(null)

  const icon = L.divIcon({
    html: `<div style="background: ${color}; width: ${size}px; height: ${size}px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.4); cursor: grab; transition: all 0.2s; z-index: 1000;" onmouseover="this.style.transform='scale(1.2)'" onmouseout="this.style.transform='scale(1)'"></div>`,
    className: 'custom-marker',
    iconSize: [size, size],
    iconAnchor: [size/2, size/2]
  })

  const deleteIcon = L.divIcon({
    html: `<div style="background: #e74c3c; color: white; width: 14px; height: 14px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 9px; font-weight: bold; cursor: pointer; border: 1px solid white; box-shadow: 0 1px 3px rgba(0,0,0,0.3);">×</div>`,
    className: 'delete-marker',
    iconSize: [14, 14],
    iconAnchor: [-2, -2]  // Смещаем крестик в верхний правый угол
  })

  return (
    <>
      <Marker
        ref={markerRef}
        position={position}
        icon={icon}
        draggable={true}
        zIndexOffset={500}  // Основной маркер имеет меньший z-index
        eventHandlers={{
          dragend: (e) => {
            const marker = e.target
            const newPos = marker.getLatLng()
            onDrag(index, [newPos.lat, newPos.lng])
          }
        }}
      />
      {isDeletable && onDelete && (
        <Marker
          position={position}
          icon={deleteIcon}
          eventHandlers={{
            click: (e) => {
              e.originalEvent?.stopPropagation()
              e.originalEvent?.preventDefault()
              onDelete(index)
            },
            mousedown: (e) => {
              e.originalEvent?.stopPropagation()
              e.originalEvent?.preventDefault()
            },
            mouseover: (e) => {
              e.originalEvent?.stopPropagation()
            },
            mouseout: (e) => {
              e.originalEvent?.stopPropagation()
            }
          }}
          // Устанавливаем более высокий z-index для крестика
          zIndexOffset={1000}
        />
      )}
    </>
  )
}

// Компонент для обработки кликов на карте
function DrawingHandler({ isDrawing, currentPolygon, onPolygonUpdate, isCreatingHole, currentHole, onHoleUpdate }: {
  isDrawing: boolean
  currentPolygon: number[][]
  onPolygonUpdate: (coordinates: number[][]) => void
  isCreatingHole?: boolean
  currentHole?: number[][]
  onHoleUpdate?: (hole: number[][]) => void
}) {
  useMapEvents({
    click: (e) => {
      if (isDrawing && !isCreatingHole) {
        const { lat, lng } = e.latlng
        const newPoint = [lng, lat] // GeoJSON format: [longitude, latitude]
        onPolygonUpdate([...currentPolygon, newPoint])
      } else if (isCreatingHole && onHoleUpdate && currentHole) {
        const { lat, lng } = e.latlng
        const newPoint = [lng, lat] // GeoJSON format: [longitude, latitude]
        onHoleUpdate([...currentHole, newPoint])
      }
    }
  })

  return null
}

// Конвертация координат для отображения
function getPolygonPositions(coordinates: number[][]): [number, number][] {
  return coordinates.map(coord => [coord[1], coord[0]]) // Leaflet format: [latitude, longitude]
}

// Функция для расчета площади полигона в гектарах
function calculatePolygonArea(coordinates: number[][]): number {
  if (coordinates.length < 3) return 0

  // Используем формулу для сферических координат (более точно для географических данных)
  const earthRadius = 6371000 // радиус Земли в метрах
  let area = 0

  for (let i = 0; i < coordinates.length; i++) {
    const j = (i + 1) % coordinates.length
    const lat1 = coordinates[i][1] * Math.PI / 180 // широта в радианах
    const lat2 = coordinates[j][1] * Math.PI / 180
    const deltaLon = (coordinates[j][0] - coordinates[i][0]) * Math.PI / 180 // долгота в радианах
    
    area += deltaLon * (2 + Math.sin(lat1) + Math.sin(lat2))
  }

  area = Math.abs(area * earthRadius * earthRadius / 2)
  
  // Переводим в гектары (1 гектар = 10,000 м²)
  return area / 10000
}

// Функция для расчета общей площади поля с учетом отверстий
export function calculateFieldArea(mainPolygon: number[][], holes: number[][][] = []): number {
  const mainArea = calculatePolygonArea(mainPolygon)
  const holesArea = holes.reduce((sum, hole) => sum + calculatePolygonArea(hole), 0)
  
  return Math.max(0, mainArea - holesArea) // площадь не может быть отрицательной
}

export function FieldMap({ 
  fields, 
  isDrawing, 
  currentPolygon, 
  onPolygonUpdate,
  currentHoles = [],
  onHolesUpdate,
  isCreatingHole = false,
  currentHole = [],
  onHoleUpdate
}: FieldMapProps) {

  // Обработчики для редактирования основного полигона
  const handleMainPolygonDrag = (index: number, newPosition: [number, number]) => {
    const newPolygon = [...currentPolygon]
    newPolygon[index] = [newPosition[1], newPosition[0]] // Convert back to GeoJSON
    onPolygonUpdate(newPolygon)
  }

  const handleMainPolygonDelete = (index: number) => {
    if (currentPolygon.length > 3) { // Минимум 3 точки для полигона
      const newPolygon = currentPolygon.filter((_, i) => i !== index)
      onPolygonUpdate(newPolygon)
    }
  }

  // Обработчики для редактирования дырок
  const handleHoleDrag = (index: number, newPosition: [number, number]) => {
    if (onHoleUpdate) {
      const newHole = [...currentHole]
      newHole[index] = [newPosition[1], newPosition[0]] // Convert back to GeoJSON
      onHoleUpdate(newHole)
    }
  }

  const handleHoleDelete = (index: number) => {
    if (onHoleUpdate && currentHole.length > 3) { // Минимум 3 точки для полигона
      const newHole = currentHole.filter((_, i) => i !== index)
      onHoleUpdate(newHole)
    }
  }

  return (
    <MapContainer
      center={[55.7558, 37.6176]} // Москва
      zoom={10}
      style={{ height: '100%', width: '100%' }}
    >
      {/* Гибридная карта Google Satellite с подписями */}
      <TileLayer
        url="https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}"
        attribution="&copy; Google Satellite"
        maxZoom={20}
      />
      
      {/* Альтернатива - можно переключать слои */}
      {/* 
      <TileLayer
        url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
        attribution="&copy; Esri World Imagery"
        maxZoom={19}
      />
      */}

      {/* Обработчик кликов для рисования */}
      <DrawingHandler
        isDrawing={isDrawing}
        currentPolygon={currentPolygon}
        onPolygonUpdate={onPolygonUpdate}
        isCreatingHole={isCreatingHole}
        currentHole={currentHole}
        onHoleUpdate={onHoleUpdate}
      />

      {/* Существующие поля */}
      {fields.map((field) => (
        <Polygon
          key={field.id}
          positions={getPolygonPositions(field.coordinates)}
          pathOptions={{
            fillColor: '#27ae60',
            fillOpacity: 0.3,
            color: '#27ae60',
            weight: 2
          }}
        />
      ))}

      {/* Предварительный полигон основного поля */}
      {currentPolygon.length >= 3 && (
        <Polygon
          positions={getPolygonPositions(currentPolygon)}
          pathOptions={{
            fillColor: isCreatingHole ? '#3498db' : '#3498db',
            fillOpacity: isCreatingHole ? 0.1 : 0.2,
            color: '#3498db',
            weight: 3,
            dashArray: isCreatingHole ? '5, 15' : '10, 10'
          }}
        />
      )}
      
      {/* Редактируемые точки основного полигона - показываем всегда когда есть точки */}
      {currentPolygon.length > 0 && !isCreatingHole && currentPolygon.map((coord, index) => (
        <EditablePoint
          key={`main-${index}`}
          position={[coord[1], coord[0]]}
          index={index}
          onDrag={handleMainPolygonDrag}
          onDelete={handleMainPolygonDelete}
          color="#3498db"
          size={10}
          isDeletable={currentPolygon.length > 3}
        />
      ))}

      {/* Существующие дырки */}
      {currentHoles.map((hole, holeIndex) => (
        <Polygon
          key={`hole-${holeIndex}`}
          positions={getPolygonPositions(hole)}
          pathOptions={{
            fillColor: '#e74c3c',
            fillOpacity: 0.3,
            color: '#e74c3c',
            weight: 2
          }}
        />
      ))}

      {/* Текущая редактируемая дырка - полигон показываем только когда точек >= 3 */}
      {isCreatingHole && currentHole.length >= 3 && (
        <Polygon
          positions={getPolygonPositions(currentHole)}
          pathOptions={{
            fillColor: '#e74c3c',
            fillOpacity: 0.4,
            color: '#e74c3c',
            weight: 3,
            dashArray: '8, 4'
          }}
        />
      )}
      
      {/* Редактируемые точки дырки - показываем сразу с первой точки */}
      {isCreatingHole && currentHole.length > 0 && currentHole.map((coord, index) => (
        <EditablePoint
          key={`hole-${index}`}
          position={[coord[1], coord[0]]}
          index={index}
          onDrag={handleHoleDrag}
          onDelete={handleHoleDelete}
          color="#e74c3c"
          size={9}
          isDeletable={currentHole.length > 3}
        />
      ))}

      {/* Линии между точками для текущего полигона */}
      {isDrawing && !isCreatingHole && currentPolygon.length > 1 && (
        <Polyline
          positions={getPolygonPositions(currentPolygon)}
          pathOptions={{
            color: '#3498db',
            weight: 3,
            dashArray: '10, 10'
          }}
        />
      )}

      {/* Линии между точками для текущей дырки */}
      {isCreatingHole && currentHole.length > 1 && (
        <Polyline
          positions={getPolygonPositions(currentHole)}
          pathOptions={{
            color: '#e74c3c',
            weight: 3,
            dashArray: '8, 4'
          }}
        />
      )}
    </MapContainer>
  )
}