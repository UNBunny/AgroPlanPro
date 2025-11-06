import { useState, useEffect } from 'react'
import { FieldMap, calculateFieldArea } from './components/FieldMap'
import FieldForm from './components/FieldFormWithDebug' // Используем форму с отладкой
import FieldList from './components/FieldList'
import { Field } from './types/Field'
import { fieldService } from './services/fieldService'

function App() {
  const [fields, setFields] = useState<Field[]>([])
  const [isDrawing, setIsDrawing] = useState(false)
  const [currentPolygon, setCurrentPolygon] = useState<number[][]>([])
  const [currentArea, setCurrentArea] = useState<number>(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  
  // Состояние для дырок
  const [currentHoles, setCurrentHoles] = useState<number[][][]>([])
  const [isCreatingHole, setIsCreatingHole] = useState(false)
  const [currentHole, setCurrentHole] = useState<number[][]>([])

  // Загрузка полей при монтировании компонента
  useEffect(() => {
    console.log("[App] Компонент App монтирован, загружаем поля")
    loadFields()
  }, [])

  // Автоматический пересчет площади при изменении полигона или дырок
  useEffect(() => {
    if (currentPolygon.length >= 3) {
      console.log("[App] Пересчитываем площадь, полигон изменился:", currentPolygon)
      // Включаем текущую создаваемую дырку в расчет, если у неё достаточно точек
      const allHoles = [...currentHoles]
      if (currentHole.length >= 3) {
        allHoles.push(currentHole)
      }
      
      const area = calculateFieldArea(currentPolygon, allHoles)
      console.log("[App] Рассчитанная площадь:", area)
      setCurrentArea(area)
    } else {
      setCurrentArea(0)
    }
  }, [currentPolygon, currentHoles, currentHole])

  const loadFields = async () => {
    try {
      console.log("[App] Начинаем загрузку полей")
      setLoading(true)
      const data = await fieldService.getAllFields()
      console.log("[App] Поля успешно загружены:", data)
      setFields(data)
      setError(null)
    } catch (err) {
      console.error("[App] Ошибка при загрузке полей:", err)
      setError('Ошибка при загрузке полей')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateField = async (fieldName: string, cropType: string, status: string) => {
    console.log("[App] handleCreateField вызван с параметрами:", { fieldName, cropType, status })
    
    if (currentPolygon.length < 3) {
      console.error("[App] Недостаточно точек для создания поля:", currentPolygon.length)
      setError('Для создания поля необходимо выделить область на карте (минимум 3 точки)')
      return
    }

    try {
      console.log("[App] Начинаем создание поля")
      setLoading(true)
      
      // Автоматически вычисляем площадь
      const calculatedArea = calculateFieldArea(currentPolygon, currentHoles)
      console.log("[App] Рассчитанная площадь:", calculatedArea)
      
      const fieldData = {
        fieldName,
        crop_type: cropType,
        status,
        areaHectares: Math.round(calculatedArea * 100) / 100, // округляем до 2 знаков
        coordinates: currentPolygon,
        holes: currentHoles.length > 0 ? currentHoles : undefined
      };
      
      console.log("[App] Отправляем данные на сервер:", JSON.stringify(fieldData))
      
      try {
        const newField = await fieldService.createField(fieldData)
        console.log("[App] Поле успешно создано:", newField)
        
        setFields(prev => [newField, ...prev])
        setCurrentPolygon([])
        setCurrentHoles([])
        setIsDrawing(false)
        setIsCreatingHole(false)
        setCurrentHole([])
        setError(null)
      } catch (apiError: any) {
        console.error("[App] Ошибка API при создании поля:", apiError)
        if (apiError.response) {
          console.error("[App] Ошибка ответа:", apiError.response.status, apiError.response.data)
        }
        throw apiError
      }
      
    } catch (err: any) {
      console.error("[App] Ошибка при создании поля:", err.message || err)
      setError(`Ошибка при создании поля: ${err.message || "Неизвестная ошибка"}`)
    } finally {
      console.log("[App] Завершаем процесс создания поля")
      setLoading(false)
    }
  }

  const handleStartDrawing = () => {
    console.log("[App] Начинаем рисование поля")
    setIsDrawing(true)
    setCurrentPolygon([])
    setCurrentArea(0)
    setCurrentHoles([])
    setIsCreatingHole(false)
    setCurrentHole([])
    setError(null)
  }

  const handleCancelDrawing = () => {
    console.log("[App] Отменяем рисование поля")
    setIsDrawing(false)
    setIsCreatingHole(false)
    setCurrentPolygon([])
    setCurrentArea(0)
    setCurrentHoles([])
    setCurrentHole([])
  }

  // Обработчики для дырок
  const handleStartCreatingHole = () => {
    console.log("[App] Начинаем создание отверстия")
    if (currentPolygon.length >= 3) {
      setIsCreatingHole(true)
      setCurrentHole([])
      setError(null)
    } else {
      setError('Сначала создайте основное поле')
    }
  }

  const handleFinishCreatingHole = () => {
    console.log("[App] Завершаем создание отверстия")
    if (currentHole.length >= 3) {
      setCurrentHoles(prev => [...prev, currentHole])
      setCurrentHole([])
      setIsCreatingHole(false)
    }
  }

  const handleCancelCreatingHole = () => {
    console.log("[App] Отменяем создание отверстия")
    setIsCreatingHole(false)
    setCurrentHole([])
  }

  return (
    <div className="app">
      <header className="header">
        <h1>🌾 Field Mapping - Управление полями</h1>
      </header>
      
      <main className="main-content">
        <aside className="sidebar">
          <div className="sidebar-header">
            <h2>Управление полями</h2>
            <p>Создавайте и управляйте сельскохозяйственными полями</p>
          </div>
          
          <div className="sidebar-content">
            {error && (
              <div className="error-message">
                {error}
              </div>
            )}
            
            <FieldForm
              onCreateField={handleCreateField}
              onStartDrawing={handleStartDrawing}
              onCancelDrawing={handleCancelDrawing}
              isDrawing={isDrawing}
              hasPolygon={currentPolygon.length >= 3}
              currentArea={currentArea}
              loading={loading}
              // Пропсы для дырок
              onStartCreatingHole={handleStartCreatingHole}
              onFinishCreatingHole={handleFinishCreatingHole}
              onCancelCreatingHole={handleCancelCreatingHole}
              isCreatingHole={isCreatingHole}
              hasHole={currentHole.length >= 3}
              holesCount={currentHoles.length}
            />
            
            <FieldList 
              fields={fields} 
              loading={loading}
            />
          </div>
        </aside>
        
        <div className={`map-container ${isDrawing ? 'drawing-mode' : ''}`}>
          <FieldMap
            fields={fields}
            isDrawing={isDrawing}
            currentPolygon={currentPolygon}
            onPolygonUpdate={setCurrentPolygon}
            currentHoles={currentHoles}
            onHolesUpdate={setCurrentHoles}
            isCreatingHole={isCreatingHole}
            currentHole={currentHole}
            onHoleUpdate={setCurrentHole}
          />
        </div>
      </main>
    </div>
  )
}

export default App
