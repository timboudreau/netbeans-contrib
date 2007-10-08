/*
 * SetConsistencyValidator.java
 * 
 * Created on Oct 8, 2007, 10:52:54 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency.validator;

import org.netbeans.modules.bpel.model.api.Assign;
import org.netbeans.modules.bpel.model.api.BooleanExpr;
import org.netbeans.modules.bpel.model.api.Branches;
import org.netbeans.modules.bpel.model.api.Catch;
import org.netbeans.modules.bpel.model.api.CatchAll;
import org.netbeans.modules.bpel.model.api.Compensate;
import org.netbeans.modules.bpel.model.api.CompensateScope;
import org.netbeans.modules.bpel.model.api.CompensationHandler;
import org.netbeans.modules.bpel.model.api.CompletionCondition;
import org.netbeans.modules.bpel.model.api.Condition;
import org.netbeans.modules.bpel.model.api.Copy;
import org.netbeans.modules.bpel.model.api.Correlation;
import org.netbeans.modules.bpel.model.api.CorrelationContainer;
import org.netbeans.modules.bpel.model.api.CorrelationSet;
import org.netbeans.modules.bpel.model.api.CorrelationSetContainer;
import org.netbeans.modules.bpel.model.api.DeadlineExpression;
import org.netbeans.modules.bpel.model.api.Documentation;
import org.netbeans.modules.bpel.model.api.Else;
import org.netbeans.modules.bpel.model.api.ElseIf;
import org.netbeans.modules.bpel.model.api.Empty;
import org.netbeans.modules.bpel.model.api.EventHandlers;
import org.netbeans.modules.bpel.model.api.Exit;
import org.netbeans.modules.bpel.model.api.ExtensibleAssign;
import org.netbeans.modules.bpel.model.api.Extension;
import org.netbeans.modules.bpel.model.api.ExtensionActivity;
import org.netbeans.modules.bpel.model.api.ExtensionContainer;
import org.netbeans.modules.bpel.model.api.ExtensionEntity;
import org.netbeans.modules.bpel.model.api.FaultHandlers;
import org.netbeans.modules.bpel.model.api.FinalCounterValue;
import org.netbeans.modules.bpel.model.api.Flow;
import org.netbeans.modules.bpel.model.api.For;
import org.netbeans.modules.bpel.model.api.ForEach;
import org.netbeans.modules.bpel.model.api.From;
import org.netbeans.modules.bpel.model.api.FromPart;
import org.netbeans.modules.bpel.model.api.FromPartContainer;
import org.netbeans.modules.bpel.model.api.If;
import org.netbeans.modules.bpel.model.api.Import;
import org.netbeans.modules.bpel.model.api.Invoke;
import org.netbeans.modules.bpel.model.api.Link;
import org.netbeans.modules.bpel.model.api.LinkContainer;
import org.netbeans.modules.bpel.model.api.Literal;
import org.netbeans.modules.bpel.model.api.MessageExchange;
import org.netbeans.modules.bpel.model.api.MessageExchangeContainer;
import org.netbeans.modules.bpel.model.api.OnAlarmEvent;
import org.netbeans.modules.bpel.model.api.OnAlarmPick;
import org.netbeans.modules.bpel.model.api.OnEvent;
import org.netbeans.modules.bpel.model.api.OnMessage;
import org.netbeans.modules.bpel.model.api.PartnerLink;
import org.netbeans.modules.bpel.model.api.PartnerLinkContainer;
import org.netbeans.modules.bpel.model.api.PatternedCorrelation;
import org.netbeans.modules.bpel.model.api.PatternedCorrelationContainer;
import org.netbeans.modules.bpel.model.api.Pick;
import org.netbeans.modules.bpel.model.api.Process;
import org.netbeans.modules.bpel.model.api.Query;
import org.netbeans.modules.bpel.model.api.ReThrow;
import org.netbeans.modules.bpel.model.api.Receive;
import org.netbeans.modules.bpel.model.api.RepeatEvery;
import org.netbeans.modules.bpel.model.api.RepeatUntil;
import org.netbeans.modules.bpel.model.api.Reply;
import org.netbeans.modules.bpel.model.api.Scope;
import org.netbeans.modules.bpel.model.api.Sequence;
import org.netbeans.modules.bpel.model.api.ServiceRef;
import org.netbeans.modules.bpel.model.api.Source;
import org.netbeans.modules.bpel.model.api.SourceContainer;
import org.netbeans.modules.bpel.model.api.StartCounterValue;
import org.netbeans.modules.bpel.model.api.Target;
import org.netbeans.modules.bpel.model.api.TargetContainer;
import org.netbeans.modules.bpel.model.api.TerminationHandler;
import org.netbeans.modules.bpel.model.api.Throw;
import org.netbeans.modules.bpel.model.api.To;
import org.netbeans.modules.bpel.model.api.ToPart;
import org.netbeans.modules.bpel.model.api.ToPartContainer;
import org.netbeans.modules.bpel.model.api.Validate;
import org.netbeans.modules.bpel.model.api.Variable;
import org.netbeans.modules.bpel.model.api.VariableContainer;
import org.netbeans.modules.bpel.model.api.Wait;
import org.netbeans.modules.bpel.model.api.While;
import org.netbeans.modules.bpel.model.api.support.BpelModelVisitor;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

/**
 *
 * @author radval
 */
public class SetConsistencyValidatorVisitor implements BpelModelVisitor {

    public void visit(Process process) {
        OutputWriter writer =  IOProvider.getDefault().getStdOut();
        writer.println("In SetConsistencyValidatorVisitor : process is: "+ process.getName());
    }

    public void visit(Empty empty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Invoke invoke) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Receive receive) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Reply reply) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Assign assign) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Wait wait) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Throw throv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Exit terminate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Flow flow) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(While whil) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Sequence sequence) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Pick pick) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Scope scope) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(PartnerLinkContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(PartnerLink link) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(FaultHandlers handlers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Catch catc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(EventHandlers handlers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(OnMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CompensationHandler handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(VariableContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Variable variable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CorrelationSetContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CorrelationSet set) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Source source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Target target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CorrelationContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Correlation correlation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(PatternedCorrelation correlation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(PatternedCorrelationContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(To to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(From from) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Compensate compensate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(LinkContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Link link) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Copy copy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CatchAll holder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(BooleanExpr expr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Branches branches) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CompletionCondition condition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Condition condition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(DeadlineExpression expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Documentation documentation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Else els) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ElseIf elseIf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ExtensibleAssign assign) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ExtensionActivity activity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Validate validate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ToPart toPart) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ToPartContainer toPartContainer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(TerminationHandler handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(TargetContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(StartCounterValue value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(SourceContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ReThrow rethrow) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(RepeatUntil repeatUntil) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(RepeatEvery repeatEvery) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(OnEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(OnAlarmPick alarmPick) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(OnAlarmEvent alarmEvent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ExtensionContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Extension extension) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(FinalCounterValue value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ForEach forEach) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Literal literal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Import imp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(If iff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(FromPart fromPart) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(FromPartContainer fromPartContainer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(For fo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(MessageExchangeContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(MessageExchange exchange) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ServiceRef ref) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(ExtensionEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(CompensateScope compensateScope) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void visit(Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
