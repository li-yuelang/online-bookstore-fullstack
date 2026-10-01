import { useState, useEffect } from 'react'

function AdminBooks() {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editBook, setEditBook] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [formData, setFormData] = useState({
    title: '', author: '', price: '', image: '', isbn: '',
    publisher: '', publishDate: '', pages: '', description: '', stock: '', rating: '5', ratingCount: '0'
  })

  const loadBooks = async () => {
    try {
      const res = await fetch('/api/v1/books')
      if (res.ok) setBooks(await res.json())
    } catch (e) {
      console.error('加载书籍失败:', e)
    }
    setLoading(false)
  }

  useEffect(() => { loadBooks() }, [])

  const filteredBooks = searchTerm
    ? books.filter(b => b.title.includes(searchTerm) || b.author.includes(searchTerm))
    : books

  const resetForm = () => {
    setFormData({ title: '', author: '', price: '', image: '', isbn: '',
      publisher: '', publishDate: '', pages: '', description: '', stock: '', rating: '5', ratingCount: '0' })
    setEditBook(null)
    setShowForm(false)
  }

  const handleEdit = (book) => {
    setFormData({
      title: book.title || '',
      author: book.author || '',
      price: book.price || '',
      image: book.image || '',
      isbn: book.isbn || '',
      publisher: book.publisher || '',
      publishDate: book.publishDate || '',
      pages: book.pages || '',
      description: book.description || '',
      stock: book.stock || '0',
      rating: book.rating || '5',
      ratingCount: book.ratingCount || '0'
    })
    setEditBook(book)
    setShowForm(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const payload = {
      ...formData,
      price: parseFloat(formData.price) || 0,
      stock: parseInt(formData.stock) || 0,
      pages: formData.pages ? parseInt(formData.pages) : null,
      rating: parseFloat(formData.rating) || 5,
      ratingCount: parseInt(formData.ratingCount) || 0
    }

    try {
      let res
      if (editBook) {
        res = await fetch(`/api/v1/admin/books/${editBook.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        })
      } else {
        res = await fetch('/api/v1/admin/books', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        })
      }
      const data = await res.json()
      if (data.success) {
        alert(editBook ? '书籍更新成功' : '书籍添加成功')
        resetForm()
        loadBooks()
      } else {
        alert(data.message || '操作失败')
      }
    } catch (e) {
      alert('操作失败：' + e.message)
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('确定要删除这本书吗？')) return
    try {
      const res = await fetch(`/api/v1/admin/books/${id}`, { method: 'DELETE' })
      const data = await res.json()
      if (data.success) {
        alert('删除成功')
        loadBooks()
      } else {
        alert(data.message || '删除失败')
      }
    } catch (e) {
      alert('删除失败：' + e.message)
    }
  }

  if (loading) {
    return <div className="admin-section"><div className="container"><p className="loading">加载中...</p></div></div>
  }

  return (
    <div className="admin-section">
      <div className="container">
        <div className="admin-header">
          <h2>书籍管理</h2>
          <button className="btn-primary" onClick={() => { resetForm(); setShowForm(true) }}>添加新书</button>
        </div>

        <div className="search-box admin-search">
          <input type="text" placeholder="搜索书名或作者..." value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)} />
        </div>

        {showForm && (
          <div className="admin-form-overlay">
            <div className="admin-form">
              <h3>{editBook ? '编辑书籍' : '添加新书'}</h3>
              <form onSubmit={handleSubmit}>
                <div className="form-grid">
                  <div className="form-group">
                    <label>书名 *</label>
                    <input type="text" value={formData.title} required
                      onChange={e => setFormData({...formData, title: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>作者 *</label>
                    <input type="text" value={formData.author} required
                      onChange={e => setFormData({...formData, author: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>价格 *</label>
                    <input type="number" step="0.01" value={formData.price} required
                      onChange={e => setFormData({...formData, price: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>库存 *</label>
                    <input type="number" value={formData.stock}
                      onChange={e => setFormData({...formData, stock: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>封面URL</label>
                    <input type="text" value={formData.image}
                      onChange={e => setFormData({...formData, image: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>ISBN</label>
                    <input type="text" value={formData.isbn}
                      onChange={e => setFormData({...formData, isbn: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>出版社</label>
                    <input type="text" value={formData.publisher}
                      onChange={e => setFormData({...formData, publisher: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>出版日期</label>
                    <input type="text" value={formData.publishDate}
                      onChange={e => setFormData({...formData, publishDate: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>页数</label>
                    <input type="number" value={formData.pages}
                      onChange={e => setFormData({...formData, pages: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>评分</label>
                    <input type="number" step="0.5" value={formData.rating}
                      onChange={e => setFormData({...formData, rating: e.target.value})} />
                  </div>
                </div>
                <div className="form-group">
                  <label>简介</label>
                  <textarea value={formData.description} rows="3"
                    onChange={e => setFormData({...formData, description: e.target.value})} />
                </div>
                <div className="form-actions">
                  <button type="submit" className="btn-primary">{editBook ? '保存修改' : '添加'}</button>
                  <button type="button" className="btn-cancel" onClick={resetForm}>取消</button>
                </div>
              </form>
            </div>
          </div>
        )}

        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>封面</th>
                <th>书名</th>
                <th>作者</th>
                <th>价格</th>
                <th>库存</th>
                <th>ISBN</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {filteredBooks.map(book => (
                <tr key={book.id}>
                  <td>{book.id}</td>
                  <td><img src={book.image} alt={book.title} className="admin-thumb" /></td>
                  <td>{book.title}</td>
                  <td>{book.author}</td>
                  <td>¥{parseFloat(book.price || 0).toFixed(2)}</td>
                  <td>
                    <span className={`stock-tag ${book.stock > 0 ? 'stock-in' : 'stock-out'}`}>
                      {book.stock || 0}
                    </span>
                  </td>
                  <td>{book.isbn || '-'}</td>
                  <td className="admin-actions">
                    <button className="btn-edit" onClick={() => handleEdit(book)}>编辑</button>
                    <button className="btn-delete" onClick={() => handleDelete(book.id)}>删除</button>
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

export default AdminBooks
