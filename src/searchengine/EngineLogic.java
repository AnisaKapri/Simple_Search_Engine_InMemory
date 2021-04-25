package searchengine;

import java.io.*;
import java.util.*;

import searchengine.Models.DocumentModel;
import searchengine.Models.ErrorModel;

public class EngineLogic {
    private Map<Integer, DocumentModel> documentsIndexed;
    private Map<String, List<DocumentModel>> wordtoDocuments;
    private String[] userCommand;


    public void EngineStart() {
        documentsIndexed = new HashMap<>();
        wordtoDocuments = new HashMap<>();
        System.out.println("Pershendetje, ky eshte nje search engine In Memory. \n Listat e komandave qe mund te ekzekutoni jane si me poshte: \n 1- index docID token...tokenN \n 2-query expression");
        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String tempComand = reader.readLine();  //si tip scanner
                userCommand = tempComand.split(" ");
                ErrorModel response = checkCommand(userCommand);
                if (response.errorCode == 500) {
                    System.out.println("index error " + response.errorDescription);
                } else {
                    switch (userCommand[0]) {
                        case "index":
                            List<String> documentTokens = Arrays.asList(userCommand);
                            documentTokens = documentTokens.subList(2, documentTokens.size());
                            DocumentModel doc = new DocumentModel(Integer.parseInt(userCommand[1]), documentTokens);
                            insertDocument(doc);
                            break;
                        case "query":
                            Query(tempComand);
                            break;
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    public ErrorModel checkCommand(String[] command) {
        ErrorModel response = new ErrorModel(500, "Komanda nuk mund te jete bosh, dhe duhet te filloj me index ose query.");
        if (command.length != 0 && (!command[0].equalsIgnoreCase("index") || !command[0].equalsIgnoreCase("query"))) {
            try {
                if (command[0].equalsIgnoreCase("index")) {
                    Integer.parseInt(command[1]);
                    response = new ErrorModel(200, "Sukses");
                } else {
                    response = new ErrorModel(200, "Sukses");
                }
            } catch (NumberFormatException e) {
                response = new ErrorModel(500, "Dokument ID duhet te jete objekt i tipit Integer");
            }
        }
        return response;
    }

    public void insertDocument(DocumentModel document) {
        if (documentsIndexed.containsKey(document.getDocID())) {
            for (String token : documentsIndexed.get(document.getDocID()).getDocumentTokens()) {
                if (!document.getDocumentTokens().contains(token)) {
                    wordtoDocuments.get(token).remove(documentsIndexed.get(document.getDocID()));
                    if (wordtoDocuments.get(token).size() == 0) {
                        wordtoDocuments.remove(token);
                    }
                }
            }
            for (String token : document.getDocumentTokens()) {
                if (!wordtoDocuments.containsKey(token)) {
                    List<DocumentModel> newDocCollection = new ArrayList<>();
                    newDocCollection.add(document);
                    wordtoDocuments.put(token, newDocCollection);
                } else {
                    List<DocumentModel> tempList = wordtoDocuments.get(token);
                    if (tempList.contains(documentsIndexed.get(document.getDocID()))) {
                        tempList.remove(document);
                        wordtoDocuments.replace(token, tempList);
                    }
                }
            }
            DocumentModel tempDocModel = documentsIndexed.get(document.getDocID());
            tempDocModel.setDocumentTokens(document.getDocumentTokens());
            documentsIndexed.replace(document.getDocID(), tempDocModel);
        } else {
            documentsIndexed.put(document.getDocID(), document);
            for (String token : document.getDocumentTokens()) {
                if (wordtoDocuments.containsKey(token)) {
                    wordtoDocuments.get(token).add(document);
                } else {
                    List<DocumentModel> newDocCollection = new ArrayList<>();
                    newDocCollection.add(document);
                    wordtoDocuments.put(token, newDocCollection);
                }
            }
        }
    }

    public void Query(String query) {
        if (query.startsWith("query")) {
            String listOfProducts = queryParser(query.substring(5));
            String[] toSearch = listOfProducts.split(" ");
            String idDocs = "";
            for (String prod : toSearch) {
                if (wordtoDocuments.containsKey(prod)) {
                    List<DocumentModel> docList = wordtoDocuments.get(prod);
                    for (DocumentModel documentModel : docList) {
                        idDocs = idDocs + " " + documentModel.getDocID();
                    }
                }
            }
            System.out.println("query " + idDocs);
        } else {
            System.out.println("Komanda nuk eshte e sakte");
        }

    }

    public String queryParser(String query) {
        String sureProducts = "";
        for (String prod : query.split("&")) {
            if (!prod.contains("(") && !prod.contains(")") && !prod.contains("|")) {
                sureProducts = sureProducts + " ";
            }
        }
        return sureProducts;
    }

}
