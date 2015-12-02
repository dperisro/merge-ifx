package com.bs.ifx.merge.entities;

import com.bs.ifx.merge.conf.MergeConfig;

import java.io.File;
import java.io.FilenameFilter;

/**
 * . Returns the files that satisfy the specified filter (XSD files)
 */
public class MergeFileFilter implements FilenameFilter {

    public boolean accept(final File dir, final String name) {
        if (name.lastIndexOf('.') > 0) {
            int lastIndex = name.lastIndexOf('.');
            String str = name.substring(lastIndex);
            if (str.equals(MergeConfig.EXT_XSD)) {
                return true;
            }
        }
        return false;
    }

}
