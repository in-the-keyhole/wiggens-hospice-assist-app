import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import Login from './modules/Auth/Login'
import Register from './modules/Auth/Register'
import ForgotPassword from './modules/Auth/ForgotPassword'
import ResetPassword from './modules/Auth/ResetPassword'
import Dashboard from './modules/Dashboard/Dashboard'
import { AuthProvider } from './modules/Auth/useAuth'
import Contacts from './modules/Contacts'
import Medications from './modules/Medications'
import Visits from './modules/Visits'
import Symptoms from './modules/Symptoms'

const router = createBrowserRouter([
  { path: '/', element: <Dashboard/> },
  { path: '/contacts', element: <Contacts/> },
  { path: '/medications', element: <Medications/> },
  { path: '/visits', element: <Visits/> },
  { path: '/symptoms', element: <Symptoms/> },
  { path: '/login', element: <Login/> },
  { path: '/register', element: <Register/> },
  { path: '/forgot-password', element: <ForgotPassword/> },
  { path: '/reset-password', element: <ResetPassword/> },
])

const theme = createTheme({})

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>
)
