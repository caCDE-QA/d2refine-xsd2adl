package edu.mayo.transforms.xsd2adl;

import edu.mayo.samepage.adl.impl.adl.rm.ADLRM;
import edu.mayo.transforms.auxiliary.TransformConstants;
import edu.mayo.transforms.auxiliary.TransformException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by dks02 on 11/16/15.
 */
public class Transform2ADLMain
{
    public static void main(String[] args)
    {
        String schema = null;
        String outputDir = null;

        ADLRM rmKind_ = null;

        try
        {
            if (args.length < 1)
                throw new TransformException("Please provide XML Schema..");

            schema = args[0];

            if ((args.length < 2)||(StringUtils.isEmpty(args[1])))
                outputDir = TransformConstants.getDefaultOutputDirectory();
            else
                outputDir = args[1];

            File fDir = new File(outputDir);
            fDir.mkdir();

            FileUtils.cleanDirectory(fDir);

            if ((args.length < 3)||(StringUtils.isEmpty(args[2])))
                rmKind_ = TransformConstants.defaultRM;
            else
                rmKind_ = TransformConstants.getRM(args[2]);
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
            return;
        }

        XSD2ADL xsd2ADL = new XSD2ADL();
        xsd2ADL.transform(schema, outputDir, rmKind_);

        System.out.println("XSD2ADL Transform Done! Results are at " + outputDir);
    }
}
