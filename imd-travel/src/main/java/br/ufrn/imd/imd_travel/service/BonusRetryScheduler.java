package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.model.BonusOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BonusRetryScheduler {

    private final BonusQueueService bonusQueueService;
    private final RestTemplate restTemplate;

    @Value("${applications.fidelity}")
    private String baseFidelityUrl;

    public BonusRetryScheduler(BonusQueueService bonusQueueService, @Qualifier("restTemplate5s") RestTemplate restTemplate) {
        this.bonusQueueService = bonusQueueService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 800000)
    public void retry() {
        BonusOperation op;

        while ((op = bonusQueueService.dequeueBonus()) != null) {

            String url = baseFidelityUrl + "/bonus?user=" + op.getUserId() +
                    "&bonus=" + op.getBonusValue();

            try {
                restTemplate.postForEntity(url, HttpEntity.EMPTY, Void.class);
            } catch (Exception e) {
                bonusQueueService.enqueueBonus(op.getUserId(), op.getBonusValue());
            }
        }
    }
}
