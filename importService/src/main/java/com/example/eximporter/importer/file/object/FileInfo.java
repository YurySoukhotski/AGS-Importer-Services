package com.example.eximporter.importer.file.object;

import com.example.eximporter.importer.file.ImportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class FileInfo {
    private Path path;
    private ImportType importType;
    private Long order;
    private Logger logger = LoggerFactory.getLogger(FileInfo.class);


    public FileInfo(Path path, ImportType importType) {
        this.path = path;
        this.importType = importType;
        this.order = extractOrder(path.toString());
    }

    public Path getPath() {
        return path;
    }

    public ImportType getImportType() {
        return importType;
    }

    public Long getOrder() {
        return order;
    }

    private Long extractOrder(String fileName) {
        try {
            int end = fileName.lastIndexOf('.');
            String tempIndex = fileName.substring(0, end);
            end = tempIndex.lastIndexOf('_');
            return Long.valueOf(tempIndex.substring(end + 1).replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            logger.error("Wrong index of file: {}", fileName);
            return 0L;
        }

    }
}
