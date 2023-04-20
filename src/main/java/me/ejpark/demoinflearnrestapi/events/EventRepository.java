package me.ejpark.demoinflearnrestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    // 과제 필터링
    // basePrice가 100에서 200 사이인 event만 조회하기
    // 현재 등록 중인 event만 조회하기 (enrollmentDateTime)
    // jpa query!
    // findByBasePrice (x) -> findByBasePriceBetween
    public Page<Event> findByBasePriceBetween(int minPrice, int maxPrice, Pageable pageable);
    public Page<Event> findByCloseEnrollmentDateTimeAfter(LocalDateTime now, Pageable pageable);


}
