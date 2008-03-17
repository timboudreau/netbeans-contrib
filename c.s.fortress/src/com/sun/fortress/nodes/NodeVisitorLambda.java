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

/** An abstract visitor over Node that provides a lambda value method;
  * all visit methods are left unimplemented. */
public abstract class NodeVisitorLambda<RetType> implements edu.rice.cs.plt.lambda.Lambda<Node, RetType>, NodeVisitor<RetType> {

    public RetType value(Node that) {
        return that.accept(this);
    }


    /** Process an instance of Component. */
    public abstract RetType forComponent(Component that);

    /** Process an instance of Api. */
    public abstract RetType forApi(Api that);

    /** Process an instance of ImportStar. */
    public abstract RetType forImportStar(ImportStar that);

    /** Process an instance of ImportNames. */
    public abstract RetType forImportNames(ImportNames that);

    /** Process an instance of ImportApi. */
    public abstract RetType forImportApi(ImportApi that);

    /** Process an instance of AliasedSimpleName. */
    public abstract RetType forAliasedSimpleName(AliasedSimpleName that);

    /** Process an instance of AliasedAPIName. */
    public abstract RetType forAliasedAPIName(AliasedAPIName that);

    /** Process an instance of Export. */
    public abstract RetType forExport(Export that);

    /** Process an instance of AbsTraitDecl. */
    public abstract RetType forAbsTraitDecl(AbsTraitDecl that);

    /** Process an instance of TraitDecl. */
    public abstract RetType forTraitDecl(TraitDecl that);

    /** Process an instance of AbsObjectDecl. */
    public abstract RetType forAbsObjectDecl(AbsObjectDecl that);

    /** Process an instance of ObjectDecl. */
    public abstract RetType forObjectDecl(ObjectDecl that);

    /** Process an instance of AbsVarDecl. */
    public abstract RetType forAbsVarDecl(AbsVarDecl that);

    /** Process an instance of VarDecl. */
    public abstract RetType forVarDecl(VarDecl that);

    /** Process an instance of LValueBind. */
    public abstract RetType forLValueBind(LValueBind that);

    /** Process an instance of UnpastingBind. */
    public abstract RetType forUnpastingBind(UnpastingBind that);

    /** Process an instance of UnpastingSplit. */
    public abstract RetType forUnpastingSplit(UnpastingSplit that);

    /** Process an instance of AbsFnDecl. */
    public abstract RetType forAbsFnDecl(AbsFnDecl that);

    /** Process an instance of FnDef. */
    public abstract RetType forFnDef(FnDef that);

    /** Process an instance of NormalParam. */
    public abstract RetType forNormalParam(NormalParam that);

    /** Process an instance of VarargsParam. */
    public abstract RetType forVarargsParam(VarargsParam that);

    /** Process an instance of DimDecl. */
    public abstract RetType forDimDecl(DimDecl that);

    /** Process an instance of UnitDecl. */
    public abstract RetType forUnitDecl(UnitDecl that);

    /** Process an instance of TestDecl. */
    public abstract RetType forTestDecl(TestDecl that);

    /** Process an instance of PropertyDecl. */
    public abstract RetType forPropertyDecl(PropertyDecl that);

    /** Process an instance of AbsExternalSyntax. */
    public abstract RetType forAbsExternalSyntax(AbsExternalSyntax that);

    /** Process an instance of ExternalSyntax. */
    public abstract RetType forExternalSyntax(ExternalSyntax that);

    /** Process an instance of GrammarDef. */
    public abstract RetType forGrammarDef(GrammarDef that);

    /** Process an instance of NonterminalDef. */
    public abstract RetType forNonterminalDef(NonterminalDef that);

    /** Process an instance of NonterminalExtensionDef. */
    public abstract RetType forNonterminalExtensionDef(NonterminalExtensionDef that);

    /** Process an instance of _TerminalDef. */
    public abstract RetType for_TerminalDef(_TerminalDef that);

    /** Process an instance of SyntaxDef. */
    public abstract RetType forSyntaxDef(SyntaxDef that);

    /** Process an instance of PrefixedSymbol. */
    public abstract RetType forPrefixedSymbol(PrefixedSymbol that);

    /** Process an instance of OptionalSymbol. */
    public abstract RetType forOptionalSymbol(OptionalSymbol that);

    /** Process an instance of RepeatSymbol. */
    public abstract RetType forRepeatSymbol(RepeatSymbol that);

    /** Process an instance of RepeatOneOrMoreSymbol. */
    public abstract RetType forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that);

    /** Process an instance of NoWhitespaceSymbol. */
    public abstract RetType forNoWhitespaceSymbol(NoWhitespaceSymbol that);

    /** Process an instance of WhitespaceSymbol. */
    public abstract RetType forWhitespaceSymbol(WhitespaceSymbol that);

    /** Process an instance of TabSymbol. */
    public abstract RetType forTabSymbol(TabSymbol that);

    /** Process an instance of FormfeedSymbol. */
    public abstract RetType forFormfeedSymbol(FormfeedSymbol that);

    /** Process an instance of CarriageReturnSymbol. */
    public abstract RetType forCarriageReturnSymbol(CarriageReturnSymbol that);

    /** Process an instance of BackspaceSymbol. */
    public abstract RetType forBackspaceSymbol(BackspaceSymbol that);

    /** Process an instance of NewlineSymbol. */
    public abstract RetType forNewlineSymbol(NewlineSymbol that);

    /** Process an instance of BreaklineSymbol. */
    public abstract RetType forBreaklineSymbol(BreaklineSymbol that);

    /** Process an instance of ItemSymbol. */
    public abstract RetType forItemSymbol(ItemSymbol that);

    /** Process an instance of NonterminalSymbol. */
    public abstract RetType forNonterminalSymbol(NonterminalSymbol that);

    /** Process an instance of KeywordSymbol. */
    public abstract RetType forKeywordSymbol(KeywordSymbol that);

    /** Process an instance of TokenSymbol. */
    public abstract RetType forTokenSymbol(TokenSymbol that);

    /** Process an instance of NotPredicateSymbol. */
    public abstract RetType forNotPredicateSymbol(NotPredicateSymbol that);

    /** Process an instance of AndPredicateSymbol. */
    public abstract RetType forAndPredicateSymbol(AndPredicateSymbol that);

    /** Process an instance of CharacterClassSymbol. */
    public abstract RetType forCharacterClassSymbol(CharacterClassSymbol that);

    /** Process an instance of CharSymbol. */
    public abstract RetType forCharSymbol(CharSymbol that);

    /** Process an instance of CharacterInterval. */
    public abstract RetType forCharacterInterval(CharacterInterval that);

    /** Process an instance of AsExpr. */
    public abstract RetType forAsExpr(AsExpr that);

    /** Process an instance of AsIfExpr. */
    public abstract RetType forAsIfExpr(AsIfExpr that);

    /** Process an instance of Assignment. */
    public abstract RetType forAssignment(Assignment that);

    /** Process an instance of Block. */
    public abstract RetType forBlock(Block that);

    /** Process an instance of CaseExpr. */
    public abstract RetType forCaseExpr(CaseExpr that);

    /** Process an instance of Do. */
    public abstract RetType forDo(Do that);

    /** Process an instance of For. */
    public abstract RetType forFor(For that);

    /** Process an instance of If. */
    public abstract RetType forIf(If that);

    /** Process an instance of Label. */
    public abstract RetType forLabel(Label that);

    /** Process an instance of ObjectExpr. */
    public abstract RetType forObjectExpr(ObjectExpr that);

    /** Process an instance of _RewriteObjectExpr. */
    public abstract RetType for_RewriteObjectExpr(_RewriteObjectExpr that);

    /** Process an instance of Try. */
    public abstract RetType forTry(Try that);

    /** Process an instance of TupleExpr. */
    public abstract RetType forTupleExpr(TupleExpr that);

    /** Process an instance of ArgExpr. */
    public abstract RetType forArgExpr(ArgExpr that);

    /** Process an instance of Typecase. */
    public abstract RetType forTypecase(Typecase that);

    /** Process an instance of While. */
    public abstract RetType forWhile(While that);

    /** Process an instance of Accumulator. */
    public abstract RetType forAccumulator(Accumulator that);

    /** Process an instance of ArrayComprehension. */
    public abstract RetType forArrayComprehension(ArrayComprehension that);

    /** Process an instance of AtomicExpr. */
    public abstract RetType forAtomicExpr(AtomicExpr that);

    /** Process an instance of Exit. */
    public abstract RetType forExit(Exit that);

    /** Process an instance of Spawn. */
    public abstract RetType forSpawn(Spawn that);

    /** Process an instance of Throw. */
    public abstract RetType forThrow(Throw that);

    /** Process an instance of TryAtomicExpr. */
    public abstract RetType forTryAtomicExpr(TryAtomicExpr that);

    /** Process an instance of FnExpr. */
    public abstract RetType forFnExpr(FnExpr that);

    /** Process an instance of LetFn. */
    public abstract RetType forLetFn(LetFn that);

    /** Process an instance of LocalVarDecl. */
    public abstract RetType forLocalVarDecl(LocalVarDecl that);

    /** Process an instance of GeneratedExpr. */
    public abstract RetType forGeneratedExpr(GeneratedExpr that);

    /** Process an instance of SubscriptExpr. */
    public abstract RetType forSubscriptExpr(SubscriptExpr that);

    /** Process an instance of FloatLiteralExpr. */
    public abstract RetType forFloatLiteralExpr(FloatLiteralExpr that);

    /** Process an instance of IntLiteralExpr. */
    public abstract RetType forIntLiteralExpr(IntLiteralExpr that);

    /** Process an instance of CharLiteralExpr. */
    public abstract RetType forCharLiteralExpr(CharLiteralExpr that);

    /** Process an instance of StringLiteralExpr. */
    public abstract RetType forStringLiteralExpr(StringLiteralExpr that);

    /** Process an instance of VoidLiteralExpr. */
    public abstract RetType forVoidLiteralExpr(VoidLiteralExpr that);

    /** Process an instance of VarRef. */
    public abstract RetType forVarRef(VarRef that);

    /** Process an instance of FieldRef. */
    public abstract RetType forFieldRef(FieldRef that);

    /** Process an instance of FieldRefForSure. */
    public abstract RetType forFieldRefForSure(FieldRefForSure that);

    /** Process an instance of _RewriteFieldRef. */
    public abstract RetType for_RewriteFieldRef(_RewriteFieldRef that);

    /** Process an instance of FnRef. */
    public abstract RetType forFnRef(FnRef that);

    /** Process an instance of _RewriteFnRef. */
    public abstract RetType for_RewriteFnRef(_RewriteFnRef that);

    /** Process an instance of OpRef. */
    public abstract RetType forOpRef(OpRef that);

    /** Process an instance of LooseJuxt. */
    public abstract RetType forLooseJuxt(LooseJuxt that);

    /** Process an instance of TightJuxt. */
    public abstract RetType forTightJuxt(TightJuxt that);

    /** Process an instance of OprExpr. */
    public abstract RetType forOprExpr(OprExpr that);

    /** Process an instance of ChainExpr. */
    public abstract RetType forChainExpr(ChainExpr that);

    /** Process an instance of CoercionInvocation. */
    public abstract RetType forCoercionInvocation(CoercionInvocation that);

    /** Process an instance of MethodInvocation. */
    public abstract RetType forMethodInvocation(MethodInvocation that);

    /** Process an instance of MathPrimary. */
    public abstract RetType forMathPrimary(MathPrimary that);

    /** Process an instance of ArrayElement. */
    public abstract RetType forArrayElement(ArrayElement that);

    /** Process an instance of ArrayElements. */
    public abstract RetType forArrayElements(ArrayElements that);

    /** Process an instance of ExponentType. */
    public abstract RetType forExponentType(ExponentType that);

    /** Process an instance of BaseDim. */
    public abstract RetType forBaseDim(BaseDim that);

    /** Process an instance of DimRef. */
    public abstract RetType forDimRef(DimRef that);

    /** Process an instance of ProductDim. */
    public abstract RetType forProductDim(ProductDim that);

    /** Process an instance of QuotientDim. */
    public abstract RetType forQuotientDim(QuotientDim that);

    /** Process an instance of ExponentDim. */
    public abstract RetType forExponentDim(ExponentDim that);

    /** Process an instance of OpDim. */
    public abstract RetType forOpDim(OpDim that);

    /** Process an instance of ArrowType. */
    public abstract RetType forArrowType(ArrowType that);

    /** Process an instance of _RewriteGenericArrowType. */
    public abstract RetType for_RewriteGenericArrowType(_RewriteGenericArrowType that);

    /** Process an instance of BottomType. */
    public abstract RetType forBottomType(BottomType that);

    /** Process an instance of IdType. */
    public abstract RetType forIdType(IdType that);

    /** Process an instance of InstantiatedType. */
    public abstract RetType forInstantiatedType(InstantiatedType that);

    /** Process an instance of ArrayType. */
    public abstract RetType forArrayType(ArrayType that);

    /** Process an instance of MatrixType. */
    public abstract RetType forMatrixType(MatrixType that);

    /** Process an instance of TupleType. */
    public abstract RetType forTupleType(TupleType that);

    /** Process an instance of ArgType. */
    public abstract RetType forArgType(ArgType that);

    /** Process an instance of VoidType. */
    public abstract RetType forVoidType(VoidType that);

    /** Process an instance of InferenceVarType. */
    public abstract RetType forInferenceVarType(InferenceVarType that);

    /** Process an instance of AndType. */
    public abstract RetType forAndType(AndType that);

    /** Process an instance of OrType. */
    public abstract RetType forOrType(OrType that);

    /** Process an instance of FixedPointType. */
    public abstract RetType forFixedPointType(FixedPointType that);

    /** Process an instance of TaggedDimType. */
    public abstract RetType forTaggedDimType(TaggedDimType that);

    /** Process an instance of TaggedUnitType. */
    public abstract RetType forTaggedUnitType(TaggedUnitType that);

    /** Process an instance of IdArg. */
    public abstract RetType forIdArg(IdArg that);

    /** Process an instance of TypeArg. */
    public abstract RetType forTypeArg(TypeArg that);

    /** Process an instance of IntArg. */
    public abstract RetType forIntArg(IntArg that);

    /** Process an instance of BoolArg. */
    public abstract RetType forBoolArg(BoolArg that);

    /** Process an instance of OprArg. */
    public abstract RetType forOprArg(OprArg that);

    /** Process an instance of DimArg. */
    public abstract RetType forDimArg(DimArg that);

    /** Process an instance of UnitArg. */
    public abstract RetType forUnitArg(UnitArg that);

    /** Process an instance of NumberConstraint. */
    public abstract RetType forNumberConstraint(NumberConstraint that);

    /** Process an instance of IntRef. */
    public abstract RetType forIntRef(IntRef that);

    /** Process an instance of SumConstraint. */
    public abstract RetType forSumConstraint(SumConstraint that);

    /** Process an instance of MinusConstraint. */
    public abstract RetType forMinusConstraint(MinusConstraint that);

    /** Process an instance of ProductConstraint. */
    public abstract RetType forProductConstraint(ProductConstraint that);

    /** Process an instance of ExponentConstraint. */
    public abstract RetType forExponentConstraint(ExponentConstraint that);

    /** Process an instance of BoolConstant. */
    public abstract RetType forBoolConstant(BoolConstant that);

    /** Process an instance of BoolRef. */
    public abstract RetType forBoolRef(BoolRef that);

    /** Process an instance of NotConstraint. */
    public abstract RetType forNotConstraint(NotConstraint that);

    /** Process an instance of OrConstraint. */
    public abstract RetType forOrConstraint(OrConstraint that);

    /** Process an instance of AndConstraint. */
    public abstract RetType forAndConstraint(AndConstraint that);

    /** Process an instance of ImpliesConstraint. */
    public abstract RetType forImpliesConstraint(ImpliesConstraint that);

    /** Process an instance of BEConstraint. */
    public abstract RetType forBEConstraint(BEConstraint that);

    /** Process an instance of WhereClause. */
    public abstract RetType forWhereClause(WhereClause that);

    /** Process an instance of WhereType. */
    public abstract RetType forWhereType(WhereType that);

    /** Process an instance of WhereNat. */
    public abstract RetType forWhereNat(WhereNat that);

    /** Process an instance of WhereInt. */
    public abstract RetType forWhereInt(WhereInt that);

    /** Process an instance of WhereBool. */
    public abstract RetType forWhereBool(WhereBool that);

    /** Process an instance of WhereUnit. */
    public abstract RetType forWhereUnit(WhereUnit that);

    /** Process an instance of WhereExtends. */
    public abstract RetType forWhereExtends(WhereExtends that);

    /** Process an instance of TypeAlias. */
    public abstract RetType forTypeAlias(TypeAlias that);

    /** Process an instance of WhereCoerces. */
    public abstract RetType forWhereCoerces(WhereCoerces that);

    /** Process an instance of WhereWidens. */
    public abstract RetType forWhereWidens(WhereWidens that);

    /** Process an instance of WhereWidensCoerces. */
    public abstract RetType forWhereWidensCoerces(WhereWidensCoerces that);

    /** Process an instance of WhereEquals. */
    public abstract RetType forWhereEquals(WhereEquals that);

    /** Process an instance of UnitConstraint. */
    public abstract RetType forUnitConstraint(UnitConstraint that);

    /** Process an instance of LEConstraint. */
    public abstract RetType forLEConstraint(LEConstraint that);

    /** Process an instance of LTConstraint. */
    public abstract RetType forLTConstraint(LTConstraint that);

    /** Process an instance of GEConstraint. */
    public abstract RetType forGEConstraint(GEConstraint that);

    /** Process an instance of GTConstraint. */
    public abstract RetType forGTConstraint(GTConstraint that);

    /** Process an instance of IEConstraint. */
    public abstract RetType forIEConstraint(IEConstraint that);

    /** Process an instance of BoolConstraintExpr. */
    public abstract RetType forBoolConstraintExpr(BoolConstraintExpr that);

    /** Process an instance of Contract. */
    public abstract RetType forContract(Contract that);

    /** Process an instance of EnsuresClause. */
    public abstract RetType forEnsuresClause(EnsuresClause that);

    /** Process an instance of ModifierAbstract. */
    public abstract RetType forModifierAbstract(ModifierAbstract that);

    /** Process an instance of ModifierAtomic. */
    public abstract RetType forModifierAtomic(ModifierAtomic that);

    /** Process an instance of ModifierGetter. */
    public abstract RetType forModifierGetter(ModifierGetter that);

    /** Process an instance of ModifierHidden. */
    public abstract RetType forModifierHidden(ModifierHidden that);

    /** Process an instance of ModifierIO. */
    public abstract RetType forModifierIO(ModifierIO that);

    /** Process an instance of ModifierOverride. */
    public abstract RetType forModifierOverride(ModifierOverride that);

    /** Process an instance of ModifierPrivate. */
    public abstract RetType forModifierPrivate(ModifierPrivate that);

    /** Process an instance of ModifierSettable. */
    public abstract RetType forModifierSettable(ModifierSettable that);

    /** Process an instance of ModifierSetter. */
    public abstract RetType forModifierSetter(ModifierSetter that);

    /** Process an instance of ModifierTest. */
    public abstract RetType forModifierTest(ModifierTest that);

    /** Process an instance of ModifierTransient. */
    public abstract RetType forModifierTransient(ModifierTransient that);

    /** Process an instance of ModifierValue. */
    public abstract RetType forModifierValue(ModifierValue that);

    /** Process an instance of ModifierVar. */
    public abstract RetType forModifierVar(ModifierVar that);

    /** Process an instance of ModifierWidens. */
    public abstract RetType forModifierWidens(ModifierWidens that);

    /** Process an instance of ModifierWrapped. */
    public abstract RetType forModifierWrapped(ModifierWrapped that);

    /** Process an instance of OperatorParam. */
    public abstract RetType forOperatorParam(OperatorParam that);

    /** Process an instance of BoolParam. */
    public abstract RetType forBoolParam(BoolParam that);

    /** Process an instance of DimensionParam. */
    public abstract RetType forDimensionParam(DimensionParam that);

    /** Process an instance of IntParam. */
    public abstract RetType forIntParam(IntParam that);

    /** Process an instance of NatParam. */
    public abstract RetType forNatParam(NatParam that);

    /** Process an instance of SimpleTypeParam. */
    public abstract RetType forSimpleTypeParam(SimpleTypeParam that);

    /** Process an instance of UnitParam. */
    public abstract RetType forUnitParam(UnitParam that);

    /** Process an instance of APIName. */
    public abstract RetType forAPIName(APIName that);

    /** Process an instance of QualifiedIdName. */
    public abstract RetType forQualifiedIdName(QualifiedIdName that);

    /** Process an instance of QualifiedOpName. */
    public abstract RetType forQualifiedOpName(QualifiedOpName that);

    /** Process an instance of Id. */
    public abstract RetType forId(Id that);

    /** Process an instance of Op. */
    public abstract RetType forOp(Op that);

    /** Process an instance of Enclosing. */
    public abstract RetType forEnclosing(Enclosing that);

    /** Process an instance of AnonymousFnName. */
    public abstract RetType forAnonymousFnName(AnonymousFnName that);

    /** Process an instance of ConstructorFnName. */
    public abstract RetType forConstructorFnName(ConstructorFnName that);

    /** Process an instance of ArrayComprehensionClause. */
    public abstract RetType forArrayComprehensionClause(ArrayComprehensionClause that);

    /** Process an instance of KeywordExpr. */
    public abstract RetType forKeywordExpr(KeywordExpr that);

    /** Process an instance of CaseClause. */
    public abstract RetType forCaseClause(CaseClause that);

    /** Process an instance of Catch. */
    public abstract RetType forCatch(Catch that);

    /** Process an instance of CatchClause. */
    public abstract RetType forCatchClause(CatchClause that);

    /** Process an instance of DoFront. */
    public abstract RetType forDoFront(DoFront that);

    /** Process an instance of IfClause. */
    public abstract RetType forIfClause(IfClause that);

    /** Process an instance of TypecaseClause. */
    public abstract RetType forTypecaseClause(TypecaseClause that);

    /** Process an instance of ExtentRange. */
    public abstract RetType forExtentRange(ExtentRange that);

    /** Process an instance of GeneratorClause. */
    public abstract RetType forGeneratorClause(GeneratorClause that);

    /** Process an instance of VarargsExpr. */
    public abstract RetType forVarargsExpr(VarargsExpr that);

    /** Process an instance of VarargsType. */
    public abstract RetType forVarargsType(VarargsType that);

    /** Process an instance of KeywordType. */
    public abstract RetType forKeywordType(KeywordType that);

    /** Process an instance of TraitTypeWhere. */
    public abstract RetType forTraitTypeWhere(TraitTypeWhere that);

    /** Process an instance of Indices. */
    public abstract RetType forIndices(Indices that);

    /** Process an instance of ParenthesisDelimitedMI. */
    public abstract RetType forParenthesisDelimitedMI(ParenthesisDelimitedMI that);

    /** Process an instance of NonParenthesisDelimitedMI. */
    public abstract RetType forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that);

    /** Process an instance of ExponentiationMI. */
    public abstract RetType forExponentiationMI(ExponentiationMI that);

    /** Process an instance of SubscriptingMI. */
    public abstract RetType forSubscriptingMI(SubscriptingMI that);

    /** Process an instance of InFixity. */
    public abstract RetType forInFixity(InFixity that);

    /** Process an instance of PreFixity. */
    public abstract RetType forPreFixity(PreFixity that);

    /** Process an instance of PostFixity. */
    public abstract RetType forPostFixity(PostFixity that);

    /** Process an instance of NoFixity. */
    public abstract RetType forNoFixity(NoFixity that);

    /** Process an instance of MultiFixity. */
    public abstract RetType forMultiFixity(MultiFixity that);

    /** Process an instance of EnclosingFixity. */
    public abstract RetType forEnclosingFixity(EnclosingFixity that);

    /** Process an instance of BigFixity. */
    public abstract RetType forBigFixity(BigFixity that);
}
