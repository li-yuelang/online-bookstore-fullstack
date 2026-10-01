import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import booksData from '../data.json'
import { useApp } from '../context/AppContext'

const BOOKS_PER_PAGE = 4

function BookList() {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(true)
  const [currentPage, setCurrentPage] = useState(1)
  const [searchTerm, setSearchTerm] = useState('')
  const { addToCart } = useApp()

  useEffect(() => {
    fetch('/api/v1/books')
      .then(res => res.json())
      .then(data => {
        setBooks(data)
        setLoading(false)
      })
      .catch(() => {
        setBooks(booksData.books)
        setLoading(false)
      })
  }, [])

  const filteredBooks = searchTerm
    ? books.filter(book =>
        book.title.includes(searchTerm) || book.author.includes(searchTerm)
      )
    : books

  const totalPages = Math.ceil(filteredBooks.length / BOOKS_PER_PAGE)
  const startIndex = (currentPage - 1) * BOOKS_PER_PAGE
  const currentBooks = filteredBooks.slice(startIndex, startIndex + BOOKS_PER_PAGE)

  const handleSearch = (e) => {
    e.preventDefault()
    setCurrentPage(1)
  }

  const handlePrevPage = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1)
    }
  }

  const handleNextPage = () => {
    if (currentPage < totalPages) {
      setCurrentPage(currentPage + 1)
    }
  }

  const handleAddToCart = async (book) => {
    const result = await addToCart({
      id: book.id,
      title: book.title,
      author: book.author,
      price: book.price,
      image: book.image
    })
    
    if (result.success) {
      alert('商品已成功加入购物车！')
    } else if (result.message && result.message !== '请先登录') {
      alert(result.message || '添加失败')
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
    <div className="book-list-page">
      <section className="hero">
        <div className="container">
          <h2>欢迎来到朗的云书店</h2>
          <p>发现您喜爱的电子书，随时随地阅读</p>
          <form className="search-box" onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="搜索书籍..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit">搜索</button>
          </form>
        </div>
      </section>

      <section className="book-list">
        <div className="container">
          <h2>热门书籍</h2>
          {loading ? (
            <p className="loading">加载中...</p>
          ) : (
            <>
              <div className="books">
                {currentBooks.map(book => (
                  <article key={book.id} className="book-item">
                    <figure className="book-cover">
                      <img src={book.image} alt={book.title} />
                    </figure>
                    <div className="book-info">
                      <h3>
                        <Link to={`/book/${book.id}`}>{book.title}</Link>
                      </h3>
                      <p className="author">作者：{book.author}</p>
                      <p className="price">¥{book.price.toFixed(2)}</p>
                      <p className="stock-info">库存：{book.stock !== undefined ? book.stock : '充足'}</p>
                      <div className="rating">
                        <span>{renderRating(book.rating)}</span>
                        <span>({book.ratingCount})</span>
                      </div>
                      <button
                        className="add-to-cart"
                        onClick={() => handleAddToCart(book)}
                      >
                        加入购物车
                      </button>
                    </div>
                  </article>
                ))}
              </div>

              {!searchTerm && totalPages > 1 && (
                <div className="pagination">
                  <button onClick={handlePrevPage} disabled={currentPage === 1}>
                    上一页
                  </button>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                    <button
                      key={page}
                      className={currentPage === page ? 'active' : ''}
                      onClick={() => setCurrentPage(page)}
                    >
                      {page}
                    </button>
                  ))}
                  <button onClick={handleNextPage} disabled={currentPage === totalPages}>
                    下一页
                  </button>
                </div>
              )}

              {searchTerm && currentBooks.length === 0 && (
                <p className="no-results">未找到相关书籍</p>
              )}
            </>
          )}
        </div>
      </section>
    </div>
  )
}

export default BookList