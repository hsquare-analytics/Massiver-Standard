-- ================================================
-- 테이블 생성
-- ================================================
CREATE TABLE IF NOT EXISTS target_table_a (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_b (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_c (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_d (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_e (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_f (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_g (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_h (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_i (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_j (
                                              id   BIGINT      PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_date (
                                                 id       BIGINT      PRIMARY KEY,
                                                 col1     VARCHAR(100),
    col2     VARCHAR(100),
    date_col TIMESTAMP
    );


-- ================================================
-- 테이블 초기화 (한 번에)
-- ================================================
TRUNCATE
    target_table_a,
    target_table_b,
    target_table_c,
    target_table_d,
    target_table_e,
    target_table_f,
    target_table_g,
    target_table_h,
    target_table_i,
    target_table_j,
    target_table_date
RESTART IDENTITY;
