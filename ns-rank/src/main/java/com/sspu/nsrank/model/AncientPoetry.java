package com.sspu.nsrank.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AncientPoetry implements Serializable {
    private String id;
    private String title;
    private String dynasty;
    private String writer;
    private String type;
    private String content;
    private String remark;
    private String translation;
    private String shangxi;
    private String audioUrl;
}
