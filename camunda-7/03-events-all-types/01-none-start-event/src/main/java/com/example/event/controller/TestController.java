package com.example.event.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for testing operations.
 * Provides endpoints to test various process scenarios.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final RuntimeService runtimeService;

    public TestController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    /**
     * Runs a specific test scenario.
     *
     * @param scenarioName Name of the test scenario
     * @return Test execution status
     */
    @PostMapping("/scenario/{scenarioName}")
    public Map<String, Object> runTestScenario(@PathVariable String scenarioName) {
        Map<String, Object> variables = switch (scenarioName) {
            case "happy-path" -> Map.of(
                "testScenario", "happy-path",
                "expectedResult", "success",
                "testData", "valid-data"
            );
            case "error-handling" -> Map.of(
                "testScenario", "error-handling",
                "simulateError", true,
                "errorType", "validation-error"
            );
            case "timeout" -> Map.of(
                "testScenario", "timeout",
                "delaySeconds", 30,
                "expectedTimeout", true
            );
            default -> Map.of("testScenario", "unknown");
        };

        var instance = runtimeService.startProcessInstanceByKey(
            "none-start-event-process",
            "TEST-" + scenarioName + "-" + System.currentTimeMillis(),
            variables
        );

        return Map.of(
            "testScenario", scenarioName,
            "processInstanceId", instance.getId(),
            "variables", variables,
            "status", "Test scenario started"
        );
    }

    /**
     * Runs load test by starting multiple process instances.
     *
     * @param instances Number of instances to start
     * @return Load test results
     */
    @PostMapping("/load-test")
    public Map<String, Object> runLoadTest(@RequestParam(defaultValue = "10") int instances) {
        List<String> processIds = new ArrayList<>();
        
        for (int i = 0; i < instances; i++) {
            Map<String, Object> variables = Map.of(
                "testType", "load-test",
                "instanceNumber", i + 1,
                "timestamp", System.currentTimeMillis()
            );

            var instance = runtimeService.startProcessInstanceByKey(
                "none-start-event-process",
                "LOAD-TEST-" + i,
                variables
            );
            processIds.add(instance.getId());
        }

        return Map.of(
            "totalInstances", instances,
            "processInstanceIds", processIds,
            "status", "Load test completed"
        );
    }
}
