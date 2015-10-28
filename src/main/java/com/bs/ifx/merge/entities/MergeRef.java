package com.bs.ifx.merge.entities;

public class MergeRef {

    private boolean ref;
    private boolean base;
    private String name;

    public MergeRef(final String nameV, final boolean baseV) {
        this.ref = true;
        this.name = nameV;
        this.base = baseV;
    }

    public MergeRef(final String nameV) {
        this.ref = true;
        this.name = nameV;
        this.base = false;
    }

    public MergeRef() {
        this.ref = false;
        this.base = false;
        this.name = null;
    }

    public boolean isRef() {
        return ref;
    }

    public boolean isBase() {
        return base;
    }

    public String getName() {
        return name;
    }

}
