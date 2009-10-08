package net.sickill.finishhim

import scala.collection.mutable.HashMap
import org.gjt.sp.jedit.View
import org.gjt.sp.util.Log

object FinishHimExecutor {
  val completors = new HashMap[View, FinishHimCompletor]()
  
  def execute(view: View) = {
    log("executing...")
    getCompletorForView(view).complete()
  }
  
  def getCompletorForView(view: View) : FinishHimCompletor = {
    completors.get(view) match {
      case None => val x = new FinishHimCompletor(view); completors(view) = x; x
      case Some(x) => x
    }
  }
  
  def log(msg: String) = {
    Log.log(Log.DEBUG, this, msg)
  }
}