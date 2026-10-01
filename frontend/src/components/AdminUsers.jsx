import { useState, useEffect } from 'react'
import { useApp } from '../context/AppContext'

function AdminUsers() {
  const { fetchAllUsers, toggleUserEnabled } = useApp()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  const loadUsers = async () => {
    setLoading(true)
    const data = await fetchAllUsers()
    setUsers(data)
    setLoading(false)
  }

  useEffect(() => {
    loadUsers()
  }, [])

  const handleToggle = async (userId) => {
    const result = await toggleUserEnabled(userId)
    if (result.success) {
      alert(result.message)
      loadUsers()
    } else {
      alert(result.message || '操作失败')
    }
  }

  if (loading) {
    return <div className="admin-section"><div className="container"><p className="loading">加载中...</p></div></div>
  }

  return (
    <div className="admin-section">
      <div className="container">
        <h2>用户管理</h2>
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>状态</th>
                <th>注册时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.email || '-'}</td>
                  <td>
                    <span className={`role-tag ${user.role === 'admin' ? 'role-admin' : 'role-customer'}`}>
                      {user.role === 'admin' ? '管理员' : '顾客'}
                    </span>
                  </td>
                  <td>
                    <span className={`status-tag ${user.enabled ? 'status-active' : 'status-disabled'}`}>
                      {user.enabled ? '正常' : '已禁用'}
                    </span>
                  </td>
                  <td>{user.createdAt ? user.createdAt.split('T')[0] : '-'}</td>
                  <td>
                    {user.role !== 'admin' && (
                      <button
                        className={`btn-${user.enabled ? 'disable' : 'enable'}`}
                        onClick={() => handleToggle(user.id)}
                      >
                        {user.enabled ? '禁用' : '解禁'}
                      </button>
                    )}
                    {user.role === 'admin' && <span className="text-muted">-</span>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default AdminUsers
