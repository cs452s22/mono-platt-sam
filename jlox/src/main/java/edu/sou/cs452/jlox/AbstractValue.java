package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.HashMap;

public class AbstractValue {

    public final static AbstractValue plus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;
        // left +
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, POSITIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);
    
        // left -
        left = new HashMap<>();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, NEGATIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);
    
        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(ZERO, left);
    
        // left Bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, BOTTOM);
        lookup.put(BOTTOM, left);
    
        // left Top
        left = new HashMap<>();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, TOP);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(TOP, left);
    
        return lookup.get(leftValue).get(rightValue);
    }
}