package com.example.demo.repository;

import com.example.demo.entity.ActivateCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivateCodesRepository extends JpaRepository<ActivateCodes, Integer>, JpaSpecificationExecutor<ActivateCodes> {
    @Query(value = "from activate_codes c where c.code=:code and c.valid>now()")
    Optional<ActivateCodes> findByCode(Integer code);

    @Modifying
    @Transactional
    @Async
    @Query(nativeQuery = true,value = "delete from activate_codes c where c.valid<=now()")
    void deleteOldCodes();

    @Modifying
    @Transactional
    @Async
    @Query(nativeQuery = true,value = "delete from activate_codes c where c.auth_user_id=?1")
    void deleteByAuthUser(UUID id);
}