package edu.mayo.transforms.xsd2adl;

import ch.qos.logback.classic.Logger;
import com.sun.xml.internal.xsom.*;
import edu.mayo.samepage.adl.impl.adl.ADLArchetype;
import edu.mayo.samepage.adl.impl.adl.ADLArchetypeHelper;
import edu.mayo.samepage.adl.impl.adl.ADLMetaData;
import edu.mayo.samepage.adl.impl.adl.env.IDType;
import edu.mayo.samepage.adl.impl.adl.rm.ADLRM;
import edu.mayo.transforms.auxiliary.TransformConstants;
import org.openehr.adl.rm.RmType;
import org.openehr.adl.rm.RmTypeAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by dks02 on 11/17/15.
 */
public class XSD2ADLHelper
{
    final static Logger logger_ = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(XSD2ADLHelper.class);

    public ADLMetaData metadata_ = null;
    public ADLArchetype archetype_ = null;
    public ADLArchetypeHelper archetypeHelper_ = new ADLArchetypeHelper();

    public ADLRM rmKind_ = TransformConstants.defaultRM;

    public static HashMap<String, ADLArchetype> registeredADLs = new HashMap<String, ADLArchetype>();

    public String convertToADL(String name, String schemaLocation, XSSchemaSet schemaSet, ADLRM rmKind)
    {
        if (schemaSet == null)
            return null;

        if (rmKind != null)
            this.rmKind_ = rmKind;

        this.metadata_ = new ADLMetaData(this.rmKind_);
        this.metadata_.setDefaultTerminologySetName("snomed-ct");

        this.archetype_ = new ADLArchetype(name, metadata_);

        String archetypeDescription = name + " has following xsd components:";

        archetypeDescription += "\n" + processModelGroups(schemaSet);
        archetypeDescription += "\n" + processElements(schemaSet);
        archetypeDescription += "\n" + processAttributeGroup(schemaSet);
        archetypeDescription += "\n" + processAttributes(schemaSet);
        archetypeDescription += "\n" + processNotationDeclarations(schemaSet);

        RmType ITEM_GROUP = metadata_.getRmType(getRMTypeName("ITEM_GROUP"));
        String topId = this.archetype_.addNewId(IDType.TERM, name, archetypeDescription);

        RmTypeAttribute item = metadata_.getRmAttribute(ITEM_GROUP.getRmType(), "item");
        MultiplicityInterval occurrence01 = this.archetypeHelper_.createMultiplicity(0, 1);
        CComplexObject topConstraint = this.archetypeHelper_.createComplexObjectConstraint(ITEM_GROUP, null, topId, null, null);

        this.archetype_.setDefinition(topConstraint);

        registeredADLs.put(schemaLocation, this.archetype_);

        return this.archetype_.serialize();
    }

    private String processModelGroups(XSSchemaSet schemaSet)
    {
        int i = 0;
        Iterator<XSModelGroupDecl> mgItr = schemaSet.iterateModelGroupDecls();
        while (mgItr.hasNext())
        {
            XSModelGroupDecl mg = mgItr.next();
            logger_.info("ModelGroup: " + mg.getName());
            i++;
        }

        String retStr = "ModelGroup Size: " + i;
        logger_.info(retStr);
        return retStr;
    }

    private String processElements(XSSchemaSet schemaSet)
    {
        int i = 0;
        Iterator<XSElementDecl> elemItr = schemaSet.iterateElementDecls();
        while (elemItr.hasNext())
        {
            XSElementDecl elem = elemItr.next();
            logger_.info("Element: " + elem.getName());
            i++;
        }

        String retStr = "Element Size: " + i;
        logger_.info(retStr);
        return retStr;
    }

    private String processAttributeGroup(XSSchemaSet schemaSet)
    {
        int i = 0;
        Iterator<XSAttGroupDecl> attgItr = schemaSet.iterateAttGroupDecls();
        while (attgItr.hasNext())
        {
            XSAttGroupDecl atg = attgItr.next();
            logger_.info("Attribute Group: " + atg.getName());
            i++;
        }

        String retStr = "Attribute Group Size: " + i;
        logger_.info(retStr);
        return retStr;
    }

    private String processAttributes(XSSchemaSet schemaSet)
    {
        int i = 0;
        Iterator<XSAttributeDecl> attItr = schemaSet.iterateAttributeDecls();
        while (attItr.hasNext())
        {
            XSAttributeDecl att = attItr.next();
            logger_.info("Attribute: " + att.getName());
            i++;
        }

        String retStr = "Attribute Size: " + i;
        logger_.info(retStr);
        return retStr;
    }

    private String processNotationDeclarations(XSSchemaSet schemaSet)
    {
        int i = 0;
        Iterator<XSNotation> notaItr = schemaSet.iterateNotations();
        while (notaItr.hasNext())
        {
            XSNotation att = notaItr.next();
            logger_.info("Notation: " + att.getName());
            i++;
        }

        String retStr = "Notation Size: " + i;
        logger_.info(retStr);
        return retStr;
    }

    private String getRMTypeName(String name)
    {
        if (this.rmKind_ == ADLRM.OPENEHR)
        {
            if ("ITEM_GROUP".equals(name))
                return "CLUSTER";

            if ("IDENTIFIER".equals(name))
                return "DV_IDENTIFIER";

            if ("COUNT".equals(name))
                return "DV_COUNT";

            if ("QUANTITY".equals(name))
                return "DV_QUANTITY";

            if ("PLAIN_TEXT".equals(name))
                return "DV_TEXT";

            if ("CODED_TEXT".equals(name))
                return "DV_CODED_TEXT";
        }

        return name;
    }

    private String getRMAttributeName(String name)
    {
        if (this.rmKind_ == ADLRM.OPENEHR)
        {
            if ("item".equals(name))
                return "items";
        }

        return name;
    }
}
