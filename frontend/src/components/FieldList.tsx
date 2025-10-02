import { Field } from '../types/Field'

interface FieldListProps {
  fields: Field[]
  loading: boolean
}

function FieldList({ fields, loading }: FieldListProps) {
  if (loading && fields.length === 0) {
    return (
      <div className="field-list">
        <div style={{ padding: '2rem', textAlign: 'center', color: '#7f8c8d' }}>
          ⏳ Загрузка полей...
        </div>
      </div>
    )
  }

  if (fields.length === 0) {
    return (
      <div className="field-list">
        <div style={{ padding: '2rem', textAlign: 'center', color: '#7f8c8d' }}>
          📭 Поля не найдены
          <br />
          <small>Создайте первое поле, выделив область на карте</small>
        </div>
      </div>
    )
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return (
    <div className="field-list">
      <h3 style={{ padding: '1rem', margin: 0, borderBottom: '1px solid #f0f0f0' }}>
        📋 Список полей ({fields.length})
      </h3>
      
      {fields.map((field) => (
        <div key={field.id} className="field-item">
          <div className="field-name">
            🌾 {field.name}
          </div>
          <div className="field-details">
            📏 Площадь: {field.area} га
            <br />
            📅 Создано: {formatDate(field.createdAt)}
            <br />
            📍 Точек: {field.coordinates.length}
          </div>
        </div>
      ))}
    </div>
  )
}

export default FieldList