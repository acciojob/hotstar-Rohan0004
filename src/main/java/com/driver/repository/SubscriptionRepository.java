package com.driver.repository;

import com.driver.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    Subscription findByUserId(Integer userId);

    @Query(value = "select sum( total_amount_paid ) from subscription;",nativeQuery = true)
    Integer calculateTotalRevenue();
}
