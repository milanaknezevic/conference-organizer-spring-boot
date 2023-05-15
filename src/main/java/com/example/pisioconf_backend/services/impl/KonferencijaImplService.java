package com.example.pisioconf_backend.services.impl;

import com.example.pisioconf_backend.repositories.KonferencijaRepository;
import com.example.pisioconf_backend.repositories.KorisnikRepository;
import com.example.pisioconf_backend.repositories.LokacijaRepository;
import com.example.pisioconf_backend.repositories.OcjenaRepository;
import com.example.pisioconf_backend.exception.NotFoundException;
import com.example.pisioconf_backend.models.dto.Konferencija;
import com.example.pisioconf_backend.models.dto.Ocjena;
import com.example.pisioconf_backend.models.entities.KonferencijaEntity;
import com.example.pisioconf_backend.models.entities.KorisnikEntity;
import com.example.pisioconf_backend.models.entities.LokacijaEntity;
import com.example.pisioconf_backend.models.requests.KonferencijaRequest;
import com.example.pisioconf_backend.services.KonferencijaService;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KonferencijaImplService implements KonferencijaService {
    private final KonferencijaRepository konferencijaRepository;
    private final KorisnikRepository korisnikRepository;
    private final ModelMapper modelMapper;
    private final LokacijaRepository lokacijaRepository;
    private final OcjenaRepository ocjenaRepository;

    public KonferencijaImplService(KonferencijaRepository konferencijaRepository, KorisnikRepository korisnikRepository, ModelMapper modelMapper,
                                   LokacijaRepository lokacijaRepository,
                                   OcjenaRepository ocjenaRepository) {
        this.konferencijaRepository = konferencijaRepository;
        this.korisnikRepository = korisnikRepository;

        this.modelMapper = modelMapper;
        this.lokacijaRepository = lokacijaRepository;
        this.ocjenaRepository = ocjenaRepository;
    }


    @Override
    public List<Konferencija> findAll() {
        return konferencijaRepository.findAll().stream().map(l -> modelMapper.map(l, Konferencija.class)).collect(Collectors.toList());
    }



    @Override
    public List<Konferencija> getAllKonferencijeByLocationId(Integer id) {
        return konferencijaRepository.getAllByLokgacijaByLokacijaId_Id(id).stream().map(l -> modelMapper.map(l, Konferencija.class)).collect(Collectors.toList());
    }

    @Override
    public Konferencija insert(KonferencijaRequest konferencijaRequest) throws NotFoundException {
        KorisnikEntity moderatorEntity = korisnikRepository.findById(konferencijaRequest.getModeratorId()).get();
        KorisnikEntity organizatorEnttity = korisnikRepository.findById(konferencijaRequest.getOrganizatorId()).get();
        LokacijaEntity lokacijaEntity = lokacijaRepository.findById(konferencijaRequest.getLokacijaId()).get();

        KonferencijaEntity konferencijaEntity = modelMapper.map(konferencijaRequest, KonferencijaEntity.class);
        konferencijaEntity.setKorisnikByModeratorId(moderatorEntity);
        konferencijaEntity.setKorisnikByOrganizatorId(organizatorEnttity);
        konferencijaEntity.setLokacijaByLokacijaId(lokacijaEntity);

        konferencijaEntity.setId(null);

        konferencijaEntity = konferencijaRepository.saveAndFlush(konferencijaEntity);
        return findById(konferencijaEntity.getId());
    }

    @Override
    public Konferencija update(Integer id, KonferencijaRequest konferencijaRequest) throws NotFoundException {
        KonferencijaEntity konferencijaEntity = konferencijaRepository.findById(id).get(); // modelMapper.map(konferencijaRequest, KonferencijaEntity.class);
        if (konferencijaRequest.getStartTime() != null) {
            konferencijaEntity.setStartTime(konferencijaRequest.getStartTime());
        }
        if (konferencijaRequest.getEndTime() != null) {
            konferencijaEntity.setEndTime(konferencijaRequest.getEndTime());
        }
        if (konferencijaRequest.getStatus() != null) {
            konferencijaEntity.setStatus(konferencijaRequest.getStatus());
        }
        if (konferencijaRequest.getNaziv() != null) {
            konferencijaEntity.setNaziv(konferencijaRequest.getNaziv());
        }

        konferencijaEntity.setId(id);

        konferencijaEntity = konferencijaRepository.saveAndFlush(konferencijaEntity);
        return findById(konferencijaEntity.getId());
    }

    @Override
    public void delete(Integer id) {
        konferencijaRepository.deleteById(id);

    }

    @Override
    public List<Ocjena> getAllOcjeneByKorisnikId(Integer id) {
        return konferencijaRepository.getAllOcjeneByKorisnikId(id).stream().map(l -> modelMapper.map(l, Ocjena.class)).collect(Collectors.toList());

    }



    @Override
    public Konferencija findById(Integer id) throws NotFoundException {
        return modelMapper.map(konferencijaRepository.findById(id).orElseThrow(NotFoundException::new), Konferencija.class);
    }

    @Override
    public List<KonferencijaEntity> findAllWhereKonferencijaIsNotFinished() {
        return konferencijaRepository.getAllNotFinishedKonferencije();
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkStatusKonferencije()
    {
        //uzmi sve konferencije i proci kroz njih i provjeravam dal je zavrsena ako jeste finished jednako ture
        LocalDateTime now = LocalDateTime.now();
        List<KonferencijaEntity> nezavrseneKonferencije=findAllWhereKonferencijaIsNotFinished();
        for(KonferencijaEntity k:nezavrseneKonferencije)
        {
            LocalDateTime vrijeme=LocalDateTime.ofInstant(k.getEndTime().toInstant(), ZoneId.systemDefault());
            if(vrijeme.isAfter(now))
            {
                k.setStatus(true);
                k=konferencijaRepository.saveAndFlush(k);
            }
        }


    }
}
