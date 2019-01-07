/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.deamon_thread;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.entity.Job;
import com.wenqi.ordermanagement.repository.JobRepository;
import com.wenqi.ordermanagement.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class JobThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PurchaseOrderService purchaseOrderService;
    private final JobRepository jobRepository;

    @Autowired
    public JobThread(PurchaseOrderService purchaseOrderService,
                     JobRepository jobRepository) {
        this.purchaseOrderService = purchaseOrderService;
        this.jobRepository = jobRepository;
    }

    @Override
    public void run() {
        Job job;

        //noinspection InfiniteLoopStatement
        while (true) {
            job = jobRepository.findTop1ByJobStatus(JobStatus.PENDING);
            if (job != null) {
                doJob(job);
                continue;
            }
            job = jobRepository.findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
            if (job != null) {
                doJob(job);
                continue;
            }
            try {
//                	logger.info("no job need to process.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doJob(Job job) {
        logger.info("processing job: " + job.getJobId());
        switch (job.getJobType()) {
            case CREATE_PURCHASE_ORDER:
                job.setJobStatus(JobStatus.PENDING);
                jobRepository.save(job);

                purchaseOrderService.createPurchaseOrderFromCustomerOrderId(job.getCustomerOrderId());

                job.setJobStatus(JobStatus.DONE);
                jobRepository.save(job);
                break;
        }
    }
}
