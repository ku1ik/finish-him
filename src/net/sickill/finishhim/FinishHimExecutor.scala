package net.sickill.finishhim

import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.Buffer
import org.gjt.sp.jedit.textarea.JEditTextArea
import org.gjt.sp.util.Log

object FinishHimExecutor {
  def execute(view: View) = {
    Log.log(Log.DEBUG, this, "executing...")
    val buffer = view.getBuffer()
    val textArea = view.getTextArea()
    val caret = textArea.getCaretPosition()
    buffer.insert(caret, ":)")
    Log.log(Log.DEBUG, this, "done!")
  }
}