package org.netbeans.modules.scala.core.interactive
import scala.tools.nsc._

import scala.concurrent.SyncVar
import scala.util.control.ControlException
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util.{SourceFile, Position}
import scala.tools.nsc.symtab._
import scala.tools.nsc.ast._

/** Interface of interactive compiler to a client such as an IDE
 */
trait CompilerControl { self: Global =>

  /** Response {
    override def toString = "TypeMember("+sym+","+tpe+","+accessible+","+inherited+","+viaView+")"
  }{
    override def toString = "TypeMember("+sym+","+tpe+","+accessible+","+inherited+","+viaView+")"
  }wrapper to client
   */ 
  type Response[T] = SyncVar[Either[T, Throwable]]

  abstract class WorkItem(val sources: List[SourceFile]) extends (() => Unit)

  /** Info given for every member found by completion
   */
  abstract class Member {
    val sym: Symbol 
    val tpe: Type
    val accessible: Boolean
  }

  case class TypeMember(sym: Symbol, tpe: Type, accessible: Boolean, inherited: Boolean, viaView: Symbol) extends Member
  case class ScopeMember(sym: Symbol, tpe: Type, accessible: Boolean, viaImport: Tree) extends Member

  /** The scheduler by which client and compiler communicate
   *  Must be initialized before starting compilerRunner
   */
  protected val scheduler = new WorkScheduler
  
  /** The compilation unit corresponding to a source file
   */
  def unitOf(s: SourceFile): RichCompilationUnit = unitOfFile get s.file match {
    case Some(unit) => 
      unit
    case None => 
      val unit = new RichCompilationUnit(s)
      unitOfFile(s.file) = unit
      unit
  }
  
  /** The compilation unit corresponding to a position */
  def unitOf(pos: Position): RichCompilationUnit = unitOf(pos.source)

  /** Remove the CompilationUnit corresponding to the given SourceFile
   *  from consideration for recompilation.
   */
  def removeUnitOf(s: SourceFile) = unitOfFile remove s.file

  /** Locate smallest tree that encloses position
   */
  def locateTree(pos: Position): Tree = 
    new Locator(pos) locateIn unitOf(pos).body
    
  /** Locates smallest context that encloses position as an optional value.
   */
  def locateContext(pos: Position): Option[Context] = 
    locateContext(unitOf(pos).contexts, pos)

  /** Returns the smallest context that contains given `pos`, throws FatalError if none exists.
   */
  def doLocateContext(pos: Position): Context = locateContext(pos) getOrElse {
    throw new FatalError("no context found for "+pos)
  }
    
  /** Make sure a set of compilation units is loaded and parsed.
   *  Return () to syncvar `result` on completion.
   */
  def askReload(sources: List[SourceFile], result: Response[Unit]) = 
    scheduler postWorkItem new WorkItem(sources) {
      def apply() = reload(sources, result)
      override def toString = "reload "+sources
    }

  /** Set sync var `result` to a fully attributed tree located at position `pos`
   */
  def askTypeAt(pos: Position, result: Response[Tree]) = 
    scheduler postWorkItem new WorkItem(List(pos.source)) {
      def apply() = self.getTypedTreeAt(pos, result)
      override def toString = "typeat "+pos.source+" "+pos.show
    }

  def askType(source: SourceFile, forceReload: Boolean, result: Response[Tree]) =
    scheduler postWorkItem new WorkItem(List(source)) {
      def apply() = self.getTypedTree(source, forceReload, result)
      override def toString = "typecheck"
  }
  
  /** Set sync var `result' to list of members that are visible
   *  as members of the tree enclosing `pos`, possibly reachable by an implicit.
   *   - if `selection` is false, as identifiers in the scope enclosing `pos`
   */
  def askTypeCompletion(pos: Position, result: Response[List[Member]]) = 
    scheduler postWorkItem new WorkItem(List(pos.source)) {
      def apply() = self.getTypeCompletion(pos, result)
      override def toString = "type completion "+pos.source+" "+pos.show
    }

  /** Set sync var `result' to list of members that are visible
   *  as members of the scope enclosing `pos`.
   */
  def askScopeCompletion(pos: Position, result: Response[List[Member]]) = 
    scheduler postWorkItem new WorkItem(List(pos.source)) {
      def apply() = self.getScopeCompletion(pos, result)
      override def toString = "scope completion "+pos.source+" "+pos.show
    }

  /** Ask to do unit first on present and subsequent type checking passes */
  def askToDoFirst(f: SourceFile) = {
    scheduler postWorkItem new WorkItem(List(f)) {
      def apply() = moveToFront(List(f))
      override def toString = "dofirst "+f
    }
  }

  /** Cancel currently pending high-priority jobs */
  def askCancel() = 
    scheduler.raise(new CancelActionReq)

  /** Cancel current compiler run and start a fresh one where everything will be re-typechecked
   *  (but not re-loaded).
   */
  def askReset() = {
    scheduler.raise(new FreshRunReq)
    scheduler postWorkItem {() => println("A action to awake scheduler to process reset except")}
  }

  /**
   * Tell the compile server to shutdown, and do not restart again
   * In interactive.Global, the `newRunnerThread` always waits for `scheduler.waitForMoreWork()`
   * before `pollForWork()`, which may cause raised `except`s never have chance to be polled, if
   * there is no more `WorkItem` in `todo` queue, so I have to post another Action to awake it.
   * @Ticket #2289
   */
  def askShutdown() = {
    scheduler.raise(new ShutdownReq)
    scheduler postWorkItem {() => println("A action to awake scheduler to process shutdown except")}
  }

  // ---------------- Interpreted exeptions -------------------

  class CancelActionReq extends Exception with ControlException
  class FreshRunReq extends Exception with ControlException
  class ShutdownReq extends Exception with ControlException

}