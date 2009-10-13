package net.sickill.finishhim

import scala.collection.mutable.HashMap
import org.gjt.sp.jedit.View

object FinishHimExecutor {
  val completors = new HashMap[View, FinishHimCompletor]()
  
  def execute(view: View) = {
    completors.getOrElseUpdate(view, new FinishHimCompletor(view)).complete()
  }  
}