package edu.mayo.transforms.auxiliary;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * Created by dks02 on 11/16/15.
 */
public class TransformUtils
{
    public static URL string2URL(String string, String name) throws TransformException
    {
        URL url = null;

        try
        {
            if (string == null)
                throw new TransformException("Error: Please provide a" + name + " URL");

            url = new URL(string);

            if (url == null)
                throw new TransformException("Error: Failed to create" + name + " URL for " + string);
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            throw new TransformException("Failed to parse/convert the " + name + " at " + name);
        }

        return url;
    }

    public static String getFileNameFromURL(String url)
    {
        if (StringUtils.isEmpty(url))
            return null;

        return FilenameUtils.getName(url);
    }

    public static File getADLFile(String schema, String outputDir)
    {
        String outputFileName = TransformUtils.getFileNameFromURL(schema);
        outputFileName = FilenameUtils.removeExtension(outputFileName);
        outputFileName = outputFileName.replaceAll("\\.", "_");
        outputFileName = outputFileName + ".adls";

        outputFileName = StringUtils.isEmpty(outputDir)? outputFileName : outputDir + File.separator + outputFileName;

        return new File(outputFileName);
    }
}
