package pfe.terrain.factory.extern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.NotExecutableContract;
import pfe.terrain.gen.algo.exception.NotParsableContractException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AlgoDataFetcher {
    private String requestMethod = "GET";
    private String algoId;



    public AlgoDataFetcher(String algoId) {
        this.algoId = algoId;
    }

    public Contract getContract() throws CannotReachRepoException, NotParsableContractException {
        String data = this.getData(this.algoId);

        Gson gson = new Gson();

        JsonElement element = gson.toJsonTree(gson.fromJson(data, List.class), List.class);

        if(!element.isJsonArray() && element.getAsJsonArray().size() != 1){
            throw new NotParsableContractException("data is not an array or not the right size");
        }

        Contract contract = Contract.fromJson(element.getAsJsonArray().get(0).toString());

        return contract;
    }

    private String getData(String id) throws CannotReachRepoException {
        try {
            URL url = new URL(ArtifactoryAlgoLister.artifUrl + "algo-data/" + id + ".json");
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
}
