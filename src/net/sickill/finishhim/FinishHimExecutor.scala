package net.sickill.finishhim

import scala.collection.mutable.HashMap
import org.gjt.sp.jedit.View
import org.gjt.sp.util.Log

object FinishHimExecutor {
  val completors = new HashMap[View, FinishHimCompletor]()
  
  def execute(view: View) = {
    log("executing...")
    completors.getOrElseUpdate(view, new FinishHimCompletor(view)).complete()
  }
  
  def log(msg: String) = {
    Log.log(Log.DEBUG, this, msg)
  }
}