package com.camunda.handles;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

public class SendAmostrasHandle implements JobHandler {
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        String to = asString(vars.get("emailTo"));
        String cc = asString(vars.get("emailCc"));       // ex: "a@x.com,b@y.com,c@z.com"
        String bcc = asString(vars.get("emailBcc"));     // opcional
        String subject = asString(vars.get("emailSubject"));
        String body = asString(vars.get("emailBody"));
        String fromVar = asString(vars.get("emailFrom"));
    }
}
