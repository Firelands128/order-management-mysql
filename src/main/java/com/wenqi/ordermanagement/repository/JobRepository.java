/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.repository;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.entity.Job;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JobRepository extends PagingAndSortingRepository<Job, Long> {

    Job findTop1ByJobStatus(JobStatus jobStatus);

}
