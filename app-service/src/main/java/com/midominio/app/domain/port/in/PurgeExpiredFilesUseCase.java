package com.midominio.app.domain.port.in;

import java.time.LocalDateTime;

/**
 * Borra los archivos subidos antes de {@code createdBefore}, junto con sus
 * datos en disco, metadatos, miniaturas y etiquetas.
 *
 * <p>Los archivos creados antes de {@code protectedBefore} se conservan siempre
 * (por ejemplo, los archivos de ejemplo de una demo). Si es {@code null},
 * no hay archivos protegidos.
 */
public interface PurgeExpiredFilesUseCase {

    /** @return la cantidad de archivos eliminados */
    int purge(LocalDateTime createdBefore, LocalDateTime protectedBefore);
}
