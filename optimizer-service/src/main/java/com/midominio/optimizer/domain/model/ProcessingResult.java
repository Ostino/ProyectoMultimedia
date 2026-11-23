package com.midominio.optimizer.domain.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProcessingResult {

    private final UUID fileId;
    private final UUID ownerId;
    private final Map<String, String> derivatives;
    private final Map<String, Object> metadata;
    private final boolean success;
    private final String errorMessage;
    private final List<String> aiTags;

    private ProcessingResult(UUID fileId, UUID ownerId, Map<String, String> derivatives,
                             Map<String, Object> metadata, boolean success, String errorMessage,
                             List<String> aiTags) {
        this.fileId       = fileId;
        this.ownerId      = ownerId;
        this.derivatives  = derivatives;
        this.metadata     = metadata;
        this.success      = success;
        this.errorMessage = errorMessage;
        this.aiTags       = aiTags == null ? List.of() : List.copyOf(aiTags);
    }

    public static ProcessingResult success(UUID fileId, UUID ownerId,
                                           Map<String, String> derivatives,
                                           Map<String, Object> metadata) {
        return new ProcessingResult(fileId, ownerId, derivatives, metadata, true, null, List.of());
    }

    public static ProcessingResult failure(UUID fileId, UUID ownerId, String errorMessage) {
        return new ProcessingResult(fileId, ownerId, Map.of(), Map.of(), false, errorMessage, List.of());
    }

    /** Copia de este resultado con las etiquetas sugeridas por IA. */
    public ProcessingResult withAiTags(List<String> tags) {
        return new ProcessingResult(fileId, ownerId, derivatives, metadata, success, errorMessage, tags);
    }

    public UUID getFileId()                    { return fileId; }
    public UUID getOwnerId()                   { return ownerId; }
    public Map<String, String> getDerivatives(){ return derivatives; }
    public Map<String, Object> getMetadata()   { return metadata; }
    public boolean isSuccess()                 { return success; }
    public String getErrorMessage()            { return errorMessage; }
    public List<String> getAiTags()            { return aiTags; }
}
