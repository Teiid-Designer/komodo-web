package org.komodo.web.backend.server.services.util;

import org.komodo.core.KomodoLexicon;
import org.komodo.spi.KException;
import org.komodo.spi.repository.Descriptor;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.KomodoType;
import org.komodo.spi.repository.Property;
import org.komodo.spi.repository.Repository.UnitOfWork;
import org.komodo.web.share.CoreConstants;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon.CreateProcedure;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon.CreateTable;
import org.modeshape.sequencer.teiid.lexicon.CoreLexicon;
import org.modeshape.sequencer.teiid.lexicon.CoreLexicon.ModelType;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;

/**
 * Relational Identifier Utilities
 */
public class RelationalIdentifier implements CoreConstants.Images {

	/**
	 * @param ko the komodo object
	 * @param uow transaction (can be null)
	 * @return the type of the komodo object
	 */
	public static KomodoType getType(KomodoObject ko, UnitOfWork uow) {
	    try {
	        UnitOfWork transaction = uow;

	        if (uow == null) {
	            transaction = ko.getRepository().createTransaction("relational-indentifier-gettype", false, null); //$NON-NLS-1$
	        }

	        KomodoType relationalType = ko.getTypeIdentifier(transaction);
	        if (relationalType != KomodoType.UNKNOWN)
	            return relationalType;

			// check primary type
			Descriptor pType = ko.getPrimaryType(transaction);
            if (pType != null) {
                if (pType.getName().equalsIgnoreCase(VdbLexicon.Vdb.VIRTUAL_DATABASE)) {
                    return KomodoType.VDB;
                }
                if (pType.getName().equalsIgnoreCase(KomodoLexicon.VdbModel.NODE_TYPE)) {
                    return KomodoType.MODEL;
                }
                if (pType.getName().equalsIgnoreCase(VdbLexicon.Translator.TRANSLATOR)) {
                    return KomodoType.VDB_TRANSLATOR;
                }
                if (pType.getName().equalsIgnoreCase(VdbLexicon.Source.SOURCE)) {
                    return KomodoType.VDB_MODEL_SOURCE;
                }
                if (pType.getName().equalsIgnoreCase(VdbLexicon.ImportVdb.IMPORT_VDB)) {
                    return KomodoType.VDB_ENTRY;
                }
                if (pType.getName().equalsIgnoreCase(VdbLexicon.ImportVdb.IMPORT_VDB)) {
                    return KomodoType.VDB_IMPORT;
                }
                if (pType.getName().equalsIgnoreCase(VdbLexicon.DataRole.DATA_ROLE)) {
                    return KomodoType.VDB_DATA_ROLE;
                }
                if (pType.getName().equalsIgnoreCase(VdbLexicon.DataRole.Permission.PERMISSION)) {
                    return KomodoType.VDB_PERMISSION;
                }
            }

            Descriptor[] descriptors = ko.getDescriptors(transaction);
            if (descriptors.length > 0) {
                for (Descriptor des : descriptors) {
                    if (des.getName().equalsIgnoreCase(KomodoLexicon.Vdb.NODE_TYPE))
                        return KomodoType.VDB;
                    else if (des.getName().equalsIgnoreCase(KomodoLexicon.VdbModel.NODE_TYPE))
                        return KomodoType.MODEL;
                    else if (des.getName().equalsIgnoreCase(CreateTable.TABLE_STATEMENT))
                        return KomodoType.TABLE;
                    else if (des.getName().equalsIgnoreCase(CreateTable.VIEW_STATEMENT))
                        return KomodoType.VIEW;
                    else if (des.getName().equalsIgnoreCase(CreateProcedure.PROCEDURE_STATEMENT)) {
                        try {
                            Property type = (ko.getParent(transaction)).getProperty(transaction, CoreLexicon.JcrId.MODEL_TYPE);
                            if (type != null && type.getStringValue(transaction).equalsIgnoreCase(ModelType.VIRTUAL)) {
                                return KomodoType.VIRTUAL_PROCEDURE;
                            }
                        } catch (KException e) {
                            e.printStackTrace();
                        }
                        return KomodoType.STORED_PROCEDURE;
                    } else if (des.getName().equalsIgnoreCase(CreateProcedure.RESULT_SET))
                        return KomodoType.TABULAR_RESULT_SET;
                    else if (des.getName().equalsIgnoreCase(CreateTable.TABLE_ELEMENT))
                        return KomodoType.COLUMN;
                    else if (des.getName().equalsIgnoreCase(CreateProcedure.PARAMETER))
                        return KomodoType.PARAMETER;
                    else if (des.getName().equalsIgnoreCase(VdbLexicon.Translator.TRANSLATOR))
                        return KomodoType.VDB_TRANSLATOR;
                    else if (des.getName().equalsIgnoreCase(VdbLexicon.Source.SOURCE))
                        return KomodoType.VDB_MODEL_SOURCE;
                    else if (des.getName().equalsIgnoreCase(VdbLexicon.ImportVdb.IMPORT_VDB))
                        return KomodoType.VDB_ENTRY;
                    else if (des.getName().equalsIgnoreCase(VdbLexicon.ImportVdb.IMPORT_VDB))
                        return KomodoType.VDB_IMPORT;
                }
            }

            if (uow == null)
                transaction.commit();

        } catch (KException e) {
            e.printStackTrace();
        }

        return KomodoType.UNKNOWN;
	}

	/**
	 * @param ko the komodo object
	 * @param uow the transaction
	 *
	 * @return komodo object is virtual
	 * @throws Exception if error occurs
	 */
	public static boolean isVirtual(KomodoObject ko, UnitOfWork uow) throws Exception {
	    UnitOfWork transaction = uow;

        if (uow == null) {
            transaction = ko.getRepository().createTransaction("komodo-object-is-virtual", false, null); //$NON-NLS-1$
        }

        try {
            switch(ko.getTypeIdentifier(transaction)) {
                case MODEL: {
                    try {
                        Property type = (ko).getProperty(transaction, CoreLexicon.JcrId.MODEL_TYPE);
                        if( type != null && type.getStringValue(transaction).equalsIgnoreCase(ModelType.VIRTUAL)) {
                            return true;
                        }
                    } catch (KException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
                case TABLE:
                case STORED_PROCEDURE:
                case VIRTUAL_PROCEDURE:
                    try {
                        return isVirtual(ko.getParent(transaction), transaction);
                    } catch (KException e) {
                        e.printStackTrace();
                    }
                    return false;

                case VIEW:
                    return true;
                default:
                    return false;
            }
        } finally {
            if (uow == null)
                transaction.commit();
        }
	}
}
