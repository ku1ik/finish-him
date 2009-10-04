package net.sickill.finishhim

import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.gui.DefaultInputHandler
import org.gjt.sp.jedit.gui.KeyEventTranslator 
import org.gjt.sp.util.Log

class FinishHimInputHandler(view: View) extends DefaultInputHandler(view) {
  val defaultInputHandler = view.getInputHandler()
  
  override def handleKey(keyStroke: KeyEventTranslator.Key, dryRun: Boolean) : Boolean = {
    val r = defaultInputHandler.handleKey(keyStroke, dryRun)
    if (FinishHimExecutor.invoked) {
      FinishHimExecutor.invoked = false
      Log.log(Log.DEBUG, this, "invoked completion action")
    } else if (FinishHimExecutor.completing) {
      FinishHimExecutor.completing = false
      Log.log(Log.DEBUG, this, "finished completion by pressing " + keyStroke)
    }
    r
  }
}