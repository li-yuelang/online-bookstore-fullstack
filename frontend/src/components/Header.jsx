import { Link, useLocation } from 'react-router-dom'
import { useApp } from '../context/AppContext'
import { useState } from 'react'

function Header() {
  const { isLoggedIn, username, role, logout } = useApp()
  const location = useLocation()
  const [showDropdown, setShowDropdown] = useState(false)

  const isActive = (path) => location.pathname === path ? 'active' : ''

  return (
    <header className="header">
      <div className="container">
        <div className="logo">
          <Link to="/"><h1>朗的云书店</h1></Link>
        </div>
        <nav className="nav">
          <ul>
            <li><Link to="/" className={isActive('/')}>首页</Link></li>
            <li><Link to="/cart" className={isActive('/cart')}>购物车</Link></li>
            <li><Link to="/order" className={isActive('/order')}>订单</Link></li>
            <li><Link to="/statistics" className={isActive('/statistics')}>统计</Link></li>
            {role === 'admin' && (
              <li className="nav-admin">
                <span className="nav-admin-label">管理员</span>
                <div className="admin-links">
                  <Link to="/admin/users" className={isActive('/admin/users')}>用户管理</Link>
                  <Link to="/admin/books" className={isActive('/admin/books')}>书籍管理</Link>
                  <Link to="/admin/orders" className={isActive('/admin/orders')}>订单管理</Link>
                </div>
              </li>
            )}
          </ul>
        </nav>
        <div className="user-actions">
          {isLoggedIn ? (
            <div className="user-menu">
              <span className="username" onClick={() => setShowDropdown(!showDropdown)}>
                {username}
                {role === 'admin' && <span className="admin-badge">管理员</span>}
              </span>
              {showDropdown && (
                <div className="user-dropdown">
                  <Link to="/profile" onClick={() => setShowDropdown(false)}>个人信息</Link>
                  <a href="#" onClick={() => { logout(); setShowDropdown(false); }}>退出登录</a>
                </div>
              )}
            </div>
          ) : (
            <Link to="/login" className="login-btn">登录</Link>
          )}
        </div>
      </div>
    </header>
  )
}

export default Header
