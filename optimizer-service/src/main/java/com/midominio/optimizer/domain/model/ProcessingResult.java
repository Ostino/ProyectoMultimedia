package com.midominio.optimizer.domain.model;

import java.util.Map;
import java.util.UUID;

public class ProcessingResult {

    private final UUID fileId;
    private final UUID ownerId;
    private final Map<String, String> derivatives;
    private final Map<String, Object> metadata;
    private final boolean success;
    private final String errorMessage;

    private ProcessingResult(UUID fileId, UUID ownerId, Map<String, String> derivatives,
                             Map<String, Object> metadata, boolean success, String errorMessage) {
        this.fileId       = fileId;
        this.ownerId      = ownerId;
        this.derivatives  = derivatives;
        this.metadata     = metadata;
        this.success      = success;
        this.errorMessage = errorMessage;
    }

    public static ProcessingResult success(UUID fileId, UUID ownerId,
                                           Map<String, String> derivatives,
                                           Map<String, Object> metadata) {
        return new ProcessingResult(fileId, ownerId, derivatives, metadata, true, null);
    }

    public static ProcessingResult failure(UUID fileId, UUID ownerId, String errorMessage) {
        return new ProcessingResult(fileId, ownerId, Map.of(), Map.of(), false, errorMessage);
    }

    public UUID getFileId()                    { return fileId; }
    public UUID getOwnerId()                   { return ownerId; }
    public Map<String, String> getDerivatives(){ return derivatives; }
    public Map<String, Object> getMetadata()   { return metadata; }
    public boolean isSuccess()                 { return success; }
    public String getErrorMessage()            { return errorMessage; }
}
