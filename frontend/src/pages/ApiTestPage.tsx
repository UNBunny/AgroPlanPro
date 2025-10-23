import React, { useState, useEffect } from 'react';
import { fieldService } from '../services/fieldServiceWithDebug';
import { Field } from '../types/Field';

const ApiTestPage: React.FC = () => {
  const [fields, setFields] = useState<Field[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<any>(null);

  useEffect(() => {
    loadFields();
  }, []);

  const loadFields = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await fieldService.getAllFields();
      setFields(data);
      setResult({ type: 'getAllFields', data });
    } catch (err: any) {
      setError(`Ошибка при загрузке полей: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const createTestField = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Создаем тестовое поле с квадратом
      const testField = {
        fieldName: 'Тестовое поле',
        crop_type: 'Пшеница',
        status: 'Активное',
        coordinates: [
          [73.368, 54.992],
          [73.370, 54.992],
          [73.370, 54.994],
          [73.368, 54.994],
          [73.368, 54.992], // Замыкаем полигон
        ],
        holes: [],
        areaHectares: 10.5
      };
      
      const response = await fieldService.createField(testField);
      setResult({ type: 'createField', data: response });
      
      // Обновляем список полей
      loadFields();
    } catch (err: any) {
      setError(`Ошибка при создании поля: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="api-test-page" style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h1>Тестирование API Полей</h1>
      
      <div style={{ marginBottom: '20px' }}>
        <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
          <button 
            onClick={loadFields}
            disabled={loading}
            style={{ 
              padding: '10px 20px', 
              backgroundColor: '#3498db', 
              color: 'white', 
              border: 'none', 
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Загрузка...' : 'Получить все поля'}
          </button>
          
          <button 
            onClick={createTestField}
            disabled={loading}
            style={{ 
              padding: '10px 20px', 
              backgroundColor: '#2ecc71', 
              color: 'white', 
              border: 'none', 
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Загрузка...' : 'Создать тестовое поле'}
          </button>
        </div>
        
        {error && (
          <div style={{ 
            padding: '15px', 
            backgroundColor: '#f8d7da', 
            color: '#721c24', 
            borderRadius: '4px',
            marginBottom: '20px'
          }}>
            <strong>Ошибка:</strong> {error}
          </div>
        )}
      </div>
      
      <div style={{ display: 'flex', gap: '20px' }}>
        <div style={{ flex: 1 }}>
          <h2>Список полей ({fields.length})</h2>
          {fields.length === 0 ? (
            <p>Нет доступных полей</p>
          ) : (
            <div style={{ 
              border: '1px solid #ddd', 
              borderRadius: '4px',
              overflow: 'hidden'
            }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ backgroundColor: '#f5f5f5' }}>
                    <th style={{ padding: '10px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>ID</th>
                    <th style={{ padding: '10px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Название</th>
                    <th style={{ padding: '10px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Культура</th>
                    <th style={{ padding: '10px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Статус</th>
                    <th style={{ padding: '10px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Площадь (га)</th>
                  </tr>
                </thead>
                <tbody>
                  {fields.map(field => (
                    <tr key={field.id} style={{ borderBottom: '1px solid #ddd' }}>
                      <td style={{ padding: '10px' }}>{field.id}</td>
                      <td style={{ padding: '10px' }}>{field.fieldName}</td>
                      <td style={{ padding: '10px' }}>{field.crop_type}</td>
                      <td style={{ padding: '10px' }}>{field.status}</td>
                      <td style={{ padding: '10px' }}>{field.areaHectares.toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
        
        <div style={{ flex: 1 }}>
          <h2>Результат последней операции</h2>
          {result ? (
            <div style={{ 
              padding: '15px', 
              backgroundColor: '#f5f5f5', 
              borderRadius: '4px',
              overflow: 'auto',
              maxHeight: '500px'
            }}>
              <h3>{result.type === 'getAllFields' ? 'Получение списка полей' : 'Создание поля'}</h3>
              <pre style={{ margin: 0 }}>
                {JSON.stringify(result.data, null, 2)}
              </pre>
            </div>
          ) : (
            <p>Нет данных для отображения</p>
          )}
        </div>
      </div>
      
      <div style={{ marginTop: '30px' }}>
        <h2>Инструкции по отладке</h2>
        <ol>
          <li>Нажмите "Получить все поля" для проверки соединения с API</li>
          <li>Нажмите "Создать тестовое поле" для проверки создания поля</li>
          <li>Смотрите консоль разработчика (F12) для детальных логов</li>
        </ol>
      </div>
    </div>
  );
};

export default ApiTestPage;
