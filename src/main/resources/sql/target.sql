-- 데이터베이스 생성
CREATE TABLE IF NOT EXISTS target_table_a (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_b (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_c (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_d (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_e (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_f (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_g (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_h (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_i (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS target_table_j (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS target_table_date (
                                                 id BIGINT PRIMARY KEY,
                                                 col1 VARCHAR(100),
    col2 VARCHAR(100),
    date_col TIMESTAMP
    );

-- 테이블 초기화
TRUNCATE TABLE target_table_a;
TRUNCATE TABLE target_table_b;
TRUNCATE TABLE target_table_c;
TRUNCATE TABLE target_table_d;
TRUNCATE TABLE target_table_e;
TRUNCATE TABLE target_table_f;
TRUNCATE TABLE target_table_g;
TRUNCATE TABLE target_table_h;
TRUNCATE TABLE target_table_i;
TRUNCATE TABLE target_table_j;
TRUNCATE TABLE target_table_date;
