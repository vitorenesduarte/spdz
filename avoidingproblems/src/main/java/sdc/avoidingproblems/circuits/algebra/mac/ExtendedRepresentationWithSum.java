/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdc.avoidingproblems.circuits.algebra.mac;

import java.util.Map;
import sdc.avoidingproblems.circuits.algebra.FieldElement;

/**
 *
 * @author Vitor Enes
 */
public class ExtendedRepresentationWithSum extends ExtendedRepresentation {

    private final FieldElement sum;

    public ExtendedRepresentationWithSum(FieldElement beta, FieldElement value, FieldElement sum, Map<Integer, FieldElement> playersMACShares) {
        super(beta, value, playersMACShares);
        this.sum = sum;
    }

    public FieldElement getSum() {
        return sum;
    }
}
