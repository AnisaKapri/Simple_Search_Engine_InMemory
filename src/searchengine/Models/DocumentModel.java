package searchengine.Models;

import java.util.List;

public class DocumentModel {

    private int docID;
    private List<String> documentTokens;

    public DocumentModel(int docID, List<String> documentTokens){
        this.docID = docID;
        this.documentTokens = documentTokens;
    }



    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public List<String> getDocumentTokens() {
        return documentTokens;
    }

    public void setDocumentTokens(List<String> documentTokens) {
        this.documentTokens = documentTokens;
    }

}
