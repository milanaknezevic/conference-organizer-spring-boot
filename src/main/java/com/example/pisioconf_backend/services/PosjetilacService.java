package com.example.pisioconf_backend.services;

import com.example.pisioconf_backend.exception.NotFoundException;
import com.example.pisioconf_backend.models.dto.Ocjena;
import com.example.pisioconf_backend.models.dto.Posjetilac;
import com.example.pisioconf_backend.models.entities.PosjetilacEntity;
import com.example.pisioconf_backend.models.entities.PosjetilacEntityPK;
import com.example.pisioconf_backend.models.requests.PosjetilacRequest;

import java.util.List;

public interface PosjetilacService {
    PosjetilacEntity insert(PosjetilacRequest posjetilacRequest) throws NotFoundException;

    List<Posjetilac> findAll();

    void delete(PosjetilacEntityPK id);


}
