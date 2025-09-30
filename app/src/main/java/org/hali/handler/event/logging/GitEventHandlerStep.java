package org.hali.handler.event.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.hali.logging.StepStatus;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum GitEventHandlerStep implements StepStatus {

    CREATE_TEMP_DIR_SUCCESS(step("create_temp_dir", "success")),
    CREATE_TEMP_DIR_ERROR(step("create_temp_dir", "error")),

    CLONE_REPOSITORY_SUCCESS(step("clone_repository", "success")),
    CLONE_REPOSITORY_ERROR(step("clone_repository", "error")),

    PARSE_PIPELINE_SUCCESS(step("parse_pipeline", "success")),
    PARSE_PIPELINE_ERROR(step("parse_pipeline", "error")),

    PIPELINE_STEPS_NOT_FOUND_ERROR(step("pipeline_steps_not_found", "error")),

    RUN_PIPELINE_SUCCESS(step("run_pipeline", "success")),
    RUN_PIPELINE_ERROR(step("run_pipeline", "error")),

    WEBHOOK_RESPONSE_SUCCESS(step("webhook_response", "success")),
    WEBHOOK_RESPONSE_ERROR(step("webhook_response", "error")),

    REMOVE_TEMP_DIR_SUCCESS(step("remove_temp_dir", "success")),
    REMOVE_TEMP_DIR_ERROR(step("remove_temp_dir", "error")),

    UNKNOWN_ERROR(step("unknown", "error"));

    private final String step;
    private final String status;

    private static String[] step(String step, String status) {
        return new String[]{step, status};
    }

    GitEventHandlerStep(String[] values) {
        this.step = values[0];
        this.status = values[1];
    }
}

