import { useApp } from '../context/AppContext'
import { Link } from 'react-router-dom'

function Profile() {
  const { username, email, role, userId } = useApp()

  return (
    <div className="profile-section">
      <div className="container">
        <div className="profile-card">
          <h2>个人信息</h2>
          <div className="profile-info">
            <div className="profile-item">
              <span className="profile-label">用户ID</span>
              <span className="profile-value">{userId}</span>
            </div>
            <div className="profile-item">
              <span className="profile-label">用户名</span>
              <span className="profile-value">{username}</span>
            </div>
            <div className="profile-item">
              <span className="profile-label">邮箱</span>
              <span className="profile-value">{email || '未设置'}</span>
            </div>
            <div className="profile-item">
              <span className="profile-label">角色</span>
              <span className="profile-value">
                <span className={`role-tag ${role === 'admin' ? 'role-admin' : 'role-customer'}`}>
                  {role === 'admin' ? '管理员' : '顾客'}
                </span>
              </span>
            </div>
          </div>
          {role === 'admin' && (
            <div className="profile-admin-links">
              <h3>管理功能</h3>
              <div className="admin-quick-links">
                <Link to="/admin/users" className="admin-link">用户管理</Link>
                <Link to="/admin/books" className="admin-link">书籍管理</Link>
                <Link to="/admin/orders" className="admin-link">订单管理</Link>
                <Link to="/statistics" className="admin-link">数据统计</Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default Profile
