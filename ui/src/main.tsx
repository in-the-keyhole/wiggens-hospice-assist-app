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

const router = createBrowserRouter([
  { path: '/', element: <Dashboard/> },
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

