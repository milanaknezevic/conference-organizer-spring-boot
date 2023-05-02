package com.example.pisioconf_backend.controllers.repositories;

import com.example.pisioconf_backend.models.entities.KonferencijaEntity;
import com.example.pisioconf_backend.models.entities.KorisnikEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KorisnikRepository extends JpaRepository<KorisnikEntity,Integer> {
    @Query("SELECT k from KonferencijaEntity  k where k.korisnikByModeratorId.id =:idk")
    List<KonferencijaEntity> getAllByKorisnikByModeratorId (Integer idk);
    @Query("SELECT k from KonferencijaEntity  k where k.korisnikByOrganizatorId.id =:id")
    List<KonferencijaEntity> getAllByKorisnikByOrganizatorId(Integer id);
}
