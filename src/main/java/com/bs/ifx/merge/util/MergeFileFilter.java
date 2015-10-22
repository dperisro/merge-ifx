package com.bs.ifx.merge.util;

import java.io.File;
import java.io.FilenameFilter;

public class MergeFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
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
