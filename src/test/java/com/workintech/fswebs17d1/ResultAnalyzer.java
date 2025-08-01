package com.workintech.fswebs17d1;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ResultAnalyzer implements TestWatcher, AfterAllCallback {
    private List<TestResultStatus> testResultsStatus = new ArrayList<>();
    private static final String taskId = "154";

    private enum TestResultStatus {
        SUCCESSFUL, ABORTED, FAILED, DISABLED;
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        testResultsStatus.add(TestResultStatus.DISABLED);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        testResultsStatus.add(TestResultStatus.SUCCESSFUL);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        testResultsStatus.add(TestResultStatus.ABORTED);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        testResultsStatus.add(TestResultStatus.FAILED);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        double success  = testResultsStatus.stream()
                .filter((data) -> data.name().equals("SUCCESSFUL")).collect(Collectors.toList()).size();

        double failure = testResultsStatus.stream()
                .filter((data) -> data.name().equals("FAILED")).collect(Collectors.toList()).size();

        double score = (double) success / (success + failure);
        String userId = "303854";

        JSONObject json = new JSONObject();
        json.put("score", score);
        json.put("taskId", taskId);
        json.put("userId", userId);
        sendTestResult(json.toString());
    }

    private void sendTestResult(String result) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost("https://coursey-gpt-backend.herokuapp.com/nextgen/taskLog/saveJavaTasks");
            StringEntity params = new StringEntity(result);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.close();
        }
    }


}
