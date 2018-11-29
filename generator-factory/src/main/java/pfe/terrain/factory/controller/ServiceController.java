package pfe.terrain.factory.controller;

import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.holder.Algorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceController {


    ArtifactoryAlgoLister lister;

    public ServiceController() {
        lister = new ArtifactoryAlgoLister();
    }

    public List<Algorithm> getAlgoList() throws IOException, CannotReachRepoException {
        return this.lister.getAlgo();
    }
}
