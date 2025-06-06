package com.inqool.tennisclub.config;

import com.inqool.tennisclub.data.model.CourtEntity;
import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
import com.inqool.tennisclub.data.repository.CourtRepository;
import com.inqool.tennisclub.data.repository.CourtSurfaceRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Profile("!test")
@Component
@ConditionalOnProperty(name = "app.init-data", havingValue = "true")
public class DataInitializer {

    private final CourtSurfaceRepository courtSurfaceRepository;
    private final CourtRepository courtRepository;

    public DataInitializer(CourtSurfaceRepository courtSurfaceRepository, CourtRepository courtRepository) {
        this.courtSurfaceRepository = courtSurfaceRepository;
        this.courtRepository = courtRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.warn("Initializing CourtSurfaces!");

        CourtSurfaceEntity surfaceEntity1 = new CourtSurfaceEntity();
        surfaceEntity1.setSurfaceName("Grass");
        surfaceEntity1.setSurfaceDescription("Short grass 7mm");
        surfaceEntity1.setCostPerMinute(BigDecimal.valueOf(1.2));

        CourtSurfaceEntity surfaceEntity2 = new CourtSurfaceEntity();
        surfaceEntity2.setSurfaceName("Hard");
        surfaceEntity2.setSurfaceDescription("Blue rubber");
        surfaceEntity2.setCostPerMinute(BigDecimal.valueOf(.1));

        CourtSurfaceEntity surfaceEntity3 = new CourtSurfaceEntity();
        surfaceEntity3.setSurfaceName("Clay");
        surfaceEntity3.setSurfaceDescription("Red clay");
        surfaceEntity3.setCostPerMinute(BigDecimal.valueOf(0.2));

        courtSurfaceRepository.save(surfaceEntity1);
        courtSurfaceRepository.save(surfaceEntity2);
        courtSurfaceRepository.save(surfaceEntity3);

        log.warn("Initializing Courts!");

        CourtEntity courtEntity1 = new CourtEntity();
        courtEntity1.setCourtNumber(1);
        courtEntity1.setCourtSurface(surfaceEntity2);

        CourtEntity courtEntity2 = new CourtEntity();
        courtEntity2.setCourtNumber(2);
        courtEntity2.setCourtSurface(surfaceEntity2);

        CourtEntity courtEntity3 = new CourtEntity();
        courtEntity3.setCourtNumber(3);
        courtEntity3.setCourtSurface(surfaceEntity3);

        CourtEntity courtEntity4 = new CourtEntity();
        courtEntity4.setCourtNumber(4);
        courtEntity4.setCourtSurface(surfaceEntity1);

        courtRepository.save(courtEntity1);
        courtRepository.save(courtEntity2);
        courtRepository.save(courtEntity3);
        courtRepository.save(courtEntity4);
    }
}
