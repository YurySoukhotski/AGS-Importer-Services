package com.example.eximporter.importer.model.extended;

import javax.persistence.*;

@Entity
@Table(name = "EX_IMPORTER_JOB_PARAMS")
public class JobExecutionParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String importType;
    private String fileName;
    private Long countAttempts;
    private Long lastIndex;
    private String jobStatus;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getAttempt() {
        return countAttempts;
    }

    public void setAttempt(Long attempt) {
        this.countAttempts = attempt;
    }

    public Long getIndex() {
        return lastIndex;
    }

    public void setIndex(Long index) {
        this.lastIndex = index;
    }

    public String getStatus() {
        return jobStatus;
    }

    public void setStatus(String status) {
        this.jobStatus = status;
    }
}
