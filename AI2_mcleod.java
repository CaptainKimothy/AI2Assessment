package ai2_mcleod;
import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.text.DecimalFormat;

/* *************************************************************************
 * Author: Kim McLeod
 * Date: 12.18.17
 * Description: Program reads text files, calculates tf score, and prints
 * results (target word, document, and tf score) to screen
 **************************************************************************/

/* *************************************************************************
Assumptions: 
 * Input will be two lines
    - First line will contain the file path pointing to
      the folder containing the documents
    - Second line will be a string containing all target words, 
      separated by spaces
 * Words are exact match only. 
    - Ex. If target word is 'whale': 'whale' will match
                                     'whales' will not match
*****************************************************************************/

/* *************************************************************************
Notes: 
 * Program will print results to screen
 * Program will only calculate TF score for target words for time and 
 * space efficiency
 * TF Score will be rounded to tenth decimal place
*****************************************************************************/



public class AI2_mcleod {

    public static List<List<TFScore>> parseFiles(File[] textFiles, List<String> targetWords) throws IOException{
        List<List<TFScore>> allScores = new ArrayList<List<TFScore>>();
        for (File file : textFiles) {
            if(file.isFile()) {
                BufferedReader inputStream = null;
                Document doc = new Document(file);
                try {
                    inputStream = new BufferedReader(new FileReader(file));
                    String line;
                    HashMap<String, Integer> wordCount = new HashMap<>();
                    while ((line = inputStream.readLine()) != null) {
                        String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                        for (String word : words){
                            doc.incrementTotalWords();
                            if(targetWords.contains(word)){
                                Integer count = wordCount.get(word);
                                wordCount.put(word, (count == null ? 1 : count + 1));
                            }
                            
                        }
                    }
                    doc.setMap(wordCount);
                    List<TFScore> docScores = doc.calculateTFScores();
                    allScores.add(docScores);
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
        return allScores;
    }
    
    
    public static HashMap<String, TFScore> findMaxTFScores(List<List<TFScore>> allScores){
        HashMap<String, Double> maxesMap = new HashMap<>();
        HashMap<String, TFScore> resultsMap = new HashMap<>();
        for (int i = 0; i < allScores.size(); i++){
            List<TFScore> tfScoresList = allScores.get(i);
            for (int j = 0; j < tfScoresList.size(); j++){
                TFScore currentDoc = tfScoresList.get(j);
                String word = currentDoc.getTargetWord();
                double score = currentDoc.getTFScore();
                if (maxesMap.containsKey(word)){
                    double oldScore = maxesMap.get(word);
                    if (score >= oldScore){
                        maxesMap.put(word, score);
                        resultsMap.put(word, currentDoc);
                    }
                } else {
                    maxesMap.put(word, score);
                    resultsMap.put(word, currentDoc);
                }
                
            }
        }
        return resultsMap;
    }
    
    
    public static void printResults(HashMap<String, TFScore> resultMap){
        for (Map.Entry<String, TFScore> entry : resultMap.entrySet()) {
            TFScore resultDoc = entry.getValue();
            System.out.println("Word: " + resultDoc.getTargetWord());
            System.out.println("Document: " + resultDoc.getDocument());
            System.out.println("TF Score: " + resultDoc.getTFScore());
            System.out.println();
        } 
    }
    
    
    public static void main(String[] args) throws IOException {
        
        Scanner in = new Scanner(System.in);
        String targetDir = in.nextLine();
        String targetWordsString = in.nextLine();
        List<String> targetWords = Arrays.asList(targetWordsString.split("\\s+"));

        File dir = new File(targetDir);
        File[] textFiles = dir.listFiles();

        List<List<TFScore>> allScores = parseFiles(textFiles, targetWords);
        HashMap<String, TFScore> resultMap = findMaxTFScores(allScores);
        printResults(resultMap);
 
    }
}

/* ******************************************************************
 * TFScore Class
 * Contains document, the target word, and the tf score
 *******************************************************************/

class TFScore {
    private final File document;
    private final String targetWord;
    private final double tfScore;
    
    public TFScore(File document, String targetWord, double tfScore) {
        this.document = document;
        this.targetWord = targetWord;
        this.tfScore = tfScore;
    }
    public double getTFScore(){
        return tfScore;
    }
    public String getTargetWord(){
        return targetWord;
    }
        public File getDocument(){
        return document;
    }
}


/* ******************************************************************
 * Document Class
 * Contains document, a word count map, and total word count
 *******************************************************************/

class Document {
    private final File document;
    private HashMap<String, Integer> wordCountMap;
    private double totalWords;
    
    public Document(File document) {
        this.document = document;
        this.wordCountMap = new HashMap<>();
        this.totalWords = 0.0;
    }

    public void incrementTotalWords() {
        totalWords++;
    }     
    public double getTotalWords(){
        return totalWords;
    }
    public void setMap(HashMap<String, Integer> wordCountMap){
        this.wordCountMap = wordCountMap;
    }
    public HashMap<String, Integer> getMap(){
        return wordCountMap;
    }
    public List<TFScore> calculateTFScores(){
        String targetWord;
        Integer count;
        double newTFScore;
        TFScore tfScore;
        List<TFScore> scoreList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            targetWord = entry.getKey();
            count = entry.getValue();
            newTFScore = count / totalWords;
            newTFScore = Math.round(newTFScore*10000000000.0)/10000000000.0;
            tfScore = new TFScore(document, targetWord, newTFScore);
            scoreList.add(tfScore);
        }
        
        return scoreList;
    }
}
