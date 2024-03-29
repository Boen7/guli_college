package com.boen.guli.pojo.subject;

import lombok.Data;

import java.util.List;

@Data
public class OneSubject {

    private String id;

    private String title;

    private List<TwoSubject> children;
}
