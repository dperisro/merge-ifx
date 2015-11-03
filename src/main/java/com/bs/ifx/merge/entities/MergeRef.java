package com.bs.ifx.merge.entities;

public class MergeRef {

    private boolean ref;
    private String name;

    public MergeRef(final String nameV) {
        this.ref = true;
        this.name = nameV;
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" ref: " + isRef() + newLine);
        result.append(" name: " + getName() + newLine);
        result.append("}");
        return result.toString();
    }

}
