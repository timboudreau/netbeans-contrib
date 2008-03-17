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

/** An abstract implementation of a visitor over Node that does not return a value.
 ** This visitor implements the visitor interface with methods that 
 ** first call forCASEDoFirst(), second visit the children, and finally 
 ** call forCASEOnly().  (CASE is replaced by the case name.)
 ** By default, each of forCASEDoFirst and forCASEOnly delegates
 ** to a more general case.  At the top of this delegation tree are
 ** defaultDoFirst() and defaultCase(), respectively, which (unless
 ** overridden) are no-ops.
 **/
public class NodeDepthFirstVisitor_void extends NodeVisitorRunnable1 {
    /**
     * This method is run for all cases that are not handled elsewhere.
     * By default, it is a no-op; subclasses may override this behavior.
    **/
    public void defaultCase(Node that) {}

    /**
     * This method is run for all DoFirst cases that are not handled elsewhere.
     * By default, it is a no-op; subclasses may override this behavior.
    **/
    public void defaultDoFirst(Node that) {}

    /* Methods to handle a node before recursion. */
    public void forAbstractNodeDoFirst(AbstractNode that) {
        defaultDoFirst(that);
    }

    public void forCompilationUnitDoFirst(CompilationUnit that) {
        forAbstractNodeDoFirst(that);
    }

    public void forComponentDoFirst(Component that) {
        forCompilationUnitDoFirst(that);
    }

    public void forApiDoFirst(Api that) {
        forCompilationUnitDoFirst(that);
    }

    public void forImportDoFirst(Import that) {
        forAbstractNodeDoFirst(that);
    }

    public void forImportedNamesDoFirst(ImportedNames that) {
        forImportDoFirst(that);
    }

    public void forImportStarDoFirst(ImportStar that) {
        forImportedNamesDoFirst(that);
    }

    public void forImportNamesDoFirst(ImportNames that) {
        forImportedNamesDoFirst(that);
    }

    public void forImportApiDoFirst(ImportApi that) {
        forImportDoFirst(that);
    }

    public void forAliasedSimpleNameDoFirst(AliasedSimpleName that) {
        forAbstractNodeDoFirst(that);
    }

    public void forAliasedAPINameDoFirst(AliasedAPIName that) {
        forAbstractNodeDoFirst(that);
    }

    public void forExportDoFirst(Export that) {
        forAbstractNodeDoFirst(that);
    }

    public void forTraitObjectAbsDeclOrDeclDoFirst(TraitObjectAbsDeclOrDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forTraitAbsDeclOrDeclDoFirst(TraitAbsDeclOrDecl that) {
        forTraitObjectAbsDeclOrDeclDoFirst(that);
    }

    public void forAbsTraitDeclDoFirst(AbsTraitDecl that) {
        forTraitAbsDeclOrDeclDoFirst(that);
    }

    public void forTraitDeclDoFirst(TraitDecl that) {
        forTraitAbsDeclOrDeclDoFirst(that);
    }

    public void forObjectAbsDeclOrDeclDoFirst(ObjectAbsDeclOrDecl that) {
        forTraitObjectAbsDeclOrDeclDoFirst(that);
    }

    public void forAbsObjectDeclDoFirst(AbsObjectDecl that) {
        forObjectAbsDeclOrDeclDoFirst(that);
    }

    public void forObjectDeclDoFirst(ObjectDecl that) {
        forObjectAbsDeclOrDeclDoFirst(that);
    }

    public void forVarAbsDeclOrDeclDoFirst(VarAbsDeclOrDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forAbsVarDeclDoFirst(AbsVarDecl that) {
        forVarAbsDeclOrDeclDoFirst(that);
    }

    public void forVarDeclDoFirst(VarDecl that) {
        forVarAbsDeclOrDeclDoFirst(that);
    }

    public void forLValueDoFirst(LValue that) {
        forAbstractNodeDoFirst(that);
    }

    public void forLValueBindDoFirst(LValueBind that) {
        forLValueDoFirst(that);
    }

    public void forUnpastingDoFirst(Unpasting that) {
        forLValueDoFirst(that);
    }

    public void forUnpastingBindDoFirst(UnpastingBind that) {
        forUnpastingDoFirst(that);
    }

    public void forUnpastingSplitDoFirst(UnpastingSplit that) {
        forUnpastingDoFirst(that);
    }

    public void forFnAbsDeclOrDeclDoFirst(FnAbsDeclOrDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forAbsFnDeclDoFirst(AbsFnDecl that) {
        forFnAbsDeclOrDeclDoFirst(that);
    }

    public void forFnDeclDoFirst(FnDecl that) {
        forFnAbsDeclOrDeclDoFirst(that);
    }

    public void forFnDefDoFirst(FnDef that) {
        forFnDeclDoFirst(that);
    }

    public void forParamDoFirst(Param that) {
        forAbstractNodeDoFirst(that);
    }

    public void forNormalParamDoFirst(NormalParam that) {
        forParamDoFirst(that);
    }

    public void forVarargsParamDoFirst(VarargsParam that) {
        forParamDoFirst(that);
    }

    public void forDimUnitDeclDoFirst(DimUnitDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forDimDeclDoFirst(DimDecl that) {
        forDimUnitDeclDoFirst(that);
    }

    public void forUnitDeclDoFirst(UnitDecl that) {
        forDimUnitDeclDoFirst(that);
    }

    public void forTestDeclDoFirst(TestDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forPropertyDeclDoFirst(PropertyDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forExternalSyntaxAbsDeclOrDeclDoFirst(ExternalSyntaxAbsDeclOrDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forAbsExternalSyntaxDoFirst(AbsExternalSyntax that) {
        forExternalSyntaxAbsDeclOrDeclDoFirst(that);
    }

    public void forExternalSyntaxDoFirst(ExternalSyntax that) {
        forExternalSyntaxAbsDeclOrDeclDoFirst(that);
    }

    public void forGrammarDeclDoFirst(GrammarDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forGrammarDefDoFirst(GrammarDef that) {
        forGrammarDeclDoFirst(that);
    }

    public void forGrammarMemberDeclDoFirst(GrammarMemberDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forNonterminalDeclDoFirst(NonterminalDecl that) {
        forGrammarMemberDeclDoFirst(that);
    }

    public void forNonterminalDefDoFirst(NonterminalDef that) {
        forNonterminalDeclDoFirst(that);
    }

    public void forNonterminalExtensionDefDoFirst(NonterminalExtensionDef that) {
        forNonterminalDeclDoFirst(that);
    }

    public void forTerminalDeclDoFirst(TerminalDecl that) {
        forGrammarMemberDeclDoFirst(that);
    }

    public void for_TerminalDefDoFirst(_TerminalDef that) {
        forTerminalDeclDoFirst(that);
    }

    public void forSyntaxDeclDoFirst(SyntaxDecl that) {
        forAbstractNodeDoFirst(that);
    }

    public void forSyntaxDefDoFirst(SyntaxDef that) {
        forSyntaxDeclDoFirst(that);
    }

    public void forSyntaxSymbolDoFirst(SyntaxSymbol that) {
        forAbstractNodeDoFirst(that);
    }

    public void forPrefixedSymbolDoFirst(PrefixedSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forOptionalSymbolDoFirst(OptionalSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forRepeatSymbolDoFirst(RepeatSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forRepeatOneOrMoreSymbolDoFirst(RepeatOneOrMoreSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forNoWhitespaceSymbolDoFirst(NoWhitespaceSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forSpecialSymbolDoFirst(SpecialSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forWhitespaceSymbolDoFirst(WhitespaceSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forTabSymbolDoFirst(TabSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forFormfeedSymbolDoFirst(FormfeedSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forCarriageReturnSymbolDoFirst(CarriageReturnSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forBackspaceSymbolDoFirst(BackspaceSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forNewlineSymbolDoFirst(NewlineSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forBreaklineSymbolDoFirst(BreaklineSymbol that) {
        forSpecialSymbolDoFirst(that);
    }

    public void forItemSymbolDoFirst(ItemSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forNonterminalSymbolDoFirst(NonterminalSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forKeywordSymbolDoFirst(KeywordSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forTokenSymbolDoFirst(TokenSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forNotPredicateSymbolDoFirst(NotPredicateSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forAndPredicateSymbolDoFirst(AndPredicateSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forCharacterClassSymbolDoFirst(CharacterClassSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forCharacterSymbolDoFirst(CharacterSymbol that) {
        forSyntaxSymbolDoFirst(that);
    }

    public void forCharSymbolDoFirst(CharSymbol that) {
        forCharacterSymbolDoFirst(that);
    }

    public void forCharacterIntervalDoFirst(CharacterInterval that) {
        forCharacterSymbolDoFirst(that);
    }

    public void forExprDoFirst(Expr that) {
        forAbstractNodeDoFirst(that);
    }

    public void forTypeAnnotatedExprDoFirst(TypeAnnotatedExpr that) {
        forExprDoFirst(that);
    }

    public void forAsExprDoFirst(AsExpr that) {
        forTypeAnnotatedExprDoFirst(that);
    }

    public void forAsIfExprDoFirst(AsIfExpr that) {
        forTypeAnnotatedExprDoFirst(that);
    }

    public void forAssignmentDoFirst(Assignment that) {
        forExprDoFirst(that);
    }

    public void forDelimitedExprDoFirst(DelimitedExpr that) {
        forExprDoFirst(that);
    }

    public void forBlockDoFirst(Block that) {
        forDelimitedExprDoFirst(that);
    }

    public void forCaseExprDoFirst(CaseExpr that) {
        forDelimitedExprDoFirst(that);
    }

    public void forDoDoFirst(Do that) {
        forDelimitedExprDoFirst(that);
    }

    public void forForDoFirst(For that) {
        forDelimitedExprDoFirst(that);
    }

    public void forIfDoFirst(If that) {
        forDelimitedExprDoFirst(that);
    }

    public void forLabelDoFirst(Label that) {
        forDelimitedExprDoFirst(that);
    }

    public void forAbstractObjectExprDoFirst(AbstractObjectExpr that) {
        forDelimitedExprDoFirst(that);
    }

    public void forObjectExprDoFirst(ObjectExpr that) {
        forAbstractObjectExprDoFirst(that);
    }

    public void for_RewriteObjectExprDoFirst(_RewriteObjectExpr that) {
        forAbstractObjectExprDoFirst(that);
    }

    public void forTryDoFirst(Try that) {
        forDelimitedExprDoFirst(that);
    }

    public void forAbstractTupleExprDoFirst(AbstractTupleExpr that) {
        forDelimitedExprDoFirst(that);
    }

    public void forTupleExprDoFirst(TupleExpr that) {
        forAbstractTupleExprDoFirst(that);
    }

    public void forArgExprDoFirst(ArgExpr that) {
        forAbstractTupleExprDoFirst(that);
    }

    public void forTypecaseDoFirst(Typecase that) {
        forDelimitedExprDoFirst(that);
    }

    public void forWhileDoFirst(While that) {
        forDelimitedExprDoFirst(that);
    }

    public void forFlowExprDoFirst(FlowExpr that) {
        forExprDoFirst(that);
    }

    public void forBigOprAppDoFirst(BigOprApp that) {
        forFlowExprDoFirst(that);
    }

    public void forAccumulatorDoFirst(Accumulator that) {
        forBigOprAppDoFirst(that);
    }

    public void forArrayComprehensionDoFirst(ArrayComprehension that) {
        forBigOprAppDoFirst(that);
    }

    public void forAtomicExprDoFirst(AtomicExpr that) {
        forFlowExprDoFirst(that);
    }

    public void forExitDoFirst(Exit that) {
        forFlowExprDoFirst(that);
    }

    public void forSpawnDoFirst(Spawn that) {
        forFlowExprDoFirst(that);
    }

    public void forThrowDoFirst(Throw that) {
        forFlowExprDoFirst(that);
    }

    public void forTryAtomicExprDoFirst(TryAtomicExpr that) {
        forFlowExprDoFirst(that);
    }

    public void forFnExprDoFirst(FnExpr that) {
        forExprDoFirst(that);
    }

    public void forLetExprDoFirst(LetExpr that) {
        forExprDoFirst(that);
    }

    public void forLetFnDoFirst(LetFn that) {
        forLetExprDoFirst(that);
    }

    public void forLocalVarDeclDoFirst(LocalVarDecl that) {
        forLetExprDoFirst(that);
    }

    public void forGeneratedExprDoFirst(GeneratedExpr that) {
        forExprDoFirst(that);
    }

    public void forOpExprDoFirst(OpExpr that) {
        forExprDoFirst(that);
    }

    public void forSubscriptExprDoFirst(SubscriptExpr that) {
        forOpExprDoFirst(that);
    }

    public void forPrimaryDoFirst(Primary that) {
        forOpExprDoFirst(that);
    }

    public void forLiteralExprDoFirst(LiteralExpr that) {
        forPrimaryDoFirst(that);
    }

    public void forNumberLiteralExprDoFirst(NumberLiteralExpr that) {
        forLiteralExprDoFirst(that);
    }

    public void forFloatLiteralExprDoFirst(FloatLiteralExpr that) {
        forNumberLiteralExprDoFirst(that);
    }

    public void forIntLiteralExprDoFirst(IntLiteralExpr that) {
        forNumberLiteralExprDoFirst(that);
    }

    public void forCharLiteralExprDoFirst(CharLiteralExpr that) {
        forLiteralExprDoFirst(that);
    }

    public void forStringLiteralExprDoFirst(StringLiteralExpr that) {
        forLiteralExprDoFirst(that);
    }

    public void forVoidLiteralExprDoFirst(VoidLiteralExpr that) {
        forLiteralExprDoFirst(that);
    }

    public void forVarRefDoFirst(VarRef that) {
        forPrimaryDoFirst(that);
    }

    public void forAbstractFieldRefDoFirst(AbstractFieldRef that) {
        forPrimaryDoFirst(that);
    }

    public void forFieldRefDoFirst(FieldRef that) {
        forAbstractFieldRefDoFirst(that);
    }

    public void forFieldRefForSureDoFirst(FieldRefForSure that) {
        forAbstractFieldRefDoFirst(that);
    }

    public void for_RewriteFieldRefDoFirst(_RewriteFieldRef that) {
        forAbstractFieldRefDoFirst(that);
    }

    public void forFunctionalRefDoFirst(FunctionalRef that) {
        forPrimaryDoFirst(that);
    }

    public void forFnRefDoFirst(FnRef that) {
        forFunctionalRefDoFirst(that);
    }

    public void for_RewriteFnRefDoFirst(_RewriteFnRef that) {
        forFunctionalRefDoFirst(that);
    }

    public void forOpRefDoFirst(OpRef that) {
        forFunctionalRefDoFirst(that);
    }

    public void forAppExprDoFirst(AppExpr that) {
        forPrimaryDoFirst(that);
    }

    public void forJuxtDoFirst(Juxt that) {
        forAppExprDoFirst(that);
    }

    public void forLooseJuxtDoFirst(LooseJuxt that) {
        forJuxtDoFirst(that);
    }

    public void forTightJuxtDoFirst(TightJuxt that) {
        forJuxtDoFirst(that);
    }

    public void forOprExprDoFirst(OprExpr that) {
        forAppExprDoFirst(that);
    }

    public void forChainExprDoFirst(ChainExpr that) {
        forAppExprDoFirst(that);
    }

    public void forCoercionInvocationDoFirst(CoercionInvocation that) {
        forAppExprDoFirst(that);
    }

    public void forMethodInvocationDoFirst(MethodInvocation that) {
        forAppExprDoFirst(that);
    }

    public void forMathPrimaryDoFirst(MathPrimary that) {
        forPrimaryDoFirst(that);
    }

    public void forArrayExprDoFirst(ArrayExpr that) {
        forPrimaryDoFirst(that);
    }

    public void forArrayElementDoFirst(ArrayElement that) {
        forArrayExprDoFirst(that);
    }

    public void forArrayElementsDoFirst(ArrayElements that) {
        forArrayExprDoFirst(that);
    }

    public void forTypeDoFirst(Type that) {
        forAbstractNodeDoFirst(that);
    }

    public void forDimExprDoFirst(DimExpr that) {
        forTypeDoFirst(that);
    }

    public void forExponentTypeDoFirst(ExponentType that) {
        forDimExprDoFirst(that);
    }

    public void forBaseDimDoFirst(BaseDim that) {
        forDimExprDoFirst(that);
    }

    public void forDimRefDoFirst(DimRef that) {
        forDimExprDoFirst(that);
    }

    public void forProductDimDoFirst(ProductDim that) {
        forDimExprDoFirst(that);
    }

    public void forQuotientDimDoFirst(QuotientDim that) {
        forDimExprDoFirst(that);
    }

    public void forExponentDimDoFirst(ExponentDim that) {
        forDimExprDoFirst(that);
    }

    public void forOpDimDoFirst(OpDim that) {
        forDimExprDoFirst(that);
    }

    public void forAbstractArrowTypeDoFirst(AbstractArrowType that) {
        forTypeDoFirst(that);
    }

    public void forArrowTypeDoFirst(ArrowType that) {
        forAbstractArrowTypeDoFirst(that);
    }

    public void for_RewriteGenericArrowTypeDoFirst(_RewriteGenericArrowType that) {
        forAbstractArrowTypeDoFirst(that);
    }

    public void forNonArrowTypeDoFirst(NonArrowType that) {
        forTypeDoFirst(that);
    }

    public void forBottomTypeDoFirst(BottomType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forTraitTypeDoFirst(TraitType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forNamedTypeDoFirst(NamedType that) {
        forTraitTypeDoFirst(that);
    }

    public void forIdTypeDoFirst(IdType that) {
        forNamedTypeDoFirst(that);
    }

    public void forInstantiatedTypeDoFirst(InstantiatedType that) {
        forNamedTypeDoFirst(that);
    }

    public void forAbbreviatedTypeDoFirst(AbbreviatedType that) {
        forTraitTypeDoFirst(that);
    }

    public void forArrayTypeDoFirst(ArrayType that) {
        forAbbreviatedTypeDoFirst(that);
    }

    public void forMatrixTypeDoFirst(MatrixType that) {
        forAbbreviatedTypeDoFirst(that);
    }

    public void forAbstractTupleTypeDoFirst(AbstractTupleType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forTupleTypeDoFirst(TupleType that) {
        forAbstractTupleTypeDoFirst(that);
    }

    public void forArgTypeDoFirst(ArgType that) {
        forAbstractTupleTypeDoFirst(that);
    }

    public void forVoidTypeDoFirst(VoidType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forInferenceVarTypeDoFirst(InferenceVarType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forAndTypeDoFirst(AndType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forOrTypeDoFirst(OrType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forFixedPointTypeDoFirst(FixedPointType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forDimTypeDoFirst(DimType that) {
        forNonArrowTypeDoFirst(that);
    }

    public void forTaggedDimTypeDoFirst(TaggedDimType that) {
        forDimTypeDoFirst(that);
    }

    public void forTaggedUnitTypeDoFirst(TaggedUnitType that) {
        forDimTypeDoFirst(that);
    }

    public void forStaticArgDoFirst(StaticArg that) {
        forTypeDoFirst(that);
    }

    public void forIdArgDoFirst(IdArg that) {
        forStaticArgDoFirst(that);
    }

    public void forTypeArgDoFirst(TypeArg that) {
        forStaticArgDoFirst(that);
    }

    public void forIntArgDoFirst(IntArg that) {
        forStaticArgDoFirst(that);
    }

    public void forBoolArgDoFirst(BoolArg that) {
        forStaticArgDoFirst(that);
    }

    public void forOprArgDoFirst(OprArg that) {
        forStaticArgDoFirst(that);
    }

    public void forDimArgDoFirst(DimArg that) {
        forStaticArgDoFirst(that);
    }

    public void forUnitArgDoFirst(UnitArg that) {
        forStaticArgDoFirst(that);
    }

    public void forStaticExprDoFirst(StaticExpr that) {
        forAbstractNodeDoFirst(that);
    }

    public void forIntExprDoFirst(IntExpr that) {
        forStaticExprDoFirst(that);
    }

    public void forIntValDoFirst(IntVal that) {
        forIntExprDoFirst(that);
    }

    public void forNumberConstraintDoFirst(NumberConstraint that) {
        forIntValDoFirst(that);
    }

    public void forIntRefDoFirst(IntRef that) {
        forIntValDoFirst(that);
    }

    public void forIntOpExprDoFirst(IntOpExpr that) {
        forIntExprDoFirst(that);
    }

    public void forSumConstraintDoFirst(SumConstraint that) {
        forIntOpExprDoFirst(that);
    }

    public void forMinusConstraintDoFirst(MinusConstraint that) {
        forIntOpExprDoFirst(that);
    }

    public void forProductConstraintDoFirst(ProductConstraint that) {
        forIntOpExprDoFirst(that);
    }

    public void forExponentConstraintDoFirst(ExponentConstraint that) {
        forIntOpExprDoFirst(that);
    }

    public void forBoolExprDoFirst(BoolExpr that) {
        forStaticExprDoFirst(that);
    }

    public void forBoolValDoFirst(BoolVal that) {
        forBoolExprDoFirst(that);
    }

    public void forBoolConstantDoFirst(BoolConstant that) {
        forBoolValDoFirst(that);
    }

    public void forBoolRefDoFirst(BoolRef that) {
        forBoolValDoFirst(that);
    }

    public void forBoolConstraintDoFirst(BoolConstraint that) {
        forBoolExprDoFirst(that);
    }

    public void forNotConstraintDoFirst(NotConstraint that) {
        forBoolConstraintDoFirst(that);
    }

    public void forBinaryBoolConstraintDoFirst(BinaryBoolConstraint that) {
        forBoolConstraintDoFirst(that);
    }

    public void forOrConstraintDoFirst(OrConstraint that) {
        forBinaryBoolConstraintDoFirst(that);
    }

    public void forAndConstraintDoFirst(AndConstraint that) {
        forBinaryBoolConstraintDoFirst(that);
    }

    public void forImpliesConstraintDoFirst(ImpliesConstraint that) {
        forBinaryBoolConstraintDoFirst(that);
    }

    public void forBEConstraintDoFirst(BEConstraint that) {
        forBinaryBoolConstraintDoFirst(that);
    }

    public void forWhereClauseDoFirst(WhereClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forWhereBindingDoFirst(WhereBinding that) {
        forAbstractNodeDoFirst(that);
    }

    public void forWhereTypeDoFirst(WhereType that) {
        forWhereBindingDoFirst(that);
    }

    public void forWhereNatDoFirst(WhereNat that) {
        forWhereBindingDoFirst(that);
    }

    public void forWhereIntDoFirst(WhereInt that) {
        forWhereBindingDoFirst(that);
    }

    public void forWhereBoolDoFirst(WhereBool that) {
        forWhereBindingDoFirst(that);
    }

    public void forWhereUnitDoFirst(WhereUnit that) {
        forWhereBindingDoFirst(that);
    }

    public void forWhereConstraintDoFirst(WhereConstraint that) {
        forAbstractNodeDoFirst(that);
    }

    public void forWhereExtendsDoFirst(WhereExtends that) {
        forWhereConstraintDoFirst(that);
    }

    public void forTypeAliasDoFirst(TypeAlias that) {
        forWhereConstraintDoFirst(that);
    }

    public void forWhereCoercesDoFirst(WhereCoerces that) {
        forWhereConstraintDoFirst(that);
    }

    public void forWhereWidensDoFirst(WhereWidens that) {
        forWhereConstraintDoFirst(that);
    }

    public void forWhereWidensCoercesDoFirst(WhereWidensCoerces that) {
        forWhereConstraintDoFirst(that);
    }

    public void forWhereEqualsDoFirst(WhereEquals that) {
        forWhereConstraintDoFirst(that);
    }

    public void forUnitConstraintDoFirst(UnitConstraint that) {
        forWhereConstraintDoFirst(that);
    }

    public void forIntConstraintDoFirst(IntConstraint that) {
        forWhereConstraintDoFirst(that);
    }

    public void forLEConstraintDoFirst(LEConstraint that) {
        forIntConstraintDoFirst(that);
    }

    public void forLTConstraintDoFirst(LTConstraint that) {
        forIntConstraintDoFirst(that);
    }

    public void forGEConstraintDoFirst(GEConstraint that) {
        forIntConstraintDoFirst(that);
    }

    public void forGTConstraintDoFirst(GTConstraint that) {
        forIntConstraintDoFirst(that);
    }

    public void forIEConstraintDoFirst(IEConstraint that) {
        forIntConstraintDoFirst(that);
    }

    public void forBoolConstraintExprDoFirst(BoolConstraintExpr that) {
        forWhereConstraintDoFirst(that);
    }

    public void forContractDoFirst(Contract that) {
        forAbstractNodeDoFirst(that);
    }

    public void forEnsuresClauseDoFirst(EnsuresClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forModifierDoFirst(Modifier that) {
        forAbstractNodeDoFirst(that);
    }

    public void forModifierAbstractDoFirst(ModifierAbstract that) {
        forModifierDoFirst(that);
    }

    public void forModifierAtomicDoFirst(ModifierAtomic that) {
        forModifierDoFirst(that);
    }

    public void forModifierGetterDoFirst(ModifierGetter that) {
        forModifierDoFirst(that);
    }

    public void forModifierHiddenDoFirst(ModifierHidden that) {
        forModifierDoFirst(that);
    }

    public void forModifierIODoFirst(ModifierIO that) {
        forModifierDoFirst(that);
    }

    public void forModifierOverrideDoFirst(ModifierOverride that) {
        forModifierDoFirst(that);
    }

    public void forModifierPrivateDoFirst(ModifierPrivate that) {
        forModifierDoFirst(that);
    }

    public void forModifierSettableDoFirst(ModifierSettable that) {
        forModifierDoFirst(that);
    }

    public void forModifierSetterDoFirst(ModifierSetter that) {
        forModifierDoFirst(that);
    }

    public void forModifierTestDoFirst(ModifierTest that) {
        forModifierDoFirst(that);
    }

    public void forModifierTransientDoFirst(ModifierTransient that) {
        forModifierDoFirst(that);
    }

    public void forModifierValueDoFirst(ModifierValue that) {
        forModifierDoFirst(that);
    }

    public void forModifierVarDoFirst(ModifierVar that) {
        forModifierDoFirst(that);
    }

    public void forModifierWidensDoFirst(ModifierWidens that) {
        forModifierDoFirst(that);
    }

    public void forModifierWrappedDoFirst(ModifierWrapped that) {
        forModifierDoFirst(that);
    }

    public void forStaticParamDoFirst(StaticParam that) {
        forAbstractNodeDoFirst(that);
    }

    public void forOperatorParamDoFirst(OperatorParam that) {
        forStaticParamDoFirst(that);
    }

    public void forIdStaticParamDoFirst(IdStaticParam that) {
        forStaticParamDoFirst(that);
    }

    public void forBoolParamDoFirst(BoolParam that) {
        forIdStaticParamDoFirst(that);
    }

    public void forDimensionParamDoFirst(DimensionParam that) {
        forIdStaticParamDoFirst(that);
    }

    public void forIntParamDoFirst(IntParam that) {
        forIdStaticParamDoFirst(that);
    }

    public void forNatParamDoFirst(NatParam that) {
        forIdStaticParamDoFirst(that);
    }

    public void forSimpleTypeParamDoFirst(SimpleTypeParam that) {
        forIdStaticParamDoFirst(that);
    }

    public void forUnitParamDoFirst(UnitParam that) {
        forIdStaticParamDoFirst(that);
    }

    public void forNameDoFirst(Name that) {
        forAbstractNodeDoFirst(that);
    }

    public void forAPINameDoFirst(APIName that) {
        forNameDoFirst(that);
    }

    public void forQualifiedNameDoFirst(QualifiedName that) {
        forNameDoFirst(that);
    }

    public void forQualifiedIdNameDoFirst(QualifiedIdName that) {
        forQualifiedNameDoFirst(that);
    }

    public void forQualifiedOpNameDoFirst(QualifiedOpName that) {
        forQualifiedNameDoFirst(that);
    }

    public void forSimpleNameDoFirst(SimpleName that) {
        forNameDoFirst(that);
    }

    public void forIdDoFirst(Id that) {
        forSimpleNameDoFirst(that);
    }

    public void forOpNameDoFirst(OpName that) {
        forSimpleNameDoFirst(that);
    }

    public void forOpDoFirst(Op that) {
        forOpNameDoFirst(that);
    }

    public void forEnclosingDoFirst(Enclosing that) {
        forOpNameDoFirst(that);
    }

    public void forAnonymousFnNameDoFirst(AnonymousFnName that) {
        forSimpleNameDoFirst(that);
    }

    public void forConstructorFnNameDoFirst(ConstructorFnName that) {
        forSimpleNameDoFirst(that);
    }

    public void forArrayComprehensionClauseDoFirst(ArrayComprehensionClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forKeywordExprDoFirst(KeywordExpr that) {
        forAbstractNodeDoFirst(that);
    }

    public void forCaseClauseDoFirst(CaseClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forCatchDoFirst(Catch that) {
        forAbstractNodeDoFirst(that);
    }

    public void forCatchClauseDoFirst(CatchClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forDoFrontDoFirst(DoFront that) {
        forAbstractNodeDoFirst(that);
    }

    public void forIfClauseDoFirst(IfClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forTypecaseClauseDoFirst(TypecaseClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forExtentRangeDoFirst(ExtentRange that) {
        forAbstractNodeDoFirst(that);
    }

    public void forGeneratorClauseDoFirst(GeneratorClause that) {
        forAbstractNodeDoFirst(that);
    }

    public void forVarargsExprDoFirst(VarargsExpr that) {
        forAbstractNodeDoFirst(that);
    }

    public void forVarargsTypeDoFirst(VarargsType that) {
        forAbstractNodeDoFirst(that);
    }

    public void forKeywordTypeDoFirst(KeywordType that) {
        forAbstractNodeDoFirst(that);
    }

    public void forTraitTypeWhereDoFirst(TraitTypeWhere that) {
        forAbstractNodeDoFirst(that);
    }

    public void forIndicesDoFirst(Indices that) {
        forAbstractNodeDoFirst(that);
    }

    public void forMathItemDoFirst(MathItem that) {
        forAbstractNodeDoFirst(that);
    }

    public void forExprMIDoFirst(ExprMI that) {
        forMathItemDoFirst(that);
    }

    public void forParenthesisDelimitedMIDoFirst(ParenthesisDelimitedMI that) {
        forExprMIDoFirst(that);
    }

    public void forNonParenthesisDelimitedMIDoFirst(NonParenthesisDelimitedMI that) {
        forExprMIDoFirst(that);
    }

    public void forNonExprMIDoFirst(NonExprMI that) {
        forMathItemDoFirst(that);
    }

    public void forExponentiationMIDoFirst(ExponentiationMI that) {
        forNonExprMIDoFirst(that);
    }

    public void forSubscriptingMIDoFirst(SubscriptingMI that) {
        forNonExprMIDoFirst(that);
    }

    public void forFixityDoFirst(Fixity that) {
        forAbstractNodeDoFirst(that);
    }

    public void forInFixityDoFirst(InFixity that) {
        forFixityDoFirst(that);
    }

    public void forPreFixityDoFirst(PreFixity that) {
        forFixityDoFirst(that);
    }

    public void forPostFixityDoFirst(PostFixity that) {
        forFixityDoFirst(that);
    }

    public void forNoFixityDoFirst(NoFixity that) {
        forFixityDoFirst(that);
    }

    public void forMultiFixityDoFirst(MultiFixity that) {
        forFixityDoFirst(that);
    }

    public void forEnclosingFixityDoFirst(EnclosingFixity that) {
        forFixityDoFirst(that);
    }

    public void forBigFixityDoFirst(BigFixity that) {
        forFixityDoFirst(that);
    }

    /* Methods to handle a node after recursion. */
    public void forAbstractNodeOnly(AbstractNode that) {
        defaultCase(that);
    }

    public void forCompilationUnitOnly(CompilationUnit that) {
        forAbstractNodeOnly(that);
    }

    public void forComponentOnly(Component that) {
        forCompilationUnitOnly(that);
    }

    public void forApiOnly(Api that) {
        forCompilationUnitOnly(that);
    }

    public void forImportOnly(Import that) {
        forAbstractNodeOnly(that);
    }

    public void forImportedNamesOnly(ImportedNames that) {
        forImportOnly(that);
    }

    public void forImportStarOnly(ImportStar that) {
        forImportedNamesOnly(that);
    }

    public void forImportNamesOnly(ImportNames that) {
        forImportedNamesOnly(that);
    }

    public void forImportApiOnly(ImportApi that) {
        forImportOnly(that);
    }

    public void forAliasedSimpleNameOnly(AliasedSimpleName that) {
        forAbstractNodeOnly(that);
    }

    public void forAliasedAPINameOnly(AliasedAPIName that) {
        forAbstractNodeOnly(that);
    }

    public void forExportOnly(Export that) {
        forAbstractNodeOnly(that);
    }

    public void forTraitObjectAbsDeclOrDeclOnly(TraitObjectAbsDeclOrDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forTraitAbsDeclOrDeclOnly(TraitAbsDeclOrDecl that) {
        forTraitObjectAbsDeclOrDeclOnly(that);
    }

    public void forAbsTraitDeclOnly(AbsTraitDecl that) {
        forTraitAbsDeclOrDeclOnly(that);
    }

    public void forTraitDeclOnly(TraitDecl that) {
        forTraitAbsDeclOrDeclOnly(that);
    }

    public void forObjectAbsDeclOrDeclOnly(ObjectAbsDeclOrDecl that) {
        forTraitObjectAbsDeclOrDeclOnly(that);
    }

    public void forAbsObjectDeclOnly(AbsObjectDecl that) {
        forObjectAbsDeclOrDeclOnly(that);
    }

    public void forObjectDeclOnly(ObjectDecl that) {
        forObjectAbsDeclOrDeclOnly(that);
    }

    public void forVarAbsDeclOrDeclOnly(VarAbsDeclOrDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forAbsVarDeclOnly(AbsVarDecl that) {
        forVarAbsDeclOrDeclOnly(that);
    }

    public void forVarDeclOnly(VarDecl that) {
        forVarAbsDeclOrDeclOnly(that);
    }

    public void forLValueOnly(LValue that) {
        forAbstractNodeOnly(that);
    }

    public void forLValueBindOnly(LValueBind that) {
        forLValueOnly(that);
    }

    public void forUnpastingOnly(Unpasting that) {
        forLValueOnly(that);
    }

    public void forUnpastingBindOnly(UnpastingBind that) {
        forUnpastingOnly(that);
    }

    public void forUnpastingSplitOnly(UnpastingSplit that) {
        forUnpastingOnly(that);
    }

    public void forFnAbsDeclOrDeclOnly(FnAbsDeclOrDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forAbsFnDeclOnly(AbsFnDecl that) {
        forFnAbsDeclOrDeclOnly(that);
    }

    public void forFnDeclOnly(FnDecl that) {
        forFnAbsDeclOrDeclOnly(that);
    }

    public void forFnDefOnly(FnDef that) {
        forFnDeclOnly(that);
    }

    public void forParamOnly(Param that) {
        forAbstractNodeOnly(that);
    }

    public void forNormalParamOnly(NormalParam that) {
        forParamOnly(that);
    }

    public void forVarargsParamOnly(VarargsParam that) {
        forParamOnly(that);
    }

    public void forDimUnitDeclOnly(DimUnitDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forDimDeclOnly(DimDecl that) {
        forDimUnitDeclOnly(that);
    }

    public void forUnitDeclOnly(UnitDecl that) {
        forDimUnitDeclOnly(that);
    }

    public void forTestDeclOnly(TestDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forPropertyDeclOnly(PropertyDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forExternalSyntaxAbsDeclOrDeclOnly(ExternalSyntaxAbsDeclOrDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forAbsExternalSyntaxOnly(AbsExternalSyntax that) {
        forExternalSyntaxAbsDeclOrDeclOnly(that);
    }

    public void forExternalSyntaxOnly(ExternalSyntax that) {
        forExternalSyntaxAbsDeclOrDeclOnly(that);
    }

    public void forGrammarDeclOnly(GrammarDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forGrammarDefOnly(GrammarDef that) {
        forGrammarDeclOnly(that);
    }

    public void forGrammarMemberDeclOnly(GrammarMemberDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forNonterminalDeclOnly(NonterminalDecl that) {
        forGrammarMemberDeclOnly(that);
    }

    public void forNonterminalDefOnly(NonterminalDef that) {
        forNonterminalDeclOnly(that);
    }

    public void forNonterminalExtensionDefOnly(NonterminalExtensionDef that) {
        forNonterminalDeclOnly(that);
    }

    public void forTerminalDeclOnly(TerminalDecl that) {
        forGrammarMemberDeclOnly(that);
    }

    public void for_TerminalDefOnly(_TerminalDef that) {
        forTerminalDeclOnly(that);
    }

    public void forSyntaxDeclOnly(SyntaxDecl that) {
        forAbstractNodeOnly(that);
    }

    public void forSyntaxDefOnly(SyntaxDef that) {
        forSyntaxDeclOnly(that);
    }

    public void forSyntaxSymbolOnly(SyntaxSymbol that) {
        forAbstractNodeOnly(that);
    }

    public void forPrefixedSymbolOnly(PrefixedSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forOptionalSymbolOnly(OptionalSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forRepeatSymbolOnly(RepeatSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forRepeatOneOrMoreSymbolOnly(RepeatOneOrMoreSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forNoWhitespaceSymbolOnly(NoWhitespaceSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forSpecialSymbolOnly(SpecialSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forWhitespaceSymbolOnly(WhitespaceSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forTabSymbolOnly(TabSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forFormfeedSymbolOnly(FormfeedSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forCarriageReturnSymbolOnly(CarriageReturnSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forBackspaceSymbolOnly(BackspaceSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forNewlineSymbolOnly(NewlineSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forBreaklineSymbolOnly(BreaklineSymbol that) {
        forSpecialSymbolOnly(that);
    }

    public void forItemSymbolOnly(ItemSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forNonterminalSymbolOnly(NonterminalSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forKeywordSymbolOnly(KeywordSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forTokenSymbolOnly(TokenSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forNotPredicateSymbolOnly(NotPredicateSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forAndPredicateSymbolOnly(AndPredicateSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forCharacterClassSymbolOnly(CharacterClassSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forCharacterSymbolOnly(CharacterSymbol that) {
        forSyntaxSymbolOnly(that);
    }

    public void forCharSymbolOnly(CharSymbol that) {
        forCharacterSymbolOnly(that);
    }

    public void forCharacterIntervalOnly(CharacterInterval that) {
        forCharacterSymbolOnly(that);
    }

    public void forExprOnly(Expr that) {
        forAbstractNodeOnly(that);
    }

    public void forTypeAnnotatedExprOnly(TypeAnnotatedExpr that) {
        forExprOnly(that);
    }

    public void forAsExprOnly(AsExpr that) {
        forTypeAnnotatedExprOnly(that);
    }

    public void forAsIfExprOnly(AsIfExpr that) {
        forTypeAnnotatedExprOnly(that);
    }

    public void forAssignmentOnly(Assignment that) {
        forExprOnly(that);
    }

    public void forDelimitedExprOnly(DelimitedExpr that) {
        forExprOnly(that);
    }

    public void forBlockOnly(Block that) {
        forDelimitedExprOnly(that);
    }

    public void forCaseExprOnly(CaseExpr that) {
        forDelimitedExprOnly(that);
    }

    public void forDoOnly(Do that) {
        forDelimitedExprOnly(that);
    }

    public void forForOnly(For that) {
        forDelimitedExprOnly(that);
    }

    public void forIfOnly(If that) {
        forDelimitedExprOnly(that);
    }

    public void forLabelOnly(Label that) {
        forDelimitedExprOnly(that);
    }

    public void forAbstractObjectExprOnly(AbstractObjectExpr that) {
        forDelimitedExprOnly(that);
    }

    public void forObjectExprOnly(ObjectExpr that) {
        forAbstractObjectExprOnly(that);
    }

    public void for_RewriteObjectExprOnly(_RewriteObjectExpr that) {
        forAbstractObjectExprOnly(that);
    }

    public void forTryOnly(Try that) {
        forDelimitedExprOnly(that);
    }

    public void forAbstractTupleExprOnly(AbstractTupleExpr that) {
        forDelimitedExprOnly(that);
    }

    public void forTupleExprOnly(TupleExpr that) {
        forAbstractTupleExprOnly(that);
    }

    public void forArgExprOnly(ArgExpr that) {
        forAbstractTupleExprOnly(that);
    }

    public void forTypecaseOnly(Typecase that) {
        forDelimitedExprOnly(that);
    }

    public void forWhileOnly(While that) {
        forDelimitedExprOnly(that);
    }

    public void forFlowExprOnly(FlowExpr that) {
        forExprOnly(that);
    }

    public void forBigOprAppOnly(BigOprApp that) {
        forFlowExprOnly(that);
    }

    public void forAccumulatorOnly(Accumulator that) {
        forBigOprAppOnly(that);
    }

    public void forArrayComprehensionOnly(ArrayComprehension that) {
        forBigOprAppOnly(that);
    }

    public void forAtomicExprOnly(AtomicExpr that) {
        forFlowExprOnly(that);
    }

    public void forExitOnly(Exit that) {
        forFlowExprOnly(that);
    }

    public void forSpawnOnly(Spawn that) {
        forFlowExprOnly(that);
    }

    public void forThrowOnly(Throw that) {
        forFlowExprOnly(that);
    }

    public void forTryAtomicExprOnly(TryAtomicExpr that) {
        forFlowExprOnly(that);
    }

    public void forFnExprOnly(FnExpr that) {
        forExprOnly(that);
    }

    public void forLetExprOnly(LetExpr that) {
        forExprOnly(that);
    }

    public void forLetFnOnly(LetFn that) {
        forLetExprOnly(that);
    }

    public void forLocalVarDeclOnly(LocalVarDecl that) {
        forLetExprOnly(that);
    }

    public void forGeneratedExprOnly(GeneratedExpr that) {
        forExprOnly(that);
    }

    public void forOpExprOnly(OpExpr that) {
        forExprOnly(that);
    }

    public void forSubscriptExprOnly(SubscriptExpr that) {
        forOpExprOnly(that);
    }

    public void forPrimaryOnly(Primary that) {
        forOpExprOnly(that);
    }

    public void forLiteralExprOnly(LiteralExpr that) {
        forPrimaryOnly(that);
    }

    public void forNumberLiteralExprOnly(NumberLiteralExpr that) {
        forLiteralExprOnly(that);
    }

    public void forFloatLiteralExprOnly(FloatLiteralExpr that) {
        forNumberLiteralExprOnly(that);
    }

    public void forIntLiteralExprOnly(IntLiteralExpr that) {
        forNumberLiteralExprOnly(that);
    }

    public void forCharLiteralExprOnly(CharLiteralExpr that) {
        forLiteralExprOnly(that);
    }

    public void forStringLiteralExprOnly(StringLiteralExpr that) {
        forLiteralExprOnly(that);
    }

    public void forVoidLiteralExprOnly(VoidLiteralExpr that) {
        forLiteralExprOnly(that);
    }

    public void forVarRefOnly(VarRef that) {
        forPrimaryOnly(that);
    }

    public void forAbstractFieldRefOnly(AbstractFieldRef that) {
        forPrimaryOnly(that);
    }

    public void forFieldRefOnly(FieldRef that) {
        forAbstractFieldRefOnly(that);
    }

    public void forFieldRefForSureOnly(FieldRefForSure that) {
        forAbstractFieldRefOnly(that);
    }

    public void for_RewriteFieldRefOnly(_RewriteFieldRef that) {
        forAbstractFieldRefOnly(that);
    }

    public void forFunctionalRefOnly(FunctionalRef that) {
        forPrimaryOnly(that);
    }

    public void forFnRefOnly(FnRef that) {
        forFunctionalRefOnly(that);
    }

    public void for_RewriteFnRefOnly(_RewriteFnRef that) {
        forFunctionalRefOnly(that);
    }

    public void forOpRefOnly(OpRef that) {
        forFunctionalRefOnly(that);
    }

    public void forAppExprOnly(AppExpr that) {
        forPrimaryOnly(that);
    }

    public void forJuxtOnly(Juxt that) {
        forAppExprOnly(that);
    }

    public void forLooseJuxtOnly(LooseJuxt that) {
        forJuxtOnly(that);
    }

    public void forTightJuxtOnly(TightJuxt that) {
        forJuxtOnly(that);
    }

    public void forOprExprOnly(OprExpr that) {
        forAppExprOnly(that);
    }

    public void forChainExprOnly(ChainExpr that) {
        forAppExprOnly(that);
    }

    public void forCoercionInvocationOnly(CoercionInvocation that) {
        forAppExprOnly(that);
    }

    public void forMethodInvocationOnly(MethodInvocation that) {
        forAppExprOnly(that);
    }

    public void forMathPrimaryOnly(MathPrimary that) {
        forPrimaryOnly(that);
    }

    public void forArrayExprOnly(ArrayExpr that) {
        forPrimaryOnly(that);
    }

    public void forArrayElementOnly(ArrayElement that) {
        forArrayExprOnly(that);
    }

    public void forArrayElementsOnly(ArrayElements that) {
        forArrayExprOnly(that);
    }

    public void forTypeOnly(Type that) {
        forAbstractNodeOnly(that);
    }

    public void forDimExprOnly(DimExpr that) {
        forTypeOnly(that);
    }

    public void forExponentTypeOnly(ExponentType that) {
        forDimExprOnly(that);
    }

    public void forBaseDimOnly(BaseDim that) {
        forDimExprOnly(that);
    }

    public void forDimRefOnly(DimRef that) {
        forDimExprOnly(that);
    }

    public void forProductDimOnly(ProductDim that) {
        forDimExprOnly(that);
    }

    public void forQuotientDimOnly(QuotientDim that) {
        forDimExprOnly(that);
    }

    public void forExponentDimOnly(ExponentDim that) {
        forDimExprOnly(that);
    }

    public void forOpDimOnly(OpDim that) {
        forDimExprOnly(that);
    }

    public void forAbstractArrowTypeOnly(AbstractArrowType that) {
        forTypeOnly(that);
    }

    public void forArrowTypeOnly(ArrowType that) {
        forAbstractArrowTypeOnly(that);
    }

    public void for_RewriteGenericArrowTypeOnly(_RewriteGenericArrowType that) {
        forAbstractArrowTypeOnly(that);
    }

    public void forNonArrowTypeOnly(NonArrowType that) {
        forTypeOnly(that);
    }

    public void forBottomTypeOnly(BottomType that) {
        forNonArrowTypeOnly(that);
    }

    public void forTraitTypeOnly(TraitType that) {
        forNonArrowTypeOnly(that);
    }

    public void forNamedTypeOnly(NamedType that) {
        forTraitTypeOnly(that);
    }

    public void forIdTypeOnly(IdType that) {
        forNamedTypeOnly(that);
    }

    public void forInstantiatedTypeOnly(InstantiatedType that) {
        forNamedTypeOnly(that);
    }

    public void forAbbreviatedTypeOnly(AbbreviatedType that) {
        forTraitTypeOnly(that);
    }

    public void forArrayTypeOnly(ArrayType that) {
        forAbbreviatedTypeOnly(that);
    }

    public void forMatrixTypeOnly(MatrixType that) {
        forAbbreviatedTypeOnly(that);
    }

    public void forAbstractTupleTypeOnly(AbstractTupleType that) {
        forNonArrowTypeOnly(that);
    }

    public void forTupleTypeOnly(TupleType that) {
        forAbstractTupleTypeOnly(that);
    }

    public void forArgTypeOnly(ArgType that) {
        forAbstractTupleTypeOnly(that);
    }

    public void forVoidTypeOnly(VoidType that) {
        forNonArrowTypeOnly(that);
    }

    public void forInferenceVarTypeOnly(InferenceVarType that) {
        forNonArrowTypeOnly(that);
    }

    public void forAndTypeOnly(AndType that) {
        forNonArrowTypeOnly(that);
    }

    public void forOrTypeOnly(OrType that) {
        forNonArrowTypeOnly(that);
    }

    public void forFixedPointTypeOnly(FixedPointType that) {
        forNonArrowTypeOnly(that);
    }

    public void forDimTypeOnly(DimType that) {
        forNonArrowTypeOnly(that);
    }

    public void forTaggedDimTypeOnly(TaggedDimType that) {
        forDimTypeOnly(that);
    }

    public void forTaggedUnitTypeOnly(TaggedUnitType that) {
        forDimTypeOnly(that);
    }

    public void forStaticArgOnly(StaticArg that) {
        forTypeOnly(that);
    }

    public void forIdArgOnly(IdArg that) {
        forStaticArgOnly(that);
    }

    public void forTypeArgOnly(TypeArg that) {
        forStaticArgOnly(that);
    }

    public void forIntArgOnly(IntArg that) {
        forStaticArgOnly(that);
    }

    public void forBoolArgOnly(BoolArg that) {
        forStaticArgOnly(that);
    }

    public void forOprArgOnly(OprArg that) {
        forStaticArgOnly(that);
    }

    public void forDimArgOnly(DimArg that) {
        forStaticArgOnly(that);
    }

    public void forUnitArgOnly(UnitArg that) {
        forStaticArgOnly(that);
    }

    public void forStaticExprOnly(StaticExpr that) {
        forAbstractNodeOnly(that);
    }

    public void forIntExprOnly(IntExpr that) {
        forStaticExprOnly(that);
    }

    public void forIntValOnly(IntVal that) {
        forIntExprOnly(that);
    }

    public void forNumberConstraintOnly(NumberConstraint that) {
        forIntValOnly(that);
    }

    public void forIntRefOnly(IntRef that) {
        forIntValOnly(that);
    }

    public void forIntOpExprOnly(IntOpExpr that) {
        forIntExprOnly(that);
    }

    public void forSumConstraintOnly(SumConstraint that) {
        forIntOpExprOnly(that);
    }

    public void forMinusConstraintOnly(MinusConstraint that) {
        forIntOpExprOnly(that);
    }

    public void forProductConstraintOnly(ProductConstraint that) {
        forIntOpExprOnly(that);
    }

    public void forExponentConstraintOnly(ExponentConstraint that) {
        forIntOpExprOnly(that);
    }

    public void forBoolExprOnly(BoolExpr that) {
        forStaticExprOnly(that);
    }

    public void forBoolValOnly(BoolVal that) {
        forBoolExprOnly(that);
    }

    public void forBoolConstantOnly(BoolConstant that) {
        forBoolValOnly(that);
    }

    public void forBoolRefOnly(BoolRef that) {
        forBoolValOnly(that);
    }

    public void forBoolConstraintOnly(BoolConstraint that) {
        forBoolExprOnly(that);
    }

    public void forNotConstraintOnly(NotConstraint that) {
        forBoolConstraintOnly(that);
    }

    public void forBinaryBoolConstraintOnly(BinaryBoolConstraint that) {
        forBoolConstraintOnly(that);
    }

    public void forOrConstraintOnly(OrConstraint that) {
        forBinaryBoolConstraintOnly(that);
    }

    public void forAndConstraintOnly(AndConstraint that) {
        forBinaryBoolConstraintOnly(that);
    }

    public void forImpliesConstraintOnly(ImpliesConstraint that) {
        forBinaryBoolConstraintOnly(that);
    }

    public void forBEConstraintOnly(BEConstraint that) {
        forBinaryBoolConstraintOnly(that);
    }

    public void forWhereClauseOnly(WhereClause that) {
        forAbstractNodeOnly(that);
    }

    public void forWhereBindingOnly(WhereBinding that) {
        forAbstractNodeOnly(that);
    }

    public void forWhereTypeOnly(WhereType that) {
        forWhereBindingOnly(that);
    }

    public void forWhereNatOnly(WhereNat that) {
        forWhereBindingOnly(that);
    }

    public void forWhereIntOnly(WhereInt that) {
        forWhereBindingOnly(that);
    }

    public void forWhereBoolOnly(WhereBool that) {
        forWhereBindingOnly(that);
    }

    public void forWhereUnitOnly(WhereUnit that) {
        forWhereBindingOnly(that);
    }

    public void forWhereConstraintOnly(WhereConstraint that) {
        forAbstractNodeOnly(that);
    }

    public void forWhereExtendsOnly(WhereExtends that) {
        forWhereConstraintOnly(that);
    }

    public void forTypeAliasOnly(TypeAlias that) {
        forWhereConstraintOnly(that);
    }

    public void forWhereCoercesOnly(WhereCoerces that) {
        forWhereConstraintOnly(that);
    }

    public void forWhereWidensOnly(WhereWidens that) {
        forWhereConstraintOnly(that);
    }

    public void forWhereWidensCoercesOnly(WhereWidensCoerces that) {
        forWhereConstraintOnly(that);
    }

    public void forWhereEqualsOnly(WhereEquals that) {
        forWhereConstraintOnly(that);
    }

    public void forUnitConstraintOnly(UnitConstraint that) {
        forWhereConstraintOnly(that);
    }

    public void forIntConstraintOnly(IntConstraint that) {
        forWhereConstraintOnly(that);
    }

    public void forLEConstraintOnly(LEConstraint that) {
        forIntConstraintOnly(that);
    }

    public void forLTConstraintOnly(LTConstraint that) {
        forIntConstraintOnly(that);
    }

    public void forGEConstraintOnly(GEConstraint that) {
        forIntConstraintOnly(that);
    }

    public void forGTConstraintOnly(GTConstraint that) {
        forIntConstraintOnly(that);
    }

    public void forIEConstraintOnly(IEConstraint that) {
        forIntConstraintOnly(that);
    }

    public void forBoolConstraintExprOnly(BoolConstraintExpr that) {
        forWhereConstraintOnly(that);
    }

    public void forContractOnly(Contract that) {
        forAbstractNodeOnly(that);
    }

    public void forEnsuresClauseOnly(EnsuresClause that) {
        forAbstractNodeOnly(that);
    }

    public void forModifierOnly(Modifier that) {
        forAbstractNodeOnly(that);
    }

    public void forModifierAbstractOnly(ModifierAbstract that) {
        forModifierOnly(that);
    }

    public void forModifierAtomicOnly(ModifierAtomic that) {
        forModifierOnly(that);
    }

    public void forModifierGetterOnly(ModifierGetter that) {
        forModifierOnly(that);
    }

    public void forModifierHiddenOnly(ModifierHidden that) {
        forModifierOnly(that);
    }

    public void forModifierIOOnly(ModifierIO that) {
        forModifierOnly(that);
    }

    public void forModifierOverrideOnly(ModifierOverride that) {
        forModifierOnly(that);
    }

    public void forModifierPrivateOnly(ModifierPrivate that) {
        forModifierOnly(that);
    }

    public void forModifierSettableOnly(ModifierSettable that) {
        forModifierOnly(that);
    }

    public void forModifierSetterOnly(ModifierSetter that) {
        forModifierOnly(that);
    }

    public void forModifierTestOnly(ModifierTest that) {
        forModifierOnly(that);
    }

    public void forModifierTransientOnly(ModifierTransient that) {
        forModifierOnly(that);
    }

    public void forModifierValueOnly(ModifierValue that) {
        forModifierOnly(that);
    }

    public void forModifierVarOnly(ModifierVar that) {
        forModifierOnly(that);
    }

    public void forModifierWidensOnly(ModifierWidens that) {
        forModifierOnly(that);
    }

    public void forModifierWrappedOnly(ModifierWrapped that) {
        forModifierOnly(that);
    }

    public void forStaticParamOnly(StaticParam that) {
        forAbstractNodeOnly(that);
    }

    public void forOperatorParamOnly(OperatorParam that) {
        forStaticParamOnly(that);
    }

    public void forIdStaticParamOnly(IdStaticParam that) {
        forStaticParamOnly(that);
    }

    public void forBoolParamOnly(BoolParam that) {
        forIdStaticParamOnly(that);
    }

    public void forDimensionParamOnly(DimensionParam that) {
        forIdStaticParamOnly(that);
    }

    public void forIntParamOnly(IntParam that) {
        forIdStaticParamOnly(that);
    }

    public void forNatParamOnly(NatParam that) {
        forIdStaticParamOnly(that);
    }

    public void forSimpleTypeParamOnly(SimpleTypeParam that) {
        forIdStaticParamOnly(that);
    }

    public void forUnitParamOnly(UnitParam that) {
        forIdStaticParamOnly(that);
    }

    public void forNameOnly(Name that) {
        forAbstractNodeOnly(that);
    }

    public void forAPINameOnly(APIName that) {
        forNameOnly(that);
    }

    public void forQualifiedNameOnly(QualifiedName that) {
        forNameOnly(that);
    }

    public void forQualifiedIdNameOnly(QualifiedIdName that) {
        forQualifiedNameOnly(that);
    }

    public void forQualifiedOpNameOnly(QualifiedOpName that) {
        forQualifiedNameOnly(that);
    }

    public void forSimpleNameOnly(SimpleName that) {
        forNameOnly(that);
    }

    public void forIdOnly(Id that) {
        forSimpleNameOnly(that);
    }

    public void forOpNameOnly(OpName that) {
        forSimpleNameOnly(that);
    }

    public void forOpOnly(Op that) {
        forOpNameOnly(that);
    }

    public void forEnclosingOnly(Enclosing that) {
        forOpNameOnly(that);
    }

    public void forAnonymousFnNameOnly(AnonymousFnName that) {
        forSimpleNameOnly(that);
    }

    public void forConstructorFnNameOnly(ConstructorFnName that) {
        forSimpleNameOnly(that);
    }

    public void forArrayComprehensionClauseOnly(ArrayComprehensionClause that) {
        forAbstractNodeOnly(that);
    }

    public void forKeywordExprOnly(KeywordExpr that) {
        forAbstractNodeOnly(that);
    }

    public void forCaseClauseOnly(CaseClause that) {
        forAbstractNodeOnly(that);
    }

    public void forCatchOnly(Catch that) {
        forAbstractNodeOnly(that);
    }

    public void forCatchClauseOnly(CatchClause that) {
        forAbstractNodeOnly(that);
    }

    public void forDoFrontOnly(DoFront that) {
        forAbstractNodeOnly(that);
    }

    public void forIfClauseOnly(IfClause that) {
        forAbstractNodeOnly(that);
    }

    public void forTypecaseClauseOnly(TypecaseClause that) {
        forAbstractNodeOnly(that);
    }

    public void forExtentRangeOnly(ExtentRange that) {
        forAbstractNodeOnly(that);
    }

    public void forGeneratorClauseOnly(GeneratorClause that) {
        forAbstractNodeOnly(that);
    }

    public void forVarargsExprOnly(VarargsExpr that) {
        forAbstractNodeOnly(that);
    }

    public void forVarargsTypeOnly(VarargsType that) {
        forAbstractNodeOnly(that);
    }

    public void forKeywordTypeOnly(KeywordType that) {
        forAbstractNodeOnly(that);
    }

    public void forTraitTypeWhereOnly(TraitTypeWhere that) {
        forAbstractNodeOnly(that);
    }

    public void forIndicesOnly(Indices that) {
        forAbstractNodeOnly(that);
    }

    public void forMathItemOnly(MathItem that) {
        forAbstractNodeOnly(that);
    }

    public void forExprMIOnly(ExprMI that) {
        forMathItemOnly(that);
    }

    public void forParenthesisDelimitedMIOnly(ParenthesisDelimitedMI that) {
        forExprMIOnly(that);
    }

    public void forNonParenthesisDelimitedMIOnly(NonParenthesisDelimitedMI that) {
        forExprMIOnly(that);
    }

    public void forNonExprMIOnly(NonExprMI that) {
        forMathItemOnly(that);
    }

    public void forExponentiationMIOnly(ExponentiationMI that) {
        forNonExprMIOnly(that);
    }

    public void forSubscriptingMIOnly(SubscriptingMI that) {
        forNonExprMIOnly(that);
    }

    public void forFixityOnly(Fixity that) {
        forAbstractNodeOnly(that);
    }

    public void forInFixityOnly(InFixity that) {
        forFixityOnly(that);
    }

    public void forPreFixityOnly(PreFixity that) {
        forFixityOnly(that);
    }

    public void forPostFixityOnly(PostFixity that) {
        forFixityOnly(that);
    }

    public void forNoFixityOnly(NoFixity that) {
        forFixityOnly(that);
    }

    public void forMultiFixityOnly(MultiFixity that) {
        forFixityOnly(that);
    }

    public void forEnclosingFixityOnly(EnclosingFixity that) {
        forFixityOnly(that);
    }

    public void forBigFixityOnly(BigFixity that) {
        forFixityOnly(that);
    }

    /* Methods to recur on each child. */
    public void forComponent(Component that) {
        forComponentDoFirst(that);
        that.getName().accept(this);
        recurOnListOfImport(that.getImports());
        recurOnListOfExport(that.getExports());
        recurOnListOfDecl(that.getDecls());
        forComponentOnly(that);
    }

    public void forApi(Api that) {
        forApiDoFirst(that);
        that.getName().accept(this);
        recurOnListOfImport(that.getImports());
        recurOnListOfAbsDecl(that.getDecls());
        forApiOnly(that);
    }

    public void forImportStar(ImportStar that) {
        forImportStarDoFirst(that);
        that.getApi().accept(this);
        recurOnListOfSimpleName(that.getExcept());
        forImportStarOnly(that);
    }

    public void forImportNames(ImportNames that) {
        forImportNamesDoFirst(that);
        that.getApi().accept(this);
        recurOnListOfAliasedSimpleName(that.getAliasedNames());
        forImportNamesOnly(that);
    }

    public void forImportApi(ImportApi that) {
        forImportApiDoFirst(that);
        recurOnListOfAliasedAPIName(that.getApis());
        forImportApiOnly(that);
    }

    public void forAliasedSimpleName(AliasedSimpleName that) {
        forAliasedSimpleNameDoFirst(that);
        that.getName().accept(this);
        recurOnOptionOfSimpleName(that.getAlias());
        forAliasedSimpleNameOnly(that);
    }

    public void forAliasedAPIName(AliasedAPIName that) {
        forAliasedAPINameDoFirst(that);
        that.getApi().accept(this);
        recurOnOptionOfId(that.getAlias());
        forAliasedAPINameOnly(that);
    }

    public void forExport(Export that) {
        forExportDoFirst(that);
        recurOnListOfAPIName(that.getApis());
        forExportOnly(that);
    }

    public void forAbsTraitDecl(AbsTraitDecl that) {
        forAbsTraitDeclDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfTraitTypeWhere(that.getExtendsClause());
        that.getWhere().accept(this);
        recurOnListOfTraitType(that.getExcludes());
        recurOnOptionOfListOfTraitType(that.getComprises());
        recurOnListOfAbsDecl(that.getDecls());
        forAbsTraitDeclOnly(that);
    }

    public void forTraitDecl(TraitDecl that) {
        forTraitDeclDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfTraitTypeWhere(that.getExtendsClause());
        that.getWhere().accept(this);
        recurOnListOfTraitType(that.getExcludes());
        recurOnOptionOfListOfTraitType(that.getComprises());
        recurOnListOfDecl(that.getDecls());
        forTraitDeclOnly(that);
    }

    public void forAbsObjectDecl(AbsObjectDecl that) {
        forAbsObjectDeclDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfTraitTypeWhere(that.getExtendsClause());
        that.getWhere().accept(this);
        recurOnOptionOfListOfParam(that.getParams());
        recurOnOptionOfListOfTraitType(that.getThrowsClause());
        that.getContract().accept(this);
        recurOnListOfAbsDecl(that.getDecls());
        forAbsObjectDeclOnly(that);
    }

    public void forObjectDecl(ObjectDecl that) {
        forObjectDeclDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfTraitTypeWhere(that.getExtendsClause());
        that.getWhere().accept(this);
        recurOnOptionOfListOfParam(that.getParams());
        recurOnOptionOfListOfTraitType(that.getThrowsClause());
        that.getContract().accept(this);
        recurOnListOfDecl(that.getDecls());
        forObjectDeclOnly(that);
    }

    public void forAbsVarDecl(AbsVarDecl that) {
        forAbsVarDeclDoFirst(that);
        recurOnListOfLValueBind(that.getLhs());
        forAbsVarDeclOnly(that);
    }

    public void forVarDecl(VarDecl that) {
        forVarDeclDoFirst(that);
        recurOnListOfLValueBind(that.getLhs());
        that.getInit().accept(this);
        forVarDeclOnly(that);
    }

    public void forLValueBind(LValueBind that) {
        forLValueBindDoFirst(that);
        that.getName().accept(this);
        recurOnOptionOfType(that.getType());
        recurOnListOfModifier(that.getMods());
        forLValueBindOnly(that);
    }

    public void forUnpastingBind(UnpastingBind that) {
        forUnpastingBindDoFirst(that);
        that.getName().accept(this);
        recurOnListOfExtentRange(that.getDim());
        forUnpastingBindOnly(that);
    }

    public void forUnpastingSplit(UnpastingSplit that) {
        forUnpastingSplitDoFirst(that);
        recurOnListOfUnpasting(that.getElems());
        forUnpastingSplitOnly(that);
    }

    public void forAbsFnDecl(AbsFnDecl that) {
        forAbsFnDeclDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfParam(that.getParams());
        recurOnOptionOfType(that.getReturnType());
        recurOnOptionOfListOfTraitType(that.getThrowsClause());
        that.getWhere().accept(this);
        that.getContract().accept(this);
        forAbsFnDeclOnly(that);
    }

    public void forFnDef(FnDef that) {
        forFnDefDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfParam(that.getParams());
        recurOnOptionOfType(that.getReturnType());
        recurOnOptionOfListOfTraitType(that.getThrowsClause());
        that.getWhere().accept(this);
        that.getContract().accept(this);
        that.getBody().accept(this);
        forFnDefOnly(that);
    }

    public void forNormalParam(NormalParam that) {
        forNormalParamDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        recurOnOptionOfType(that.getType());
        recurOnOptionOfExpr(that.getDefaultExpr());
        forNormalParamOnly(that);
    }

    public void forVarargsParam(VarargsParam that) {
        forVarargsParamDoFirst(that);
        recurOnListOfModifier(that.getMods());
        that.getName().accept(this);
        that.getVarargsType().accept(this);
        forVarargsParamOnly(that);
    }

    public void forDimDecl(DimDecl that) {
        forDimDeclDoFirst(that);
        that.getDim().accept(this);
        recurOnOptionOfType(that.getDerived());
        recurOnOptionOfId(that.getDefault());
        forDimDeclOnly(that);
    }

    public void forUnitDecl(UnitDecl that) {
        forUnitDeclDoFirst(that);
        recurOnListOfId(that.getUnits());
        recurOnOptionOfType(that.getDim());
        recurOnOptionOfExpr(that.getDef());
        forUnitDeclOnly(that);
    }

    public void forTestDecl(TestDecl that) {
        forTestDeclDoFirst(that);
        that.getName().accept(this);
        recurOnListOfGeneratorClause(that.getGens());
        that.getExpr().accept(this);
        forTestDeclOnly(that);
    }

    public void forPropertyDecl(PropertyDecl that) {
        forPropertyDeclDoFirst(that);
        recurOnOptionOfId(that.getName());
        recurOnListOfParam(that.getParams());
        that.getExpr().accept(this);
        forPropertyDeclOnly(that);
    }

    public void forAbsExternalSyntax(AbsExternalSyntax that) {
        forAbsExternalSyntaxDoFirst(that);
        that.getOpenExpander().accept(this);
        that.getName().accept(this);
        that.getCloseExpander().accept(this);
        forAbsExternalSyntaxOnly(that);
    }

    public void forExternalSyntax(ExternalSyntax that) {
        forExternalSyntaxDoFirst(that);
        that.getOpenExpander().accept(this);
        that.getName().accept(this);
        that.getCloseExpander().accept(this);
        that.getExpr().accept(this);
        forExternalSyntaxOnly(that);
    }

    public void forGrammarDef(GrammarDef that) {
        forGrammarDefDoFirst(that);
        that.getName().accept(this);
        recurOnListOfQualifiedIdName(that.getExtends());
        recurOnListOfGrammarMemberDecl(that.getMembers());
        forGrammarDefOnly(that);
    }

    public void forNonterminalDef(NonterminalDef that) {
        forNonterminalDefDoFirst(that);
        that.getName().accept(this);
        recurOnOptionOfTraitType(that.getType());
        recurOnOptionOfModifier(that.getModifier());
        recurOnListOfSyntaxDef(that.getSyntaxDefs());
        forNonterminalDefOnly(that);
    }

    public void forNonterminalExtensionDef(NonterminalExtensionDef that) {
        forNonterminalExtensionDefDoFirst(that);
        that.getName().accept(this);
        recurOnOptionOfTraitType(that.getType());
        recurOnOptionOfModifier(that.getModifier());
        recurOnListOfSyntaxDef(that.getSyntaxDefs());
        forNonterminalExtensionDefOnly(that);
    }

    public void for_TerminalDef(_TerminalDef that) {
        for_TerminalDefDoFirst(that);
        that.getName().accept(this);
        recurOnOptionOfTraitType(that.getType());
        recurOnOptionOfModifier(that.getModifier());
        that.getSyntaxDef().accept(this);
        for_TerminalDefOnly(that);
    }

    public void forSyntaxDef(SyntaxDef that) {
        forSyntaxDefDoFirst(that);
        recurOnListOfSyntaxSymbol(that.getSyntaxSymbols());
        that.getTransformationExpression().accept(this);
        forSyntaxDefOnly(that);
    }

    public void forPrefixedSymbol(PrefixedSymbol that) {
        forPrefixedSymbolDoFirst(that);
        recurOnOptionOfId(that.getId());
        that.getSymbol().accept(this);
        forPrefixedSymbolOnly(that);
    }

    public void forOptionalSymbol(OptionalSymbol that) {
        forOptionalSymbolDoFirst(that);
        that.getSymbol().accept(this);
        forOptionalSymbolOnly(that);
    }

    public void forRepeatSymbol(RepeatSymbol that) {
        forRepeatSymbolDoFirst(that);
        that.getSymbol().accept(this);
        forRepeatSymbolOnly(that);
    }

    public void forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that) {
        forRepeatOneOrMoreSymbolDoFirst(that);
        that.getSymbol().accept(this);
        forRepeatOneOrMoreSymbolOnly(that);
    }

    public void forNoWhitespaceSymbol(NoWhitespaceSymbol that) {
        forNoWhitespaceSymbolDoFirst(that);
        that.getSymbol().accept(this);
        forNoWhitespaceSymbolOnly(that);
    }

    public void forWhitespaceSymbol(WhitespaceSymbol that) {
        forWhitespaceSymbolDoFirst(that);
        forWhitespaceSymbolOnly(that);
    }

    public void forTabSymbol(TabSymbol that) {
        forTabSymbolDoFirst(that);
        forTabSymbolOnly(that);
    }

    public void forFormfeedSymbol(FormfeedSymbol that) {
        forFormfeedSymbolDoFirst(that);
        forFormfeedSymbolOnly(that);
    }

    public void forCarriageReturnSymbol(CarriageReturnSymbol that) {
        forCarriageReturnSymbolDoFirst(that);
        forCarriageReturnSymbolOnly(that);
    }

    public void forBackspaceSymbol(BackspaceSymbol that) {
        forBackspaceSymbolDoFirst(that);
        forBackspaceSymbolOnly(that);
    }

    public void forNewlineSymbol(NewlineSymbol that) {
        forNewlineSymbolDoFirst(that);
        forNewlineSymbolOnly(that);
    }

    public void forBreaklineSymbol(BreaklineSymbol that) {
        forBreaklineSymbolDoFirst(that);
        forBreaklineSymbolOnly(that);
    }

    public void forItemSymbol(ItemSymbol that) {
        forItemSymbolDoFirst(that);
        forItemSymbolOnly(that);
    }

    public void forNonterminalSymbol(NonterminalSymbol that) {
        forNonterminalSymbolDoFirst(that);
        that.getNonterminal().accept(this);
        forNonterminalSymbolOnly(that);
    }

    public void forKeywordSymbol(KeywordSymbol that) {
        forKeywordSymbolDoFirst(that);
        forKeywordSymbolOnly(that);
    }

    public void forTokenSymbol(TokenSymbol that) {
        forTokenSymbolDoFirst(that);
        forTokenSymbolOnly(that);
    }

    public void forNotPredicateSymbol(NotPredicateSymbol that) {
        forNotPredicateSymbolDoFirst(that);
        that.getSymbol().accept(this);
        forNotPredicateSymbolOnly(that);
    }

    public void forAndPredicateSymbol(AndPredicateSymbol that) {
        forAndPredicateSymbolDoFirst(that);
        that.getSymbol().accept(this);
        forAndPredicateSymbolOnly(that);
    }

    public void forCharacterClassSymbol(CharacterClassSymbol that) {
        forCharacterClassSymbolDoFirst(that);
        recurOnListOfCharacterSymbol(that.getCharacters());
        forCharacterClassSymbolOnly(that);
    }

    public void forCharSymbol(CharSymbol that) {
        forCharSymbolDoFirst(that);
        forCharSymbolOnly(that);
    }

    public void forCharacterInterval(CharacterInterval that) {
        forCharacterIntervalDoFirst(that);
        forCharacterIntervalOnly(that);
    }

    public void forAsExpr(AsExpr that) {
        forAsExprDoFirst(that);
        that.getExpr().accept(this);
        that.getType().accept(this);
        forAsExprOnly(that);
    }

    public void forAsIfExpr(AsIfExpr that) {
        forAsIfExprDoFirst(that);
        that.getExpr().accept(this);
        that.getType().accept(this);
        forAsIfExprOnly(that);
    }

    public void forAssignment(Assignment that) {
        forAssignmentDoFirst(that);
        recurOnListOfLHS(that.getLhs());
        recurOnOptionOfOp(that.getOpr());
        that.getRhs().accept(this);
        forAssignmentOnly(that);
    }

    public void forBlock(Block that) {
        forBlockDoFirst(that);
        recurOnListOfExpr(that.getExprs());
        forBlockOnly(that);
    }

    public void forCaseExpr(CaseExpr that) {
        forCaseExprDoFirst(that);
        recurOnOptionOfExpr(that.getParam());
        recurOnOptionOfOp(that.getCompare());
        recurOnListOfCaseClause(that.getClauses());
        recurOnOptionOfBlock(that.getElseClause());
        forCaseExprOnly(that);
    }

    public void forDo(Do that) {
        forDoDoFirst(that);
        recurOnListOfDoFront(that.getFronts());
        forDoOnly(that);
    }

    public void forFor(For that) {
        forForDoFirst(that);
        recurOnListOfGeneratorClause(that.getGens());
        that.getBody().accept(this);
        forForOnly(that);
    }

    public void forIf(If that) {
        forIfDoFirst(that);
        recurOnListOfIfClause(that.getClauses());
        recurOnOptionOfBlock(that.getElseClause());
        forIfOnly(that);
    }

    public void forLabel(Label that) {
        forLabelDoFirst(that);
        that.getName().accept(this);
        that.getBody().accept(this);
        forLabelOnly(that);
    }

    public void forObjectExpr(ObjectExpr that) {
        forObjectExprDoFirst(that);
        recurOnListOfTraitTypeWhere(that.getExtendsClause());
        recurOnListOfDecl(that.getDecls());
        forObjectExprOnly(that);
    }

    public void for_RewriteObjectExpr(_RewriteObjectExpr that) {
        for_RewriteObjectExprDoFirst(that);
        recurOnListOfTraitTypeWhere(that.getExtendsClause());
        recurOnListOfDecl(that.getDecls());
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfStaticArg(that.getStaticArgs());
        recurOnOptionOfListOfParam(that.getParams());
        for_RewriteObjectExprOnly(that);
    }

    public void forTry(Try that) {
        forTryDoFirst(that);
        that.getBody().accept(this);
        recurOnOptionOfCatch(that.getCatchClause());
        recurOnListOfTraitType(that.getForbid());
        recurOnOptionOfBlock(that.getFinallyClause());
        forTryOnly(that);
    }

    public void forTupleExpr(TupleExpr that) {
        forTupleExprDoFirst(that);
        recurOnListOfExpr(that.getExprs());
        forTupleExprOnly(that);
    }

    public void forArgExpr(ArgExpr that) {
        forArgExprDoFirst(that);
        recurOnListOfExpr(that.getExprs());
        recurOnOptionOfVarargsExpr(that.getVarargs());
        recurOnListOfKeywordExpr(that.getKeywords());
        forArgExprOnly(that);
    }

    public void forTypecase(Typecase that) {
        forTypecaseDoFirst(that);
        recurOnListOfTypecaseClause(that.getClauses());
        recurOnOptionOfBlock(that.getElseClause());
        forTypecaseOnly(that);
    }

    public void forWhile(While that) {
        forWhileDoFirst(that);
        that.getTest().accept(this);
        that.getBody().accept(this);
        forWhileOnly(that);
    }

    public void forAccumulator(Accumulator that) {
        forAccumulatorDoFirst(that);
        recurOnListOfStaticArg(that.getStaticArgs());
        that.getOpr().accept(this);
        recurOnListOfGeneratorClause(that.getGens());
        that.getBody().accept(this);
        forAccumulatorOnly(that);
    }

    public void forArrayComprehension(ArrayComprehension that) {
        forArrayComprehensionDoFirst(that);
        recurOnListOfStaticArg(that.getStaticArgs());
        recurOnListOfArrayComprehensionClause(that.getClauses());
        forArrayComprehensionOnly(that);
    }

    public void forAtomicExpr(AtomicExpr that) {
        forAtomicExprDoFirst(that);
        that.getExpr().accept(this);
        forAtomicExprOnly(that);
    }

    public void forExit(Exit that) {
        forExitDoFirst(that);
        recurOnOptionOfId(that.getTarget());
        recurOnOptionOfExpr(that.getReturnExpr());
        forExitOnly(that);
    }

    public void forSpawn(Spawn that) {
        forSpawnDoFirst(that);
        that.getBody().accept(this);
        forSpawnOnly(that);
    }

    public void forThrow(Throw that) {
        forThrowDoFirst(that);
        that.getExpr().accept(this);
        forThrowOnly(that);
    }

    public void forTryAtomicExpr(TryAtomicExpr that) {
        forTryAtomicExprDoFirst(that);
        that.getExpr().accept(this);
        forTryAtomicExprOnly(that);
    }

    public void forFnExpr(FnExpr that) {
        forFnExprDoFirst(that);
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        recurOnListOfParam(that.getParams());
        recurOnOptionOfType(that.getReturnType());
        that.getWhere().accept(this);
        recurOnOptionOfListOfTraitType(that.getThrowsClause());
        that.getBody().accept(this);
        forFnExprOnly(that);
    }

    public void forLetFn(LetFn that) {
        forLetFnDoFirst(that);
        recurOnListOfExpr(that.getBody());
        recurOnListOfFnDef(that.getFns());
        forLetFnOnly(that);
    }

    public void forLocalVarDecl(LocalVarDecl that) {
        forLocalVarDeclDoFirst(that);
        recurOnListOfExpr(that.getBody());
        recurOnListOfLValue(that.getLhs());
        recurOnOptionOfExpr(that.getRhs());
        forLocalVarDeclOnly(that);
    }

    public void forGeneratedExpr(GeneratedExpr that) {
        forGeneratedExprDoFirst(that);
        that.getExpr().accept(this);
        recurOnListOfGeneratorClause(that.getGens());
        forGeneratedExprOnly(that);
    }

    public void forSubscriptExpr(SubscriptExpr that) {
        forSubscriptExprDoFirst(that);
        that.getObj().accept(this);
        recurOnListOfExpr(that.getSubs());
        recurOnOptionOfEnclosing(that.getOp());
        forSubscriptExprOnly(that);
    }

    public void forFloatLiteralExpr(FloatLiteralExpr that) {
        forFloatLiteralExprDoFirst(that);
        forFloatLiteralExprOnly(that);
    }

    public void forIntLiteralExpr(IntLiteralExpr that) {
        forIntLiteralExprDoFirst(that);
        forIntLiteralExprOnly(that);
    }

    public void forCharLiteralExpr(CharLiteralExpr that) {
        forCharLiteralExprDoFirst(that);
        forCharLiteralExprOnly(that);
    }

    public void forStringLiteralExpr(StringLiteralExpr that) {
        forStringLiteralExprDoFirst(that);
        forStringLiteralExprOnly(that);
    }

    public void forVoidLiteralExpr(VoidLiteralExpr that) {
        forVoidLiteralExprDoFirst(that);
        forVoidLiteralExprOnly(that);
    }

    public void forVarRef(VarRef that) {
        forVarRefDoFirst(that);
        that.getVar().accept(this);
        forVarRefOnly(that);
    }

    public void forFieldRef(FieldRef that) {
        forFieldRefDoFirst(that);
        that.getObj().accept(this);
        that.getField().accept(this);
        forFieldRefOnly(that);
    }

    public void forFieldRefForSure(FieldRefForSure that) {
        forFieldRefForSureDoFirst(that);
        that.getObj().accept(this);
        that.getField().accept(this);
        forFieldRefForSureOnly(that);
    }

    public void for_RewriteFieldRef(_RewriteFieldRef that) {
        for_RewriteFieldRefDoFirst(that);
        that.getObj().accept(this);
        that.getField().accept(this);
        for_RewriteFieldRefOnly(that);
    }

    public void forFnRef(FnRef that) {
        forFnRefDoFirst(that);
        recurOnListOfQualifiedIdName(that.getFns());
        recurOnListOfStaticArg(that.getStaticArgs());
        forFnRefOnly(that);
    }

    public void for_RewriteFnRef(_RewriteFnRef that) {
        for_RewriteFnRefDoFirst(that);
        that.getFn().accept(this);
        recurOnListOfStaticArg(that.getStaticArgs());
        for_RewriteFnRefOnly(that);
    }

    public void forOpRef(OpRef that) {
        forOpRefDoFirst(that);
        recurOnListOfQualifiedOpName(that.getOps());
        recurOnListOfStaticArg(that.getStaticArgs());
        forOpRefOnly(that);
    }

    public void forLooseJuxt(LooseJuxt that) {
        forLooseJuxtDoFirst(that);
        recurOnListOfExpr(that.getExprs());
        forLooseJuxtOnly(that);
    }

    public void forTightJuxt(TightJuxt that) {
        forTightJuxtDoFirst(that);
        recurOnListOfExpr(that.getExprs());
        forTightJuxtOnly(that);
    }

    public void forOprExpr(OprExpr that) {
        forOprExprDoFirst(that);
        that.getOp().accept(this);
        recurOnListOfExpr(that.getArgs());
        forOprExprOnly(that);
    }

    public void forChainExpr(ChainExpr that) {
        forChainExprDoFirst(that);
        that.getFirst().accept(this);
        forChainExprOnly(that);
    }

    public void forCoercionInvocation(CoercionInvocation that) {
        forCoercionInvocationDoFirst(that);
        that.getType().accept(this);
        recurOnListOfStaticArg(that.getStaticArgs());
        that.getArg().accept(this);
        forCoercionInvocationOnly(that);
    }

    public void forMethodInvocation(MethodInvocation that) {
        forMethodInvocationDoFirst(that);
        that.getObj().accept(this);
        that.getMethod().accept(this);
        recurOnListOfStaticArg(that.getStaticArgs());
        that.getArg().accept(this);
        forMethodInvocationOnly(that);
    }

    public void forMathPrimary(MathPrimary that) {
        forMathPrimaryDoFirst(that);
        that.getFront().accept(this);
        recurOnListOfMathItem(that.getRest());
        forMathPrimaryOnly(that);
    }

    public void forArrayElement(ArrayElement that) {
        forArrayElementDoFirst(that);
        that.getElement().accept(this);
        forArrayElementOnly(that);
    }

    public void forArrayElements(ArrayElements that) {
        forArrayElementsDoFirst(that);
        recurOnListOfArrayExpr(that.getElements());
        forArrayElementsOnly(that);
    }

    public void forExponentType(ExponentType that) {
        forExponentTypeDoFirst(that);
        that.getBase().accept(this);
        that.getPower().accept(this);
        forExponentTypeOnly(that);
    }

    public void forBaseDim(BaseDim that) {
        forBaseDimDoFirst(that);
        forBaseDimOnly(that);
    }

    public void forDimRef(DimRef that) {
        forDimRefDoFirst(that);
        that.getName().accept(this);
        forDimRefOnly(that);
    }

    public void forProductDim(ProductDim that) {
        forProductDimDoFirst(that);
        that.getMultiplier().accept(this);
        that.getMultiplicand().accept(this);
        forProductDimOnly(that);
    }

    public void forQuotientDim(QuotientDim that) {
        forQuotientDimDoFirst(that);
        that.getNumerator().accept(this);
        that.getDenominator().accept(this);
        forQuotientDimOnly(that);
    }

    public void forExponentDim(ExponentDim that) {
        forExponentDimDoFirst(that);
        that.getBase().accept(this);
        that.getPower().accept(this);
        forExponentDimOnly(that);
    }

    public void forOpDim(OpDim that) {
        forOpDimDoFirst(that);
        that.getVal().accept(this);
        that.getOp().accept(this);
        forOpDimOnly(that);
    }

    public void forArrowType(ArrowType that) {
        forArrowTypeDoFirst(that);
        that.getDomain().accept(this);
        that.getRange().accept(this);
        recurOnOptionOfListOfType(that.getThrowsClause());
        forArrowTypeOnly(that);
    }

    public void for_RewriteGenericArrowType(_RewriteGenericArrowType that) {
        for_RewriteGenericArrowTypeDoFirst(that);
        that.getDomain().accept(this);
        that.getRange().accept(this);
        recurOnOptionOfListOfType(that.getThrowsClause());
        recurOnListOfStaticParam(that.getStaticParams());
        that.getWhere().accept(this);
        for_RewriteGenericArrowTypeOnly(that);
    }

    public void forBottomType(BottomType that) {
        forBottomTypeDoFirst(that);
        forBottomTypeOnly(that);
    }

    public void forIdType(IdType that) {
        forIdTypeDoFirst(that);
        that.getName().accept(this);
        forIdTypeOnly(that);
    }

    public void forInstantiatedType(InstantiatedType that) {
        forInstantiatedTypeDoFirst(that);
        that.getName().accept(this);
        recurOnListOfStaticArg(that.getArgs());
        forInstantiatedTypeOnly(that);
    }

    public void forArrayType(ArrayType that) {
        forArrayTypeDoFirst(that);
        that.getElement().accept(this);
        that.getIndices().accept(this);
        forArrayTypeOnly(that);
    }

    public void forMatrixType(MatrixType that) {
        forMatrixTypeDoFirst(that);
        that.getElement().accept(this);
        recurOnListOfExtentRange(that.getDimensions());
        forMatrixTypeOnly(that);
    }

    public void forTupleType(TupleType that) {
        forTupleTypeDoFirst(that);
        recurOnListOfType(that.getElements());
        forTupleTypeOnly(that);
    }

    public void forArgType(ArgType that) {
        forArgTypeDoFirst(that);
        recurOnListOfType(that.getElements());
        recurOnOptionOfVarargsType(that.getVarargs());
        recurOnListOfKeywordType(that.getKeywords());
        forArgTypeOnly(that);
    }

    public void forVoidType(VoidType that) {
        forVoidTypeDoFirst(that);
        forVoidTypeOnly(that);
    }

    public void forInferenceVarType(InferenceVarType that) {
        forInferenceVarTypeDoFirst(that);
        forInferenceVarTypeOnly(that);
    }

    public void forAndType(AndType that) {
        forAndTypeDoFirst(that);
        that.getFirst().accept(this);
        that.getSecond().accept(this);
        forAndTypeOnly(that);
    }

    public void forOrType(OrType that) {
        forOrTypeDoFirst(that);
        that.getFirst().accept(this);
        that.getSecond().accept(this);
        forOrTypeOnly(that);
    }

    public void forFixedPointType(FixedPointType that) {
        forFixedPointTypeDoFirst(that);
        that.getName().accept(this);
        that.getBody().accept(this);
        forFixedPointTypeOnly(that);
    }

    public void forTaggedDimType(TaggedDimType that) {
        forTaggedDimTypeDoFirst(that);
        that.getType().accept(this);
        that.getDim().accept(this);
        recurOnOptionOfExpr(that.getUnit());
        forTaggedDimTypeOnly(that);
    }

    public void forTaggedUnitType(TaggedUnitType that) {
        forTaggedUnitTypeDoFirst(that);
        that.getType().accept(this);
        that.getUnit().accept(this);
        forTaggedUnitTypeOnly(that);
    }

    public void forIdArg(IdArg that) {
        forIdArgDoFirst(that);
        that.getName().accept(this);
        forIdArgOnly(that);
    }

    public void forTypeArg(TypeArg that) {
        forTypeArgDoFirst(that);
        that.getType().accept(this);
        forTypeArgOnly(that);
    }

    public void forIntArg(IntArg that) {
        forIntArgDoFirst(that);
        that.getVal().accept(this);
        forIntArgOnly(that);
    }

    public void forBoolArg(BoolArg that) {
        forBoolArgDoFirst(that);
        that.getBool().accept(this);
        forBoolArgOnly(that);
    }

    public void forOprArg(OprArg that) {
        forOprArgDoFirst(that);
        that.getName().accept(this);
        forOprArgOnly(that);
    }

    public void forDimArg(DimArg that) {
        forDimArgDoFirst(that);
        that.getDim().accept(this);
        forDimArgOnly(that);
    }

    public void forUnitArg(UnitArg that) {
        forUnitArgDoFirst(that);
        that.getUnit().accept(this);
        forUnitArgOnly(that);
    }

    public void forNumberConstraint(NumberConstraint that) {
        forNumberConstraintDoFirst(that);
        that.getVal().accept(this);
        forNumberConstraintOnly(that);
    }

    public void forIntRef(IntRef that) {
        forIntRefDoFirst(that);
        that.getName().accept(this);
        forIntRefOnly(that);
    }

    public void forSumConstraint(SumConstraint that) {
        forSumConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forSumConstraintOnly(that);
    }

    public void forMinusConstraint(MinusConstraint that) {
        forMinusConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forMinusConstraintOnly(that);
    }

    public void forProductConstraint(ProductConstraint that) {
        forProductConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forProductConstraintOnly(that);
    }

    public void forExponentConstraint(ExponentConstraint that) {
        forExponentConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forExponentConstraintOnly(that);
    }

    public void forBoolConstant(BoolConstant that) {
        forBoolConstantDoFirst(that);
        forBoolConstantOnly(that);
    }

    public void forBoolRef(BoolRef that) {
        forBoolRefDoFirst(that);
        that.getName().accept(this);
        forBoolRefOnly(that);
    }

    public void forNotConstraint(NotConstraint that) {
        forNotConstraintDoFirst(that);
        that.getBool().accept(this);
        forNotConstraintOnly(that);
    }

    public void forOrConstraint(OrConstraint that) {
        forOrConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forOrConstraintOnly(that);
    }

    public void forAndConstraint(AndConstraint that) {
        forAndConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forAndConstraintOnly(that);
    }

    public void forImpliesConstraint(ImpliesConstraint that) {
        forImpliesConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forImpliesConstraintOnly(that);
    }

    public void forBEConstraint(BEConstraint that) {
        forBEConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forBEConstraintOnly(that);
    }

    public void forWhereClause(WhereClause that) {
        forWhereClauseDoFirst(that);
        recurOnListOfWhereBinding(that.getBindings());
        recurOnListOfWhereConstraint(that.getConstraints());
        forWhereClauseOnly(that);
    }

    public void forWhereType(WhereType that) {
        forWhereTypeDoFirst(that);
        that.getName().accept(this);
        recurOnListOfTraitType(that.getSupers());
        forWhereTypeOnly(that);
    }

    public void forWhereNat(WhereNat that) {
        forWhereNatDoFirst(that);
        that.getName().accept(this);
        forWhereNatOnly(that);
    }

    public void forWhereInt(WhereInt that) {
        forWhereIntDoFirst(that);
        that.getName().accept(this);
        forWhereIntOnly(that);
    }

    public void forWhereBool(WhereBool that) {
        forWhereBoolDoFirst(that);
        that.getName().accept(this);
        forWhereBoolOnly(that);
    }

    public void forWhereUnit(WhereUnit that) {
        forWhereUnitDoFirst(that);
        that.getName().accept(this);
        forWhereUnitOnly(that);
    }

    public void forWhereExtends(WhereExtends that) {
        forWhereExtendsDoFirst(that);
        that.getName().accept(this);
        recurOnListOfTraitType(that.getSupers());
        forWhereExtendsOnly(that);
    }

    public void forTypeAlias(TypeAlias that) {
        forTypeAliasDoFirst(that);
        that.getName().accept(this);
        recurOnListOfStaticParam(that.getStaticParams());
        that.getType().accept(this);
        forTypeAliasOnly(that);
    }

    public void forWhereCoerces(WhereCoerces that) {
        forWhereCoercesDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forWhereCoercesOnly(that);
    }

    public void forWhereWidens(WhereWidens that) {
        forWhereWidensDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forWhereWidensOnly(that);
    }

    public void forWhereWidensCoerces(WhereWidensCoerces that) {
        forWhereWidensCoercesDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forWhereWidensCoercesOnly(that);
    }

    public void forWhereEquals(WhereEquals that) {
        forWhereEqualsDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forWhereEqualsOnly(that);
    }

    public void forUnitConstraint(UnitConstraint that) {
        forUnitConstraintDoFirst(that);
        that.getName().accept(this);
        forUnitConstraintOnly(that);
    }

    public void forLEConstraint(LEConstraint that) {
        forLEConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forLEConstraintOnly(that);
    }

    public void forLTConstraint(LTConstraint that) {
        forLTConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forLTConstraintOnly(that);
    }

    public void forGEConstraint(GEConstraint that) {
        forGEConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forGEConstraintOnly(that);
    }

    public void forGTConstraint(GTConstraint that) {
        forGTConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forGTConstraintOnly(that);
    }

    public void forIEConstraint(IEConstraint that) {
        forIEConstraintDoFirst(that);
        that.getLeft().accept(this);
        that.getRight().accept(this);
        forIEConstraintOnly(that);
    }

    public void forBoolConstraintExpr(BoolConstraintExpr that) {
        forBoolConstraintExprDoFirst(that);
        that.getConstraint().accept(this);
        forBoolConstraintExprOnly(that);
    }

    public void forContract(Contract that) {
        forContractDoFirst(that);
        recurOnOptionOfListOfExpr(that.getRequires());
        recurOnOptionOfListOfEnsuresClause(that.getEnsures());
        recurOnOptionOfListOfExpr(that.getInvariants());
        forContractOnly(that);
    }

    public void forEnsuresClause(EnsuresClause that) {
        forEnsuresClauseDoFirst(that);
        that.getPost().accept(this);
        recurOnOptionOfExpr(that.getPre());
        forEnsuresClauseOnly(that);
    }

    public void forModifierAbstract(ModifierAbstract that) {
        forModifierAbstractDoFirst(that);
        forModifierAbstractOnly(that);
    }

    public void forModifierAtomic(ModifierAtomic that) {
        forModifierAtomicDoFirst(that);
        forModifierAtomicOnly(that);
    }

    public void forModifierGetter(ModifierGetter that) {
        forModifierGetterDoFirst(that);
        forModifierGetterOnly(that);
    }

    public void forModifierHidden(ModifierHidden that) {
        forModifierHiddenDoFirst(that);
        forModifierHiddenOnly(that);
    }

    public void forModifierIO(ModifierIO that) {
        forModifierIODoFirst(that);
        forModifierIOOnly(that);
    }

    public void forModifierOverride(ModifierOverride that) {
        forModifierOverrideDoFirst(that);
        forModifierOverrideOnly(that);
    }

    public void forModifierPrivate(ModifierPrivate that) {
        forModifierPrivateDoFirst(that);
        forModifierPrivateOnly(that);
    }

    public void forModifierSettable(ModifierSettable that) {
        forModifierSettableDoFirst(that);
        forModifierSettableOnly(that);
    }

    public void forModifierSetter(ModifierSetter that) {
        forModifierSetterDoFirst(that);
        forModifierSetterOnly(that);
    }

    public void forModifierTest(ModifierTest that) {
        forModifierTestDoFirst(that);
        forModifierTestOnly(that);
    }

    public void forModifierTransient(ModifierTransient that) {
        forModifierTransientDoFirst(that);
        forModifierTransientOnly(that);
    }

    public void forModifierValue(ModifierValue that) {
        forModifierValueDoFirst(that);
        forModifierValueOnly(that);
    }

    public void forModifierVar(ModifierVar that) {
        forModifierVarDoFirst(that);
        forModifierVarOnly(that);
    }

    public void forModifierWidens(ModifierWidens that) {
        forModifierWidensDoFirst(that);
        forModifierWidensOnly(that);
    }

    public void forModifierWrapped(ModifierWrapped that) {
        forModifierWrappedDoFirst(that);
        forModifierWrappedOnly(that);
    }

    public void forOperatorParam(OperatorParam that) {
        forOperatorParamDoFirst(that);
        that.getName().accept(this);
        forOperatorParamOnly(that);
    }

    public void forBoolParam(BoolParam that) {
        forBoolParamDoFirst(that);
        that.getName().accept(this);
        forBoolParamOnly(that);
    }

    public void forDimensionParam(DimensionParam that) {
        forDimensionParamDoFirst(that);
        that.getName().accept(this);
        forDimensionParamOnly(that);
    }

    public void forIntParam(IntParam that) {
        forIntParamDoFirst(that);
        that.getName().accept(this);
        forIntParamOnly(that);
    }

    public void forNatParam(NatParam that) {
        forNatParamDoFirst(that);
        that.getName().accept(this);
        forNatParamOnly(that);
    }

    public void forSimpleTypeParam(SimpleTypeParam that) {
        forSimpleTypeParamDoFirst(that);
        that.getName().accept(this);
        recurOnListOfTraitType(that.getExtendsClause());
        forSimpleTypeParamOnly(that);
    }

    public void forUnitParam(UnitParam that) {
        forUnitParamDoFirst(that);
        that.getName().accept(this);
        recurOnOptionOfType(that.getDim());
        forUnitParamOnly(that);
    }

    public void forAPIName(APIName that) {
        forAPINameDoFirst(that);
        recurOnListOfId(that.getIds());
        forAPINameOnly(that);
    }

    public void forQualifiedIdName(QualifiedIdName that) {
        forQualifiedIdNameDoFirst(that);
        recurOnOptionOfAPIName(that.getApi());
        that.getName().accept(this);
        forQualifiedIdNameOnly(that);
    }

    public void forQualifiedOpName(QualifiedOpName that) {
        forQualifiedOpNameDoFirst(that);
        recurOnOptionOfAPIName(that.getApi());
        that.getName().accept(this);
        forQualifiedOpNameOnly(that);
    }

    public void forId(Id that) {
        forIdDoFirst(that);
        forIdOnly(that);
    }

    public void forOp(Op that) {
        forOpDoFirst(that);
        recurOnOptionOfFixity(that.getFixity());
        forOpOnly(that);
    }

    public void forEnclosing(Enclosing that) {
        forEnclosingDoFirst(that);
        that.getOpen().accept(this);
        that.getClose().accept(this);
        forEnclosingOnly(that);
    }

    public void forAnonymousFnName(AnonymousFnName that) {
        forAnonymousFnNameDoFirst(that);
        forAnonymousFnNameOnly(that);
    }

    public void forConstructorFnName(ConstructorFnName that) {
        forConstructorFnNameDoFirst(that);
        that.getDef().accept(this);
        forConstructorFnNameOnly(that);
    }

    public void forArrayComprehensionClause(ArrayComprehensionClause that) {
        forArrayComprehensionClauseDoFirst(that);
        recurOnListOfExpr(that.getBind());
        that.getInit().accept(this);
        recurOnListOfGeneratorClause(that.getGens());
        forArrayComprehensionClauseOnly(that);
    }

    public void forKeywordExpr(KeywordExpr that) {
        forKeywordExprDoFirst(that);
        that.getName().accept(this);
        that.getInit().accept(this);
        forKeywordExprOnly(that);
    }

    public void forCaseClause(CaseClause that) {
        forCaseClauseDoFirst(that);
        that.getMatch().accept(this);
        that.getBody().accept(this);
        forCaseClauseOnly(that);
    }

    public void forCatch(Catch that) {
        forCatchDoFirst(that);
        that.getName().accept(this);
        recurOnListOfCatchClause(that.getClauses());
        forCatchOnly(that);
    }

    public void forCatchClause(CatchClause that) {
        forCatchClauseDoFirst(that);
        that.getMatch().accept(this);
        that.getBody().accept(this);
        forCatchClauseOnly(that);
    }

    public void forDoFront(DoFront that) {
        forDoFrontDoFirst(that);
        recurOnOptionOfExpr(that.getLoc());
        that.getExpr().accept(this);
        forDoFrontOnly(that);
    }

    public void forIfClause(IfClause that) {
        forIfClauseDoFirst(that);
        that.getTest().accept(this);
        that.getBody().accept(this);
        forIfClauseOnly(that);
    }

    public void forTypecaseClause(TypecaseClause that) {
        forTypecaseClauseDoFirst(that);
        recurOnListOfType(that.getMatch());
        that.getBody().accept(this);
        forTypecaseClauseOnly(that);
    }

    public void forExtentRange(ExtentRange that) {
        forExtentRangeDoFirst(that);
        recurOnOptionOfStaticArg(that.getBase());
        recurOnOptionOfStaticArg(that.getSize());
        forExtentRangeOnly(that);
    }

    public void forGeneratorClause(GeneratorClause that) {
        forGeneratorClauseDoFirst(that);
        recurOnListOfId(that.getBind());
        that.getInit().accept(this);
        forGeneratorClauseOnly(that);
    }

    public void forVarargsExpr(VarargsExpr that) {
        forVarargsExprDoFirst(that);
        that.getVarargs().accept(this);
        forVarargsExprOnly(that);
    }

    public void forVarargsType(VarargsType that) {
        forVarargsTypeDoFirst(that);
        that.getType().accept(this);
        forVarargsTypeOnly(that);
    }

    public void forKeywordType(KeywordType that) {
        forKeywordTypeDoFirst(that);
        that.getName().accept(this);
        that.getType().accept(this);
        forKeywordTypeOnly(that);
    }

    public void forTraitTypeWhere(TraitTypeWhere that) {
        forTraitTypeWhereDoFirst(that);
        that.getType().accept(this);
        that.getWhere().accept(this);
        forTraitTypeWhereOnly(that);
    }

    public void forIndices(Indices that) {
        forIndicesDoFirst(that);
        recurOnListOfExtentRange(that.getExtents());
        forIndicesOnly(that);
    }

    public void forParenthesisDelimitedMI(ParenthesisDelimitedMI that) {
        forParenthesisDelimitedMIDoFirst(that);
        that.getExpr().accept(this);
        forParenthesisDelimitedMIOnly(that);
    }

    public void forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that) {
        forNonParenthesisDelimitedMIDoFirst(that);
        that.getExpr().accept(this);
        forNonParenthesisDelimitedMIOnly(that);
    }

    public void forExponentiationMI(ExponentiationMI that) {
        forExponentiationMIDoFirst(that);
        that.getOp().accept(this);
        recurOnOptionOfExpr(that.getExpr());
        forExponentiationMIOnly(that);
    }

    public void forSubscriptingMI(SubscriptingMI that) {
        forSubscriptingMIDoFirst(that);
        that.getOp().accept(this);
        recurOnListOfExpr(that.getExprs());
        forSubscriptingMIOnly(that);
    }

    public void forInFixity(InFixity that) {
        forInFixityDoFirst(that);
        forInFixityOnly(that);
    }

    public void forPreFixity(PreFixity that) {
        forPreFixityDoFirst(that);
        forPreFixityOnly(that);
    }

    public void forPostFixity(PostFixity that) {
        forPostFixityDoFirst(that);
        forPostFixityOnly(that);
    }

    public void forNoFixity(NoFixity that) {
        forNoFixityDoFirst(that);
        forNoFixityOnly(that);
    }

    public void forMultiFixity(MultiFixity that) {
        forMultiFixityDoFirst(that);
        forMultiFixityOnly(that);
    }

    public void forEnclosingFixity(EnclosingFixity that) {
        forEnclosingFixityDoFirst(that);
        forEnclosingFixityOnly(that);
    }

    public void forBigFixity(BigFixity that) {
        forBigFixityDoFirst(that);
        forBigFixityOnly(that);
    }


    public void recurOnListOfImport(List<Import> that) {
        for (Import elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfExport(List<Export> that) {
        for (Export elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfDecl(List<Decl> that) {
        for (Decl elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfAbsDecl(List<AbsDecl> that) {
        for (AbsDecl elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfSimpleName(List<SimpleName> that) {
        for (SimpleName elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfAliasedSimpleName(List<AliasedSimpleName> that) {
        for (AliasedSimpleName elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfAliasedAPIName(List<AliasedAPIName> that) {
        for (AliasedAPIName elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfSimpleName(Option<SimpleName> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnOptionOfId(Option<Id> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfAPIName(List<APIName> that) {
        for (APIName elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfModifier(List<Modifier> that) {
        for (Modifier elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfStaticParam(List<StaticParam> that) {
        for (StaticParam elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfTraitTypeWhere(List<TraitTypeWhere> that) {
        for (TraitTypeWhere elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfTraitType(List<TraitType> that) {
        for (TraitType elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfListOfTraitType(Option<List<TraitType>> that) {
        if (that.isSome()) { recurOnListOfTraitType(edu.rice.cs.plt.tuple.Option.unwrap(that)); }
    }

    public void recurOnOptionOfListOfParam(Option<List<Param>> that) {
        if (that.isSome()) { recurOnListOfParam(edu.rice.cs.plt.tuple.Option.unwrap(that)); }
    }

    public void recurOnListOfParam(List<Param> that) {
        for (Param elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfLValueBind(List<LValueBind> that) {
        for (LValueBind elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfType(Option<Type> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfExtentRange(List<ExtentRange> that) {
        for (ExtentRange elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfUnpasting(List<Unpasting> that) {
        for (Unpasting elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfExpr(Option<Expr> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfId(List<Id> that) {
        for (Id elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfGeneratorClause(List<GeneratorClause> that) {
        for (GeneratorClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfQualifiedIdName(List<QualifiedIdName> that) {
        for (QualifiedIdName elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfGrammarMemberDecl(List<GrammarMemberDecl> that) {
        for (GrammarMemberDecl elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfTraitType(Option<TraitType> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnOptionOfModifier(Option<? extends Modifier> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfSyntaxDef(List<SyntaxDef> that) {
        for (SyntaxDef elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfSyntaxSymbol(List<SyntaxSymbol> that) {
        for (SyntaxSymbol elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfCharacterSymbol(List<CharacterSymbol> that) {
        for (CharacterSymbol elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfLHS(List<LHS> that) {
        for (LHS elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfOp(Option<Op> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfExpr(List<Expr> that) {
        for (Expr elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfCaseClause(List<CaseClause> that) {
        for (CaseClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfBlock(Option<Block> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfDoFront(List<DoFront> that) {
        for (DoFront elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfIfClause(List<IfClause> that) {
        for (IfClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfStaticArg(List<StaticArg> that) {
        for (StaticArg elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfCatch(Option<Catch> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnOptionOfVarargsExpr(Option<VarargsExpr> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfKeywordExpr(List<KeywordExpr> that) {
        for (KeywordExpr elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfTypecaseClause(List<TypecaseClause> that) {
        for (TypecaseClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfArrayComprehensionClause(List<ArrayComprehensionClause> that) {
        for (ArrayComprehensionClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfFnDef(List<FnDef> that) {
        for (FnDef elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfLValue(List<LValue> that) {
        for (LValue elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfEnclosing(Option<Enclosing> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfQualifiedOpName(List<QualifiedOpName> that) {
        for (QualifiedOpName elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfMathItem(List<MathItem> that) {
        for (MathItem elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfArrayExpr(List<ArrayExpr> that) {
        for (ArrayExpr elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfListOfType(Option<List<Type>> that) {
        if (that.isSome()) { recurOnListOfType(edu.rice.cs.plt.tuple.Option.unwrap(that)); }
    }

    public void recurOnListOfType(List<Type> that) {
        for (Type elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfVarargsType(Option<VarargsType> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfKeywordType(List<KeywordType> that) {
        for (KeywordType elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfWhereBinding(List<WhereBinding> that) {
        for (WhereBinding elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnListOfWhereConstraint(List<WhereConstraint> that) {
        for (WhereConstraint elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfListOfExpr(Option<List<Expr>> that) {
        if (that.isSome()) { recurOnListOfExpr(edu.rice.cs.plt.tuple.Option.unwrap(that)); }
    }

    public void recurOnOptionOfListOfEnsuresClause(Option<List<EnsuresClause>> that) {
        if (that.isSome()) { recurOnListOfEnsuresClause(edu.rice.cs.plt.tuple.Option.unwrap(that)); }
    }

    public void recurOnListOfEnsuresClause(List<EnsuresClause> that) {
        for (EnsuresClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfAPIName(Option<APIName> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnOptionOfFixity(Option<Fixity> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }

    public void recurOnListOfCatchClause(List<CatchClause> that) {
        for (CatchClause elt : that) {
            elt.accept(this);
        }
    }

    public void recurOnOptionOfStaticArg(Option<StaticArg> that) {
        if (that.isSome()) { 
        edu.rice.cs.plt.tuple.Option.unwrap(that).accept(this); }
    }
}
