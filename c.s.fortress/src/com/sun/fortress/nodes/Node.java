package com.sun.fortress.nodes;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.fortress.nodes_util.*;
import com.sun.fortress.parser_util.*;
import com.sun.fortress.parser_util.precedence_opexpr.*;
import com.sun.fortress.useful.*;
import edu.rice.cs.plt.tuple.Option;

public interface Node extends HasAt {
    public Span getSpan();

    public <RetType> RetType accept(NodeVisitor<RetType> visitor);
    public void accept(NodeVisitor_void visitor);
    public void outputHelp(TabPrintWriter writer, boolean lossless);
    public abstract int generateHashCode();
}
