package com.garbigo.auth.repository;

import com.garbigo.auth.model.LiveLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveLocationRepository extends MongoRepository<LiveLocation, String> {
}