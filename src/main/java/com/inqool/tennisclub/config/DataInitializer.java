package com.inqool.tennisclub.config;

import com.inqool.tennisclub.data.model.CourtSurfaceEntity;
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

    public DataInitializer(CourtSurfaceRepository courtSurfaceRepository) {
        this.courtSurfaceRepository = courtSurfaceRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.warn("Initializing CourtSurfaces!");

        CourtSurfaceEntity entity1 = new CourtSurfaceEntity();
        entity1.setSurfaceName("Grass");
        entity1.setSurfaceDescription("Short grass 7mm");
        entity1.setCostPerMinute(BigDecimal.valueOf(1.2));

        CourtSurfaceEntity entity2 = new CourtSurfaceEntity();
        entity2.setSurfaceName("Hard");
        entity2.setSurfaceDescription("Blue rubber");
        entity2.setCostPerMinute(BigDecimal.valueOf(.1));

        CourtSurfaceEntity entity3 = new CourtSurfaceEntity();
        entity3.setSurfaceName("Clay");
        entity3.setSurfaceDescription("Red clay");
        entity3.setCostPerMinute(BigDecimal.valueOf(0.2));

        courtSurfaceRepository.save(entity1);
        courtSurfaceRepository.save(entity2);
        courtSurfaceRepository.save(entity3);
    }
}
