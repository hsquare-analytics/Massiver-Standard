-- ================================================
-- 테이블 생성
-- ================================================
CREATE TABLE IF NOT EXISTS source_table_a (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_b (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_c (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_d (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_e (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_f (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_g (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_h (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_i (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_j (
                                              id      BIGINT       PRIMARY KEY,
                                              col1    VARCHAR(100),
    col2    VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS source_table_date (
                                                 id        BIGINT       PRIMARY KEY,
                                                 col1      VARCHAR(100),
    col2      VARCHAR(100),
    date_col  TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS source_table_date_massive (
    id        BIGINT,
    id2      BIGINT,
    id3      BIGINT,
    col1      VARCHAR(100),
    col2      VARCHAR(100),
    date_col  TIMESTAMP,
    PRIMARY KEY (id, id2, id3)
);


-- ================================================
-- 테이블 초기화 (한 번에)
-- ================================================
TRUNCATE
    source_table_a,
    source_table_b,
    source_table_c,
    source_table_d,
    source_table_e,
    source_table_f,
    source_table_g,
    source_table_h,
    source_table_i,
    source_table_j,
    source_table_date,
    source_table_date_massive
RESTART IDENTITY;


-- ================================================
-- 데이터 입력
-- ================================================

-- A 테이블에 1~5,000,000 까지 대량 생성
INSERT INTO source_table_a (id, col1, col2)
SELECT gs, 'val' || gs, 'val' || gs
FROM generate_series(1, 5000000) AS gs;

INSERT INTO source_table_date_massive (id, id2, id3, col1, col2, date_col)
SELECT
    gs,
    gs + 1,
    gs + 2,
    'val' || gs,
    'val' || gs,
    DATE '2025-01-01' + (gs - 1) * INTERVAL '1 day'
-- gs=1 → 2025-01-01, gs=2 → 2025-01-02, …, gs=5000000 → (2025-01-01 + 4999999일)
FROM generate_series(1, 5000000) AS gs;

-- 나머지 테이블에는 소량 샘플 데이터
INSERT INTO source_table_b (id, col1, col2) VALUES
                                                (1, 'e', 'f'),
                                                (2, 'g', 'h');

INSERT INTO source_table_c (id, col1, col2) VALUES
                                                (1, 'i', 'j'),
                                                (2, 'k', 'l');

INSERT INTO source_table_d (id, col1, col2) VALUES
                                                (1, 'm', 'n'),
                                                (2, 'o', 'p');

INSERT INTO source_table_e (id, col1, col2) VALUES
                                                (1, 'q', 'r'),
                                                (2, 's', 't');

INSERT INTO source_table_f (id, col1, col2) VALUES
                                                (1, 'u', 'v'),
                                                (2, 'w', 'x');

INSERT INTO source_table_g (id, col1, col2) VALUES
                                                (1, 'y', 'z'),
                                                (2, 'aa', 'bb');

INSERT INTO source_table_h (id, col1, col2) VALUES
                                                (1, 'cc', 'dd'),
                                                (2, 'ee', 'ff');

INSERT INTO source_table_i (id, col1, col2) VALUES
                                                (1, 'gg', 'hh'),
                                                (2, 'ii', 'jj');

INSERT INTO source_table_j (id, col1, col2) VALUES
                                                (1, 'kk', 'll'),
                                                (2, 'mm', 'nn');

INSERT INTO source_table_date (id, col1, col2, date_col) VALUES
                                                             (1, 'oo', 'pp', '2023-01-01 00:00:00'),
                                                             (2, 'qq', 'rr', '2024-01-01 00:00:00'),
                                                             (3, 'ss', 'tt', '2025-01-01 00:00:00');
