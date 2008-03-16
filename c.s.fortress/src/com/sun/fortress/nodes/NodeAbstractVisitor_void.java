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
 ** This visitor implements the visitor interface with methods that each 
 ** delegate to a case representing their superclass.  At the top of this
 ** delegation tree is the method defaultCase(), which (unless overridden)
 ** is a no-op.
 **/
public class NodeAbstractVisitor_void extends NodeVisitorRunnable1 {
    /**
     * This method is run for all cases that are not handled elsewhere.
     * By default, it is a no-op; subclasses may override this behavior.
    **/
    public void defaultCase(Node that) {}

    /* Methods to visit an item. */
    public void forAbstractNode(AbstractNode that) {
        defaultCase(that);
    }

    public void forCompilationUnit(CompilationUnit that) {
        forAbstractNode(that);
    }

    public void forComponent(Component that) {
        forCompilationUnit(that);
    }

    public void forApi(Api that) {
        forCompilationUnit(that);
    }

    public void forImport(Import that) {
        forAbstractNode(that);
    }

    public void forImportedNames(ImportedNames that) {
        forImport(that);
    }

    public void forImportStar(ImportStar that) {
        forImportedNames(that);
    }

    public void forImportNames(ImportNames that) {
        forImportedNames(that);
    }

    public void forImportApi(ImportApi that) {
        forImport(that);
    }

    public void forAliasedSimpleName(AliasedSimpleName that) {
        forAbstractNode(that);
    }

    public void forAliasedAPIName(AliasedAPIName that) {
        forAbstractNode(that);
    }

    public void forExport(Export that) {
        forAbstractNode(that);
    }

    public void forTraitObjectAbsDeclOrDecl(TraitObjectAbsDeclOrDecl that) {
        forAbstractNode(that);
    }

    public void forTraitAbsDeclOrDecl(TraitAbsDeclOrDecl that) {
        forTraitObjectAbsDeclOrDecl(that);
    }

    public void forAbsTraitDecl(AbsTraitDecl that) {
        forTraitAbsDeclOrDecl(that);
    }

    public void forTraitDecl(TraitDecl that) {
        forTraitAbsDeclOrDecl(that);
    }

    public void forObjectAbsDeclOrDecl(ObjectAbsDeclOrDecl that) {
        forTraitObjectAbsDeclOrDecl(that);
    }

    public void forAbsObjectDecl(AbsObjectDecl that) {
        forObjectAbsDeclOrDecl(that);
    }

    public void forObjectDecl(ObjectDecl that) {
        forObjectAbsDeclOrDecl(that);
    }

    public void forVarAbsDeclOrDecl(VarAbsDeclOrDecl that) {
        forAbstractNode(that);
    }

    public void forAbsVarDecl(AbsVarDecl that) {
        forVarAbsDeclOrDecl(that);
    }

    public void forVarDecl(VarDecl that) {
        forVarAbsDeclOrDecl(that);
    }

    public void forLValue(LValue that) {
        forAbstractNode(that);
    }

    public void forLValueBind(LValueBind that) {
        forLValue(that);
    }

    public void forUnpasting(Unpasting that) {
        forLValue(that);
    }

    public void forUnpastingBind(UnpastingBind that) {
        forUnpasting(that);
    }

    public void forUnpastingSplit(UnpastingSplit that) {
        forUnpasting(that);
    }

    public void forFnAbsDeclOrDecl(FnAbsDeclOrDecl that) {
        forAbstractNode(that);
    }

    public void forAbsFnDecl(AbsFnDecl that) {
        forFnAbsDeclOrDecl(that);
    }

    public void forFnDecl(FnDecl that) {
        forFnAbsDeclOrDecl(that);
    }

    public void forFnDef(FnDef that) {
        forFnDecl(that);
    }

    public void forParam(Param that) {
        forAbstractNode(that);
    }

    public void forNormalParam(NormalParam that) {
        forParam(that);
    }

    public void forVarargsParam(VarargsParam that) {
        forParam(that);
    }

    public void forDimUnitDecl(DimUnitDecl that) {
        forAbstractNode(that);
    }

    public void forDimDecl(DimDecl that) {
        forDimUnitDecl(that);
    }

    public void forUnitDecl(UnitDecl that) {
        forDimUnitDecl(that);
    }

    public void forTestDecl(TestDecl that) {
        forAbstractNode(that);
    }

    public void forPropertyDecl(PropertyDecl that) {
        forAbstractNode(that);
    }

    public void forExternalSyntaxAbsDeclOrDecl(ExternalSyntaxAbsDeclOrDecl that) {
        forAbstractNode(that);
    }

    public void forAbsExternalSyntax(AbsExternalSyntax that) {
        forExternalSyntaxAbsDeclOrDecl(that);
    }

    public void forExternalSyntax(ExternalSyntax that) {
        forExternalSyntaxAbsDeclOrDecl(that);
    }

    public void forGrammarDecl(GrammarDecl that) {
        forAbstractNode(that);
    }

    public void forGrammarDef(GrammarDef that) {
        forGrammarDecl(that);
    }

    public void forGrammarMemberDecl(GrammarMemberDecl that) {
        forAbstractNode(that);
    }

    public void forNonterminalDecl(NonterminalDecl that) {
        forGrammarMemberDecl(that);
    }

    public void forNonterminalDef(NonterminalDef that) {
        forNonterminalDecl(that);
    }

    public void forNonterminalExtensionDef(NonterminalExtensionDef that) {
        forNonterminalDecl(that);
    }

    public void forTerminalDecl(TerminalDecl that) {
        forGrammarMemberDecl(that);
    }

    public void for_TerminalDef(_TerminalDef that) {
        forTerminalDecl(that);
    }

    public void forSyntaxDecl(SyntaxDecl that) {
        forAbstractNode(that);
    }

    public void forSyntaxDef(SyntaxDef that) {
        forSyntaxDecl(that);
    }

    public void forSyntaxSymbol(SyntaxSymbol that) {
        forAbstractNode(that);
    }

    public void forPrefixedSymbol(PrefixedSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forOptionalSymbol(OptionalSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forRepeatSymbol(RepeatSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forNoWhitespaceSymbol(NoWhitespaceSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forSpecialSymbol(SpecialSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forWhitespaceSymbol(WhitespaceSymbol that) {
        forSpecialSymbol(that);
    }

    public void forTabSymbol(TabSymbol that) {
        forSpecialSymbol(that);
    }

    public void forFormfeedSymbol(FormfeedSymbol that) {
        forSpecialSymbol(that);
    }

    public void forCarriageReturnSymbol(CarriageReturnSymbol that) {
        forSpecialSymbol(that);
    }

    public void forBackspaceSymbol(BackspaceSymbol that) {
        forSpecialSymbol(that);
    }

    public void forNewlineSymbol(NewlineSymbol that) {
        forSpecialSymbol(that);
    }

    public void forBreaklineSymbol(BreaklineSymbol that) {
        forSpecialSymbol(that);
    }

    public void forItemSymbol(ItemSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forNonterminalSymbol(NonterminalSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forKeywordSymbol(KeywordSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forTokenSymbol(TokenSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forNotPredicateSymbol(NotPredicateSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forAndPredicateSymbol(AndPredicateSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forCharacterClassSymbol(CharacterClassSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forCharacterSymbol(CharacterSymbol that) {
        forSyntaxSymbol(that);
    }

    public void forCharSymbol(CharSymbol that) {
        forCharacterSymbol(that);
    }

    public void forCharacterInterval(CharacterInterval that) {
        forCharacterSymbol(that);
    }

    public void forExpr(Expr that) {
        forAbstractNode(that);
    }

    public void forTypeAnnotatedExpr(TypeAnnotatedExpr that) {
        forExpr(that);
    }

    public void forAsExpr(AsExpr that) {
        forTypeAnnotatedExpr(that);
    }

    public void forAsIfExpr(AsIfExpr that) {
        forTypeAnnotatedExpr(that);
    }

    public void forAssignment(Assignment that) {
        forExpr(that);
    }

    public void forDelimitedExpr(DelimitedExpr that) {
        forExpr(that);
    }

    public void forBlock(Block that) {
        forDelimitedExpr(that);
    }

    public void forCaseExpr(CaseExpr that) {
        forDelimitedExpr(that);
    }

    public void forDo(Do that) {
        forDelimitedExpr(that);
    }

    public void forFor(For that) {
        forDelimitedExpr(that);
    }

    public void forIf(If that) {
        forDelimitedExpr(that);
    }

    public void forLabel(Label that) {
        forDelimitedExpr(that);
    }

    public void forAbstractObjectExpr(AbstractObjectExpr that) {
        forDelimitedExpr(that);
    }

    public void forObjectExpr(ObjectExpr that) {
        forAbstractObjectExpr(that);
    }

    public void for_RewriteObjectExpr(_RewriteObjectExpr that) {
        forAbstractObjectExpr(that);
    }

    public void forTry(Try that) {
        forDelimitedExpr(that);
    }

    public void forAbstractTupleExpr(AbstractTupleExpr that) {
        forDelimitedExpr(that);
    }

    public void forTupleExpr(TupleExpr that) {
        forAbstractTupleExpr(that);
    }

    public void forArgExpr(ArgExpr that) {
        forAbstractTupleExpr(that);
    }

    public void forTypecase(Typecase that) {
        forDelimitedExpr(that);
    }

    public void forWhile(While that) {
        forDelimitedExpr(that);
    }

    public void forFlowExpr(FlowExpr that) {
        forExpr(that);
    }

    public void forBigOprApp(BigOprApp that) {
        forFlowExpr(that);
    }

    public void forAccumulator(Accumulator that) {
        forBigOprApp(that);
    }

    public void forArrayComprehension(ArrayComprehension that) {
        forBigOprApp(that);
    }

    public void forAtomicExpr(AtomicExpr that) {
        forFlowExpr(that);
    }

    public void forExit(Exit that) {
        forFlowExpr(that);
    }

    public void forSpawn(Spawn that) {
        forFlowExpr(that);
    }

    public void forThrow(Throw that) {
        forFlowExpr(that);
    }

    public void forTryAtomicExpr(TryAtomicExpr that) {
        forFlowExpr(that);
    }

    public void forFnExpr(FnExpr that) {
        forExpr(that);
    }

    public void forLetExpr(LetExpr that) {
        forExpr(that);
    }

    public void forLetFn(LetFn that) {
        forLetExpr(that);
    }

    public void forLocalVarDecl(LocalVarDecl that) {
        forLetExpr(that);
    }

    public void forGeneratedExpr(GeneratedExpr that) {
        forExpr(that);
    }

    public void forOpExpr(OpExpr that) {
        forExpr(that);
    }

    public void forSubscriptExpr(SubscriptExpr that) {
        forOpExpr(that);
    }

    public void forPrimary(Primary that) {
        forOpExpr(that);
    }

    public void forLiteralExpr(LiteralExpr that) {
        forPrimary(that);
    }

    public void forNumberLiteralExpr(NumberLiteralExpr that) {
        forLiteralExpr(that);
    }

    public void forFloatLiteralExpr(FloatLiteralExpr that) {
        forNumberLiteralExpr(that);
    }

    public void forIntLiteralExpr(IntLiteralExpr that) {
        forNumberLiteralExpr(that);
    }

    public void forCharLiteralExpr(CharLiteralExpr that) {
        forLiteralExpr(that);
    }

    public void forStringLiteralExpr(StringLiteralExpr that) {
        forLiteralExpr(that);
    }

    public void forVoidLiteralExpr(VoidLiteralExpr that) {
        forLiteralExpr(that);
    }

    public void forVarRef(VarRef that) {
        forPrimary(that);
    }

    public void forAbstractFieldRef(AbstractFieldRef that) {
        forPrimary(that);
    }

    public void forFieldRef(FieldRef that) {
        forAbstractFieldRef(that);
    }

    public void forFieldRefForSure(FieldRefForSure that) {
        forAbstractFieldRef(that);
    }

    public void for_RewriteFieldRef(_RewriteFieldRef that) {
        forAbstractFieldRef(that);
    }

    public void forFunctionalRef(FunctionalRef that) {
        forPrimary(that);
    }

    public void forFnRef(FnRef that) {
        forFunctionalRef(that);
    }

    public void for_RewriteFnRef(_RewriteFnRef that) {
        forFunctionalRef(that);
    }

    public void forOpRef(OpRef that) {
        forFunctionalRef(that);
    }

    public void forAppExpr(AppExpr that) {
        forPrimary(that);
    }

    public void forJuxt(Juxt that) {
        forAppExpr(that);
    }

    public void forLooseJuxt(LooseJuxt that) {
        forJuxt(that);
    }

    public void forTightJuxt(TightJuxt that) {
        forJuxt(that);
    }

    public void forOprExpr(OprExpr that) {
        forAppExpr(that);
    }

    public void forChainExpr(ChainExpr that) {
        forAppExpr(that);
    }

    public void forCoercionInvocation(CoercionInvocation that) {
        forAppExpr(that);
    }

    public void forMethodInvocation(MethodInvocation that) {
        forAppExpr(that);
    }

    public void forMathPrimary(MathPrimary that) {
        forPrimary(that);
    }

    public void forArrayExpr(ArrayExpr that) {
        forPrimary(that);
    }

    public void forArrayElement(ArrayElement that) {
        forArrayExpr(that);
    }

    public void forArrayElements(ArrayElements that) {
        forArrayExpr(that);
    }

    public void forType(Type that) {
        forAbstractNode(that);
    }

    public void forDimExpr(DimExpr that) {
        forType(that);
    }

    public void forExponentType(ExponentType that) {
        forDimExpr(that);
    }

    public void forBaseDim(BaseDim that) {
        forDimExpr(that);
    }

    public void forDimRef(DimRef that) {
        forDimExpr(that);
    }

    public void forProductDim(ProductDim that) {
        forDimExpr(that);
    }

    public void forQuotientDim(QuotientDim that) {
        forDimExpr(that);
    }

    public void forExponentDim(ExponentDim that) {
        forDimExpr(that);
    }

    public void forOpDim(OpDim that) {
        forDimExpr(that);
    }

    public void forAbstractArrowType(AbstractArrowType that) {
        forType(that);
    }

    public void forArrowType(ArrowType that) {
        forAbstractArrowType(that);
    }

    public void for_RewriteGenericArrowType(_RewriteGenericArrowType that) {
        forAbstractArrowType(that);
    }

    public void forNonArrowType(NonArrowType that) {
        forType(that);
    }

    public void forBottomType(BottomType that) {
        forNonArrowType(that);
    }

    public void forTraitType(TraitType that) {
        forNonArrowType(that);
    }

    public void forNamedType(NamedType that) {
        forTraitType(that);
    }

    public void forIdType(IdType that) {
        forNamedType(that);
    }

    public void forInstantiatedType(InstantiatedType that) {
        forNamedType(that);
    }

    public void forAbbreviatedType(AbbreviatedType that) {
        forTraitType(that);
    }

    public void forArrayType(ArrayType that) {
        forAbbreviatedType(that);
    }

    public void forMatrixType(MatrixType that) {
        forAbbreviatedType(that);
    }

    public void forAbstractTupleType(AbstractTupleType that) {
        forNonArrowType(that);
    }

    public void forTupleType(TupleType that) {
        forAbstractTupleType(that);
    }

    public void forArgType(ArgType that) {
        forAbstractTupleType(that);
    }

    public void forVoidType(VoidType that) {
        forNonArrowType(that);
    }

    public void forInferenceVarType(InferenceVarType that) {
        forNonArrowType(that);
    }

    public void forAndType(AndType that) {
        forNonArrowType(that);
    }

    public void forOrType(OrType that) {
        forNonArrowType(that);
    }

    public void forFixedPointType(FixedPointType that) {
        forNonArrowType(that);
    }

    public void forDimType(DimType that) {
        forNonArrowType(that);
    }

    public void forTaggedDimType(TaggedDimType that) {
        forDimType(that);
    }

    public void forTaggedUnitType(TaggedUnitType that) {
        forDimType(that);
    }

    public void forStaticArg(StaticArg that) {
        forType(that);
    }

    public void forIdArg(IdArg that) {
        forStaticArg(that);
    }

    public void forTypeArg(TypeArg that) {
        forStaticArg(that);
    }

    public void forIntArg(IntArg that) {
        forStaticArg(that);
    }

    public void forBoolArg(BoolArg that) {
        forStaticArg(that);
    }

    public void forOprArg(OprArg that) {
        forStaticArg(that);
    }

    public void forDimArg(DimArg that) {
        forStaticArg(that);
    }

    public void forUnitArg(UnitArg that) {
        forStaticArg(that);
    }

    public void forStaticExpr(StaticExpr that) {
        forAbstractNode(that);
    }

    public void forIntExpr(IntExpr that) {
        forStaticExpr(that);
    }

    public void forIntVal(IntVal that) {
        forIntExpr(that);
    }

    public void forNumberConstraint(NumberConstraint that) {
        forIntVal(that);
    }

    public void forIntRef(IntRef that) {
        forIntVal(that);
    }

    public void forIntOpExpr(IntOpExpr that) {
        forIntExpr(that);
    }

    public void forSumConstraint(SumConstraint that) {
        forIntOpExpr(that);
    }

    public void forMinusConstraint(MinusConstraint that) {
        forIntOpExpr(that);
    }

    public void forProductConstraint(ProductConstraint that) {
        forIntOpExpr(that);
    }

    public void forExponentConstraint(ExponentConstraint that) {
        forIntOpExpr(that);
    }

    public void forBoolExpr(BoolExpr that) {
        forStaticExpr(that);
    }

    public void forBoolVal(BoolVal that) {
        forBoolExpr(that);
    }

    public void forBoolConstant(BoolConstant that) {
        forBoolVal(that);
    }

    public void forBoolRef(BoolRef that) {
        forBoolVal(that);
    }

    public void forBoolConstraint(BoolConstraint that) {
        forBoolExpr(that);
    }

    public void forNotConstraint(NotConstraint that) {
        forBoolConstraint(that);
    }

    public void forBinaryBoolConstraint(BinaryBoolConstraint that) {
        forBoolConstraint(that);
    }

    public void forOrConstraint(OrConstraint that) {
        forBinaryBoolConstraint(that);
    }

    public void forAndConstraint(AndConstraint that) {
        forBinaryBoolConstraint(that);
    }

    public void forImpliesConstraint(ImpliesConstraint that) {
        forBinaryBoolConstraint(that);
    }

    public void forBEConstraint(BEConstraint that) {
        forBinaryBoolConstraint(that);
    }

    public void forWhereClause(WhereClause that) {
        forAbstractNode(that);
    }

    public void forWhereBinding(WhereBinding that) {
        forAbstractNode(that);
    }

    public void forWhereType(WhereType that) {
        forWhereBinding(that);
    }

    public void forWhereNat(WhereNat that) {
        forWhereBinding(that);
    }

    public void forWhereInt(WhereInt that) {
        forWhereBinding(that);
    }

    public void forWhereBool(WhereBool that) {
        forWhereBinding(that);
    }

    public void forWhereUnit(WhereUnit that) {
        forWhereBinding(that);
    }

    public void forWhereConstraint(WhereConstraint that) {
        forAbstractNode(that);
    }

    public void forWhereExtends(WhereExtends that) {
        forWhereConstraint(that);
    }

    public void forTypeAlias(TypeAlias that) {
        forWhereConstraint(that);
    }

    public void forWhereCoerces(WhereCoerces that) {
        forWhereConstraint(that);
    }

    public void forWhereWidens(WhereWidens that) {
        forWhereConstraint(that);
    }

    public void forWhereWidensCoerces(WhereWidensCoerces that) {
        forWhereConstraint(that);
    }

    public void forWhereEquals(WhereEquals that) {
        forWhereConstraint(that);
    }

    public void forUnitConstraint(UnitConstraint that) {
        forWhereConstraint(that);
    }

    public void forIntConstraint(IntConstraint that) {
        forWhereConstraint(that);
    }

    public void forLEConstraint(LEConstraint that) {
        forIntConstraint(that);
    }

    public void forLTConstraint(LTConstraint that) {
        forIntConstraint(that);
    }

    public void forGEConstraint(GEConstraint that) {
        forIntConstraint(that);
    }

    public void forGTConstraint(GTConstraint that) {
        forIntConstraint(that);
    }

    public void forIEConstraint(IEConstraint that) {
        forIntConstraint(that);
    }

    public void forBoolConstraintExpr(BoolConstraintExpr that) {
        forWhereConstraint(that);
    }

    public void forContract(Contract that) {
        forAbstractNode(that);
    }

    public void forEnsuresClause(EnsuresClause that) {
        forAbstractNode(that);
    }

    public void forModifier(Modifier that) {
        forAbstractNode(that);
    }

    public void forModifierAbstract(ModifierAbstract that) {
        forModifier(that);
    }

    public void forModifierAtomic(ModifierAtomic that) {
        forModifier(that);
    }

    public void forModifierGetter(ModifierGetter that) {
        forModifier(that);
    }

    public void forModifierHidden(ModifierHidden that) {
        forModifier(that);
    }

    public void forModifierIO(ModifierIO that) {
        forModifier(that);
    }

    public void forModifierOverride(ModifierOverride that) {
        forModifier(that);
    }

    public void forModifierPrivate(ModifierPrivate that) {
        forModifier(that);
    }

    public void forModifierSettable(ModifierSettable that) {
        forModifier(that);
    }

    public void forModifierSetter(ModifierSetter that) {
        forModifier(that);
    }

    public void forModifierTest(ModifierTest that) {
        forModifier(that);
    }

    public void forModifierTransient(ModifierTransient that) {
        forModifier(that);
    }

    public void forModifierValue(ModifierValue that) {
        forModifier(that);
    }

    public void forModifierVar(ModifierVar that) {
        forModifier(that);
    }

    public void forModifierWidens(ModifierWidens that) {
        forModifier(that);
    }

    public void forModifierWrapped(ModifierWrapped that) {
        forModifier(that);
    }

    public void forStaticParam(StaticParam that) {
        forAbstractNode(that);
    }

    public void forOperatorParam(OperatorParam that) {
        forStaticParam(that);
    }

    public void forIdStaticParam(IdStaticParam that) {
        forStaticParam(that);
    }

    public void forBoolParam(BoolParam that) {
        forIdStaticParam(that);
    }

    public void forDimensionParam(DimensionParam that) {
        forIdStaticParam(that);
    }

    public void forIntParam(IntParam that) {
        forIdStaticParam(that);
    }

    public void forNatParam(NatParam that) {
        forIdStaticParam(that);
    }

    public void forSimpleTypeParam(SimpleTypeParam that) {
        forIdStaticParam(that);
    }

    public void forUnitParam(UnitParam that) {
        forIdStaticParam(that);
    }

    public void forName(Name that) {
        forAbstractNode(that);
    }

    public void forAPIName(APIName that) {
        forName(that);
    }

    public void forQualifiedName(QualifiedName that) {
        forName(that);
    }

    public void forQualifiedIdName(QualifiedIdName that) {
        forQualifiedName(that);
    }

    public void forQualifiedOpName(QualifiedOpName that) {
        forQualifiedName(that);
    }

    public void forSimpleName(SimpleName that) {
        forName(that);
    }

    public void forId(Id that) {
        forSimpleName(that);
    }

    public void forOpName(OpName that) {
        forSimpleName(that);
    }

    public void forOp(Op that) {
        forOpName(that);
    }

    public void forEnclosing(Enclosing that) {
        forOpName(that);
    }

    public void forAnonymousFnName(AnonymousFnName that) {
        forSimpleName(that);
    }

    public void forConstructorFnName(ConstructorFnName that) {
        forSimpleName(that);
    }

    public void forArrayComprehensionClause(ArrayComprehensionClause that) {
        forAbstractNode(that);
    }

    public void forKeywordExpr(KeywordExpr that) {
        forAbstractNode(that);
    }

    public void forCaseClause(CaseClause that) {
        forAbstractNode(that);
    }

    public void forCatch(Catch that) {
        forAbstractNode(that);
    }

    public void forCatchClause(CatchClause that) {
        forAbstractNode(that);
    }

    public void forDoFront(DoFront that) {
        forAbstractNode(that);
    }

    public void forIfClause(IfClause that) {
        forAbstractNode(that);
    }

    public void forTypecaseClause(TypecaseClause that) {
        forAbstractNode(that);
    }

    public void forExtentRange(ExtentRange that) {
        forAbstractNode(that);
    }

    public void forGeneratorClause(GeneratorClause that) {
        forAbstractNode(that);
    }

    public void forVarargsExpr(VarargsExpr that) {
        forAbstractNode(that);
    }

    public void forVarargsType(VarargsType that) {
        forAbstractNode(that);
    }

    public void forKeywordType(KeywordType that) {
        forAbstractNode(that);
    }

    public void forTraitTypeWhere(TraitTypeWhere that) {
        forAbstractNode(that);
    }

    public void forIndices(Indices that) {
        forAbstractNode(that);
    }

    public void forMathItem(MathItem that) {
        forAbstractNode(that);
    }

    public void forExprMI(ExprMI that) {
        forMathItem(that);
    }

    public void forParenthesisDelimitedMI(ParenthesisDelimitedMI that) {
        forExprMI(that);
    }

    public void forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that) {
        forExprMI(that);
    }

    public void forNonExprMI(NonExprMI that) {
        forMathItem(that);
    }

    public void forExponentiationMI(ExponentiationMI that) {
        forNonExprMI(that);
    }

    public void forSubscriptingMI(SubscriptingMI that) {
        forNonExprMI(that);
    }

    public void forFixity(Fixity that) {
        forAbstractNode(that);
    }

    public void forInFixity(InFixity that) {
        forFixity(that);
    }

    public void forPreFixity(PreFixity that) {
        forFixity(that);
    }

    public void forPostFixity(PostFixity that) {
        forFixity(that);
    }

    public void forNoFixity(NoFixity that) {
        forFixity(that);
    }

    public void forMultiFixity(MultiFixity that) {
        forFixity(that);
    }

    public void forEnclosingFixity(EnclosingFixity that) {
        forFixity(that);
    }

    public void forBigFixity(BigFixity that) {
        forFixity(that);
    }

}
