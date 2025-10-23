import { useState, useEffect } from 'react'
import { FieldMap, calculateFieldArea } from '../components/FieldMap'
import FieldForm from '../components/FieldForm'
import FieldList from '../components/FieldList'
import { Field } from '../types/Field'
import { fieldService } from '../services/fieldService'

const FieldsPage = () => {
  const [activeTab, setActiveTab] = useState<'list' | 'create'>('list');
  const [fields, setFields] = useState<Field[]>([]);
  const [isDrawing, setIsDrawing] = useState(false);
  const [currentPolygon, setCurrentPolygon] = useState<number[][]>([]);
  const [currentArea, setCurrentArea] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Состояние для дырок
  const [currentHoles, setCurrentHoles] = useState<number[][][]>([]);
  const [isCreatingHole, setIsCreatingHole] = useState(false);
  const [currentHole, setCurrentHole] = useState<number[][]>([]);

  // Загрузка полей при монтировании компонента
  useEffect(() => {
    loadFields();
  }, []);

  // Автоматический пересчет площади при изменении полигона или дырок
  useEffect(() => {
    if (currentPolygon.length >= 3) {
      // Включаем текущую создаваемую дырку в расчет, если у неё достаточно точек
      const allHoles = [...currentHoles];
      if (currentHole.length >= 3) {
        allHoles.push(currentHole);
      }
      
      const area = calculateFieldArea(currentPolygon, allHoles);
      setCurrentArea(area);
    } else {
      setCurrentArea(0);
    }
  }, [currentPolygon, currentHoles, currentHole]);

  const loadFields = async () => {
    try {
      setLoading(true);
      const data = await fieldService.getAllFields();
      setFields(data);
      setError(null);
    } catch (err) {
      setError('Ошибка при загрузке полей');
      console.error('Error loading fields:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateField = async (fieldName: string, cropType: string, status: string) => {
    if (currentPolygon.length < 3) {
      setError('Для создания поля необходимо выделить область на карте (минимум 3 точки)');
      return;
    }

    try {
      setLoading(true);
      
      // Автоматически вычисляем площадь
      const calculatedArea = calculateFieldArea(currentPolygon, currentHoles);
      
      const newField = await fieldService.createField({
        fieldName,
        crop_type: cropType,
        status,
        areaHectares: Math.round(calculatedArea * 100) / 100, // округляем до 2 знаков
        coordinates: currentPolygon,
        holes: currentHoles.length > 0 ? currentHoles : undefined
      });
      
      setFields(prev => [newField, ...prev]);
      setCurrentPolygon([]);
      setCurrentHoles([]);
      setIsDrawing(false);
      setIsCreatingHole(false);
      setCurrentHole([]);
      setError(null);
      
      // После успешного создания переходим к списку полей
      setActiveTab('list');
    } catch (err) {
      setError('Ошибка при создании поля');
      console.error('Error creating field:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStartDrawing = () => {
    setIsDrawing(true);
    setCurrentPolygon([]);
    setCurrentArea(0);
    setCurrentHoles([]);
    setIsCreatingHole(false);
    setCurrentHole([]);
    setError(null);
  };

  const handleCancelDrawing = () => {
    setIsDrawing(false);
    setIsCreatingHole(false);
    setCurrentPolygon([]);
    setCurrentArea(0);
    setCurrentHoles([]);
    setCurrentHole([]);
  };

  // Обработчики для дырок
  const handleStartCreatingHole = () => {
    if (currentPolygon.length >= 3) {
      setIsCreatingHole(true);
      setCurrentHole([]);
      setError(null);
    } else {
      setError('Сначала создайте основное поле');
    }
  };

  const handleFinishCreatingHole = () => {
    if (currentHole.length >= 3) {
      setCurrentHoles(prev => [...prev, currentHole]);
      setCurrentHole([]);
      setIsCreatingHole(false);
    }
  };

  const handleCancelCreatingHole = () => {
    setIsCreatingHole(false);
    setCurrentHole([]);
  };

  const handleCreateClick = () => {
    setActiveTab('create');
    // При переключении на создание поля, сразу начинаем рисование
    handleStartDrawing();
  };

  return (
    <div className="fields-page">
      <div className="page-header">
        <h2>Управление полями</h2>
        <div className="page-actions">
          {activeTab === 'list' ? (
            <button 
              className="btn btn-primary" 
              onClick={handleCreateClick}
            >
              ➕ Создать новое поле
            </button>
          ) : (
            <button 
              className="btn btn-secondary" 
              onClick={() => {
                setActiveTab('list');
                handleCancelDrawing();
              }}
            >
              ⬅️ К списку полей
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <div className="tabs-container">
        <div className="tabs">
          <button 
            className={`tab ${activeTab === 'list' ? 'active' : ''}`}
            onClick={() => {
              setActiveTab('list');
              handleCancelDrawing();
            }}
          >
            📋 Список полей
          </button>
          <button 
            className={`tab ${activeTab === 'create' ? 'active' : ''}`}
            onClick={handleCreateClick}
          >
            ➕ Создать поле
          </button>
        </div>

        <div className="tab-content">
          {activeTab === 'list' ? (
            <div className="list-tab">
              <FieldList 
                fields={fields} 
                loading={loading} 
              />
            </div>
          ) : (
            <div className="create-tab">
              <div className="create-field-container">
                <div className="form-section">
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
                </div>
                <div className="map-section">
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
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default FieldsPage;
