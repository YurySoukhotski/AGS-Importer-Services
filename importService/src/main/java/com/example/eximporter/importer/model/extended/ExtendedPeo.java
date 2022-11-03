package com.example.eximporter.importer.model.extended;

import com.example.eximporter.importer.model.api.Peo;

/**
 * Contains peo data  with linking data
 */
public class ExtendedPeo {
    private Peo peo;
    private String peoFpId;
    private String articleFpId;


    public ExtendedPeo(Peo peo, String peoFpId, String articleFpId) {
        this.peo = peo;
        this.peoFpId = peoFpId;
        this.articleFpId = articleFpId;
    }

    /**
     * Getter for property 'peo'.
     *
     * @return Value for property 'peo'.
     */
    public Peo getPeo() {
        return peo;
    }

    /**
     * Setter for property 'peo'.
     *
     * @param peo Value to set for property 'peo'.
     */
    public void setPeo(Peo peo) {
        this.peo = peo;
    }

    /**
     * Getter for property 'peoFpId'.
     *
     * @return Value for property 'peoFpId'.
     */
    public String getPeoFpId() {
        return peoFpId;
    }

    public String getArticleFpId() {
        return articleFpId;
    }
}
