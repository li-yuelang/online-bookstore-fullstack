import { useState } from 'react'
import { useApp } from '../context/AppContext'

function Statistics() {
  const { role, userId, fetchSalesStatistics, fetchConsumptionStatistics, fetchMyPurchaseStatistics } = useApp()
  const [activeTab, setActiveTab] = useState(role === 'admin' ? 'sales' : 'my')
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [salesData, setSalesData] = useState([])
  const [consumptionData, setConsumptionData] = useState([])
  const [myData, setMyData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)

  const handleSearch = async () => {
    setLoading(true)
    setSearched(true)
    try {
      if (activeTab === 'sales') {
        const data = await fetchSalesStatistics(startDate, endDate)
        setSalesData(data)
      } else if (activeTab === 'consumption') {
        const data = await fetchConsumptionStatistics(startDate, endDate)
        setConsumptionData(data)
      } else if (activeTab === 'my') {
        const data = await fetchMyPurchaseStatistics(userId, startDate, endDate)
        if (data && data.length > 0) {
          setMyData(data[0])
        } else {
          setMyData({ items: [], totalBooks: 0, totalAmount: 0, itemCount: 0 })
        }
      }
    } catch (e) {
      console.error('统计查询失败:', e)
    }
    setLoading(false)
  }

  const renderSalesRanking = () => (
    <div className="stat-content">
      <h3>热销榜</h3>
      {!searched ? (
        <p className="stat-hint">请选择时间范围后点击查询</p>
      ) : salesData.length === 0 ? (
        <p className="no-results">该时间范围内没有销售数据</p>
      ) : (
        <table className="stat-table">
          <thead>
            <tr>
              <th>排名</th>
              <th>书名</th>
              <th>作者</th>
              <th>单价</th>
              <th>销量</th>
              <th>总金额</th>
            </tr>
          </thead>
          <tbody>
            {salesData.map(item => (
              <tr key={item.rank} className={item.rank <= 3 ? 'top-rank' : ''}>
                <td className="rank-cell">
                  {item.rank <= 3 ? <span className={`rank-badge rank-${item.rank}`}>{item.rank}</span> : item.rank}
                </td>
                <td>{item.title}</td>
                <td>{item.author}</td>
                <td>¥{parseFloat(item.price || 0).toFixed(2)}</td>
                <td className="num-cell">{item.totalQuantity}</td>
                <td className="num-cell">¥{parseFloat(item.totalAmount || 0).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )

  const renderConsumptionRanking = () => (
    <div className="stat-content">
      <h3>消费榜</h3>
      {!searched ? (
        <p className="stat-hint">请选择时间范围后点击查询</p>
      ) : consumptionData.length === 0 ? (
        <p className="no-results">该时间范围内没有消费数据</p>
      ) : (
        <table className="stat-table">
          <thead>
            <tr>
              <th>排名</th>
              <th>用户名</th>
              <th>订单数</th>
              <th>累计消费</th>
            </tr>
          </thead>
          <tbody>
            {consumptionData.map(item => (
              <tr key={item.rank} className={item.rank <= 3 ? 'top-rank' : ''}>
                <td className="rank-cell">
                  {item.rank <= 3 ? <span className={`rank-badge rank-${item.rank}`}>{item.rank}</span> : item.rank}
                </td>
                <td>{item.username}</td>
                <td className="num-cell">{item.orderCount}</td>
                <td className="num-cell">¥{parseFloat(item.totalSpent || 0).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )

  const renderMyPurchase = () => (
    <div className="stat-content">
      <h3>我的购买统计</h3>
      {!searched ? (
        <p className="stat-hint">请选择时间范围后点击查询</p>
      ) : myData && myData.items && myData.items.length > 0 ? (
        <>
          <div className="stat-summary-cards">
            <div className="stat-card">
              <div className="stat-card-label">购书种类</div>
              <div className="stat-card-value">{myData.itemCount}</div>
            </div>
            <div className="stat-card">
              <div className="stat-card-label">购书总本数</div>
              <div className="stat-card-value">{myData.totalBooks}</div>
            </div>
            <div className="stat-card">
              <div className="stat-card-label">总金额</div>
              <div className="stat-card-value">¥{parseFloat(myData.totalAmount || 0).toFixed(2)}</div>
            </div>
          </div>
          <table className="stat-table">
            <thead>
              <tr>
                <th>书名</th>
                <th>作者</th>
                <th>单价</th>
                <th>购买数量</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              {myData.items.map((item, idx) => (
                <tr key={idx}>
                  <td>{item.title}</td>
                  <td>{item.author}</td>
                  <td>¥{parseFloat(item.price || 0).toFixed(2)}</td>
                  <td className="num-cell">{item.totalQuantity}</td>
                  <td className="num-cell">¥{parseFloat(item.totalAmount || 0).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      ) : (
        <p className="no-results">该时间范围内没有购买记录</p>
      )}
    </div>
  )

  return (
    <div className="statistics-section">
      <div className="container">
        <h2>数据统计</h2>

        <div className="stat-tabs">
          {role === 'admin' && (
            <>
              <button className={`stat-tab ${activeTab === 'sales' ? 'active' : ''}`}
                onClick={() => { setActiveTab('sales'); setSearched(false) }}>
                热销榜
              </button>
              <button className={`stat-tab ${activeTab === 'consumption' ? 'active' : ''}`}
                onClick={() => { setActiveTab('consumption'); setSearched(false) }}>
                消费榜
              </button>
            </>
          )}
          <button className={`stat-tab ${activeTab === 'my' ? 'active' : ''}`}
            onClick={() => { setActiveTab('my'); setSearched(false) }}>
            我的购买统计
          </button>
        </div>

        <div className="stat-filter">
          <div className="filter-group">
            <label>开始日期</label>
            <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
          </div>
          <div className="filter-group">
            <label>结束日期</label>
            <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
          </div>
          <button className="btn-primary" onClick={handleSearch} disabled={loading}>
            {loading ? '查询中...' : '查询'}
          </button>
        </div>

        {loading ? <p className="loading">加载中...</p> : (
          <>
            {activeTab === 'sales' && renderSalesRanking()}
            {activeTab === 'consumption' && renderConsumptionRanking()}
            {activeTab === 'my' && renderMyPurchase()}
          </>
        )}
      </div>
    </div>
  )
}

export default Statistics
