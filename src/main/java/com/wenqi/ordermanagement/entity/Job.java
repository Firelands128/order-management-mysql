/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.entity;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.constants.JobType;

import javax.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long jobId;

    @Column(name = "job_type")
    private JobType jobType;

    @Column(name = "customer_order_id")
    private long customerOrderId;

    @Column(name = "status")
    private JobStatus jobStatus;

    public Job() {

    }

    public Job(JobType jobType, long customerOrderId) {
        this.jobType = jobType;
        this.customerOrderId = customerOrderId;
        this.jobStatus = JobStatus.AWAITING_PROCESS;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public long getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", jobType=" + jobType +
                ", customerOrderId=" + customerOrderId +
                ", jobStatus=" + jobStatus +
                '}';
    }
}
