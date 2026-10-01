-- 在线书店数据库完整初始化脚本
-- 迭代二版本：包含购物车和订单功能

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS online_library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE online_library;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'customer',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 书籍表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(500),
    rating DOUBLE NOT NULL,
    rating_count INT NOT NULL,
    publisher VARCHAR(100),
    publish_date VARCHAR(20),
    pages INT,
    isbn VARCHAR(20),
    stock INT NOT NULL DEFAULT 0,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    rating VARCHAR(20) NOT NULL,
    date VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车表
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_book (user_id, book_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_id VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化书籍数据
INSERT IGNORE INTO books (id, title, author, price, image, rating, rating_count, publisher, publish_date, pages, isbn, description) VALUES
(1, 'JavaScript 高级程序设计', '尼古拉斯·扎卡斯', 99.00, 'https://file.ituring.com.cn/LargeCover/20080d81a4fb8a268c40', 5, 120, '人民邮电出版社', '2020-01-01', 768, '9787115528039', '《JavaScript 高级程序设计》是一本面向中级到高级 JavaScript 开发人员的专业书籍。'),
(2, 'Python 编程：从入门到实践', '埃里克·马瑟斯', 89.00, 'https://file.ituring.com.cn/LargeCover/23047b0f0134221867e2', 4.5, 98, '人民邮电出版社', '2019-10-01', 448, '9787115428028', '《Python 编程：从入门到实践》是一本面向初学者的 Python 编程入门书籍。'),
(3, '深入理解计算机系统', 'Randal E. Bryant', 129.00, 'https://img12.360buyimg.com/n1/jfs/t1/328223/32/18971/145043/68c3d0f2Fc1141045/610b642b09eb064e.jpg', 5, 156, '机械工业出版社', '2016-01-01', 720, '9787111407010', '《深入理解计算机系统》是一本系统介绍计算机系统的书籍。'),
(4, '算法导论', 'Thomas H. Cormen', 119.00, 'https://cdn.how2cs.cn/csguide/%E7%AE%97%E6%B3%95%E5%AF%BC%E8%AE%BA.jpg', 4.5, 87, '机械工业出版社', '2013-01-01', 780, '9787111407010', '《算法导论》是一本经典的算法教材。'),
(5, '红楼梦', '曹雪芹', 98.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgUTr2lWLRz5u7mnRXvQzuY3JDFXahuO7LVQ&s', 5, 256, '人民文学出版社', '2008-07-01', 1200, '9787020002207', '《红楼梦》是中国古典四大名著之一。'),
(6, '三国演义', '罗贯中', 88.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkW6B4l1se4_82SdRABMen0IkJtWqORGCpuQ&s', 4.5, 234, '人民文学出版社', '2008-07-01', 960, '9787020002207', '《三国演义》是中国古典四大名著之一。'),
(7, '水浒传', '施耐庵', 85.00, 'https://static.airchina.com.cn/upload/t/jWuZv5etyRvOB6L_a8mpHZ426gE=/670x670/img/store/438/1652348073012..jpg', 4.5, 212, '人民文学出版社', '2008-07-01', 1000, '9787020002207', '《水浒传》是中国古典四大名著之一。'),
(8, '西游记', '吴承恩', 82.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQTa0_DE8rbWzyEdAUWtmRV3ZEwi-elqwKLQQ&s', 5, 268, '人民文学出版社', '2008-07-01', 880, '9787020002207', '《西游记》是中国古典四大名著之一。'),
(9, '百年孤独', '加西亚·马尔克斯', 78.00, 'https://m.media-amazon.com/images/I/41ZAmA+408L._AC_UF1000,1000_QL80_.jpg', 4.5, 189, '南海出版公司', '2017-08-01', 360, '9787544291170', '《百年孤独》是哥伦比亚作家加西亚·马尔克斯的代表作。'),
(10, '巴黎圣母院', '维克多·雨果', 75.00, 'https://piccdn3.umiwi.com/img/201902/27/201902270158200726868926.jpg?x-oss-process=image/resize,m_fill,h_320,w_240', 4.5, 178, '人民文学出版社', '2018-05-01', 420, '9787020002207', '《巴黎圣母院》是法国作家维克多·雨果的代表作之一。');

-- 初始化评论数据（完整的 20 条评论）
INSERT IGNORE INTO reviews (id, book_id, name, rating, date, content) VALUES
(1, 1, '张三', '★★★★★', '2026-03-01', '这本书非常棒，内容全面且深入，是 JavaScript 开发者的必读书籍。作者讲解清晰，示例丰富，非常适合学习 JavaScript 的高级特性。'),
(2, 1, '李四', '★★★★☆', '2026-02-15', '内容很详细，但是有些地方比较难懂，需要反复阅读。不过总体来说是一本非常好的 JavaScript 书籍。'),
(3, 2, '王五', '★★★★★', '2026-03-05', '这本书非常适合 Python 初学者，作者讲解清晰，示例丰富，通过实际项目学习非常有帮助。'),
(4, 2, '赵六', '★★★★☆', '2026-02-20', '内容很基础，适合入门，但是对于有一定编程基础的人来说可能有点简单。'),
(5, 3, '孙七', '★★★★★', '2026-03-10', '这本书是计算机专业学生的必读书籍，内容全面且深入，讲解清晰，帮助我更好地理解计算机系统的工作原理。'),
(6, 3, '周八', '★★★★☆', '2026-02-25', '内容有点难度，需要一定的计算机基础，但是非常值得一读。'),
(7, 4, '吴九', '★★★★★', '2026-03-12', '这本书是算法学习的经典教材，内容全面，讲解详细，是计算机专业学生的必读书籍。'),
(8, 4, '郑十', '★★★★☆', '2026-02-28', '内容比较理论化，需要一定的数学基础，但是对于理解算法的原理非常有帮助。'),
(9, 5, '林黛玉', '★★★★★', '2026-03-01', '满纸荒唐言，一把辛酸泪。都云作者痴，谁解其中味？'),
(10, 5, '贾宝玉', '★★★★★', '2026-02-15', '假作真时真亦假，无为有处有还无。'),
(11, 6, '诸葛亮', '★★★★★', '2026-03-05', '鞠躬尽瘁，死而后已。'),
(12, 6, '曹操', '★★★★☆', '2026-02-20', '宁教我负天下人，休教天下人负我。'),
(13, 7, '宋江', '★★★★★', '2026-03-10', '忠义堂前绣字红旗后，一书''山东呼保义''，一书''河北玉麒麟''。'),
(14, 7, '武松', '★★★★☆', '2026-02-25', '酒壮英雄胆，打虎景阳冈。'),
(15, 8, '孙悟空', '★★★★★', '2026-03-12', '俺老孙来也！'),
(16, 8, '猪八戒', '★★★★☆', '2026-02-28', '大师兄，师傅被妖怪抓走了！'),
(17, 9, '马尔克斯', '★★★★★', '2026-03-08', '许多年后，奥雷良诺上校站在行刑队面前，准会想起父亲带他去参观冰块的那个遥远的下午。'),
(18, 9, '读者', '★★★★☆', '2026-02-22', '魔幻现实主义的巅峰之作，每一页都充满了惊喜。'),
(19, 10, '维克多·雨果', '★★★★★', '2026-03-13', '丑在美的旁边，畸形靠近着优美，丑怪藏在崇高背后，美与丑并存，光明与黑暗相共。'),
(20, 10, '读者', '★★★★☆', '2026-02-29', '卡西莫多的形象让人难忘，这是一部关于爱与美的经典之作。');

-- 创建默认管理员账号（仅供演示；密码：admin123）
INSERT IGNORE INTO users (id, username, password, email, role, enabled) VALUES
(1, 'admin', 'admin123', 'admin@onlinelibrary.com', 'admin', TRUE);

-- 为现有书籍补充库存（ALTER TABLE自动添加stock列后，设置默认库存）
UPDATE books SET stock = 50 WHERE stock IS NULL OR stock = 0;
