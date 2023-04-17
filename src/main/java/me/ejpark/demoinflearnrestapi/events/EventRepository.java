package me.ejpark.demoinflearnrestapi.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    // 과제 필터링
    // basePrice가 100에서 200 사이인 event만 조회하기
    // 현재 등록 중인 event만 조회하기 (enrollment)


    String EVENT_FILTER_BASE_PRICE =
            "SELECT * FROM EVENT WHERE basePrice BETWEEN 100 AND 200";


    // 지금 날짜가 beginEnrollment와 closeEnrollmentDateTime 사이일 것
//    LocalDateTime now = LocalDateTime.now();
//    String EVENT_IN_ENROLLMENT =
//            "SELECT * FROM EVENT WHERE beginEnrollment < :now and endEnrollment > :now";
//
//    @Query(EVENT_IN_ENROLLMENT)
//    List<Event> findByEnrollment(@Param("now") LocalDateTime now);
//
//    @Query(EVENT_FILTER_BASE_PRICE)
//    List<Event> findByBasePrice();
//





    /*
        @Query(FILTER_CUSTOMERS_ON_FIRST_NAME_AND_LAST_NAME_QUERY)
    Page<Customer> findByFirstNameLikeAndLastNameLike(String firstNameFilter, String lastNameFilter, Pageable pageable);

     */


    /*
        /** controller
     * @param firstNameFilter Filter for the first Name if required
     * @param lastNameFilter  Filter for the last Name if required
     * @return List of filtered customers
//     @GetMapping("/api/v1/customers")
//    public List<Customer> fetchCustomersAsFilteredList(@RequestParam(defaultValue = "") String firstNameFilter,
//                                                       @RequestParam(defaultValue = "") String lastNameFilter) {
//        return customerService.fetchFilteredCustomerDataAsList(firstNameFilter, lastNameFilter);
//    }
//
//     */




}
