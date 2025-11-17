package br.ufrn.imd.imd_travel.model;

public class BonusOperation {

    private final Long userId;
    private final int bonusValue;

    public BonusOperation(Long userId, int bonusValue) {
        this.userId = userId;
        this.bonusValue = bonusValue;
    }

    public Long getUserId() {
        return userId;
    }

    public int getBonusValue() {
        return bonusValue;
    }
}

