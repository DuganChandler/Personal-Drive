import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from 'react-router'
import AppRoutes from './AppRoutes.js'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router = {AppRoutes}/>
  </StrictMode>,
)
