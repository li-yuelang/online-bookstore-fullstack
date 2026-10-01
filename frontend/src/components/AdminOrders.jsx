import { useState, useEffect } from 'react'
import { useApp } from '../context/AppContext'
import { Link } from 'react-router-dom'

function AdminOrders() {
  const { searchAllOrders } = useApp()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [bookName, setBookName] = useState('')

  const loadOrders = async (params = {}) => {
    setLoading(true)
    const data = await searchAllOrders(params)
    setOrders(data)
    setLoading(false)
  }

  useEffect(() => { loadOrders() }, [])

  const handleSearch = (e) => {
    e.preventDefault()
    loadOrders({ startDate, endDate, bookName })
  }

  const handleReset = () => {
    setStartDate('')
    setEndDate('')
    setBookName('')
    loadOrders()
  }

  if (loading) {
    return <div className="admin-section"><div className="container"><p className="loading">加载中...</p></div></div>
  }

  return (
    <div className="admin-section">
      <div className="container">
        <h2>订单管理（全部订单）</h2>

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

        {orders.length === 0 ? (
          <p className="no-results">暂无订单</p>
        ) : (
          <div className="order-list">
            {orders.map(order => (
              <div key={order.id} className="order-item">
                <div className="order-header">
                  <div className="order-id">订单号：{order.orderId}</div>
                  <div className="order-user">用户：{order.username || order.userId}</div>
                  <div className="order-date">
                    {order.createdAt ? new Date(order.createdAt).toLocaleString() : '-'}
                  </div>
                  <div className="order-status">{order.status}</div>
                </div>
                <div className="order-items">
                  {order.items && order.items.map((item, idx) => (
                    <div key={idx} className="order-item-detail">
                      <figure className="order-item-cover">
                        <img src={item.image} alt={item.title} />
                      </figure>
                      <div className="order-item-info">
                        <h4>{item.title}</h4>
                        <p className="author">{item.author}</p>
                        <p className="price">¥{parseFloat(item.price || 0).toFixed(2)} × {item.quantity}</p>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="order-summary">
                  <span>合计：¥{parseFloat(order.totalAmount || 0).toFixed(2)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminOrders
