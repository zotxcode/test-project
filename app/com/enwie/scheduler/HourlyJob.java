package com.enwie.scheduler;

import com.enwie.service.InstagramService;

/**
 * Created by hendriksaragih on 7/2/17.
 */
public class HourlyJob extends BaseJob {

    public HourlyJob(String cron) {
        super(cron);
    }

    @Override
    public void doJob() {
        updateInstagramBanner();
    }

    private void updateInstagramBanner() {
        new InstagramService().syncBanner();
    }
}