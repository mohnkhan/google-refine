package com.google.refine.tests.importers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.json.JSONObject;
import org.mockito.Mockito;

import com.google.refine.ProjectMetadata;
import com.google.refine.RefineServlet;
import com.google.refine.importers.ImportingParserBase;
import com.google.refine.importers.tree.ImportColumnGroup;
import com.google.refine.importers.tree.TreeImportingParserBase;
import com.google.refine.importers.tree.XmlImportUtilities;
import com.google.refine.importing.ImportingJob;
import com.google.refine.importing.ImportingManager;
import com.google.refine.model.Project;
import com.google.refine.tests.RefineServletStub;
import com.google.refine.tests.RefineTest;

abstract class ImporterTest extends RefineTest {
    //mock dependencies
    protected Project project;
    protected ProjectMetadata metadata;
    protected ImportingJob job;
    protected RefineServlet servlet;
    
    protected JSONObject options;
    
    public void SetUp(){
        //FIXME - should we try and use mock(Project.class); - seems unnecessary complexity

        servlet = new RefineServletStub();
        ImportingManager.initialize(servlet);
        project = new Project();
        metadata = new ProjectMetadata();
        job = ImportingManager.createJob();
        
        options = Mockito.mock(JSONObject.class);
    }
    
    public void TearDown(){
        project = null;
        metadata = null;
        
        ImportingManager.disposeJob(job.id);
        job = null;
        
        options = null;
    }
    
    protected void parseOneFile(ImportingParserBase parser, Reader reader) {
        parser.parseOneFile(
            project,
            metadata,
            job,
            "file-source",
            reader,
            -1,
            options,
            new ArrayList<Exception>()
        );
        project.update();
    }
    
    protected void parseOneFile(ImportingParserBase parser, InputStream inputStream) {
        parser.parseOneFile(
            project,
            metadata,
            job,
            "file-source",
            inputStream,
            -1,
            options,
            new ArrayList<Exception>()
        );
        project.update();
    }
    
    protected void parseOneFile(TreeImportingParserBase parser, Reader reader) {
        ImportColumnGroup rootColumnGroup = new ImportColumnGroup();
        parser.parseOneFile(
            project,
            metadata,
            job,
            "file-source",
            reader,
            rootColumnGroup,
            -1,
            options,
            new ArrayList<Exception>()
        );
        XmlImportUtilities.createColumnsFromImport(project, rootColumnGroup);
        project.columnModel.update();
    }
    
    protected void parseOneFile(TreeImportingParserBase parser, InputStream inputStream, JSONObject options) {
        ImportColumnGroup rootColumnGroup = new ImportColumnGroup();
        
        Reader reader = new InputStreamReader(inputStream);
        parser.parseOneFile(
            project,
            metadata,
            job,
            "file-source",
            reader,
            rootColumnGroup,
            -1,
            options,
            new ArrayList<Exception>()
        );
        XmlImportUtilities.createColumnsFromImport(project, rootColumnGroup);
        project.columnModel.update();
        try {
            reader.close();
        } catch (IOException e) {
            //ignore errors on close
        }
    }
}
