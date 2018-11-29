package pfe.terrain.factory.extern;

import com.google.gson.Gson;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.NoArtifStringException;
import pfe.terrain.factory.holder.Algorithm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArtifactoryAlgoLister {
    private String artifUrl = "http://146.148.16.106/artifactory/";
    private String path = "api/search/gavc?a=algo.*&repos=pfe";
    private String requestMethod = "GET";
    private String resultKey = "results";
    private String uriKey = "uri";
    private String filterExtension = ".jar";
    private String artifactPattern = "(algo\\.[^\\.]*\\.[^\\/]*\\/)";

    private Logger logger = Logger.getLogger("AlgoLister");

    public ArtifactoryAlgoLister() {
    }

    public ArtifactoryAlgoLister(String url){
        this.artifUrl = url;
    }

    public List<Algorithm> getAlgo() throws CannotReachRepoException, IOException{

        String list = this.getList();

        Gson gson = new Gson();

        List<Map<String,String>> uriList = (List<Map<String,String>>)gson.fromJson(list, Map.class).get(this.resultKey);

        Set<Algorithm> algorithms = new HashSet<>();

        for(Map map : uriList){
            String uri = String.valueOf(map.get(this.uriKey));
            if(uri.endsWith(this.filterExtension)) {
                try {
                    algorithms.add(new Algorithm(this.getArtifactId(uri)));
                } catch (NoArtifStringException e){
                    logger.log(Level.WARNING,"could not find id in : " + uri);
                }
            }
        }



        return new ArrayList<>(algorithms);
    }


    private String getList() throws CannotReachRepoException {
        try {
            URL url = new URL(this.artifUrl + this.path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(this.requestMethod);

            if (con.getResponseCode() != 200) {
                throw new CannotReachRepoException();
            }

            Scanner scanner = new Scanner(con.getInputStream());

            StringBuilder builder = new StringBuilder();

            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            return builder.toString();
        } catch (Exception e){
            throw new CannotReachRepoException();
        }
    }

    public String getArtifactId(String uri) throws NoArtifStringException {
        Pattern pattern = Pattern.compile(this.artifactPattern);
        Matcher matcher = pattern.matcher(uri);

        if(matcher.find()){
            String id = matcher.group();

            return id.substring(0,id.length()-1);
        }

        throw new NoArtifStringException();


    }
}
