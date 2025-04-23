package planit.massiverstandard.database.service;

import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.dto.DataBaseCreateDto;

/**
 * 설명: 데이터베이스 생성 인터페이스
 * 작성일: 2025. 04. 22.
 * 작성자: Mason
 */
public interface CreateDb {

    /**
     * 데이터베이스 생성
     * @param dto 데이터베이스 생성 DTO
     * @return 생성된 데이터베이스 객체
     */
    DataBase create(DataBaseCreateDto dto);
}
