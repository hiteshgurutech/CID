package com.example.cidapp.model;

import java.io.Serializable;

/**
 * Created by Hitesh on 7/9/17.
 */

public class TransctionListModel implements Serializable {

    private String TranscationType;

    public String getTranscationType() {
        return TranscationType;
    }

    public void setTranscationType(String transcationType) {
        TranscationType = transcationType;
    }


}
