-- 데이터베이스 생성
CREATE TABLE IF NOT EXISTS source_table_a (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_b (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_c (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_d (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_e (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_f (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_g (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_h (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_i (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_j (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100)
    );
CREATE TABLE IF NOT EXISTS source_table_date (
                                              id BIGINT PRIMARY KEY,
                                              col1 VARCHAR(100),
    col2 VARCHAR(100),
    date_col TIMESTAMP
    );

-- 테이블 초기화
TRUNCATE TABLE source_table_a;
TRUNCATE TABLE source_table_b;
TRUNCATE TABLE source_table_c;
TRUNCATE TABLE source_table_d;
TRUNCATE TABLE source_table_e;
TRUNCATE TABLE source_table_f;
TRUNCATE TABLE source_table_g;
TRUNCATE TABLE source_table_h;
TRUNCATE TABLE source_table_i;
TRUNCATE TABLE source_table_j;
TRUNCATE TABLE source_table_date;

-- 데이터 입력
INSERT INTO source_table_a (id, col1, col2)
SELECT X, 'val' || X, 'val' || X
FROM (
         WITH RECURSIVE seq(x) AS (
             SELECT 1
             UNION ALL
             SELECT x + 1 FROM seq WHERE x < 5000000
         )
         SELECT x FROM seq
     ) AS generated;

INSERT INTO source_table_b (id, col1, col2) VALUES (1, 'e', 'f');
INSERT INTO source_table_b (id, col1, col2) VALUES (2, 'g', 'h');

INSERT INTO source_table_c (id, col1, col2) VALUES (1, 'i', 'j');
INSERT INTO source_table_c (id, col1, col2) VALUES (2, 'k', 'l');

INSERT INTO source_table_d (id, col1, col2) VALUES (1, 'm', 'n');
INSERT INTO source_table_d (id, col1, col2) VALUES (2, 'o', 'p');

INSERT INTO source_table_e (id, col1, col2) VALUES (1, 'q', 'r');
INSERT INTO source_table_e (id, col1, col2) VALUES (2, 's', 't');

INSERT INTO source_table_f (id, col1, col2) VALUES (1, 'u', 'v');
INSERT INTO source_table_f (id, col1, col2) VALUES (2, 'w', 'x');

INSERT INTO source_table_g (id, col1, col2) VALUES (1, 'y', 'z');
INSERT INTO source_table_g (id, col1, col2) VALUES (2, 'aa', 'bb');

INSERT INTO source_table_h (id, col1, col2) VALUES (1, 'cc', 'dd');
INSERT INTO source_table_h (id, col1, col2) VALUES (2, 'ee', 'ff');

INSERT INTO source_table_i (id, col1, col2) VALUES (1, 'gg', 'hh');
INSERT INTO source_table_i (id, col1, col2) VALUES (2, 'ii', 'jj');

INSERT INTO source_table_j (id, col1, col2) VALUES (1, 'kk', 'll');
INSERT INTO source_table_j (id, col1, col2) VALUES (2, 'mm', 'nn');

INSERT INTO source_table_date (id, col1, col2, date_col)
VALUES (1, 'oo', 'pp', '2023-01-01 00:00:00');
INSERT INTO source_table_date (id, col1, col2, date_col)
VALUES (2, 'qq', 'rr', '2024-01-01 00:00:00');
INSERT INTO source_table_date (id, col1, col2, date_col)
VALUES (3, 'ss', 'tt', '2025-01-01 00:00:00');

