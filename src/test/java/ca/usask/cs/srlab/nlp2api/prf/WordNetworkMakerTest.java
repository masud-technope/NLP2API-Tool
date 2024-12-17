package ca.usask.cs.srlab.nlp2api.prf;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class WordNetworkMakerTest {

    ArrayList<String> sentences;

    @BeforeEach
    public void setup() {
        sentences = new ArrayList<>();
        sentences.add("What tables are using in the Oracle Netsuite WBS?");
        sentences.add("How to track open database connections with entity framework 8?");
        sentences.add("How can I click on DONE button?");
        sentences.add("How can I change the color of the svg?");
    }

    @Test
    public void testCreateNetwork() {
        WordNetworkMaker wordNetMaker = new WordNetworkMaker(this.sentences);
        DirectedGraph<String, DefaultEdge> graph = wordNetMaker.createWordNetwork();
        System.out.print(graph.vertexSet());
        for(DefaultEdge edge: graph.edgeSet()){
            System.out.println(graph.getEdgeSource(edge)+"---"+graph.getEdgeTarget(edge));
        }
    }
}
