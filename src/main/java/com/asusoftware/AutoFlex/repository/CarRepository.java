package com.asusoftware.AutoFlex.repository;

import com.asusoftware.AutoFlex.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID>, JpaSpecificationExecutor<Car> {
}
