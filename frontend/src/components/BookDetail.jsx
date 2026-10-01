import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import booksData from '../data.json'
import { useApp } from '../context/AppContext'

function BookDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { addToCart, userId } = useApp()
  const [book, setBook] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch(`/api/v1/book/${id}`)
      .then(res => res.json())
      .then(data => {
        if (data && data.id) {
          setBook(data)
        } else {
          setBook(booksData.books.find(b => b.id === parseInt(id)) || null)
        }
        setLoading(false)
      })
      .catch(() => {
        setBook(booksData.books.find(b => b.id === parseInt(id)) || null)
        setLoading(false)
      })
  }, [id])

  if (loading) {
    return (
      <div className="book-detail">
        <div className="container">
          <p className="loading">加载中...</p>
        </div>
      </div>
    )
  }

  if (!book || !book.id) {
    return (
      <div className="book-detail">
        <div className="container">
          <h2>书籍未找到</h2>
          <Link to="/" className="back-link">返回书籍列表</Link>
        </div>
      </div>
    )
  }

  const handleAddToCart = async () => {
    
    
    // 先检查是否登录
    if (!userId) {
      alert('请先登录')
      navigate('/login')
      return
    }

    // 已登录时才执行加入购物车
    const result = await addToCart({
      id: book.id,
      title: book.title,
      author: book.author,
      price: book.price,
      image: book.image
    })

    if (result.success) {
      alert('商品已成功加入购物车！')
    }
  }

  const renderRating = (rating) => {
    let stars = ''
    for (let i = 1; i <= 5; i++) {
      if (i <= rating) {
        stars += '★'
      } else if (i - 0.5 <= rating) {
        stars += '☆'
      } else {
        stars += '☆'
      }
    }
    return stars
  }

  return (
    <div className="book-detail">
      <div className="container">
        <div className="book-detail-content">
          <figure className="book-cover-large">
            <img src={book.image} alt={book.title} />
          </figure>
          <div className="book-info-detail">
            <h2>{book.title}</h2>
            <p className="author">作者：{book.author}</p>
            <div className="rating">
              <span>{renderRating(book.rating)}</span>
              <span>({book.ratingCount})</span>
            </div>
            <p className="price">¥{parseFloat(book.price || 0).toFixed(2)}</p>
            <div className="book-meta">
              <p><strong>出版社：</strong>{book.publisher}</p>
              <p><strong>出版日期：</strong>{book.publishDate}</p>
              <p><strong>页数：</strong>{book.pages}</p>
              <p><strong>ISBN：</strong>{book.isbn}</p>
              <p><strong>库存：</strong>{book.stock !== undefined ? book.stock : '未知'}</p>
            </div>
            <div className="book-actions">
              <button className="add-to-cart" onClick={handleAddToCart}>加入购物车</button>
            </div>
          </div>
        </div>

        {book.description && (
          <div className="book-description">
            <h3>内容简介</h3>
            <div dangerouslySetInnerHTML={{ __html: book.description }} />
          </div>
        )}

        {book.reviews && book.reviews.length > 0 && (
          <div className="book-reviews">
            <h3>读者评论</h3>
            <div className="reviews-list">
              {book.reviews.map((review, index) => (
                <div key={index} className="review-item">
                  <div className="review-header">
                    <span className="review-name">{review.name}</span>
                    <span className="review-rating">{review.rating}</span>
                    <span className="review-date">{review.date}</span>
                  </div>
                  <div className="review-content">
                    {review.content}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="back-to-list">
          <Link to="/" className="back-link">返回书籍列表</Link>
        </div>
      </div>
    </div>
  )
}

export default BookDetail
