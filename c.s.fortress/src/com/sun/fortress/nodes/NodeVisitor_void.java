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

/** An interface for visitors over Node that do not return a value. */
public interface NodeVisitor_void {

    /** Process an instance of Component. */
    public void forComponent(Component that);

    /** Process an instance of Api. */
    public void forApi(Api that);

    /** Process an instance of ImportStar. */
    public void forImportStar(ImportStar that);

    /** Process an instance of ImportNames. */
    public void forImportNames(ImportNames that);

    /** Process an instance of ImportApi. */
    public void forImportApi(ImportApi that);

    /** Process an instance of AliasedSimpleName. */
    public void forAliasedSimpleName(AliasedSimpleName that);

    /** Process an instance of AliasedAPIName. */
    public void forAliasedAPIName(AliasedAPIName that);

    /** Process an instance of Export. */
    public void forExport(Export that);

    /** Process an instance of AbsTraitDecl. */
    public void forAbsTraitDecl(AbsTraitDecl that);

    /** Process an instance of TraitDecl. */
    public void forTraitDecl(TraitDecl that);

    /** Process an instance of AbsObjectDecl. */
    public void forAbsObjectDecl(AbsObjectDecl that);

    /** Process an instance of ObjectDecl. */
    public void forObjectDecl(ObjectDecl that);

    /** Process an instance of AbsVarDecl. */
    public void forAbsVarDecl(AbsVarDecl that);

    /** Process an instance of VarDecl. */
    public void forVarDecl(VarDecl that);

    /** Process an instance of LValueBind. */
    public void forLValueBind(LValueBind that);

    /** Process an instance of UnpastingBind. */
    public void forUnpastingBind(UnpastingBind that);

    /** Process an instance of UnpastingSplit. */
    public void forUnpastingSplit(UnpastingSplit that);

    /** Process an instance of AbsFnDecl. */
    public void forAbsFnDecl(AbsFnDecl that);

    /** Process an instance of FnDef. */
    public void forFnDef(FnDef that);

    /** Process an instance of NormalParam. */
    public void forNormalParam(NormalParam that);

    /** Process an instance of VarargsParam. */
    public void forVarargsParam(VarargsParam that);

    /** Process an instance of DimDecl. */
    public void forDimDecl(DimDecl that);

    /** Process an instance of UnitDecl. */
    public void forUnitDecl(UnitDecl that);

    /** Process an instance of TestDecl. */
    public void forTestDecl(TestDecl that);

    /** Process an instance of PropertyDecl. */
    public void forPropertyDecl(PropertyDecl that);

    /** Process an instance of AbsExternalSyntax. */
    public void forAbsExternalSyntax(AbsExternalSyntax that);

    /** Process an instance of ExternalSyntax. */
    public void forExternalSyntax(ExternalSyntax that);

    /** Process an instance of GrammarDef. */
    public void forGrammarDef(GrammarDef that);

    /** Process an instance of NonterminalDef. */
    public void forNonterminalDef(NonterminalDef that);

    /** Process an instance of NonterminalExtensionDef. */
    public void forNonterminalExtensionDef(NonterminalExtensionDef that);

    /** Process an instance of _TerminalDef. */
    public void for_TerminalDef(_TerminalDef that);

    /** Process an instance of SyntaxDef. */
    public void forSyntaxDef(SyntaxDef that);

    /** Process an instance of PrefixedSymbol. */
    public void forPrefixedSymbol(PrefixedSymbol that);

    /** Process an instance of OptionalSymbol. */
    public void forOptionalSymbol(OptionalSymbol that);

    /** Process an instance of RepeatSymbol. */
    public void forRepeatSymbol(RepeatSymbol that);

    /** Process an instance of RepeatOneOrMoreSymbol. */
    public void forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that);

    /** Process an instance of NoWhitespaceSymbol. */
    public void forNoWhitespaceSymbol(NoWhitespaceSymbol that);

    /** Process an instance of WhitespaceSymbol. */
    public void forWhitespaceSymbol(WhitespaceSymbol that);

    /** Process an instance of TabSymbol. */
    public void forTabSymbol(TabSymbol that);

    /** Process an instance of FormfeedSymbol. */
    public void forFormfeedSymbol(FormfeedSymbol that);

    /** Process an instance of CarriageReturnSymbol. */
    public void forCarriageReturnSymbol(CarriageReturnSymbol that);

    /** Process an instance of BackspaceSymbol. */
    public void forBackspaceSymbol(BackspaceSymbol that);

    /** Process an instance of NewlineSymbol. */
    public void forNewlineSymbol(NewlineSymbol that);

    /** Process an instance of BreaklineSymbol. */
    public void forBreaklineSymbol(BreaklineSymbol that);

    /** Process an instance of ItemSymbol. */
    public void forItemSymbol(ItemSymbol that);

    /** Process an instance of NonterminalSymbol. */
    public void forNonterminalSymbol(NonterminalSymbol that);

    /** Process an instance of KeywordSymbol. */
    public void forKeywordSymbol(KeywordSymbol that);

    /** Process an instance of TokenSymbol. */
    public void forTokenSymbol(TokenSymbol that);

    /** Process an instance of NotPredicateSymbol. */
    public void forNotPredicateSymbol(NotPredicateSymbol that);

    /** Process an instance of AndPredicateSymbol. */
    public void forAndPredicateSymbol(AndPredicateSymbol that);

    /** Process an instance of CharacterClassSymbol. */
    public void forCharacterClassSymbol(CharacterClassSymbol that);

    /** Process an instance of CharSymbol. */
    public void forCharSymbol(CharSymbol that);

    /** Process an instance of CharacterInterval. */
    public void forCharacterInterval(CharacterInterval that);

    /** Process an instance of AsExpr. */
    public void forAsExpr(AsExpr that);

    /** Process an instance of AsIfExpr. */
    public void forAsIfExpr(AsIfExpr that);

    /** Process an instance of Assignment. */
    public void forAssignment(Assignment that);

    /** Process an instance of Block. */
    public void forBlock(Block that);

    /** Process an instance of CaseExpr. */
    public void forCaseExpr(CaseExpr that);

    /** Process an instance of Do. */
    public void forDo(Do that);

    /** Process an instance of For. */
    public void forFor(For that);

    /** Process an instance of If. */
    public void forIf(If that);

    /** Process an instance of Label. */
    public void forLabel(Label that);

    /** Process an instance of ObjectExpr. */
    public void forObjectExpr(ObjectExpr that);

    /** Process an instance of _RewriteObjectExpr. */
    public void for_RewriteObjectExpr(_RewriteObjectExpr that);

    /** Process an instance of Try. */
    public void forTry(Try that);

    /** Process an instance of TupleExpr. */
    public void forTupleExpr(TupleExpr that);

    /** Process an instance of ArgExpr. */
    public void forArgExpr(ArgExpr that);

    /** Process an instance of Typecase. */
    public void forTypecase(Typecase that);

    /** Process an instance of While. */
    public void forWhile(While that);

    /** Process an instance of Accumulator. */
    public void forAccumulator(Accumulator that);

    /** Process an instance of ArrayComprehension. */
    public void forArrayComprehension(ArrayComprehension that);

    /** Process an instance of AtomicExpr. */
    public void forAtomicExpr(AtomicExpr that);

    /** Process an instance of Exit. */
    public void forExit(Exit that);

    /** Process an instance of Spawn. */
    public void forSpawn(Spawn that);

    /** Process an instance of Throw. */
    public void forThrow(Throw that);

    /** Process an instance of TryAtomicExpr. */
    public void forTryAtomicExpr(TryAtomicExpr that);

    /** Process an instance of FnExpr. */
    public void forFnExpr(FnExpr that);

    /** Process an instance of LetFn. */
    public void forLetFn(LetFn that);

    /** Process an instance of LocalVarDecl. */
    public void forLocalVarDecl(LocalVarDecl that);

    /** Process an instance of GeneratedExpr. */
    public void forGeneratedExpr(GeneratedExpr that);

    /** Process an instance of SubscriptExpr. */
    public void forSubscriptExpr(SubscriptExpr that);

    /** Process an instance of FloatLiteralExpr. */
    public void forFloatLiteralExpr(FloatLiteralExpr that);

    /** Process an instance of IntLiteralExpr. */
    public void forIntLiteralExpr(IntLiteralExpr that);

    /** Process an instance of CharLiteralExpr. */
    public void forCharLiteralExpr(CharLiteralExpr that);

    /** Process an instance of StringLiteralExpr. */
    public void forStringLiteralExpr(StringLiteralExpr that);

    /** Process an instance of VoidLiteralExpr. */
    public void forVoidLiteralExpr(VoidLiteralExpr that);

    /** Process an instance of VarRef. */
    public void forVarRef(VarRef that);

    /** Process an instance of FieldRef. */
    public void forFieldRef(FieldRef that);

    /** Process an instance of FieldRefForSure. */
    public void forFieldRefForSure(FieldRefForSure that);

    /** Process an instance of _RewriteFieldRef. */
    public void for_RewriteFieldRef(_RewriteFieldRef that);

    /** Process an instance of FnRef. */
    public void forFnRef(FnRef that);

    /** Process an instance of _RewriteFnRef. */
    public void for_RewriteFnRef(_RewriteFnRef that);

    /** Process an instance of OpRef. */
    public void forOpRef(OpRef that);

    /** Process an instance of LooseJuxt. */
    public void forLooseJuxt(LooseJuxt that);

    /** Process an instance of TightJuxt. */
    public void forTightJuxt(TightJuxt that);

    /** Process an instance of OprExpr. */
    public void forOprExpr(OprExpr that);

    /** Process an instance of ChainExpr. */
    public void forChainExpr(ChainExpr that);

    /** Process an instance of CoercionInvocation. */
    public void forCoercionInvocation(CoercionInvocation that);

    /** Process an instance of MethodInvocation. */
    public void forMethodInvocation(MethodInvocation that);

    /** Process an instance of MathPrimary. */
    public void forMathPrimary(MathPrimary that);

    /** Process an instance of ArrayElement. */
    public void forArrayElement(ArrayElement that);

    /** Process an instance of ArrayElements. */
    public void forArrayElements(ArrayElements that);

    /** Process an instance of ExponentType. */
    public void forExponentType(ExponentType that);

    /** Process an instance of BaseDim. */
    public void forBaseDim(BaseDim that);

    /** Process an instance of DimRef. */
    public void forDimRef(DimRef that);

    /** Process an instance of ProductDim. */
    public void forProductDim(ProductDim that);

    /** Process an instance of QuotientDim. */
    public void forQuotientDim(QuotientDim that);

    /** Process an instance of ExponentDim. */
    public void forExponentDim(ExponentDim that);

    /** Process an instance of OpDim. */
    public void forOpDim(OpDim that);

    /** Process an instance of ArrowType. */
    public void forArrowType(ArrowType that);

    /** Process an instance of _RewriteGenericArrowType. */
    public void for_RewriteGenericArrowType(_RewriteGenericArrowType that);

    /** Process an instance of BottomType. */
    public void forBottomType(BottomType that);

    /** Process an instance of IdType. */
    public void forIdType(IdType that);

    /** Process an instance of InstantiatedType. */
    public void forInstantiatedType(InstantiatedType that);

    /** Process an instance of ArrayType. */
    public void forArrayType(ArrayType that);

    /** Process an instance of MatrixType. */
    public void forMatrixType(MatrixType that);

    /** Process an instance of TupleType. */
    public void forTupleType(TupleType that);

    /** Process an instance of ArgType. */
    public void forArgType(ArgType that);

    /** Process an instance of VoidType. */
    public void forVoidType(VoidType that);

    /** Process an instance of InferenceVarType. */
    public void forInferenceVarType(InferenceVarType that);

    /** Process an instance of AndType. */
    public void forAndType(AndType that);

    /** Process an instance of OrType. */
    public void forOrType(OrType that);

    /** Process an instance of FixedPointType. */
    public void forFixedPointType(FixedPointType that);

    /** Process an instance of TaggedDimType. */
    public void forTaggedDimType(TaggedDimType that);

    /** Process an instance of TaggedUnitType. */
    public void forTaggedUnitType(TaggedUnitType that);

    /** Process an instance of IdArg. */
    public void forIdArg(IdArg that);

    /** Process an instance of TypeArg. */
    public void forTypeArg(TypeArg that);

    /** Process an instance of IntArg. */
    public void forIntArg(IntArg that);

    /** Process an instance of BoolArg. */
    public void forBoolArg(BoolArg that);

    /** Process an instance of OprArg. */
    public void forOprArg(OprArg that);

    /** Process an instance of DimArg. */
    public void forDimArg(DimArg that);

    /** Process an instance of UnitArg. */
    public void forUnitArg(UnitArg that);

    /** Process an instance of NumberConstraint. */
    public void forNumberConstraint(NumberConstraint that);

    /** Process an instance of IntRef. */
    public void forIntRef(IntRef that);

    /** Process an instance of SumConstraint. */
    public void forSumConstraint(SumConstraint that);

    /** Process an instance of MinusConstraint. */
    public void forMinusConstraint(MinusConstraint that);

    /** Process an instance of ProductConstraint. */
    public void forProductConstraint(ProductConstraint that);

    /** Process an instance of ExponentConstraint. */
    public void forExponentConstraint(ExponentConstraint that);

    /** Process an instance of BoolConstant. */
    public void forBoolConstant(BoolConstant that);

    /** Process an instance of BoolRef. */
    public void forBoolRef(BoolRef that);

    /** Process an instance of NotConstraint. */
    public void forNotConstraint(NotConstraint that);

    /** Process an instance of OrConstraint. */
    public void forOrConstraint(OrConstraint that);

    /** Process an instance of AndConstraint. */
    public void forAndConstraint(AndConstraint that);

    /** Process an instance of ImpliesConstraint. */
    public void forImpliesConstraint(ImpliesConstraint that);

    /** Process an instance of BEConstraint. */
    public void forBEConstraint(BEConstraint that);

    /** Process an instance of WhereClause. */
    public void forWhereClause(WhereClause that);

    /** Process an instance of WhereType. */
    public void forWhereType(WhereType that);

    /** Process an instance of WhereNat. */
    public void forWhereNat(WhereNat that);

    /** Process an instance of WhereInt. */
    public void forWhereInt(WhereInt that);

    /** Process an instance of WhereBool. */
    public void forWhereBool(WhereBool that);

    /** Process an instance of WhereUnit. */
    public void forWhereUnit(WhereUnit that);

    /** Process an instance of WhereExtends. */
    public void forWhereExtends(WhereExtends that);

    /** Process an instance of TypeAlias. */
    public void forTypeAlias(TypeAlias that);

    /** Process an instance of WhereCoerces. */
    public void forWhereCoerces(WhereCoerces that);

    /** Process an instance of WhereWidens. */
    public void forWhereWidens(WhereWidens that);

    /** Process an instance of WhereWidensCoerces. */
    public void forWhereWidensCoerces(WhereWidensCoerces that);

    /** Process an instance of WhereEquals. */
    public void forWhereEquals(WhereEquals that);

    /** Process an instance of UnitConstraint. */
    public void forUnitConstraint(UnitConstraint that);

    /** Process an instance of LEConstraint. */
    public void forLEConstraint(LEConstraint that);

    /** Process an instance of LTConstraint. */
    public void forLTConstraint(LTConstraint that);

    /** Process an instance of GEConstraint. */
    public void forGEConstraint(GEConstraint that);

    /** Process an instance of GTConstraint. */
    public void forGTConstraint(GTConstraint that);

    /** Process an instance of IEConstraint. */
    public void forIEConstraint(IEConstraint that);

    /** Process an instance of BoolConstraintExpr. */
    public void forBoolConstraintExpr(BoolConstraintExpr that);

    /** Process an instance of Contract. */
    public void forContract(Contract that);

    /** Process an instance of EnsuresClause. */
    public void forEnsuresClause(EnsuresClause that);

    /** Process an instance of ModifierAbstract. */
    public void forModifierAbstract(ModifierAbstract that);

    /** Process an instance of ModifierAtomic. */
    public void forModifierAtomic(ModifierAtomic that);

    /** Process an instance of ModifierGetter. */
    public void forModifierGetter(ModifierGetter that);

    /** Process an instance of ModifierHidden. */
    public void forModifierHidden(ModifierHidden that);

    /** Process an instance of ModifierIO. */
    public void forModifierIO(ModifierIO that);

    /** Process an instance of ModifierOverride. */
    public void forModifierOverride(ModifierOverride that);

    /** Process an instance of ModifierPrivate. */
    public void forModifierPrivate(ModifierPrivate that);

    /** Process an instance of ModifierSettable. */
    public void forModifierSettable(ModifierSettable that);

    /** Process an instance of ModifierSetter. */
    public void forModifierSetter(ModifierSetter that);

    /** Process an instance of ModifierTest. */
    public void forModifierTest(ModifierTest that);

    /** Process an instance of ModifierTransient. */
    public void forModifierTransient(ModifierTransient that);

    /** Process an instance of ModifierValue. */
    public void forModifierValue(ModifierValue that);

    /** Process an instance of ModifierVar. */
    public void forModifierVar(ModifierVar that);

    /** Process an instance of ModifierWidens. */
    public void forModifierWidens(ModifierWidens that);

    /** Process an instance of ModifierWrapped. */
    public void forModifierWrapped(ModifierWrapped that);

    /** Process an instance of OperatorParam. */
    public void forOperatorParam(OperatorParam that);

    /** Process an instance of BoolParam. */
    public void forBoolParam(BoolParam that);

    /** Process an instance of DimensionParam. */
    public void forDimensionParam(DimensionParam that);

    /** Process an instance of IntParam. */
    public void forIntParam(IntParam that);

    /** Process an instance of NatParam. */
    public void forNatParam(NatParam that);

    /** Process an instance of SimpleTypeParam. */
    public void forSimpleTypeParam(SimpleTypeParam that);

    /** Process an instance of UnitParam. */
    public void forUnitParam(UnitParam that);

    /** Process an instance of APIName. */
    public void forAPIName(APIName that);

    /** Process an instance of QualifiedIdName. */
    public void forQualifiedIdName(QualifiedIdName that);

    /** Process an instance of QualifiedOpName. */
    public void forQualifiedOpName(QualifiedOpName that);

    /** Process an instance of Id. */
    public void forId(Id that);

    /** Process an instance of Op. */
    public void forOp(Op that);

    /** Process an instance of Enclosing. */
    public void forEnclosing(Enclosing that);

    /** Process an instance of AnonymousFnName. */
    public void forAnonymousFnName(AnonymousFnName that);

    /** Process an instance of ConstructorFnName. */
    public void forConstructorFnName(ConstructorFnName that);

    /** Process an instance of ArrayComprehensionClause. */
    public void forArrayComprehensionClause(ArrayComprehensionClause that);

    /** Process an instance of KeywordExpr. */
    public void forKeywordExpr(KeywordExpr that);

    /** Process an instance of CaseClause. */
    public void forCaseClause(CaseClause that);

    /** Process an instance of Catch. */
    public void forCatch(Catch that);

    /** Process an instance of CatchClause. */
    public void forCatchClause(CatchClause that);

    /** Process an instance of DoFront. */
    public void forDoFront(DoFront that);

    /** Process an instance of IfClause. */
    public void forIfClause(IfClause that);

    /** Process an instance of TypecaseClause. */
    public void forTypecaseClause(TypecaseClause that);

    /** Process an instance of ExtentRange. */
    public void forExtentRange(ExtentRange that);

    /** Process an instance of GeneratorClause. */
    public void forGeneratorClause(GeneratorClause that);

    /** Process an instance of VarargsExpr. */
    public void forVarargsExpr(VarargsExpr that);

    /** Process an instance of VarargsType. */
    public void forVarargsType(VarargsType that);

    /** Process an instance of KeywordType. */
    public void forKeywordType(KeywordType that);

    /** Process an instance of TraitTypeWhere. */
    public void forTraitTypeWhere(TraitTypeWhere that);

    /** Process an instance of Indices. */
    public void forIndices(Indices that);

    /** Process an instance of ParenthesisDelimitedMI. */
    public void forParenthesisDelimitedMI(ParenthesisDelimitedMI that);

    /** Process an instance of NonParenthesisDelimitedMI. */
    public void forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that);

    /** Process an instance of ExponentiationMI. */
    public void forExponentiationMI(ExponentiationMI that);

    /** Process an instance of SubscriptingMI. */
    public void forSubscriptingMI(SubscriptingMI that);

    /** Process an instance of InFixity. */
    public void forInFixity(InFixity that);

    /** Process an instance of PreFixity. */
    public void forPreFixity(PreFixity that);

    /** Process an instance of PostFixity. */
    public void forPostFixity(PostFixity that);

    /** Process an instance of NoFixity. */
    public void forNoFixity(NoFixity that);

    /** Process an instance of MultiFixity. */
    public void forMultiFixity(MultiFixity that);

    /** Process an instance of EnclosingFixity. */
    public void forEnclosingFixity(EnclosingFixity that);

    /** Process an instance of BigFixity. */
    public void forBigFixity(BigFixity that);
}
