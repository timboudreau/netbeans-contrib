package org.netbeans.modules.scala.core.interactive
import scala.tools.nsc._

import java.io.{ PrintWriter, StringWriter }

import java.util.logging.Logger
import scala.collection.mutable.{LinkedHashMap, SynchronizedMap}
import scala.concurrent.SyncVar
import scala.util.control.ControlException
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util.{SourceFile, Position, RangePosition, OffsetPosition, NoPosition}
import scala.tools.nsc.reporters._
import scala.tools.nsc.symtab._
import scala.tools.nsc.ast._

// ======= Modifed by Caoyuan

/** The main class of the presentation compiler in an interactive environment such as an IDE
 */
class Global(settings: Settings, reporter: Reporter) 
extends scala.tools.nsc.Global(settings, reporter) 
   with CompilerControl
   with RangePositions
   with ContextTrees
   with RichCompilationUnits {
  self =>

  import definitions._

  final val debugIDE = false

  protected val GlobalLog = Logger.getLogger(this.getClass.getName)

  override def onlyPresentation = true

  /** A list indicating in which order some units should be typechecked.
   *  All units in firsts are typechecked before any unit not in this list
   *  Modified by askToDoFirst, reload, typeAtTree.
   */
  var firsts: List[SourceFile] = List()

  /** A map of all loaded files to the rich compilation units that correspond to them.
   */ 
  val unitOfFile = new LinkedHashMap[AbstractFile, RichCompilationUnit] with
  SynchronizedMap[AbstractFile, RichCompilationUnit]

  /** The currently active typer run */
  var currentTyperRun: TyperRun = _

  /** Is a background compiler run needed? */
  private var outOfDate = false
  var currentAction: () => Unit = _

  /** Units compiled by a run with id >= minRunId are considered up-to-date  */
  private[interactive] var minRunId = 1

  /** Is a reload/background compiler currently running? */
  private var acting = false

  // ----------- Overriding hooks in nsc.Global -----------------------
  
  /** Called from typechecker, which signal hereby that a node has been completely typechecked.
   *  If the node is included in unit.targetPos, abandons run and returns newly attributed tree.
   *  Otherwise, if there's some higher priority work to be done, also abandons run with a FreshRunReq.
   *  @param  context  The context that typechecked the node
   *  @param  old      The original node
   *  @param  result   The transformed node
   */
  override def signalDone(context: Context, old: Tree, result: Tree) {
    def integrateNew() {
      context.unit.body = new TreeReplacer(old, result) transform context.unit.body
    }
    if (activeLocks == 0) {
      if (context.unit != null && 
          result.pos.isOpaqueRange && 
          (result.pos includes context.unit.targetPos)) {
        integrateNew()
        var located = new Locator(context.unit.targetPos) locateIn result
        if (located == EmptyTree) {
          println("something's wrong: no "+context.unit+" in "+result+result.pos)
          located = result
        }
        throw new TyperResult(located)
      }
      val typerRun = currentTyperRun
      
      while(true) 
        try {
          pollForWork()
          if (typerRun == currentTyperRun)
            return
            
          integrateNew()
          throw new FreshRunReq
        } catch {
          case ex : ValidateError => // Ignore, this will have been reported elsewhere
          case t : Throwable => throw t
        }
    }
  }

  /** Called from typechecker every time a context is created.
   *  Registers the context in a context tree
   */
  override def registerContext(c: Context) = c.unit match {
    case u: RichCompilationUnit => addContext(u.contexts, c)
    case _ =>
  }

  // ----------------- Polling ---------------------------------------

  /** Called from runner thread and signalDone:
   *  Poll for exeptions. 
   *  Poll for work reload/typedTreeAt/doFirst commands during background checking.
   */
  def pollForWork() {
    scheduler.pollException() match {
      case Some(ex: CancelActionReq) => if (acting) throw ex
      case Some(ex: FreshRunReq) => 
        currentTyperRun = new TyperRun()
        minRunId = currentRunId
        if (outOfDate) throw ex 
        else outOfDate = true
      case Some(ex: Throwable) => throw ex
      case _ =>
    }
    scheduler.nextWorkItem() match {
      case Some(action) =>
        try {
          acting = true
          currentAction = action
          if (debugIDE) println("picked up work item: "+action)
          action()
          if (debugIDE) println("done with work item: "+action)
        } catch {
          case ex: CancelActionReq =>
            if (debugIDE) println("cancelled work item: "+action)
        } finally {
          if (debugIDE) println("quitting work item: "+action)
          acting = false
          currentAction = null
        }
      case None =>
    }
  }    

  def debugInfo(source : SourceFile, start : Int, length : Int): String = {
    println("DEBUG INFO "+source+"/"+start+"/"+length)
    val end = start+length
    val pos = rangePos(source, start, start, end)

    val tree = locateTree(pos)
    val sw = new StringWriter
    val pw = new PrintWriter(sw)
    treePrinters.create(pw).print(tree)
    pw.flush
    
    val typed = new Response[Tree]
    askTypeAt(pos, false, typed)
    val typ = typed.get.left.toOption match {
      case Some(tree) =>
        val sw = new StringWriter
        val pw = new PrintWriter(sw)
        treePrinters.create(pw).print(tree)
        pw.flush
        sw.toString
      case None => "<None>"      
    }

    val completionResponse = new Response[List[Member]]
    askTypeCompletion(pos, false, completionResponse)
    val completion = completionResponse.get.left.toOption match {
      case Some(members) =>
        members mkString "\n"
      case None => "<None>"      
    }
    
    source.content.view.drop(start).take(length).mkString+" : "+source.path+" ("+start+", "+end+
    ")\n\nlocateTree:\n"+sw.toString+"\n\naskTypeAt:\n"+typ+"\n\ncompletion:\n"+completion
  }

  // ----------------- The Background Runner Thread -----------------------

  /** The current presentation compiler runner */
  private var compileRunner = newRunnerThread

  /** Create a new presentation compiler runner.
   */
  def newRunnerThread: Thread = new Thread("Scala Presentation Compiler") {
    override def run() {
      try {
        while (true) {
          scheduler.waitForMoreWork()
          pollForWork()
          while (outOfDate) {
            try {
              backgroundCompile()
              outOfDate = false
            } catch {
              case ex: FreshRunReq =>
            }
          }
        }
      } catch {
        case ex: InterruptedException =>
          Thread.currentThread.interrupt // interrupt again to avoid posible out-loop issue
        case ex: ShutdownReq =>
          GlobalLog.info("ShutdownReq processed")
          Thread.currentThread.interrupt
        case ex => 
          GlobalLog.info(ex.getClass.getSimpleName + " processed, will start a newRunnerThread")
          outOfDate = false
          compileRunner = newRunnerThread
          ex match { 
            case _ : ValidateError => // This will have been reported elsewhere
            case _ => ex.printStackTrace(); inform("Fatal Error: "+ex)
          }
      }
    }
    start()
  }

  /** Compile all given units
   */ 
  private def backgroundCompile() {
    if (debugIDE) inform("Starting new presentation compiler type checking pass")
    reporter.reset
    firsts = firsts filter (s => unitOfFile contains (s.file))
    val prefix = firsts map unitOf
    val units = prefix ::: (unitOfFile.valuesIterator.toList diff prefix) filter (!_.isUpToDate)
    recompile(units)
    if (debugIDE) inform("Everything is now up to date")
  }

  /** Reset unit to just-parsed state */
  def reset(unit: RichCompilationUnit): Unit =
    if (unit.status > JustParsed) {
      unit.depends.clear()
      unit.defined.clear()
      unit.synthetics.clear()
      unit.toCheck.clear()
      unit.targetPos = NoPosition
      unit.contexts.clear()
      unit.body = EmptyTree
      unit.status = NotLoaded
    }

  /** Parse unit and create a name index. */
  def parse(unit: RichCompilationUnit): Unit = {
    val start = System.currentTimeMillis
    currentTyperRun.compileLate(unit)
    if (!reporter.hasErrors) validatePositions(unit.body)
    GlobalLog.info("Parse took " + (System.currentTimeMillis - start) + "ms")
    //println("parsed: [["+unit.body+"]]")
    unit.status = JustParsed
  }

  /** Make sure symbol and type attributes are reset and recompile units. 
   */
  def recompile(units: List[RichCompilationUnit]) {
    for (unit <- units) {
      reset(unit)
      if (debugIDE) inform("parsing: "+unit)
      parse(unit)
    }
    for (unit <- units) {
      if (debugIDE) inform("type checking: "+unit)
      activeLocks = 0
      currentTyperRun.typeCheck(unit)
      unit.status = currentRunId
    }
  }

  /** Move list of files to front of firsts */
  def moveToFront(fs: List[SourceFile]) {
    firsts = fs ::: (firsts diff fs)
  }

  // ----------------- Implementations of client commmands -----------------------
  
  def respond[T](result: Response[T])(op: => T): Unit = try {
    result set Left(op)
  } catch {
    case ex =>
      result set Right(ex)
      throw ex
  }

  /** Make sure a set of compilation units is loaded and parsed */
  def reloadSources(sources: List[SourceFile]) {
    currentTyperRun = new TyperRun()
    for (source <- sources) {
      val unit = new RichCompilationUnit(source)
      unitOfFile(source.file) = unit
      parse(unit)
    }
    moveToFront(sources)
  }

  /** Make sure a set of compilation units is loaded and parsed */
  def reload(sources: List[SourceFile], result: Response[Unit]) {
    respond(result)(reloadSources(sources))
    if (outOfDate) throw new FreshRunReq
    else outOfDate = true
  }

  /** A fully attributed tree located at position `pos`  */
  def typedTreeAt(pos: Position, forceReload: Boolean): Tree = {
    val unit = unitOf(pos)
    val sources = List(pos.source)
    if (unit.status == NotLoaded || forceReload) reloadSources(sources)
    moveToFront(sources)
    val typedTree = currentTyperRun.typedTreeAt(pos)
    new Locator(pos) locateIn typedTree
  }

  /** A fully attributed tree corresponding to the entire compilation unit  */
  def typedTree(source: SourceFile, forceReload: Boolean): Tree = {
    val unit = unitOf(source)
    val sources = List(source)
    if (unit.status == NotLoaded || forceReload) reloadSources(sources)
    moveToFront(sources)
    currentTyperRun.typedTree(unitOf(source))
  }

  /** Set sync var `result` to a fully attributed tree located at position `pos`  */
  def getTypedTreeAt(pos: Position, forceReload: Boolean, result: Response[Tree]) {
    respond(result)(typedTreeAt(pos, forceReload))
  }

  /** Set sync var `result` to a fully attributed tree corresponding to the entire compilation unit  */
  def getTypedTree(source : SourceFile, forceReload: Boolean, result: Response[Tree]) {
    respond(result)(typedTree(source, forceReload))
  }

  def stabilizedType(tree: Tree): Type = tree match {
    case Ident(_) if tree.symbol.isStable => singleType(NoPrefix, tree.symbol)
    case Select(qual, _) if tree.symbol.isStable => singleType(qual.tpe, tree.symbol)
    case _ => tree.tpe
  }

  import analyzer.{SearchResult, ImplicitSearch}

  def getScopeCompletion(pos: Position, forceReload: Boolean, result: Response[List[Member]]) {
    respond(result) { scopeMembers(pos, forceReload) }
  }

  val Dollar = newTermName("$")

  /** Return all members visible without prefix in context enclosing `pos`. */
  def scopeMembers(pos: Position, forceReload: Boolean): List[ScopeMember] = {
    typedTreeAt(pos, forceReload) // to make sure context is entered
    val context = try {
      doLocateContext(pos)
    } catch {case ex => ex.printStackTrace; NoContext}

    val locals = new LinkedHashMap[Name, ScopeMember]
    def addScopeMember(sym: Symbol, pre: Type, viaImport: Tree) =
      if (!sym.name.decode.containsName(Dollar) &&
          !sym.hasFlag(Flags.SYNTHETIC) &&
          !locals.contains(sym.name)) {
        //println("adding scope member: "+pre+" "+sym)
        val tpe = try {
          pre.memberType(sym)
        } catch {case ex => ex.printStackTrace; NoPrefix}

        locals(sym.name) = new ScopeMember(
          sym,
          tpe,
          context.isAccessible(sym, pre, false),
          viaImport)
      }
    var cx = context
    while (cx != NoContext) {
      for (sym <- cx.scope)
        addScopeMember(sym, NoPrefix, EmptyTree)

      cx = cx.enclMethod
      if (cx != NoContext) {
        for (sym <- cx.scope)
          addScopeMember(sym, NoPrefix, EmptyTree)
      }

      cx = cx.enclClass
      val pre = cx.prefix
      for (sym <- pre.members)
        addScopeMember(sym, pre, EmptyTree)
      cx = cx.outer
    }
    for (imp <- context.imports) {
      val pre = imp.qual.tpe
      for (sym <- imp.allImportedSymbols) {
        addScopeMember(sym, pre, imp.qual)
      }
    }
    val result = locals.valuesIterator.toList
    if (debugIDE) for (m <- result) println(m)
    result
  }

  def getTypeCompletion(pos: Position, forceReload: Boolean, result: Response[List[Member]]) {
    respond(result) { typeMembers(pos, forceReload) }
    if (debugIDE) scopeMembers(pos, forceReload)
  }

  def typeMembers(pos: Position, forceReload: Boolean): List[TypeMember] = {
    val tree = typedTreeAt(pos, forceReload: Boolean)
    GlobalLog.info("Get typeMembers at treeType=" + tree.getClass.getSimpleName + ", tree=" + tree + ", tpe=" + tree.tpe)

    val tpe = tree.tpe match {
      case x@(null | ErrorType | NoType) =>
        recoveredType(tree) match {
          case Some(x) => x.resultType
          case None =>
            GlobalLog.warning("Tree type is null or error: tree=" + tree + ", tpe=" + x + ", but we have qualToRecoveredType=" + qualToRecoveredType)
            return Nil
        }
      case x => x.resultType
    }

    val isPackage = tpe.typeSymbol hasFlag Flags.PACKAGE

    val context = try {
      doLocateContext(pos)
    } catch {case ex => println(ex.getMessage); NoContext}
    val superAccess = tree.isInstanceOf[Super]
    val scope = newScope
    val members = new LinkedHashMap[Symbol, TypeMember]
    def addTypeMember(sym: Symbol, pre: Type, inherited: Boolean, viaView: Symbol) {
      val symtpe = pre.memberType(sym)
      if (scope.lookupAll(sym.name) forall (sym => !(members(sym).tpe matches symtpe))) {
        scope enter sym
        members(sym) = new TypeMember(
          sym,
          symtpe,
          context.isAccessible(sym, pre, superAccess && (viaView == NoSymbol)),
          inherited,
          viaView)
      }
    }

    def addPackageMember(sym: Symbol, pre: Type, inherited: Boolean, viaView: Symbol) {
      // * don't ask symtpe here via pre.memberType(sym) or sym.tpe, which may throw "no-symbol does not have owner" in doComplete
      members(sym) = new TypeMember(
        sym,
        NoPrefix,
        context.isAccessible(sym, pre, false),
        inherited,
        viaView)
    }

    def viewApply(view: SearchResult): Tree = {
      assert(view.tree != EmptyTree)
      try {
        analyzer.newTyper(context.makeImplicit(false)).typed(Apply(view.tree, List(tree)) setPos tree.pos)
      } catch {
        case ex: TypeError => EmptyTree
      }
    }

    // ----- begin adding members

    if (isPackage) {
      val pre = tpe
      for (sym <- tpe.members if !sym.isError && sym.nameString.indexOf('$') == -1) {
        addPackageMember(sym, pre, false, NoSymbol)
      }
    } else {

      val pre = try {
        stabilizedType(tree) match {
          case null => tpe
          case x => x
        }
      } catch {case ex => println(ex.getMessage); tpe}

      try {
        for (sym <- tpe.decls) {
          addTypeMember(sym, pre, false, NoSymbol)
        }
      } catch {case ex => ex.printStackTrace}

      try {
        for (sym <- tpe.members) {
          addTypeMember(sym, pre, true, NoSymbol)
        }
      } catch {case ex => ex.printStackTrace}

      try {
        val applicableViews: List[SearchResult] =
          new ImplicitSearch(tree, definitions.functionType(List(tpe), definitions.AnyClass.tpe), true, context.makeImplicit(false)).allImplicits

        for (view <- applicableViews) {
          val vtree = viewApply(view)
          val vpre = stabilizedType(vtree)
          for (sym <- vtree.tpe.members) {
            addTypeMember(sym, vpre, false, view.tree.symbol)
          }
        }
      } catch {case ex => ex.printStackTrace}

    }

    members.valuesIterator.toList
  }

  final def recoveredType(tree: Tree): Option[Type] = {
    def find(atree: Tree) = qualToRecoveredType.get(atree) match {
      case Some(tpe) => Some(tpe)
      case None => qualToRecoveredType find {
          case (Select(qual, _), _) => qual == atree
          case (SelectFromTypeTree(qual, _), _) => qual == atree
          case (Apply(fun, _), _) => fun == atree
          case (x, _) => x == atree // usaully Ident tree
        } match {
          case Some((_, tpe)) => Some(tpe)
          case None => None
        }
    }
    
    find(tree) match {
      case None =>
        tree match {
          case Select(qual, _) => find(qual)
          case SelectFromTypeTree(qual, _) => find(qual)
          case Apply(fun, _) => find(fun)
          case _ => None
        }
      case x => x
    }

  }

  // ---------------- Helper classes ---------------------------

  /** A transformer that replaces tree `from` with tree `to` in a given tree */
  class TreeReplacer(from: Tree, to: Tree) extends Transformer {
    override def transform(t: Tree): Tree = {
      if (t == from) to
      else if ((t.pos includes from.pos) || t.pos.isTransparent) super.transform(t)
      else t
    }
  }

  /** A traverser that resets all type and symbol attributes in a tree
   object ResetAttrs extends Transformer {
   override def transform(t: Tree): Tree = {
   if (t.hasSymbol) t.symbol = NoSymbol
   t match {
   case EmptyTree =>
   t
   case tt: TypeTree =>
   if (tt.original != null) tt.original
   else t
   case _ =>
   t.tpe = null
   super.transform(t)
   }
   }
   }
   */

  /** The typer run */
  class TyperRun extends Run {
    // units is always empty
    // symSource, symData are ignored
    override def compiles(sym: Symbol) = false

    // * added by Caoyuan
    // phaseName = "lambdalift"
    /* object lambdaLiftInteractive extends {
     val global: Global.this.type = Global.this
     val runsAfter = List[String]("lazyvals")
     val runsRightAfter = None
     } with LambdaLift */

    def lambdaLiftedTree(unit: RichCompilationUnit): Tree = {
      assert(unit.status >= JustParsed)
      unit.targetPos = NoPosition
      enterSuperAccessors(unit)
      enterPickler(unit)
      enterRefChecks(unit)
      enterUncurry(unit)
      enterExplicitOuter(unit)
      enterLambdaLift(unit)
      unit.body
    }

    val superAccessorsPhaseInter = superAccessors.newPhase(typerPhase)
    val picklerPhaseInter = pickler.newPhase(superAccessorsPhaseInter)
    val refchecksPhaseInter = refchecks.newPhase(picklerPhaseInter)
    val uncurryPhaseInter = uncurry.newPhase(refchecksPhaseInter)
    val explicitOuterPhaseInter = explicitOuter.newPhase(uncurryPhaseInter)
    val lambdaLiftPhaseInter = lambdaLift.newPhase(explicitOuterPhaseInter)

    def enterSuperAccessors(unit: CompilationUnit): Unit = applyPhase(superAccessorsPhaseInter, unit)
    def enterPickler(unit: CompilationUnit): Unit = applyPhase(picklerPhaseInter, unit)
    def enterRefChecks(unit: CompilationUnit): Unit = applyPhase(refchecksPhaseInter, unit)
    def enterUncurry(unit: CompilationUnit): Unit = applyPhase(uncurryPhaseInter, unit)
    def enterExplicitOuter(unit: CompilationUnit): Unit = applyPhase(explicitOuterPhaseInter, unit)
    def enterLambdaLift(unit: CompilationUnit): Unit = applyPhase(lambdaLiftPhaseInter, unit)
 
    // * end added by Caoyuan

    def typeCheck(unit: CompilationUnit): Unit = applyPhase(typerPhase, unit)

    def enterNames(unit: CompilationUnit): Unit = applyPhase(namerPhase, unit)

    /** Return fully attributed tree at given position
     *  (i.e. largest tree that's contained by position)
     */
    def typedTreeAt(pos: Position): Tree = {
      println("starting typedTreeAt")
      val tree = locateTree(pos)
      println("at pos "+pos+" was found: "+tree+tree.pos.show)
      if (tree.tpe ne null) {
        println("already attributed")
        tree
      } else {
        val unit = unitOf(pos)
        assert(unit.status >= JustParsed)
        unit.targetPos = pos
        try {
          println("starting targeted type check")
          typeCheck(unit)
          throw new FatalError("tree not found")
        } catch {
          case ex: TyperResult => 
            ex.tree
        } finally {
          unit.targetPos = NoPosition
        }
      }
    } 

    def typedTree(unit: RichCompilationUnit): Tree = {
      assert(unit.status >= JustParsed)
      unit.targetPos = NoPosition
      val start = System.currentTimeMillis
      typeCheck(unit)
      GlobalLog.info("Typer took " + (System.currentTimeMillis - start) + "ms")
      unit.body
    } 

    /** Apply a phase to a compilation unit
     *  @return true iff typechecked correctly
     */
    private def applyPhase(phase: Phase, unit: CompilationUnit) {
      val oldSource = reporter.getSource          
      try {
        reporter.setSource(unit.source)    
        atPhase(phase) { phase.asInstanceOf[GlobalPhase] applyPhase unit }
      } finally {
        reporter setSource oldSource
      }
    }
  }

  class TyperResult(val tree: Tree) extends Exception with ControlException
  
  assert(globalPhase.id == 0)
}

