import { Routes, Route, Navigate } from 'react-router-dom'
import { AppProvider, useApp } from './context/AppContext'
import Layout from './components/Layout'
import BookList from './components/BookList'
import BookDetail from './components/BookDetail'
import Cart from './components/Cart'
import Order from './components/Order'
import Login from './components/Login'
import Register from './components/Register'
import Profile from './components/Profile'
import AdminUsers from './components/AdminUsers'
import AdminBooks from './components/AdminBooks'
import AdminOrders from './components/AdminOrders'
import Statistics from './components/Statistics'
import './App.css'

function ProtectedRoute({ children }) {
  const { isLoggedIn } = useApp()
  if (!isLoggedIn) {
    return <Navigate to="/login" replace />
  }
  return children
}

function AdminRoute({ children }) {
  const { isLoggedIn, role } = useApp()
  if (!isLoggedIn) {
    return <Navigate to="/login" replace />
  }
  if (role !== 'admin') {
    return <Navigate to="/" replace />
  }
  return children
}

function AppRoutes() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<BookList />} />
        <Route path="/book/:id" element={<BookDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/order" element={
          <ProtectedRoute>
            <Order />
          </ProtectedRoute>
        } />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/profile" element={
          <ProtectedRoute>
            <Profile />
          </ProtectedRoute>
        } />
        <Route path="/statistics" element={
          <ProtectedRoute>
            <Statistics />
          </ProtectedRoute>
        } />
        {/* 管理员路由 */}
        <Route path="/admin/users" element={
          <AdminRoute>
            <AdminUsers />
          </AdminRoute>
        } />
        <Route path="/admin/books" element={
          <AdminRoute>
            <AdminBooks />
          </AdminRoute>
        } />
        <Route path="/admin/orders" element={
          <AdminRoute>
            <AdminOrders />
          </AdminRoute>
        } />
      </Routes>
    </Layout>
  )
}

function App() {
  return (
    <AppProvider>
      <AppRoutes />
    </AppProvider>
  )
}

export default App
