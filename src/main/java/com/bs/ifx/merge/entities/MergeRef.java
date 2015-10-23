package com.bs.ifx.merge.entities;

public class MergeRef {

    private boolean ref;
    private String name;

    public MergeRef(String name) {
        this.ref = true;
        this.name = name;
    }

    public MergeRef() {
        this.ref = false;
        this.name = null;
    }

    public boolean isRef() {
        return ref;
    }

    public String getName() {
        return name;
    }

}
