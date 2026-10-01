import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import { useState, useEffect } from 'react'

function Cart() {
  const { userId, cart, updateCartQuantity, removeFromCart, checkout, loadCart } = useApp()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (userId) {
      loadCart(userId).then(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [userId])

  const totalPrice = cart.reduce((sum, item) => sum + (parseFloat(item.price) || 0) * (item.quantity || 1), 0)

  const handleCheckout = async () => {
    if (cart.length === 0) return
    const result = await checkout()
    if (result.success) {
      alert('订单提交成功！')
      navigate('/order')
    } else {
      alert(result.message || '订单提交失败')
    }
  }

  const handleUpdateQuantity = async (bookId, newQuantity) => {
    if (newQuantity < 1) return
    const result = await updateCartQuantity(bookId, newQuantity)
    if (!result.success) {
      alert(result.message || '更新失败')
    }
  }

  const handleRemove = async (bookId) => {
    const result = await removeFromCart(bookId)
    if (!result.success) {
      alert(result.message || '删除失败')
    }
  }

  if (loading) {
    return (
      <div className="cart-section">
        <div className="container">
          <p className="loading">加载中...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="cart-section">
      <div className="container">
        <h2>购物车</h2>
        <div className="cart-items">
          {cart.length === 0 ? (
            <div className="empty-cart">
              <p>购物车内暂无商品哦，前往主页购买吧</p>
            </div>
          ) : (
            cart.map((item) => (
              <div key={item.bookId || item.id} className="cart-item">
                <figure className="cart-item-cover">
                  <img src={item.image} alt={item.title} />
                </figure>
                <div className="cart-item-info">
                  <h3>
                    {item.bookId ? (
                      <Link to={`/book/${item.bookId}`}>{item.title}</Link>
                    ) : (
                      item.title
                    )}
                  </h3>
                  <p className="author">作者：{item.author}</p>
                  <p className="price">¥{(parseFloat(item.price) || 0).toFixed(2)}</p>
                </div>
                <div className="cart-item-quantity">
                  <button
                    className="quantity-btn"
                    onClick={() => handleUpdateQuantity(item.bookId || item.id, (item.quantity || 1) - 1)}
                  >
                    -
                  </button>
                  <input
                    type="number"
                    value={item.quantity || 1}
                    min="1"
                    onChange={(e) => handleUpdateQuantity(item.bookId || item.id, parseInt(e.target.value) || 1)}
                  />
                  <button
                    className="quantity-btn"
                    onClick={() => handleUpdateQuantity(item.bookId || item.id, (item.quantity || 1) + 1)}
                  >
                    +
                  </button>
                </div>
                <div className="cart-item-total">
                  <p>¥{((parseFloat(item.price) || 0) * (item.quantity || 1)).toFixed(2)}</p>
                </div>
                <div className="cart-item-remove">
                  <button onClick={() => handleRemove(item.bookId || item.id)}>删除</button>
                </div>
              </div>
            ))
          )}
        </div>

        {cart.length > 0 && (
          <div className="cart-summary">
            <div className="summary-item">
              <span>商品总价：</span>
              <span>¥{totalPrice.toFixed(2)}</span>
            </div>
            <div className="summary-item">
              <span>运费：</span>
              <span>¥0.00</span>
            </div>
            <div className="summary-item total">
              <span>合计：</span>
              <span>¥{totalPrice.toFixed(2)}</span>
            </div>
            <div className="cart-actions">
              <Link to="/" className="continue-shopping">继续购物</Link>
              <button className="checkout" onClick={handleCheckout}>去结算</button>
            </div>
          </div>
        )}

        {cart.length === 0 && (
          <div className="cart-summary">
            <div className="summary-item">
              <span>商品总价：</span>
              <span>¥0.00</span>
            </div>
            <div className="summary-item">
              <span>运费：</span>
              <span>¥0.00</span>
            </div>
            <div className="summary-item total">
              <span>合计：</span>
              <span>¥0.00</span>
            </div>
            <div className="cart-actions">
              <Link to="/" className="continue-shopping">继续购物</Link>
              <button className="checkout disabled" disabled>去结算</button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default Cart
