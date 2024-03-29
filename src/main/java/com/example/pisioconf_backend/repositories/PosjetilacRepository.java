package com.example.pisioconf_backend.repositories;

import com.example.pisioconf_backend.models.entities.KonferencijaEntity;
import com.example.pisioconf_backend.models.entities.KorisnikEntity;
import com.example.pisioconf_backend.models.entities.PosjetilacEntity;
import com.example.pisioconf_backend.models.entities.PosjetilacEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosjetilacRepository extends JpaRepository<PosjetilacEntity,PosjetilacEntityPK> {
    List<PosjetilacEntity> findByIdKorisnikId(Integer korisnikId);
    List<KonferencijaEntity> findAllByKorisnikId(Integer korsnikId);
}
