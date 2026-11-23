package com.midominio.optimizer.domain.port.out;

import com.midominio.optimizer.domain.model.MediaFile;
import com.midominio.optimizer.domain.model.ProcessingResult;

import java.util.List;

/**
 * Sugiere etiquetas para un archivo ya procesado (por ejemplo, con un modelo de IA).
 *
 * <p>Las implementaciones pueden lanzar cualquier {@link RuntimeException} si el
 * proveedor no está disponible (sin API key, cuota agotada, timeout, error 5xx...).
 * Quien la usa debe tratar las etiquetas como opcionales.
 */
public interface AutoTagger {

    List<String> suggestTags(MediaFile file, ProcessingResult result);
}
