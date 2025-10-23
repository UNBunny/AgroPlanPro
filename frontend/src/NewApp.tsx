import { useState } from 'react'
import AdminLayout from './components/layout/AdminLayout'
import FieldsPage from './pages/FieldsPage'
import './styles/admin.css'

function App() {
  const [activePage, setActivePage] = useState<string>('fields')

  // Функция для смены активной страницы (для будущего расширения)
  const renderActivePage = () => {
    switch (activePage) {
      case 'fields':
      default:
        return <FieldsPage />
    }
  }

  return (
    <AdminLayout>
      {renderActivePage()}
    </AdminLayout>
  )
}

export default App
