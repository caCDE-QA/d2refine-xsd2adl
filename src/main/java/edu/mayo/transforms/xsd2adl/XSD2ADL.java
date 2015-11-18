package edu.mayo.transforms.xsd2adl;

import ch.qos.logback.classic.Logger;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.parser.SchemaDocument;
import com.sun.xml.internal.xsom.parser.XSOMParser;
import edu.mayo.samepage.adl.impl.adl.rm.ADLRM;
import edu.mayo.transforms.auxiliary.TransformUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.lexemantix.utils.file.LMFileUtils;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by dks02 on 11/16/15.
 */
public class XSD2ADL
{
    final static Logger logger_ = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(XSD2ADL.class);

    public String transform(String xsdSchema, String outputDir, ADLRM rmKind)
    {
        File adlFile = TransformUtils.getADLFile(xsdSchema, outputDir);
        String name = FilenameUtils.removeExtension(adlFile.getName());

        if (!toProcess(adlFile, xsdSchema, outputDir))
            return null;

        String text = (new Date()).toString() + "\n" + xsdSchema + " TEST ADL ";
        // Uncomment the line below and comment line above before releasing
        // String text = "";

        store(adlFile,  text);

        try
        {
            XSOMParser xsomParser = new XSOMParser();
            xsomParser.parse(xsdSchema);

            XSSchemaSet schemaSet = xsomParser.getResult();

            for(SchemaDocument document : xsomParser.getDocuments())
                logger_.info("Document:" + document.getSystemId());

            for (XSSchema schema : schemaSet.getSchemas())
            {
                String schemaId = schema.getLocator().getSystemId();

                if (StringUtils.isEmpty(schemaId))
                    continue;

                if (schemaId.toLowerCase().indexOf("tcga") == -1)
                    continue;

                if (schemaId.equals(xsdSchema))
                    continue;

                logger_.info("Processing " + schemaId);

                transform(schemaId, outputDir, rmKind);
            }

            XSD2ADLHelper helper_ = new XSD2ADLHelper();
            text = helper_.convertToADL(name, xsdSchema, schemaSet, rmKind);
        }
        catch (SAXException e)
        {
            e.printStackTrace();
            return null;
        }

        store(adlFile, text);
        return text;
    }

    private boolean toProcess(File adlFile, String xsdSchema, String outputDir)
    {
        if (adlFile == null)
        {
            logger_.error("Failed to get an ADL File from " + xsdSchema + " for the output directory " + outputDir);
            return false;
        }

        if (adlFile.exists())
        {
            logger_.debug("Schema '" + xsdSchema + "' already processed. Skipping...");
            return false;
        }

        return true;
    }

    private void store(File adl, String text)
    {
        try
        {
            if (!adl.exists())
                adl.createNewFile();

            LMFileUtils.setContents(adl, text, false);
        }
        catch (IOException e)
        {
            logger_.error("Failed to create ADL File " + adl);
            e.printStackTrace();
        }
    }
}
