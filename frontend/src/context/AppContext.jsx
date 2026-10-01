import { createContext, useContext, useState, useEffect } from 'react'

// 全局应用上下文，管理登录态、购物车、订单、管理员操作和统计功能
const AppContext = createContext()

export function AppProvider({ children }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [username, setUsername] = useState('')
  const [userId, setUserId] = useState(null)
  const [role, setRole] = useState('')
  const [email, setEmail] = useState('')
  const [cart, setCart] = useState([])
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)
      setIsLoggedIn(true)
      setUsername(user.username)
      setUserId(user.id)
      setRole(user.role || 'customer')
      setEmail(user.email || '')
      loadCart(user.id)
      loadOrders(user.id)
    }
    setLoading(false)
  }, [])

  const loadCart = async (uid) => {
    try {
      const response = await fetch(`/api/v1/cart?userId=${uid}`)
      if (response.ok) {
        const data = await response.json()
        setCart(data || [])
      }
    } catch (error) {
      console.error('加载购物车失败:', error)
    }
  }

  const loadOrders = async (uid) => {
    try {
      const response = await fetch(`/api/v1/orders?userId=${uid}`)
      if (response.ok) {
        const data = await response.json()
        setOrders(data || [])
      }
    } catch (error) {
      console.error('加载订单失败:', error)
    }
  }

  // 登录：保存用户信息到 localStorage，更新全局状态并加载购物车/订单
  const login = (user) => {
    localStorage.setItem('user', JSON.stringify(user))
    setIsLoggedIn(true)
    setUsername(user.username)
    setUserId(user.id)
    setRole(user.role || 'customer')
    setEmail(user.email || '')
    loadCart(user.id)
    loadOrders(user.id)
  }

  // 退出登录：清除 localStorage 和全局状态
  const logout = () => {
    localStorage.removeItem('user')
    setIsLoggedIn(false)
    setUsername('')
    setUserId(null)
    setRole('')
    setEmail('')
    setCart([])
    setOrders([])
  }

  const addToCart = async (book) => {
    if (!userId) {
      alert('请先登录')
      return { success: false, message: '请先登录' }
    }

    try {
      const response = await fetch('/api/v1/cart/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId,
          bookId: book.id,
          quantity: 1
        })
      })
      const data = await response.json()
      if (data.success) {
        await loadCart(userId)
        return { success: true }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('添加购物车失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  const removeFromCart = async (bookId) => {
    try {
      const response = await fetch('/api/v1/cart/remove', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId,
          bookId: bookId
        })
      })
      const data = await response.json()
      if (data.success) {
        await loadCart(userId)
        return { success: true }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('删除购物车失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  const updateCartQuantity = async (bookId, quantity) => {
    try {
      const response = await fetch('/api/v1/cart/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId,
          bookId: bookId,
          quantity: quantity
        })
      })
      const data = await response.json()
      if (data.success) {
        await loadCart(userId)
        return { success: true }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('更新购物车失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  const clearCart = async () => {
    try {
      const response = await fetch(`/api/v1/cart/clear?userId=${userId}`, {
        method: 'DELETE'
      })
      const data = await response.json()
      if (data.success) {
        setCart([])
        return { success: true }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('清空购物车失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  // 结算购物车：创建订单，成功后清空购物车并刷新订单列表
  const checkout = async () => {
    if (cart.length === 0) {
      return { success: false, message: '购物车为空' }
    }

    try {
      const items = cart.map(item => ({
        bookId: item.bookId || item.id,
        quantity: item.quantity,
        price: parseFloat(item.price || 0),
        title: item.title,
        author: item.author,
        image: item.image
      }))

      const response = await fetch('/api/v1/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId,
          items: items
        })
      })
      const data = await response.json()
      
      if (data.success) {
        setCart([])
        try {
          await fetch(`/api/v1/cart/clear?userId=${userId}`, {
            method: 'DELETE'
          })
        } catch (clearError) {
          console.error('清空购物车失败:', clearError)
        }
        await loadOrders(userId)
        return { success: true, order: data.data }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('创建订单失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  const clearOrders = async () => {
    for (const order of orders) {
      try {
        await fetch(`/api/v1/orders/${order.orderId}`, {
          method: 'DELETE'
        })
      } catch (error) {
        console.error('删除订单失败:', error)
      }
    }
    await loadOrders(userId)
  }

  // 用户注册：调注册接口，成功后自动登录
  const register = async (user) => {
    try {
      const response = await fetch('/api/v1/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
      })
      const data = await response.json()
      if (data.success) {
        const loginResponse = await fetch('/api/v1/users/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: user.username, password: user.password })
        })
        const loginData = await loginResponse.json()
        if (loginData.success) {
          login({
            username: user.username,
            id: loginData.id,
            role: loginData.role || 'customer',
            email: loginData.email || user.email
          })
        }
        return { success: true }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('注册失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  // 用户登录验证：调用后端 API，处理被禁用用户的拦截提示
  const authenticate = async (username, password) => {
    try {
      const response = await fetch('/api/v1/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      })
      const data = await response.json()
      if (data.success) {
        login({
          username: data.username,
          id: data.id,
          role: data.role || 'customer',
          email: data.email || ''
        })
        return { success: true }
      }
      return { success: false, message: data.message }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  // 管理员：获取所有用户
  const fetchAllUsers = async () => {
    try {
      const response = await fetch('/api/v1/users/all')
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('获取用户列表失败:', error)
      return []
    }
  }

  // 管理员：切换用户禁用/解禁
  const toggleUserEnabled = async (userId) => {
    try {
      const response = await fetch(`/api/v1/users/${userId}/toggle-enabled`, {
        method: 'PUT'
      })
      return await response.json()
    } catch (error) {
      console.error('切换用户状态失败:', error)
      return { success: false, message: '网络错误' }
    }
  }

  // 管理员：获取所有订单
  const fetchAllOrders = async () => {
    try {
      const response = await fetch('/api/v1/orders/admin/all')
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('获取所有订单失败:', error)
      return []
    }
  }

  // 管理员：搜索所有订单
  const searchAllOrders = async (params) => {
    try {
      const query = new URLSearchParams()
      if (params.startDate) query.append('startDate', params.startDate)
      if (params.endDate) query.append('endDate', params.endDate)
      if (params.bookName) query.append('bookName', params.bookName)
      const response = await fetch(`/api/v1/orders/admin/search?${query.toString()}`)
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('搜索订单失败:', error)
      return []
    }
  }

  // 管理员/顾客：统计接口
  const fetchSalesStatistics = async (startDate, endDate) => {
    try {
      const query = new URLSearchParams()
      if (startDate) query.append('startDate', startDate)
      if (endDate) query.append('endDate', endDate)
      const response = await fetch(`/api/v1/orders/statistics/sales?${query.toString()}`)
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('获取销售统计失败:', error)
      return []
    }
  }

  const fetchConsumptionStatistics = async (startDate, endDate) => {
    try {
      const query = new URLSearchParams()
      if (startDate) query.append('startDate', startDate)
      if (endDate) query.append('endDate', endDate)
      const response = await fetch(`/api/v1/orders/statistics/consumption?${query.toString()}`)
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('获取消费统计失败:', error)
      return []
    }
  }

  const fetchMyPurchaseStatistics = async (uid, startDate, endDate) => {
    try {
      const query = new URLSearchParams()
      query.append('userId', uid || userId)
      if (startDate) query.append('startDate', startDate)
      if (endDate) query.append('endDate', endDate)
      const response = await fetch(`/api/v1/orders/statistics/my-purchase?${query.toString()}`)
      if (response.ok) {
        return await response.json()
      }
      return []
    } catch (error) {
      console.error('获取个人购买统计失败:', error)
      return []
    }
  }

  return (
    <AppContext.Provider value={{
      isLoggedIn,
      username,
      userId,
      role,
      email,
      cart,
      orders,
      loading,
      login,
      logout,
      addToCart,
      removeFromCart,
      updateCartQuantity,
      clearCart,
      checkout,
      clearOrders,
      register,
      authenticate,
      loadCart,
      loadOrders,
      // 管理员函数
      fetchAllUsers,
      toggleUserEnabled,
      fetchAllOrders,
      searchAllOrders,
      // 统计函数
      fetchSalesStatistics,
      fetchConsumptionStatistics,
      fetchMyPurchaseStatistics,
    }}>
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  const context = useContext(AppContext)
  if (!context) {
    throw new Error('useApp must be used within an AppProvider')
  }
  return context
}
