package com.example.eximporter.importer.model.extended;

import javax.persistence.*;

@Entity
@Table(name = "EX_IMPORTER_PARKED_PEO")
public class ParkedPeo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long attempt;

    private Long idPeo;

    private String pageFpId;
    private String fileName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttempt() {
        return attempt;
    }

    public void setAttempt(Long attempt) {
        this.attempt = attempt;
    }

    public Long getIdPeo() {
        return idPeo;
    }

    public String getPageFpId() {
        return pageFpId;
    }

    public void setIdPeo(Long idPeo) {
        this.idPeo = idPeo;
    }

    public void setPageFpId(String pageFpId) {
        this.pageFpId = pageFpId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
