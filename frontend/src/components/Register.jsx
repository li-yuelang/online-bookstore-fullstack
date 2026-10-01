import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useApp } from '../context/AppContext'

function Register() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const { register } = useApp()
  const navigate = useNavigate()

  const validateEmail = (email) => {
    const re = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
    return re.test(email)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    // 前端校验
    if (!username.trim()) {
      setError('请输入用户名')
      return
    }
    if (username.length < 2 || username.length > 20) {
      setError('用户名长度应为2-20个字符')
      return
    }
    if (!password.trim()) {
      setError('请输入密码')
      return
    }
    if (password.length < 6) {
      setError('密码长度不能少于6位')
      return
    }
    if (password !== confirmPassword) {
      setError('两次输入的密码不一致')
      setPassword('')
      setConfirmPassword('')
      return
    }
    if (!email.trim()) {
      setError('请输入邮箱')
      return
    }
    if (!validateEmail(email)) {
      setError('邮箱格式不正确')
      return
    }

    const result = await register({ username, password, email })
    if (result.success) {
      navigate('/')
    } else {
      setError(result.message)
    }
  }

  return (
    <div className="login-section">
      <div className="container">
        <div className="login-form">
          <h2>用户注册</h2>
          {error && <div className="form-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="reg-username">用户名</label>
              <input
                type="text"
                id="reg-username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="2-20个字符"
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-email">邮箱</label>
              <input
                type="email"
                id="reg-email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="请输入邮箱地址"
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-password">密码</label>
              <input
                type="password"
                id="reg-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="至少6位密码"
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-confirm-password">确认密码</label>
              <input
                type="password"
                id="reg-confirm-password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="再次输入密码"
              />
            </div>
            <button type="submit" className="login-submit">注册</button>
            <div className="register-link">
              <p>已有账号？ <Link to="/login">立即登录</Link></p>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default Register
