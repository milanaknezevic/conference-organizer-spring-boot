package com.example.pisioconf_backend.services;

import com.example.pisioconf_backend.exception.NotFoundException;
import com.example.pisioconf_backend.models.dto.Rezervacija;
import com.example.pisioconf_backend.models.entities.RezervacijaEntity;
import com.example.pisioconf_backend.models.requests.RezervacijaRequest;

import java.util.List;

public interface RezervacijaService {
    RezervacijaEntity insert(RezervacijaRequest rezervacijaRequest) throws NotFoundException;
    List<Rezervacija> findAll();

    void delete(Integer id);

    Rezervacija update(Integer id, RezervacijaRequest rezervacijaRequest);
}
