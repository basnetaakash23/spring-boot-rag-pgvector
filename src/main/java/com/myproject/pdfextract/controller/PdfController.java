package com.myproject.pdfextract.controller;

import com.myproject.pdfextract.service.PdfExtract;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    
    private final PdfExtract pdfExtract;

    public PdfController(PdfExtract pdfExtract) {
        this.pdfExtract = pdfExtract;
    }
    
    @GetMapping
    public ResponseEntity<List<Document>> getTextDocuments(){
        return ResponseEntity.status(HttpStatus.OK).body(pdfExtract.processPdf());
    }

    @GetMapping("/html")
    public ResponseEntity<String> getHtmlDocuments(){
        return ResponseEntity.status(HttpStatus.OK).body(pdfExtract.pdfToHtml());
    }

    @GetMapping("/rag")
    public void getRagDocuments(){
        pdfExtract.processForRagPipeline();
    }
    
    
}
