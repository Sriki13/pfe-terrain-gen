package pfe.terrain.factory.extern;

import com.google.gson.Gson;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.NoArtifStringException;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.utils.Fetcher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArtifactoryAlgoLister implements Fetcher<List<String>> {
    public final static String artifUrl = "http://35.189.252.97/artifactory/";
    private String path = "api/search/artifact?repos=pfe&name=algo";
    private String requestMethod = "GET";
    private String resultKey = "results";
    private String uriKey = "uri";
    private String filterExtension = ".jar";
    private String artifactPattern = "(algo\\.[^\\.]*\\.[^\\/]*\\/)";
    private String url;

    private Logger logger = Logger.getLogger("AlgoLister");

    public ArtifactoryAlgoLister() {
        this.url = artifUrl;
    }

    public ArtifactoryAlgoLister(String url){
        this.url= url;
    }

    public List<String> getAlgo() throws CannotReachRepoException, IOException{

        String list = this.getList();

        Gson gson = new Gson();

        List<Map<String,String>> uriList = (List<Map<String,String>>)gson.fromJson(list, Map.class).get(this.resultKey);

        Set<String> algorithms = new HashSet<>();

        for(Map map : uriList){
            String uri = String.valueOf(map.get(this.uriKey));
            if(uri.endsWith(this.filterExtension)) {
                try {
                    algorithms.add(this.getArtifactId(uri));
                } catch (NoArtifStringException e){
                    logger.log(Level.WARNING,"could not find id in : " + uri);
                }
            }
        }
        return new ArrayList<>(algorithms);
    }


    private String getList() throws CannotReachRepoException {
        try {
            URL url = new URL(this.url + this.path);
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

    @Override
    public List<String> fetch() throws Exception {
        return this.getAlgo();
    }
}
