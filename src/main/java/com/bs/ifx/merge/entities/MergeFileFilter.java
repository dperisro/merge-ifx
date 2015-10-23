package com.bs.ifx.merge.entities;

import java.io.File;
import java.io.FilenameFilter;

public class MergeFileFilter implements FilenameFilter {

    @Override
    public boolean accept(final File dir, final String name) {
        if (name.lastIndexOf('.') > 0) {
            int lastIndex = name.lastIndexOf('.');
            String str = name.substring(lastIndex);
            if (str.equals(".xsd")) {
                return true;
            }
        }
        return false;
    }

}
