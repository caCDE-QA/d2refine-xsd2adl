package edu.mayo.transforms.auxiliary;

import edu.mayo.samepage.adl.impl.adl.rm.ADLRM;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * Created by dks02 on 11/16/15.
 */
public class TransformConstants
{
    public static String outputDirectoryAtHome = "xsd2adl";
    public static ADLRM defaultRM = ADLRM.OPENCIMI;

    public static String getDefaultOutputDirectory()
    {
        return System.getProperty("user.home") + File.separator +
                TransformConstants.outputDirectoryAtHome;
    }

    public static ADLRM getRM(String rmName)
    {
        if (StringUtils.isEmpty(rmName))
                return TransformConstants.defaultRM;

        if (rmName.trim().equals(ADLRM.OPENEHR))
            return ADLRM.OPENEHR;

        if (rmName.trim().equals(ADLRM.OPENCIMI))
            return ADLRM.OPENCIMI;

        return TransformConstants.defaultRM;
    }
}
