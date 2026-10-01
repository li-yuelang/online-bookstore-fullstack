import { Link } from 'react-router-dom'

function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-content">
          <div className="footer-section">
            <h3>关于我们</h3>
            <p>朗的云书店是一个专注于提供高质量电子书的平台，为读者提供便捷的阅读体验。</p>
          </div>
          <div className="footer-section">
            <h3>联系我们</h3>
            <p>邮箱：contact@ebookstore.com</p>
            <p>电话：123-4567-8910</p>
          </div>
          <div className="footer-section">
            <h3>快速链接</h3>
            <ul>
              <li><Link to="/">首页</Link></li>
              <li><Link to="/cart">购物车</Link></li>
              <li><Link to="/order">订单</Link></li>
            </ul>
          </div>
        </div>
        <div className="footer-bottom">
          <p>&copy; 2026 朗的云书店. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  )
}

export default Footer