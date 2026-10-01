import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '../context/AppContext'

function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const { authenticate } = useApp()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    // JS前端校验：检查空输入
    if (!username.trim()) {
      setError('请输入用户名')
      return
    }
    if (!password.trim()) {
      setError('请输入密码')
      return
    }

    const result = await authenticate(username, password)
    if (result.success) {
      navigate('/')
    } else {
      setError(result.message)
      setPassword('')
    }
  }

  return (
    <div className="login-section">
      <div className="container">
        <div className="login-form">
          <h2>用户登录</h2>
          {error && <div className="form-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">用户名</label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="请输入用户名"
              />
            </div>
            <div className="form-group">
              <label htmlFor="password">密码</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="请输入密码"
              />
            </div>
            <button type="submit" className="login-submit">登录</button>
            <div className="register-link">
              <p>还没有账号？ <Link to="/register">立即注册</Link></p>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default Login
