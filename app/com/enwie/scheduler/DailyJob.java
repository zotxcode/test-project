package com.enwie.scheduler;

import models.*;

/**
 * Created by hendriksaragih on 7/2/17.
 */
public class DailyJob extends BaseJob {

    public DailyJob(String cron) {
        super(cron);
    }

    @Override
    public void doJob() {
        applyPromotion();
    }

    private void applyPromotion() {
        PromotionProduct.updatePromotion(getBrandId());
    }
}