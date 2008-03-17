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

/** An abstract void visitor over Node that provides a Runnable1 run method;
  * all visit methods are left unimplemented. */
public abstract class NodeVisitorRunnable1 implements edu.rice.cs.plt.lambda.Runnable1<Node>, NodeVisitor_void {

    public void run(Node that) {
        that.accept(this);
    }


    /** Process an instance of Component. */
    public abstract void forComponent(Component that);

    /** Process an instance of Api. */
    public abstract void forApi(Api that);

    /** Process an instance of ImportStar. */
    public abstract void forImportStar(ImportStar that);

    /** Process an instance of ImportNames. */
    public abstract void forImportNames(ImportNames that);

    /** Process an instance of ImportApi. */
    public abstract void forImportApi(ImportApi that);

    /** Process an instance of AliasedSimpleName. */
    public abstract void forAliasedSimpleName(AliasedSimpleName that);

    /** Process an instance of AliasedAPIName. */
    public abstract void forAliasedAPIName(AliasedAPIName that);

    /** Process an instance of Export. */
    public abstract void forExport(Export that);

    /** Process an instance of AbsTraitDecl. */
    public abstract void forAbsTraitDecl(AbsTraitDecl that);

    /** Process an instance of TraitDecl. */
    public abstract void forTraitDecl(TraitDecl that);

    /** Process an instance of AbsObjectDecl. */
    public abstract void forAbsObjectDecl(AbsObjectDecl that);

    /** Process an instance of ObjectDecl. */
    public abstract void forObjectDecl(ObjectDecl that);

    /** Process an instance of AbsVarDecl. */
    public abstract void forAbsVarDecl(AbsVarDecl that);

    /** Process an instance of VarDecl. */
    public abstract void forVarDecl(VarDecl that);

    /** Process an instance of LValueBind. */
    public abstract void forLValueBind(LValueBind that);

    /** Process an instance of UnpastingBind. */
    public abstract void forUnpastingBind(UnpastingBind that);

    /** Process an instance of UnpastingSplit. */
    public abstract void forUnpastingSplit(UnpastingSplit that);

    /** Process an instance of AbsFnDecl. */
    public abstract void forAbsFnDecl(AbsFnDecl that);

    /** Process an instance of FnDef. */
    public abstract void forFnDef(FnDef that);

    /** Process an instance of NormalParam. */
    public abstract void forNormalParam(NormalParam that);

    /** Process an instance of VarargsParam. */
    public abstract void forVarargsParam(VarargsParam that);

    /** Process an instance of DimDecl. */
    public abstract void forDimDecl(DimDecl that);

    /** Process an instance of UnitDecl. */
    public abstract void forUnitDecl(UnitDecl that);

    /** Process an instance of TestDecl. */
    public abstract void forTestDecl(TestDecl that);

    /** Process an instance of PropertyDecl. */
    public abstract void forPropertyDecl(PropertyDecl that);

    /** Process an instance of AbsExternalSyntax. */
    public abstract void forAbsExternalSyntax(AbsExternalSyntax that);

    /** Process an instance of ExternalSyntax. */
    public abstract void forExternalSyntax(ExternalSyntax that);

    /** Process an instance of GrammarDef. */
    public abstract void forGrammarDef(GrammarDef that);

    /** Process an instance of NonterminalDef. */
    public abstract void forNonterminalDef(NonterminalDef that);

    /** Process an instance of NonterminalExtensionDef. */
    public abstract void forNonterminalExtensionDef(NonterminalExtensionDef that);

    /** Process an instance of _TerminalDef. */
    public abstract void for_TerminalDef(_TerminalDef that);

    /** Process an instance of SyntaxDef. */
    public abstract void forSyntaxDef(SyntaxDef that);

    /** Process an instance of PrefixedSymbol. */
    public abstract void forPrefixedSymbol(PrefixedSymbol that);

    /** Process an instance of OptionalSymbol. */
    public abstract void forOptionalSymbol(OptionalSymbol that);

    /** Process an instance of RepeatSymbol. */
    public abstract void forRepeatSymbol(RepeatSymbol that);

    /** Process an instance of RepeatOneOrMoreSymbol. */
    public abstract void forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that);

    /** Process an instance of NoWhitespaceSymbol. */
    public abstract void forNoWhitespaceSymbol(NoWhitespaceSymbol that);

    /** Process an instance of WhitespaceSymbol. */
    public abstract void forWhitespaceSymbol(WhitespaceSymbol that);

    /** Process an instance of TabSymbol. */
    public abstract void forTabSymbol(TabSymbol that);

    /** Process an instance of FormfeedSymbol. */
    public abstract void forFormfeedSymbol(FormfeedSymbol that);

    /** Process an instance of CarriageReturnSymbol. */
    public abstract void forCarriageReturnSymbol(CarriageReturnSymbol that);

    /** Process an instance of BackspaceSymbol. */
    public abstract void forBackspaceSymbol(BackspaceSymbol that);

    /** Process an instance of NewlineSymbol. */
    public abstract void forNewlineSymbol(NewlineSymbol that);

    /** Process an instance of BreaklineSymbol. */
    public abstract void forBreaklineSymbol(BreaklineSymbol that);

    /** Process an instance of ItemSymbol. */
    public abstract void forItemSymbol(ItemSymbol that);

    /** Process an instance of NonterminalSymbol. */
    public abstract void forNonterminalSymbol(NonterminalSymbol that);

    /** Process an instance of KeywordSymbol. */
    public abstract void forKeywordSymbol(KeywordSymbol that);

    /** Process an instance of TokenSymbol. */
    public abstract void forTokenSymbol(TokenSymbol that);

    /** Process an instance of NotPredicateSymbol. */
    public abstract void forNotPredicateSymbol(NotPredicateSymbol that);

    /** Process an instance of AndPredicateSymbol. */
    public abstract void forAndPredicateSymbol(AndPredicateSymbol that);

    /** Process an instance of CharacterClassSymbol. */
    public abstract void forCharacterClassSymbol(CharacterClassSymbol that);

    /** Process an instance of CharSymbol. */
    public abstract void forCharSymbol(CharSymbol that);

    /** Process an instance of CharacterInterval. */
    public abstract void forCharacterInterval(CharacterInterval that);

    /** Process an instance of AsExpr. */
    public abstract void forAsExpr(AsExpr that);

    /** Process an instance of AsIfExpr. */
    public abstract void forAsIfExpr(AsIfExpr that);

    /** Process an instance of Assignment. */
    public abstract void forAssignment(Assignment that);

    /** Process an instance of Block. */
    public abstract void forBlock(Block that);

    /** Process an instance of CaseExpr. */
    public abstract void forCaseExpr(CaseExpr that);

    /** Process an instance of Do. */
    public abstract void forDo(Do that);

    /** Process an instance of For. */
    public abstract void forFor(For that);

    /** Process an instance of If. */
    public abstract void forIf(If that);

    /** Process an instance of Label. */
    public abstract void forLabel(Label that);

    /** Process an instance of ObjectExpr. */
    public abstract void forObjectExpr(ObjectExpr that);

    /** Process an instance of _RewriteObjectExpr. */
    public abstract void for_RewriteObjectExpr(_RewriteObjectExpr that);

    /** Process an instance of Try. */
    public abstract void forTry(Try that);

    /** Process an instance of TupleExpr. */
    public abstract void forTupleExpr(TupleExpr that);

    /** Process an instance of ArgExpr. */
    public abstract void forArgExpr(ArgExpr that);

    /** Process an instance of Typecase. */
    public abstract void forTypecase(Typecase that);

    /** Process an instance of While. */
    public abstract void forWhile(While that);

    /** Process an instance of Accumulator. */
    public abstract void forAccumulator(Accumulator that);

    /** Process an instance of ArrayComprehension. */
    public abstract void forArrayComprehension(ArrayComprehension that);

    /** Process an instance of AtomicExpr. */
    public abstract void forAtomicExpr(AtomicExpr that);

    /** Process an instance of Exit. */
    public abstract void forExit(Exit that);

    /** Process an instance of Spawn. */
    public abstract void forSpawn(Spawn that);

    /** Process an instance of Throw. */
    public abstract void forThrow(Throw that);

    /** Process an instance of TryAtomicExpr. */
    public abstract void forTryAtomicExpr(TryAtomicExpr that);

    /** Process an instance of FnExpr. */
    public abstract void forFnExpr(FnExpr that);

    /** Process an instance of LetFn. */
    public abstract void forLetFn(LetFn that);

    /** Process an instance of LocalVarDecl. */
    public abstract void forLocalVarDecl(LocalVarDecl that);

    /** Process an instance of GeneratedExpr. */
    public abstract void forGeneratedExpr(GeneratedExpr that);

    /** Process an instance of SubscriptExpr. */
    public abstract void forSubscriptExpr(SubscriptExpr that);

    /** Process an instance of FloatLiteralExpr. */
    public abstract void forFloatLiteralExpr(FloatLiteralExpr that);

    /** Process an instance of IntLiteralExpr. */
    public abstract void forIntLiteralExpr(IntLiteralExpr that);

    /** Process an instance of CharLiteralExpr. */
    public abstract void forCharLiteralExpr(CharLiteralExpr that);

    /** Process an instance of StringLiteralExpr. */
    public abstract void forStringLiteralExpr(StringLiteralExpr that);

    /** Process an instance of VoidLiteralExpr. */
    public abstract void forVoidLiteralExpr(VoidLiteralExpr that);

    /** Process an instance of VarRef. */
    public abstract void forVarRef(VarRef that);

    /** Process an instance of FieldRef. */
    public abstract void forFieldRef(FieldRef that);

    /** Process an instance of FieldRefForSure. */
    public abstract void forFieldRefForSure(FieldRefForSure that);

    /** Process an instance of _RewriteFieldRef. */
    public abstract void for_RewriteFieldRef(_RewriteFieldRef that);

    /** Process an instance of FnRef. */
    public abstract void forFnRef(FnRef that);

    /** Process an instance of _RewriteFnRef. */
    public abstract void for_RewriteFnRef(_RewriteFnRef that);

    /** Process an instance of OpRef. */
    public abstract void forOpRef(OpRef that);

    /** Process an instance of LooseJuxt. */
    public abstract void forLooseJuxt(LooseJuxt that);

    /** Process an instance of TightJuxt. */
    public abstract void forTightJuxt(TightJuxt that);

    /** Process an instance of OprExpr. */
    public abstract void forOprExpr(OprExpr that);

    /** Process an instance of ChainExpr. */
    public abstract void forChainExpr(ChainExpr that);

    /** Process an instance of CoercionInvocation. */
    public abstract void forCoercionInvocation(CoercionInvocation that);

    /** Process an instance of MethodInvocation. */
    public abstract void forMethodInvocation(MethodInvocation that);

    /** Process an instance of MathPrimary. */
    public abstract void forMathPrimary(MathPrimary that);

    /** Process an instance of ArrayElement. */
    public abstract void forArrayElement(ArrayElement that);

    /** Process an instance of ArrayElements. */
    public abstract void forArrayElements(ArrayElements that);

    /** Process an instance of ExponentType. */
    public abstract void forExponentType(ExponentType that);

    /** Process an instance of BaseDim. */
    public abstract void forBaseDim(BaseDim that);

    /** Process an instance of DimRef. */
    public abstract void forDimRef(DimRef that);

    /** Process an instance of ProductDim. */
    public abstract void forProductDim(ProductDim that);

    /** Process an instance of QuotientDim. */
    public abstract void forQuotientDim(QuotientDim that);

    /** Process an instance of ExponentDim. */
    public abstract void forExponentDim(ExponentDim that);

    /** Process an instance of OpDim. */
    public abstract void forOpDim(OpDim that);

    /** Process an instance of ArrowType. */
    public abstract void forArrowType(ArrowType that);

    /** Process an instance of _RewriteGenericArrowType. */
    public abstract void for_RewriteGenericArrowType(_RewriteGenericArrowType that);

    /** Process an instance of BottomType. */
    public abstract void forBottomType(BottomType that);

    /** Process an instance of IdType. */
    public abstract void forIdType(IdType that);

    /** Process an instance of InstantiatedType. */
    public abstract void forInstantiatedType(InstantiatedType that);

    /** Process an instance of ArrayType. */
    public abstract void forArrayType(ArrayType that);

    /** Process an instance of MatrixType. */
    public abstract void forMatrixType(MatrixType that);

    /** Process an instance of TupleType. */
    public abstract void forTupleType(TupleType that);

    /** Process an instance of ArgType. */
    public abstract void forArgType(ArgType that);

    /** Process an instance of VoidType. */
    public abstract void forVoidType(VoidType that);

    /** Process an instance of InferenceVarType. */
    public abstract void forInferenceVarType(InferenceVarType that);

    /** Process an instance of AndType. */
    public abstract void forAndType(AndType that);

    /** Process an instance of OrType. */
    public abstract void forOrType(OrType that);

    /** Process an instance of FixedPointType. */
    public abstract void forFixedPointType(FixedPointType that);

    /** Process an instance of TaggedDimType. */
    public abstract void forTaggedDimType(TaggedDimType that);

    /** Process an instance of TaggedUnitType. */
    public abstract void forTaggedUnitType(TaggedUnitType that);

    /** Process an instance of IdArg. */
    public abstract void forIdArg(IdArg that);

    /** Process an instance of TypeArg. */
    public abstract void forTypeArg(TypeArg that);

    /** Process an instance of IntArg. */
    public abstract void forIntArg(IntArg that);

    /** Process an instance of BoolArg. */
    public abstract void forBoolArg(BoolArg that);

    /** Process an instance of OprArg. */
    public abstract void forOprArg(OprArg that);

    /** Process an instance of DimArg. */
    public abstract void forDimArg(DimArg that);

    /** Process an instance of UnitArg. */
    public abstract void forUnitArg(UnitArg that);

    /** Process an instance of NumberConstraint. */
    public abstract void forNumberConstraint(NumberConstraint that);

    /** Process an instance of IntRef. */
    public abstract void forIntRef(IntRef that);

    /** Process an instance of SumConstraint. */
    public abstract void forSumConstraint(SumConstraint that);

    /** Process an instance of MinusConstraint. */
    public abstract void forMinusConstraint(MinusConstraint that);

    /** Process an instance of ProductConstraint. */
    public abstract void forProductConstraint(ProductConstraint that);

    /** Process an instance of ExponentConstraint. */
    public abstract void forExponentConstraint(ExponentConstraint that);

    /** Process an instance of BoolConstant. */
    public abstract void forBoolConstant(BoolConstant that);

    /** Process an instance of BoolRef. */
    public abstract void forBoolRef(BoolRef that);

    /** Process an instance of NotConstraint. */
    public abstract void forNotConstraint(NotConstraint that);

    /** Process an instance of OrConstraint. */
    public abstract void forOrConstraint(OrConstraint that);

    /** Process an instance of AndConstraint. */
    public abstract void forAndConstraint(AndConstraint that);

    /** Process an instance of ImpliesConstraint. */
    public abstract void forImpliesConstraint(ImpliesConstraint that);

    /** Process an instance of BEConstraint. */
    public abstract void forBEConstraint(BEConstraint that);

    /** Process an instance of WhereClause. */
    public abstract void forWhereClause(WhereClause that);

    /** Process an instance of WhereType. */
    public abstract void forWhereType(WhereType that);

    /** Process an instance of WhereNat. */
    public abstract void forWhereNat(WhereNat that);

    /** Process an instance of WhereInt. */
    public abstract void forWhereInt(WhereInt that);

    /** Process an instance of WhereBool. */
    public abstract void forWhereBool(WhereBool that);

    /** Process an instance of WhereUnit. */
    public abstract void forWhereUnit(WhereUnit that);

    /** Process an instance of WhereExtends. */
    public abstract void forWhereExtends(WhereExtends that);

    /** Process an instance of TypeAlias. */
    public abstract void forTypeAlias(TypeAlias that);

    /** Process an instance of WhereCoerces. */
    public abstract void forWhereCoerces(WhereCoerces that);

    /** Process an instance of WhereWidens. */
    public abstract void forWhereWidens(WhereWidens that);

    /** Process an instance of WhereWidensCoerces. */
    public abstract void forWhereWidensCoerces(WhereWidensCoerces that);

    /** Process an instance of WhereEquals. */
    public abstract void forWhereEquals(WhereEquals that);

    /** Process an instance of UnitConstraint. */
    public abstract void forUnitConstraint(UnitConstraint that);

    /** Process an instance of LEConstraint. */
    public abstract void forLEConstraint(LEConstraint that);

    /** Process an instance of LTConstraint. */
    public abstract void forLTConstraint(LTConstraint that);

    /** Process an instance of GEConstraint. */
    public abstract void forGEConstraint(GEConstraint that);

    /** Process an instance of GTConstraint. */
    public abstract void forGTConstraint(GTConstraint that);

    /** Process an instance of IEConstraint. */
    public abstract void forIEConstraint(IEConstraint that);

    /** Process an instance of BoolConstraintExpr. */
    public abstract void forBoolConstraintExpr(BoolConstraintExpr that);

    /** Process an instance of Contract. */
    public abstract void forContract(Contract that);

    /** Process an instance of EnsuresClause. */
    public abstract void forEnsuresClause(EnsuresClause that);

    /** Process an instance of ModifierAbstract. */
    public abstract void forModifierAbstract(ModifierAbstract that);

    /** Process an instance of ModifierAtomic. */
    public abstract void forModifierAtomic(ModifierAtomic that);

    /** Process an instance of ModifierGetter. */
    public abstract void forModifierGetter(ModifierGetter that);

    /** Process an instance of ModifierHidden. */
    public abstract void forModifierHidden(ModifierHidden that);

    /** Process an instance of ModifierIO. */
    public abstract void forModifierIO(ModifierIO that);

    /** Process an instance of ModifierOverride. */
    public abstract void forModifierOverride(ModifierOverride that);

    /** Process an instance of ModifierPrivate. */
    public abstract void forModifierPrivate(ModifierPrivate that);

    /** Process an instance of ModifierSettable. */
    public abstract void forModifierSettable(ModifierSettable that);

    /** Process an instance of ModifierSetter. */
    public abstract void forModifierSetter(ModifierSetter that);

    /** Process an instance of ModifierTest. */
    public abstract void forModifierTest(ModifierTest that);

    /** Process an instance of ModifierTransient. */
    public abstract void forModifierTransient(ModifierTransient that);

    /** Process an instance of ModifierValue. */
    public abstract void forModifierValue(ModifierValue that);

    /** Process an instance of ModifierVar. */
    public abstract void forModifierVar(ModifierVar that);

    /** Process an instance of ModifierWidens. */
    public abstract void forModifierWidens(ModifierWidens that);

    /** Process an instance of ModifierWrapped. */
    public abstract void forModifierWrapped(ModifierWrapped that);

    /** Process an instance of OperatorParam. */
    public abstract void forOperatorParam(OperatorParam that);

    /** Process an instance of BoolParam. */
    public abstract void forBoolParam(BoolParam that);

    /** Process an instance of DimensionParam. */
    public abstract void forDimensionParam(DimensionParam that);

    /** Process an instance of IntParam. */
    public abstract void forIntParam(IntParam that);

    /** Process an instance of NatParam. */
    public abstract void forNatParam(NatParam that);

    /** Process an instance of SimpleTypeParam. */
    public abstract void forSimpleTypeParam(SimpleTypeParam that);

    /** Process an instance of UnitParam. */
    public abstract void forUnitParam(UnitParam that);

    /** Process an instance of APIName. */
    public abstract void forAPIName(APIName that);

    /** Process an instance of QualifiedIdName. */
    public abstract void forQualifiedIdName(QualifiedIdName that);

    /** Process an instance of QualifiedOpName. */
    public abstract void forQualifiedOpName(QualifiedOpName that);

    /** Process an instance of Id. */
    public abstract void forId(Id that);

    /** Process an instance of Op. */
    public abstract void forOp(Op that);

    /** Process an instance of Enclosing. */
    public abstract void forEnclosing(Enclosing that);

    /** Process an instance of AnonymousFnName. */
    public abstract void forAnonymousFnName(AnonymousFnName that);

    /** Process an instance of ConstructorFnName. */
    public abstract void forConstructorFnName(ConstructorFnName that);

    /** Process an instance of ArrayComprehensionClause. */
    public abstract void forArrayComprehensionClause(ArrayComprehensionClause that);

    /** Process an instance of KeywordExpr. */
    public abstract void forKeywordExpr(KeywordExpr that);

    /** Process an instance of CaseClause. */
    public abstract void forCaseClause(CaseClause that);

    /** Process an instance of Catch. */
    public abstract void forCatch(Catch that);

    /** Process an instance of CatchClause. */
    public abstract void forCatchClause(CatchClause that);

    /** Process an instance of DoFront. */
    public abstract void forDoFront(DoFront that);

    /** Process an instance of IfClause. */
    public abstract void forIfClause(IfClause that);

    /** Process an instance of TypecaseClause. */
    public abstract void forTypecaseClause(TypecaseClause that);

    /** Process an instance of ExtentRange. */
    public abstract void forExtentRange(ExtentRange that);

    /** Process an instance of GeneratorClause. */
    public abstract void forGeneratorClause(GeneratorClause that);

    /** Process an instance of VarargsExpr. */
    public abstract void forVarargsExpr(VarargsExpr that);

    /** Process an instance of VarargsType. */
    public abstract void forVarargsType(VarargsType that);

    /** Process an instance of KeywordType. */
    public abstract void forKeywordType(KeywordType that);

    /** Process an instance of TraitTypeWhere. */
    public abstract void forTraitTypeWhere(TraitTypeWhere that);

    /** Process an instance of Indices. */
    public abstract void forIndices(Indices that);

    /** Process an instance of ParenthesisDelimitedMI. */
    public abstract void forParenthesisDelimitedMI(ParenthesisDelimitedMI that);

    /** Process an instance of NonParenthesisDelimitedMI. */
    public abstract void forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that);

    /** Process an instance of ExponentiationMI. */
    public abstract void forExponentiationMI(ExponentiationMI that);

    /** Process an instance of SubscriptingMI. */
    public abstract void forSubscriptingMI(SubscriptingMI that);

    /** Process an instance of InFixity. */
    public abstract void forInFixity(InFixity that);

    /** Process an instance of PreFixity. */
    public abstract void forPreFixity(PreFixity that);

    /** Process an instance of PostFixity. */
    public abstract void forPostFixity(PostFixity that);

    /** Process an instance of NoFixity. */
    public abstract void forNoFixity(NoFixity that);

    /** Process an instance of MultiFixity. */
    public abstract void forMultiFixity(MultiFixity that);

    /** Process an instance of EnclosingFixity. */
    public abstract void forEnclosingFixity(EnclosingFixity that);

    /** Process an instance of BigFixity. */
    public abstract void forBigFixity(BigFixity that);
}
