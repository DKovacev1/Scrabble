package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Gene;
import hr.java.scrabble.states.TileStateBase;

public class GeneImpl implements Gene<TileStateBase> {

    private TileStateBase gene;

    @Override
    public TileStateBase getGene() {
        return gene;
    }

    @Override
    public void setGene(TileStateBase gene) {
        this.gene = gene;
    }

}
