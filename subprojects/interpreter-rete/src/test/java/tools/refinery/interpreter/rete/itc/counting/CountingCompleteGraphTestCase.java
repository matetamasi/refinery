/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package tools.refinery.interpreter.rete.itc.counting;

import org.junit.Test;
import tools.refinery.interpreter.rete.itc.alg.counting.CountingAlg;
import tools.refinery.interpreter.rete.itc.alg.counting.CountingTcRelation;
import tools.refinery.interpreter.rete.itc.graphimpl.Graph;

import static org.junit.Assert.assertEquals;

public class CountingCompleteGraphTestCase {

    @Test
    public void testResult() {
        int nodeCount = 10;
        Graph<Integer> g = new Graph<Integer>();
        CountingAlg<Integer> ca = new CountingAlg<Integer>(g);

        for (int i = 0;i<nodeCount;i++) {
            g.insertNode(i);
        }

        //inserting edges
        for (int i = 0;i<nodeCount;i++) {
            for (int j = 0;j<nodeCount;j++) {
                if (i < j) {
                    g.insertEdge(i, j);
                    assertEquals(CountingTcRelation.createFrom(g), ca.getTcRelation());
                }
            }
        }

        for (int i = 0;i<nodeCount;i++) {
            for (int j = 0;j<nodeCount;j++) {
                if (i < j) {
                    g.deleteEdgeIfExists(i, j);
                    assertEquals(CountingTcRelation.createFrom(g), ca.getTcRelation());
                }
            }
        }
    }

}
