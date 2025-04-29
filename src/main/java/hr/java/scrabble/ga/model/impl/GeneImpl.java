package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Gene;
import hr.java.scrabble.states.TileStateBase;
import lombok.Getter;
import lombok.ToString;

@ToString
public class GeneImpl implements Gene<TileStateBase> {

    private final TileStateBase gene;
    @Getter
    private final TileStateBase initialTile;

    public GeneImpl(TileStateBase gene, TileStateBase initialTile) {
        this.gene = gene;
        this.initialTile = initialTile;
    }

    @Override
    public TileStateBase getGene() {
        return gene;
    }

    @Override
    public TileStateBase getCopy() {
        return gene.getCopy();
    }

}
