import { Link } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import { useState, useEffect } from 'react'

function Order() {
  const { userId, orders, clearOrders, loadOrders } = useApp()
  const [loading, setLoading] = useState(true)
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [bookName, setBookName] = useState('')
  const [filteredOrders, setFilteredOrders] = useState([])

  useEffect(() => {
    if (userId) {
      loadOrders(userId).then(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [userId])

  useEffect(() => {
    setFilteredOrders(orders)
  }, [orders])

  const handleSearch = async (e) => {
    e.preventDefault()
    try {
      const query = new URLSearchParams()
      query.append('userId', userId)
      if (startDate) query.append('startDate', startDate)
      if (endDate) query.append('endDate', endDate)
      if (bookName) query.append('bookName', bookName)

      const res = await fetch(`/api/v1/orders/search?${query.toString()}`)
      if (res.ok) {
        const data = await res.json()
        setFilteredOrders(data)
      }
    } catch (error) {
      console.error('搜索订单失败:', error)
    }
  }

  const handleReset = () => {
    setStartDate('')
    setEndDate('')
    setBookName('')
    setFilteredOrders(orders)
  }

  const handleClearOrders = async () => {
    if (filteredOrders.length === 0) {
      alert('没有历史订单可清空')
      return
    }
    if (confirm('确定要清空所有历史订单吗？此操作不可恢复。')) {
      await clearOrders()
      alert('历史订单已清空')
    }
  }

  if (loading) {
    return (
      <div className="order-section">
        <div className="container">
          <p className="loading">加载中...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="order-section">
      <div className="container">
        <div className="order-header-container">
          <h2>我的订单</h2>
          <button className="clear-orders-btn" onClick={handleClearOrders}>清空历史订单</button>
        </div>

        <form className="order-search-form" onSubmit={handleSearch}>
          <div className="search-fields">
            <div className="form-group">
              <label>开始日期</label>
              <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
            </div>
            <div className="form-group">
              <label>结束日期</label>
              <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
            </div>
            <div className="form-group">
              <label>书籍名称</label>
              <input type="text" value={bookName} placeholder="输入书名过滤"
                onChange={e => setBookName(e.target.value)} />
            </div>
          </div>
          <div className="search-actions">
            <button type="submit" className="btn-primary">搜索</button>
            <button type="button" className="btn-cancel" onClick={handleReset}>重置</button>
          </div>
        </form>

        {filteredOrders.length === 0 ? (
          <div className="order-list">
            <div className="empty-order">
              <p>{orders.length === 0 ? '您还没有订单' : '未找到匹配的订单'}</p>
              <Link to="/" className="continue-shopping">去购物</Link>
            </div>
          </div>
        ) : (
          <div className="order-list">
            {filteredOrders.map((order) => (
              <div key={order.id} className="order-item">
                <div className="order-header">
                  <div className="order-id">订单号：{order.orderId}</div>
                  <div className="order-date">{order.createdAt ? order.createdAt.split('T')[0] : order.date}</div>
                  <div className="order-status">已完成</div>
                </div>
                <div className="order-items">
                  {order.items && order.items.map((item, itemIndex) => (
                    <div key={itemIndex} className="order-item-detail">
                      <figure className="order-item-cover">
                        <img src={item.image} alt={item.title} />
                      </figure>
                      <div className="order-item-info">
                        <h3>
                          {item.bookId ? (
                            <Link to={`/book/${item.bookId}`}>{item.title}</Link>
                          ) : (
                            item.title
                          )}
                        </h3>
                        <p className="author">作者：{item.author}</p>
                        <p className="price">¥{(parseFloat(item.price) || 0).toFixed(2)}</p>
                        <p className="quantity">数量：{item.quantity}</p>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="order-summary">
                  <div className="order-total">
                    <span>合计：</span>
                    <span>¥{(parseFloat(order.totalAmount) || 0).toFixed(2)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Order
