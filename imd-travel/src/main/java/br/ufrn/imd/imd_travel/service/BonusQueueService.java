package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.model.BonusOperation;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class BonusQueueService {

    private final BlockingQueue<BonusOperation> queue = new LinkedBlockingQueue<>();

    public void enqueueBonus(Long userId, int bonusValue) {
        queue.add(new BonusOperation(userId, bonusValue));
    }

    public BonusOperation dequeueBonus() {
        return queue.poll();
    }
}
