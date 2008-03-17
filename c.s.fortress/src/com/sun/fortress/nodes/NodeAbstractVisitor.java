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

/** A parametric abstract implementation of a visitor over Node that return a value.
 ** This visitor implements the visitor interface with methods that each 
 ** delegate to a case representing their superclass.  At the top of this
 ** delegation tree is the method defaultCase(), which (unless overridden)
 ** throws an exception.
 **/public abstract class NodeAbstractVisitor<RetType>  extends NodeVisitorLambda<RetType> {
    /**
     * This method is run for all cases that are not handled elsewhere.
     * By default, an exception is thrown; subclasses may override this behavior.
     * @throws IllegalArgumentException
    **/
    public RetType defaultCase(Node that) {
        throw new IllegalArgumentException("Visitor " + getClass().getName() + " does not support visiting values of type " + that.getClass().getName());
    }

    /* Methods to visit an item. */
    public RetType forAbstractNode(AbstractNode that) {
        return defaultCase(that);
    }

    public RetType forCompilationUnit(CompilationUnit that) {
        return forAbstractNode(that);
    }

    public RetType forComponent(Component that) {
        return forCompilationUnit(that);
    }

    public RetType forApi(Api that) {
        return forCompilationUnit(that);
    }

    public RetType forImport(Import that) {
        return forAbstractNode(that);
    }

    public RetType forImportedNames(ImportedNames that) {
        return forImport(that);
    }

    public RetType forImportStar(ImportStar that) {
        return forImportedNames(that);
    }

    public RetType forImportNames(ImportNames that) {
        return forImportedNames(that);
    }

    public RetType forImportApi(ImportApi that) {
        return forImport(that);
    }

    public RetType forAliasedSimpleName(AliasedSimpleName that) {
        return forAbstractNode(that);
    }

    public RetType forAliasedAPIName(AliasedAPIName that) {
        return forAbstractNode(that);
    }

    public RetType forExport(Export that) {
        return forAbstractNode(that);
    }

    public RetType forTraitObjectAbsDeclOrDecl(TraitObjectAbsDeclOrDecl that) {
        return forAbstractNode(that);
    }

    public RetType forTraitAbsDeclOrDecl(TraitAbsDeclOrDecl that) {
        return forTraitObjectAbsDeclOrDecl(that);
    }

    public RetType forAbsTraitDecl(AbsTraitDecl that) {
        return forTraitAbsDeclOrDecl(that);
    }

    public RetType forTraitDecl(TraitDecl that) {
        return forTraitAbsDeclOrDecl(that);
    }

    public RetType forObjectAbsDeclOrDecl(ObjectAbsDeclOrDecl that) {
        return forTraitObjectAbsDeclOrDecl(that);
    }

    public RetType forAbsObjectDecl(AbsObjectDecl that) {
        return forObjectAbsDeclOrDecl(that);
    }

    public RetType forObjectDecl(ObjectDecl that) {
        return forObjectAbsDeclOrDecl(that);
    }

    public RetType forVarAbsDeclOrDecl(VarAbsDeclOrDecl that) {
        return forAbstractNode(that);
    }

    public RetType forAbsVarDecl(AbsVarDecl that) {
        return forVarAbsDeclOrDecl(that);
    }

    public RetType forVarDecl(VarDecl that) {
        return forVarAbsDeclOrDecl(that);
    }

    public RetType forLValue(LValue that) {
        return forAbstractNode(that);
    }

    public RetType forLValueBind(LValueBind that) {
        return forLValue(that);
    }

    public RetType forUnpasting(Unpasting that) {
        return forLValue(that);
    }

    public RetType forUnpastingBind(UnpastingBind that) {
        return forUnpasting(that);
    }

    public RetType forUnpastingSplit(UnpastingSplit that) {
        return forUnpasting(that);
    }

    public RetType forFnAbsDeclOrDecl(FnAbsDeclOrDecl that) {
        return forAbstractNode(that);
    }

    public RetType forAbsFnDecl(AbsFnDecl that) {
        return forFnAbsDeclOrDecl(that);
    }

    public RetType forFnDecl(FnDecl that) {
        return forFnAbsDeclOrDecl(that);
    }

    public RetType forFnDef(FnDef that) {
        return forFnDecl(that);
    }

    public RetType forParam(Param that) {
        return forAbstractNode(that);
    }

    public RetType forNormalParam(NormalParam that) {
        return forParam(that);
    }

    public RetType forVarargsParam(VarargsParam that) {
        return forParam(that);
    }

    public RetType forDimUnitDecl(DimUnitDecl that) {
        return forAbstractNode(that);
    }

    public RetType forDimDecl(DimDecl that) {
        return forDimUnitDecl(that);
    }

    public RetType forUnitDecl(UnitDecl that) {
        return forDimUnitDecl(that);
    }

    public RetType forTestDecl(TestDecl that) {
        return forAbstractNode(that);
    }

    public RetType forPropertyDecl(PropertyDecl that) {
        return forAbstractNode(that);
    }

    public RetType forExternalSyntaxAbsDeclOrDecl(ExternalSyntaxAbsDeclOrDecl that) {
        return forAbstractNode(that);
    }

    public RetType forAbsExternalSyntax(AbsExternalSyntax that) {
        return forExternalSyntaxAbsDeclOrDecl(that);
    }

    public RetType forExternalSyntax(ExternalSyntax that) {
        return forExternalSyntaxAbsDeclOrDecl(that);
    }

    public RetType forGrammarDecl(GrammarDecl that) {
        return forAbstractNode(that);
    }

    public RetType forGrammarDef(GrammarDef that) {
        return forGrammarDecl(that);
    }

    public RetType forGrammarMemberDecl(GrammarMemberDecl that) {
        return forAbstractNode(that);
    }

    public RetType forNonterminalDecl(NonterminalDecl that) {
        return forGrammarMemberDecl(that);
    }

    public RetType forNonterminalDef(NonterminalDef that) {
        return forNonterminalDecl(that);
    }

    public RetType forNonterminalExtensionDef(NonterminalExtensionDef that) {
        return forNonterminalDecl(that);
    }

    public RetType forTerminalDecl(TerminalDecl that) {
        return forGrammarMemberDecl(that);
    }

    public RetType for_TerminalDef(_TerminalDef that) {
        return forTerminalDecl(that);
    }

    public RetType forSyntaxDecl(SyntaxDecl that) {
        return forAbstractNode(that);
    }

    public RetType forSyntaxDef(SyntaxDef that) {
        return forSyntaxDecl(that);
    }

    public RetType forSyntaxSymbol(SyntaxSymbol that) {
        return forAbstractNode(that);
    }

    public RetType forPrefixedSymbol(PrefixedSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forOptionalSymbol(OptionalSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forRepeatSymbol(RepeatSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forNoWhitespaceSymbol(NoWhitespaceSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forSpecialSymbol(SpecialSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forWhitespaceSymbol(WhitespaceSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forTabSymbol(TabSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forFormfeedSymbol(FormfeedSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forCarriageReturnSymbol(CarriageReturnSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forBackspaceSymbol(BackspaceSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forNewlineSymbol(NewlineSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forBreaklineSymbol(BreaklineSymbol that) {
        return forSpecialSymbol(that);
    }

    public RetType forItemSymbol(ItemSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forNonterminalSymbol(NonterminalSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forKeywordSymbol(KeywordSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forTokenSymbol(TokenSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forNotPredicateSymbol(NotPredicateSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forAndPredicateSymbol(AndPredicateSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forCharacterClassSymbol(CharacterClassSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forCharacterSymbol(CharacterSymbol that) {
        return forSyntaxSymbol(that);
    }

    public RetType forCharSymbol(CharSymbol that) {
        return forCharacterSymbol(that);
    }

    public RetType forCharacterInterval(CharacterInterval that) {
        return forCharacterSymbol(that);
    }

    public RetType forExpr(Expr that) {
        return forAbstractNode(that);
    }

    public RetType forTypeAnnotatedExpr(TypeAnnotatedExpr that) {
        return forExpr(that);
    }

    public RetType forAsExpr(AsExpr that) {
        return forTypeAnnotatedExpr(that);
    }

    public RetType forAsIfExpr(AsIfExpr that) {
        return forTypeAnnotatedExpr(that);
    }

    public RetType forAssignment(Assignment that) {
        return forExpr(that);
    }

    public RetType forDelimitedExpr(DelimitedExpr that) {
        return forExpr(that);
    }

    public RetType forBlock(Block that) {
        return forDelimitedExpr(that);
    }

    public RetType forCaseExpr(CaseExpr that) {
        return forDelimitedExpr(that);
    }

    public RetType forDo(Do that) {
        return forDelimitedExpr(that);
    }

    public RetType forFor(For that) {
        return forDelimitedExpr(that);
    }

    public RetType forIf(If that) {
        return forDelimitedExpr(that);
    }

    public RetType forLabel(Label that) {
        return forDelimitedExpr(that);
    }

    public RetType forAbstractObjectExpr(AbstractObjectExpr that) {
        return forDelimitedExpr(that);
    }

    public RetType forObjectExpr(ObjectExpr that) {
        return forAbstractObjectExpr(that);
    }

    public RetType for_RewriteObjectExpr(_RewriteObjectExpr that) {
        return forAbstractObjectExpr(that);
    }

    public RetType forTry(Try that) {
        return forDelimitedExpr(that);
    }

    public RetType forAbstractTupleExpr(AbstractTupleExpr that) {
        return forDelimitedExpr(that);
    }

    public RetType forTupleExpr(TupleExpr that) {
        return forAbstractTupleExpr(that);
    }

    public RetType forArgExpr(ArgExpr that) {
        return forAbstractTupleExpr(that);
    }

    public RetType forTypecase(Typecase that) {
        return forDelimitedExpr(that);
    }

    public RetType forWhile(While that) {
        return forDelimitedExpr(that);
    }

    public RetType forFlowExpr(FlowExpr that) {
        return forExpr(that);
    }

    public RetType forBigOprApp(BigOprApp that) {
        return forFlowExpr(that);
    }

    public RetType forAccumulator(Accumulator that) {
        return forBigOprApp(that);
    }

    public RetType forArrayComprehension(ArrayComprehension that) {
        return forBigOprApp(that);
    }

    public RetType forAtomicExpr(AtomicExpr that) {
        return forFlowExpr(that);
    }

    public RetType forExit(Exit that) {
        return forFlowExpr(that);
    }

    public RetType forSpawn(Spawn that) {
        return forFlowExpr(that);
    }

    public RetType forThrow(Throw that) {
        return forFlowExpr(that);
    }

    public RetType forTryAtomicExpr(TryAtomicExpr that) {
        return forFlowExpr(that);
    }

    public RetType forFnExpr(FnExpr that) {
        return forExpr(that);
    }

    public RetType forLetExpr(LetExpr that) {
        return forExpr(that);
    }

    public RetType forLetFn(LetFn that) {
        return forLetExpr(that);
    }

    public RetType forLocalVarDecl(LocalVarDecl that) {
        return forLetExpr(that);
    }

    public RetType forGeneratedExpr(GeneratedExpr that) {
        return forExpr(that);
    }

    public RetType forOpExpr(OpExpr that) {
        return forExpr(that);
    }

    public RetType forSubscriptExpr(SubscriptExpr that) {
        return forOpExpr(that);
    }

    public RetType forPrimary(Primary that) {
        return forOpExpr(that);
    }

    public RetType forLiteralExpr(LiteralExpr that) {
        return forPrimary(that);
    }

    public RetType forNumberLiteralExpr(NumberLiteralExpr that) {
        return forLiteralExpr(that);
    }

    public RetType forFloatLiteralExpr(FloatLiteralExpr that) {
        return forNumberLiteralExpr(that);
    }

    public RetType forIntLiteralExpr(IntLiteralExpr that) {
        return forNumberLiteralExpr(that);
    }

    public RetType forCharLiteralExpr(CharLiteralExpr that) {
        return forLiteralExpr(that);
    }

    public RetType forStringLiteralExpr(StringLiteralExpr that) {
        return forLiteralExpr(that);
    }

    public RetType forVoidLiteralExpr(VoidLiteralExpr that) {
        return forLiteralExpr(that);
    }

    public RetType forVarRef(VarRef that) {
        return forPrimary(that);
    }

    public RetType forAbstractFieldRef(AbstractFieldRef that) {
        return forPrimary(that);
    }

    public RetType forFieldRef(FieldRef that) {
        return forAbstractFieldRef(that);
    }

    public RetType forFieldRefForSure(FieldRefForSure that) {
        return forAbstractFieldRef(that);
    }

    public RetType for_RewriteFieldRef(_RewriteFieldRef that) {
        return forAbstractFieldRef(that);
    }

    public RetType forFunctionalRef(FunctionalRef that) {
        return forPrimary(that);
    }

    public RetType forFnRef(FnRef that) {
        return forFunctionalRef(that);
    }

    public RetType for_RewriteFnRef(_RewriteFnRef that) {
        return forFunctionalRef(that);
    }

    public RetType forOpRef(OpRef that) {
        return forFunctionalRef(that);
    }

    public RetType forAppExpr(AppExpr that) {
        return forPrimary(that);
    }

    public RetType forJuxt(Juxt that) {
        return forAppExpr(that);
    }

    public RetType forLooseJuxt(LooseJuxt that) {
        return forJuxt(that);
    }

    public RetType forTightJuxt(TightJuxt that) {
        return forJuxt(that);
    }

    public RetType forOprExpr(OprExpr that) {
        return forAppExpr(that);
    }

    public RetType forChainExpr(ChainExpr that) {
        return forAppExpr(that);
    }

    public RetType forCoercionInvocation(CoercionInvocation that) {
        return forAppExpr(that);
    }

    public RetType forMethodInvocation(MethodInvocation that) {
        return forAppExpr(that);
    }

    public RetType forMathPrimary(MathPrimary that) {
        return forPrimary(that);
    }

    public RetType forArrayExpr(ArrayExpr that) {
        return forPrimary(that);
    }

    public RetType forArrayElement(ArrayElement that) {
        return forArrayExpr(that);
    }

    public RetType forArrayElements(ArrayElements that) {
        return forArrayExpr(that);
    }

    public RetType forType(Type that) {
        return forAbstractNode(that);
    }

    public RetType forDimExpr(DimExpr that) {
        return forType(that);
    }

    public RetType forExponentType(ExponentType that) {
        return forDimExpr(that);
    }

    public RetType forBaseDim(BaseDim that) {
        return forDimExpr(that);
    }

    public RetType forDimRef(DimRef that) {
        return forDimExpr(that);
    }

    public RetType forProductDim(ProductDim that) {
        return forDimExpr(that);
    }

    public RetType forQuotientDim(QuotientDim that) {
        return forDimExpr(that);
    }

    public RetType forExponentDim(ExponentDim that) {
        return forDimExpr(that);
    }

    public RetType forOpDim(OpDim that) {
        return forDimExpr(that);
    }

    public RetType forAbstractArrowType(AbstractArrowType that) {
        return forType(that);
    }

    public RetType forArrowType(ArrowType that) {
        return forAbstractArrowType(that);
    }

    public RetType for_RewriteGenericArrowType(_RewriteGenericArrowType that) {
        return forAbstractArrowType(that);
    }

    public RetType forNonArrowType(NonArrowType that) {
        return forType(that);
    }

    public RetType forBottomType(BottomType that) {
        return forNonArrowType(that);
    }

    public RetType forTraitType(TraitType that) {
        return forNonArrowType(that);
    }

    public RetType forNamedType(NamedType that) {
        return forTraitType(that);
    }

    public RetType forIdType(IdType that) {
        return forNamedType(that);
    }

    public RetType forInstantiatedType(InstantiatedType that) {
        return forNamedType(that);
    }

    public RetType forAbbreviatedType(AbbreviatedType that) {
        return forTraitType(that);
    }

    public RetType forArrayType(ArrayType that) {
        return forAbbreviatedType(that);
    }

    public RetType forMatrixType(MatrixType that) {
        return forAbbreviatedType(that);
    }

    public RetType forAbstractTupleType(AbstractTupleType that) {
        return forNonArrowType(that);
    }

    public RetType forTupleType(TupleType that) {
        return forAbstractTupleType(that);
    }

    public RetType forArgType(ArgType that) {
        return forAbstractTupleType(that);
    }

    public RetType forVoidType(VoidType that) {
        return forNonArrowType(that);
    }

    public RetType forInferenceVarType(InferenceVarType that) {
        return forNonArrowType(that);
    }

    public RetType forAndType(AndType that) {
        return forNonArrowType(that);
    }

    public RetType forOrType(OrType that) {
        return forNonArrowType(that);
    }

    public RetType forFixedPointType(FixedPointType that) {
        return forNonArrowType(that);
    }

    public RetType forDimType(DimType that) {
        return forNonArrowType(that);
    }

    public RetType forTaggedDimType(TaggedDimType that) {
        return forDimType(that);
    }

    public RetType forTaggedUnitType(TaggedUnitType that) {
        return forDimType(that);
    }

    public RetType forStaticArg(StaticArg that) {
        return forType(that);
    }

    public RetType forIdArg(IdArg that) {
        return forStaticArg(that);
    }

    public RetType forTypeArg(TypeArg that) {
        return forStaticArg(that);
    }

    public RetType forIntArg(IntArg that) {
        return forStaticArg(that);
    }

    public RetType forBoolArg(BoolArg that) {
        return forStaticArg(that);
    }

    public RetType forOprArg(OprArg that) {
        return forStaticArg(that);
    }

    public RetType forDimArg(DimArg that) {
        return forStaticArg(that);
    }

    public RetType forUnitArg(UnitArg that) {
        return forStaticArg(that);
    }

    public RetType forStaticExpr(StaticExpr that) {
        return forAbstractNode(that);
    }

    public RetType forIntExpr(IntExpr that) {
        return forStaticExpr(that);
    }

    public RetType forIntVal(IntVal that) {
        return forIntExpr(that);
    }

    public RetType forNumberConstraint(NumberConstraint that) {
        return forIntVal(that);
    }

    public RetType forIntRef(IntRef that) {
        return forIntVal(that);
    }

    public RetType forIntOpExpr(IntOpExpr that) {
        return forIntExpr(that);
    }

    public RetType forSumConstraint(SumConstraint that) {
        return forIntOpExpr(that);
    }

    public RetType forMinusConstraint(MinusConstraint that) {
        return forIntOpExpr(that);
    }

    public RetType forProductConstraint(ProductConstraint that) {
        return forIntOpExpr(that);
    }

    public RetType forExponentConstraint(ExponentConstraint that) {
        return forIntOpExpr(that);
    }

    public RetType forBoolExpr(BoolExpr that) {
        return forStaticExpr(that);
    }

    public RetType forBoolVal(BoolVal that) {
        return forBoolExpr(that);
    }

    public RetType forBoolConstant(BoolConstant that) {
        return forBoolVal(that);
    }

    public RetType forBoolRef(BoolRef that) {
        return forBoolVal(that);
    }

    public RetType forBoolConstraint(BoolConstraint that) {
        return forBoolExpr(that);
    }

    public RetType forNotConstraint(NotConstraint that) {
        return forBoolConstraint(that);
    }

    public RetType forBinaryBoolConstraint(BinaryBoolConstraint that) {
        return forBoolConstraint(that);
    }

    public RetType forOrConstraint(OrConstraint that) {
        return forBinaryBoolConstraint(that);
    }

    public RetType forAndConstraint(AndConstraint that) {
        return forBinaryBoolConstraint(that);
    }

    public RetType forImpliesConstraint(ImpliesConstraint that) {
        return forBinaryBoolConstraint(that);
    }

    public RetType forBEConstraint(BEConstraint that) {
        return forBinaryBoolConstraint(that);
    }

    public RetType forWhereClause(WhereClause that) {
        return forAbstractNode(that);
    }

    public RetType forWhereBinding(WhereBinding that) {
        return forAbstractNode(that);
    }

    public RetType forWhereType(WhereType that) {
        return forWhereBinding(that);
    }

    public RetType forWhereNat(WhereNat that) {
        return forWhereBinding(that);
    }

    public RetType forWhereInt(WhereInt that) {
        return forWhereBinding(that);
    }

    public RetType forWhereBool(WhereBool that) {
        return forWhereBinding(that);
    }

    public RetType forWhereUnit(WhereUnit that) {
        return forWhereBinding(that);
    }

    public RetType forWhereConstraint(WhereConstraint that) {
        return forAbstractNode(that);
    }

    public RetType forWhereExtends(WhereExtends that) {
        return forWhereConstraint(that);
    }

    public RetType forTypeAlias(TypeAlias that) {
        return forWhereConstraint(that);
    }

    public RetType forWhereCoerces(WhereCoerces that) {
        return forWhereConstraint(that);
    }

    public RetType forWhereWidens(WhereWidens that) {
        return forWhereConstraint(that);
    }

    public RetType forWhereWidensCoerces(WhereWidensCoerces that) {
        return forWhereConstraint(that);
    }

    public RetType forWhereEquals(WhereEquals that) {
        return forWhereConstraint(that);
    }

    public RetType forUnitConstraint(UnitConstraint that) {
        return forWhereConstraint(that);
    }

    public RetType forIntConstraint(IntConstraint that) {
        return forWhereConstraint(that);
    }

    public RetType forLEConstraint(LEConstraint that) {
        return forIntConstraint(that);
    }

    public RetType forLTConstraint(LTConstraint that) {
        return forIntConstraint(that);
    }

    public RetType forGEConstraint(GEConstraint that) {
        return forIntConstraint(that);
    }

    public RetType forGTConstraint(GTConstraint that) {
        return forIntConstraint(that);
    }

    public RetType forIEConstraint(IEConstraint that) {
        return forIntConstraint(that);
    }

    public RetType forBoolConstraintExpr(BoolConstraintExpr that) {
        return forWhereConstraint(that);
    }

    public RetType forContract(Contract that) {
        return forAbstractNode(that);
    }

    public RetType forEnsuresClause(EnsuresClause that) {
        return forAbstractNode(that);
    }

    public RetType forModifier(Modifier that) {
        return forAbstractNode(that);
    }

    public RetType forModifierAbstract(ModifierAbstract that) {
        return forModifier(that);
    }

    public RetType forModifierAtomic(ModifierAtomic that) {
        return forModifier(that);
    }

    public RetType forModifierGetter(ModifierGetter that) {
        return forModifier(that);
    }

    public RetType forModifierHidden(ModifierHidden that) {
        return forModifier(that);
    }

    public RetType forModifierIO(ModifierIO that) {
        return forModifier(that);
    }

    public RetType forModifierOverride(ModifierOverride that) {
        return forModifier(that);
    }

    public RetType forModifierPrivate(ModifierPrivate that) {
        return forModifier(that);
    }

    public RetType forModifierSettable(ModifierSettable that) {
        return forModifier(that);
    }

    public RetType forModifierSetter(ModifierSetter that) {
        return forModifier(that);
    }

    public RetType forModifierTest(ModifierTest that) {
        return forModifier(that);
    }

    public RetType forModifierTransient(ModifierTransient that) {
        return forModifier(that);
    }

    public RetType forModifierValue(ModifierValue that) {
        return forModifier(that);
    }

    public RetType forModifierVar(ModifierVar that) {
        return forModifier(that);
    }

    public RetType forModifierWidens(ModifierWidens that) {
        return forModifier(that);
    }

    public RetType forModifierWrapped(ModifierWrapped that) {
        return forModifier(that);
    }

    public RetType forStaticParam(StaticParam that) {
        return forAbstractNode(that);
    }

    public RetType forOperatorParam(OperatorParam that) {
        return forStaticParam(that);
    }

    public RetType forIdStaticParam(IdStaticParam that) {
        return forStaticParam(that);
    }

    public RetType forBoolParam(BoolParam that) {
        return forIdStaticParam(that);
    }

    public RetType forDimensionParam(DimensionParam that) {
        return forIdStaticParam(that);
    }

    public RetType forIntParam(IntParam that) {
        return forIdStaticParam(that);
    }

    public RetType forNatParam(NatParam that) {
        return forIdStaticParam(that);
    }

    public RetType forSimpleTypeParam(SimpleTypeParam that) {
        return forIdStaticParam(that);
    }

    public RetType forUnitParam(UnitParam that) {
        return forIdStaticParam(that);
    }

    public RetType forName(Name that) {
        return forAbstractNode(that);
    }

    public RetType forAPIName(APIName that) {
        return forName(that);
    }

    public RetType forQualifiedName(QualifiedName that) {
        return forName(that);
    }

    public RetType forQualifiedIdName(QualifiedIdName that) {
        return forQualifiedName(that);
    }

    public RetType forQualifiedOpName(QualifiedOpName that) {
        return forQualifiedName(that);
    }

    public RetType forSimpleName(SimpleName that) {
        return forName(that);
    }

    public RetType forId(Id that) {
        return forSimpleName(that);
    }

    public RetType forOpName(OpName that) {
        return forSimpleName(that);
    }

    public RetType forOp(Op that) {
        return forOpName(that);
    }

    public RetType forEnclosing(Enclosing that) {
        return forOpName(that);
    }

    public RetType forAnonymousFnName(AnonymousFnName that) {
        return forSimpleName(that);
    }

    public RetType forConstructorFnName(ConstructorFnName that) {
        return forSimpleName(that);
    }

    public RetType forArrayComprehensionClause(ArrayComprehensionClause that) {
        return forAbstractNode(that);
    }

    public RetType forKeywordExpr(KeywordExpr that) {
        return forAbstractNode(that);
    }

    public RetType forCaseClause(CaseClause that) {
        return forAbstractNode(that);
    }

    public RetType forCatch(Catch that) {
        return forAbstractNode(that);
    }

    public RetType forCatchClause(CatchClause that) {
        return forAbstractNode(that);
    }

    public RetType forDoFront(DoFront that) {
        return forAbstractNode(that);
    }

    public RetType forIfClause(IfClause that) {
        return forAbstractNode(that);
    }

    public RetType forTypecaseClause(TypecaseClause that) {
        return forAbstractNode(that);
    }

    public RetType forExtentRange(ExtentRange that) {
        return forAbstractNode(that);
    }

    public RetType forGeneratorClause(GeneratorClause that) {
        return forAbstractNode(that);
    }

    public RetType forVarargsExpr(VarargsExpr that) {
        return forAbstractNode(that);
    }

    public RetType forVarargsType(VarargsType that) {
        return forAbstractNode(that);
    }

    public RetType forKeywordType(KeywordType that) {
        return forAbstractNode(that);
    }

    public RetType forTraitTypeWhere(TraitTypeWhere that) {
        return forAbstractNode(that);
    }

    public RetType forIndices(Indices that) {
        return forAbstractNode(that);
    }

    public RetType forMathItem(MathItem that) {
        return forAbstractNode(that);
    }

    public RetType forExprMI(ExprMI that) {
        return forMathItem(that);
    }

    public RetType forParenthesisDelimitedMI(ParenthesisDelimitedMI that) {
        return forExprMI(that);
    }

    public RetType forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that) {
        return forExprMI(that);
    }

    public RetType forNonExprMI(NonExprMI that) {
        return forMathItem(that);
    }

    public RetType forExponentiationMI(ExponentiationMI that) {
        return forNonExprMI(that);
    }

    public RetType forSubscriptingMI(SubscriptingMI that) {
        return forNonExprMI(that);
    }

    public RetType forFixity(Fixity that) {
        return forAbstractNode(that);
    }

    public RetType forInFixity(InFixity that) {
        return forFixity(that);
    }

    public RetType forPreFixity(PreFixity that) {
        return forFixity(that);
    }

    public RetType forPostFixity(PostFixity that) {
        return forFixity(that);
    }

    public RetType forNoFixity(NoFixity that) {
        return forFixity(that);
    }

    public RetType forMultiFixity(MultiFixity that) {
        return forFixity(that);
    }

    public RetType forEnclosingFixity(EnclosingFixity that) {
        return forFixity(that);
    }

    public RetType forBigFixity(BigFixity that) {
        return forFixity(that);
    }


}
