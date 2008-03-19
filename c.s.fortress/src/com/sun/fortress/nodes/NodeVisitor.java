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

/** A parametric interface for visitors over Node that return a value. */
public interface NodeVisitor<RetType> {

    /** Process an instance of Component. */
    public RetType forComponent(Component that);

    /** Process an instance of Api. */
    public RetType forApi(Api that);

    /** Process an instance of ImportStar. */
    public RetType forImportStar(ImportStar that);

    /** Process an instance of ImportNames. */
    public RetType forImportNames(ImportNames that);

    /** Process an instance of ImportApi. */
    public RetType forImportApi(ImportApi that);

    /** Process an instance of AliasedSimpleName. */
    public RetType forAliasedSimpleName(AliasedSimpleName that);

    /** Process an instance of AliasedAPIName. */
    public RetType forAliasedAPIName(AliasedAPIName that);

    /** Process an instance of Export. */
    public RetType forExport(Export that);

    /** Process an instance of AbsTraitDecl. */
    public RetType forAbsTraitDecl(AbsTraitDecl that);

    /** Process an instance of TraitDecl. */
    public RetType forTraitDecl(TraitDecl that);

    /** Process an instance of AbsObjectDecl. */
    public RetType forAbsObjectDecl(AbsObjectDecl that);

    /** Process an instance of ObjectDecl. */
    public RetType forObjectDecl(ObjectDecl that);

    /** Process an instance of AbsVarDecl. */
    public RetType forAbsVarDecl(AbsVarDecl that);

    /** Process an instance of VarDecl. */
    public RetType forVarDecl(VarDecl that);

    /** Process an instance of LValueBind. */
    public RetType forLValueBind(LValueBind that);

    /** Process an instance of UnpastingBind. */
    public RetType forUnpastingBind(UnpastingBind that);

    /** Process an instance of UnpastingSplit. */
    public RetType forUnpastingSplit(UnpastingSplit that);

    /** Process an instance of AbsFnDecl. */
    public RetType forAbsFnDecl(AbsFnDecl that);

    /** Process an instance of FnDef. */
    public RetType forFnDef(FnDef that);

    /** Process an instance of NormalParam. */
    public RetType forNormalParam(NormalParam that);

    /** Process an instance of VarargsParam. */
    public RetType forVarargsParam(VarargsParam that);

    /** Process an instance of DimDecl. */
    public RetType forDimDecl(DimDecl that);

    /** Process an instance of UnitDecl. */
    public RetType forUnitDecl(UnitDecl that);

    /** Process an instance of TestDecl. */
    public RetType forTestDecl(TestDecl that);

    /** Process an instance of PropertyDecl. */
    public RetType forPropertyDecl(PropertyDecl that);

    /** Process an instance of AbsExternalSyntax. */
    public RetType forAbsExternalSyntax(AbsExternalSyntax that);

    /** Process an instance of ExternalSyntax. */
    public RetType forExternalSyntax(ExternalSyntax that);

    /** Process an instance of GrammarDef. */
    public RetType forGrammarDef(GrammarDef that);

    /** Process an instance of NonterminalDef. */
    public RetType forNonterminalDef(NonterminalDef that);

    /** Process an instance of NonterminalExtensionDef. */
    public RetType forNonterminalExtensionDef(NonterminalExtensionDef that);

    /** Process an instance of _TerminalDef. */
    public RetType for_TerminalDef(_TerminalDef that);

    /** Process an instance of SyntaxDef. */
    public RetType forSyntaxDef(SyntaxDef that);

    /** Process an instance of PrefixedSymbol. */
    public RetType forPrefixedSymbol(PrefixedSymbol that);

    /** Process an instance of OptionalSymbol. */
    public RetType forOptionalSymbol(OptionalSymbol that);

    /** Process an instance of RepeatSymbol. */
    public RetType forRepeatSymbol(RepeatSymbol that);

    /** Process an instance of RepeatOneOrMoreSymbol. */
    public RetType forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that);

    /** Process an instance of NoWhitespaceSymbol. */
    public RetType forNoWhitespaceSymbol(NoWhitespaceSymbol that);

    /** Process an instance of WhitespaceSymbol. */
    public RetType forWhitespaceSymbol(WhitespaceSymbol that);

    /** Process an instance of TabSymbol. */
    public RetType forTabSymbol(TabSymbol that);

    /** Process an instance of FormfeedSymbol. */
    public RetType forFormfeedSymbol(FormfeedSymbol that);

    /** Process an instance of CarriageReturnSymbol. */
    public RetType forCarriageReturnSymbol(CarriageReturnSymbol that);

    /** Process an instance of BackspaceSymbol. */
    public RetType forBackspaceSymbol(BackspaceSymbol that);

    /** Process an instance of NewlineSymbol. */
    public RetType forNewlineSymbol(NewlineSymbol that);

    /** Process an instance of BreaklineSymbol. */
    public RetType forBreaklineSymbol(BreaklineSymbol that);

    /** Process an instance of ItemSymbol. */
    public RetType forItemSymbol(ItemSymbol that);

    /** Process an instance of NonterminalSymbol. */
    public RetType forNonterminalSymbol(NonterminalSymbol that);

    /** Process an instance of KeywordSymbol. */
    public RetType forKeywordSymbol(KeywordSymbol that);

    /** Process an instance of TokenSymbol. */
    public RetType forTokenSymbol(TokenSymbol that);

    /** Process an instance of NotPredicateSymbol. */
    public RetType forNotPredicateSymbol(NotPredicateSymbol that);

    /** Process an instance of AndPredicateSymbol. */
    public RetType forAndPredicateSymbol(AndPredicateSymbol that);

    /** Process an instance of CharacterClassSymbol. */
    public RetType forCharacterClassSymbol(CharacterClassSymbol that);

    /** Process an instance of CharSymbol. */
    public RetType forCharSymbol(CharSymbol that);

    /** Process an instance of CharacterInterval. */
    public RetType forCharacterInterval(CharacterInterval that);

    /** Process an instance of AsExpr. */
    public RetType forAsExpr(AsExpr that);

    /** Process an instance of AsIfExpr. */
    public RetType forAsIfExpr(AsIfExpr that);

    /** Process an instance of Assignment. */
    public RetType forAssignment(Assignment that);

    /** Process an instance of Block. */
    public RetType forBlock(Block that);

    /** Process an instance of CaseExpr. */
    public RetType forCaseExpr(CaseExpr that);

    /** Process an instance of Do. */
    public RetType forDo(Do that);

    /** Process an instance of For. */
    public RetType forFor(For that);

    /** Process an instance of If. */
    public RetType forIf(If that);

    /** Process an instance of Label. */
    public RetType forLabel(Label that);

    /** Process an instance of ObjectExpr. */
    public RetType forObjectExpr(ObjectExpr that);

    /** Process an instance of _RewriteObjectExpr. */
    public RetType for_RewriteObjectExpr(_RewriteObjectExpr that);

    /** Process an instance of Try. */
    public RetType forTry(Try that);

    /** Process an instance of TupleExpr. */
    public RetType forTupleExpr(TupleExpr that);

    /** Process an instance of ArgExpr. */
    public RetType forArgExpr(ArgExpr that);

    /** Process an instance of Typecase. */
    public RetType forTypecase(Typecase that);

    /** Process an instance of While. */
    public RetType forWhile(While that);

    /** Process an instance of Accumulator. */
    public RetType forAccumulator(Accumulator that);

    /** Process an instance of ArrayComprehension. */
    public RetType forArrayComprehension(ArrayComprehension that);

    /** Process an instance of AtomicExpr. */
    public RetType forAtomicExpr(AtomicExpr that);

    /** Process an instance of Exit. */
    public RetType forExit(Exit that);

    /** Process an instance of Spawn. */
    public RetType forSpawn(Spawn that);

    /** Process an instance of Throw. */
    public RetType forThrow(Throw that);

    /** Process an instance of TryAtomicExpr. */
    public RetType forTryAtomicExpr(TryAtomicExpr that);

    /** Process an instance of FnExpr. */
    public RetType forFnExpr(FnExpr that);

    /** Process an instance of LetFn. */
    public RetType forLetFn(LetFn that);

    /** Process an instance of LocalVarDecl. */
    public RetType forLocalVarDecl(LocalVarDecl that);

    /** Process an instance of GeneratedExpr. */
    public RetType forGeneratedExpr(GeneratedExpr that);

    /** Process an instance of SubscriptExpr. */
    public RetType forSubscriptExpr(SubscriptExpr that);

    /** Process an instance of FloatLiteralExpr. */
    public RetType forFloatLiteralExpr(FloatLiteralExpr that);

    /** Process an instance of IntLiteralExpr. */
    public RetType forIntLiteralExpr(IntLiteralExpr that);

    /** Process an instance of CharLiteralExpr. */
    public RetType forCharLiteralExpr(CharLiteralExpr that);

    /** Process an instance of StringLiteralExpr. */
    public RetType forStringLiteralExpr(StringLiteralExpr that);

    /** Process an instance of VoidLiteralExpr. */
    public RetType forVoidLiteralExpr(VoidLiteralExpr that);

    /** Process an instance of VarRef. */
    public RetType forVarRef(VarRef that);

    /** Process an instance of FieldRef. */
    public RetType forFieldRef(FieldRef that);

    /** Process an instance of FieldRefForSure. */
    public RetType forFieldRefForSure(FieldRefForSure that);

    /** Process an instance of _RewriteFieldRef. */
    public RetType for_RewriteFieldRef(_RewriteFieldRef that);

    /** Process an instance of FnRef. */
    public RetType forFnRef(FnRef that);

    /** Process an instance of _RewriteFnRef. */
    public RetType for_RewriteFnRef(_RewriteFnRef that);

    /** Process an instance of OpRef. */
    public RetType forOpRef(OpRef that);

    /** Process an instance of LooseJuxt. */
    public RetType forLooseJuxt(LooseJuxt that);

    /** Process an instance of TightJuxt. */
    public RetType forTightJuxt(TightJuxt that);

    /** Process an instance of OprExpr. */
    public RetType forOprExpr(OprExpr that);

    /** Process an instance of ChainExpr. */
    public RetType forChainExpr(ChainExpr that);

    /** Process an instance of CoercionInvocation. */
    public RetType forCoercionInvocation(CoercionInvocation that);

    /** Process an instance of MethodInvocation. */
    public RetType forMethodInvocation(MethodInvocation that);

    /** Process an instance of MathPrimary. */
    public RetType forMathPrimary(MathPrimary that);

    /** Process an instance of ArrayElement. */
    public RetType forArrayElement(ArrayElement that);

    /** Process an instance of ArrayElements. */
    public RetType forArrayElements(ArrayElements that);

    /** Process an instance of ExponentType. */
    public RetType forExponentType(ExponentType that);

    /** Process an instance of BaseDim. */
    public RetType forBaseDim(BaseDim that);

    /** Process an instance of DimRef. */
    public RetType forDimRef(DimRef that);

    /** Process an instance of ProductDim. */
    public RetType forProductDim(ProductDim that);

    /** Process an instance of QuotientDim. */
    public RetType forQuotientDim(QuotientDim that);

    /** Process an instance of ExponentDim. */
    public RetType forExponentDim(ExponentDim that);

    /** Process an instance of OpDim. */
    public RetType forOpDim(OpDim that);

    /** Process an instance of ArrowType. */
    public RetType forArrowType(ArrowType that);

    /** Process an instance of _RewriteGenericArrowType. */
    public RetType for_RewriteGenericArrowType(_RewriteGenericArrowType that);

    /** Process an instance of BottomType. */
    public RetType forBottomType(BottomType that);

    /** Process an instance of IdType. */
    public RetType forIdType(IdType that);

    /** Process an instance of InstantiatedType. */
    public RetType forInstantiatedType(InstantiatedType that);

    /** Process an instance of ArrayType. */
    public RetType forArrayType(ArrayType that);

    /** Process an instance of MatrixType. */
    public RetType forMatrixType(MatrixType that);

    /** Process an instance of TupleType. */
    public RetType forTupleType(TupleType that);

    /** Process an instance of ArgType. */
    public RetType forArgType(ArgType that);

    /** Process an instance of VoidType. */
    public RetType forVoidType(VoidType that);

    /** Process an instance of InferenceVarType. */
    public RetType forInferenceVarType(InferenceVarType that);

    /** Process an instance of AndType. */
    public RetType forAndType(AndType that);

    /** Process an instance of OrType. */
    public RetType forOrType(OrType that);

    /** Process an instance of FixedPointType. */
    public RetType forFixedPointType(FixedPointType that);

    /** Process an instance of TaggedDimType. */
    public RetType forTaggedDimType(TaggedDimType that);

    /** Process an instance of TaggedUnitType. */
    public RetType forTaggedUnitType(TaggedUnitType that);

    /** Process an instance of IdArg. */
    public RetType forIdArg(IdArg that);

    /** Process an instance of TypeArg. */
    public RetType forTypeArg(TypeArg that);

    /** Process an instance of IntArg. */
    public RetType forIntArg(IntArg that);

    /** Process an instance of BoolArg. */
    public RetType forBoolArg(BoolArg that);

    /** Process an instance of OprArg. */
    public RetType forOprArg(OprArg that);

    /** Process an instance of DimArg. */
    public RetType forDimArg(DimArg that);

    /** Process an instance of UnitArg. */
    public RetType forUnitArg(UnitArg that);

    /** Process an instance of NumberConstraint. */
    public RetType forNumberConstraint(NumberConstraint that);

    /** Process an instance of IntRef. */
    public RetType forIntRef(IntRef that);

    /** Process an instance of SumConstraint. */
    public RetType forSumConstraint(SumConstraint that);

    /** Process an instance of MinusConstraint. */
    public RetType forMinusConstraint(MinusConstraint that);

    /** Process an instance of ProductConstraint. */
    public RetType forProductConstraint(ProductConstraint that);

    /** Process an instance of ExponentConstraint. */
    public RetType forExponentConstraint(ExponentConstraint that);

    /** Process an instance of BoolConstant. */
    public RetType forBoolConstant(BoolConstant that);

    /** Process an instance of BoolRef. */
    public RetType forBoolRef(BoolRef that);

    /** Process an instance of NotConstraint. */
    public RetType forNotConstraint(NotConstraint that);

    /** Process an instance of OrConstraint. */
    public RetType forOrConstraint(OrConstraint that);

    /** Process an instance of AndConstraint. */
    public RetType forAndConstraint(AndConstraint that);

    /** Process an instance of ImpliesConstraint. */
    public RetType forImpliesConstraint(ImpliesConstraint that);

    /** Process an instance of BEConstraint. */
    public RetType forBEConstraint(BEConstraint that);

    /** Process an instance of WhereClause. */
    public RetType forWhereClause(WhereClause that);

    /** Process an instance of WhereType. */
    public RetType forWhereType(WhereType that);

    /** Process an instance of WhereNat. */
    public RetType forWhereNat(WhereNat that);

    /** Process an instance of WhereInt. */
    public RetType forWhereInt(WhereInt that);

    /** Process an instance of WhereBool. */
    public RetType forWhereBool(WhereBool that);

    /** Process an instance of WhereUnit. */
    public RetType forWhereUnit(WhereUnit that);

    /** Process an instance of WhereExtends. */
    public RetType forWhereExtends(WhereExtends that);

    /** Process an instance of TypeAlias. */
    public RetType forTypeAlias(TypeAlias that);

    /** Process an instance of WhereCoerces. */
    public RetType forWhereCoerces(WhereCoerces that);

    /** Process an instance of WhereWidens. */
    public RetType forWhereWidens(WhereWidens that);

    /** Process an instance of WhereWidensCoerces. */
    public RetType forWhereWidensCoerces(WhereWidensCoerces that);

    /** Process an instance of WhereEquals. */
    public RetType forWhereEquals(WhereEquals that);

    /** Process an instance of UnitConstraint. */
    public RetType forUnitConstraint(UnitConstraint that);

    /** Process an instance of LEConstraint. */
    public RetType forLEConstraint(LEConstraint that);

    /** Process an instance of LTConstraint. */
    public RetType forLTConstraint(LTConstraint that);

    /** Process an instance of GEConstraint. */
    public RetType forGEConstraint(GEConstraint that);

    /** Process an instance of GTConstraint. */
    public RetType forGTConstraint(GTConstraint that);

    /** Process an instance of IEConstraint. */
    public RetType forIEConstraint(IEConstraint that);

    /** Process an instance of BoolConstraintExpr. */
    public RetType forBoolConstraintExpr(BoolConstraintExpr that);

    /** Process an instance of Contract. */
    public RetType forContract(Contract that);

    /** Process an instance of EnsuresClause. */
    public RetType forEnsuresClause(EnsuresClause that);

    /** Process an instance of ModifierAbstract. */
    public RetType forModifierAbstract(ModifierAbstract that);

    /** Process an instance of ModifierAtomic. */
    public RetType forModifierAtomic(ModifierAtomic that);

    /** Process an instance of ModifierGetter. */
    public RetType forModifierGetter(ModifierGetter that);

    /** Process an instance of ModifierHidden. */
    public RetType forModifierHidden(ModifierHidden that);

    /** Process an instance of ModifierIO. */
    public RetType forModifierIO(ModifierIO that);

    /** Process an instance of ModifierOverride. */
    public RetType forModifierOverride(ModifierOverride that);

    /** Process an instance of ModifierPrivate. */
    public RetType forModifierPrivate(ModifierPrivate that);

    /** Process an instance of ModifierSettable. */
    public RetType forModifierSettable(ModifierSettable that);

    /** Process an instance of ModifierSetter. */
    public RetType forModifierSetter(ModifierSetter that);

    /** Process an instance of ModifierTest. */
    public RetType forModifierTest(ModifierTest that);

    /** Process an instance of ModifierTransient. */
    public RetType forModifierTransient(ModifierTransient that);

    /** Process an instance of ModifierValue. */
    public RetType forModifierValue(ModifierValue that);

    /** Process an instance of ModifierVar. */
    public RetType forModifierVar(ModifierVar that);

    /** Process an instance of ModifierWidens. */
    public RetType forModifierWidens(ModifierWidens that);

    /** Process an instance of ModifierWrapped. */
    public RetType forModifierWrapped(ModifierWrapped that);

    /** Process an instance of OperatorParam. */
    public RetType forOperatorParam(OperatorParam that);

    /** Process an instance of BoolParam. */
    public RetType forBoolParam(BoolParam that);

    /** Process an instance of DimensionParam. */
    public RetType forDimensionParam(DimensionParam that);

    /** Process an instance of IntParam. */
    public RetType forIntParam(IntParam that);

    /** Process an instance of NatParam. */
    public RetType forNatParam(NatParam that);

    /** Process an instance of SimpleTypeParam. */
    public RetType forSimpleTypeParam(SimpleTypeParam that);

    /** Process an instance of UnitParam. */
    public RetType forUnitParam(UnitParam that);

    /** Process an instance of APIName. */
    public RetType forAPIName(APIName that);

    /** Process an instance of QualifiedIdName. */
    public RetType forQualifiedIdName(QualifiedIdName that);

    /** Process an instance of QualifiedOpName. */
    public RetType forQualifiedOpName(QualifiedOpName that);

    /** Process an instance of Id. */
    public RetType forId(Id that);

    /** Process an instance of Op. */
    public RetType forOp(Op that);

    /** Process an instance of Enclosing. */
    public RetType forEnclosing(Enclosing that);

    /** Process an instance of AnonymousFnName. */
    public RetType forAnonymousFnName(AnonymousFnName that);

    /** Process an instance of ConstructorFnName. */
    public RetType forConstructorFnName(ConstructorFnName that);

    /** Process an instance of ArrayComprehensionClause. */
    public RetType forArrayComprehensionClause(ArrayComprehensionClause that);

    /** Process an instance of KeywordExpr. */
    public RetType forKeywordExpr(KeywordExpr that);

    /** Process an instance of CaseClause. */
    public RetType forCaseClause(CaseClause that);

    /** Process an instance of Catch. */
    public RetType forCatch(Catch that);

    /** Process an instance of CatchClause. */
    public RetType forCatchClause(CatchClause that);

    /** Process an instance of DoFront. */
    public RetType forDoFront(DoFront that);

    /** Process an instance of IfClause. */
    public RetType forIfClause(IfClause that);

    /** Process an instance of TypecaseClause. */
    public RetType forTypecaseClause(TypecaseClause that);

    /** Process an instance of ExtentRange. */
    public RetType forExtentRange(ExtentRange that);

    /** Process an instance of GeneratorClause. */
    public RetType forGeneratorClause(GeneratorClause that);

    /** Process an instance of VarargsExpr. */
    public RetType forVarargsExpr(VarargsExpr that);

    /** Process an instance of VarargsType. */
    public RetType forVarargsType(VarargsType that);

    /** Process an instance of KeywordType. */
    public RetType forKeywordType(KeywordType that);

    /** Process an instance of TraitTypeWhere. */
    public RetType forTraitTypeWhere(TraitTypeWhere that);

    /** Process an instance of Indices. */
    public RetType forIndices(Indices that);

    /** Process an instance of ParenthesisDelimitedMI. */
    public RetType forParenthesisDelimitedMI(ParenthesisDelimitedMI that);

    /** Process an instance of NonParenthesisDelimitedMI. */
    public RetType forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that);

    /** Process an instance of ExponentiationMI. */
    public RetType forExponentiationMI(ExponentiationMI that);

    /** Process an instance of SubscriptingMI. */
    public RetType forSubscriptingMI(SubscriptingMI that);

    /** Process an instance of InFixity. */
    public RetType forInFixity(InFixity that);

    /** Process an instance of PreFixity. */
    public RetType forPreFixity(PreFixity that);

    /** Process an instance of PostFixity. */
    public RetType forPostFixity(PostFixity that);

    /** Process an instance of NoFixity. */
    public RetType forNoFixity(NoFixity that);

    /** Process an instance of MultiFixity. */
    public RetType forMultiFixity(MultiFixity that);

    /** Process an instance of EnclosingFixity. */
    public RetType forEnclosingFixity(EnclosingFixity that);

    /** Process an instance of BigFixity. */
    public RetType forBigFixity(BigFixity that);
}
