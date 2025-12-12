-- 创建contacts表
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20),
    is_favorite BOOLEAN DEFAULT FALSE
);

-- 创建contact_info表
CREATE TABLE IF NOT EXISTS contact_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contact_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    contact_value VARCHAR(100) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE
);