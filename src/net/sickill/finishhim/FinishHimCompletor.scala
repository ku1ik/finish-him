package net.sickill.finishhim

import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.Buffer
import org.gjt.sp.jedit.textarea.JEditTextArea
import org.gjt.sp.jedit.textarea.Selection
import org.gjt.sp.util.Log
import org.gjt.sp.jedit.TextUtilities
import org.gjt.sp.jedit.jEdit
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter
import org.gjt.sp.jedit.EditPane
import scala.collection.jcl.ArrayList

class FinishHimCompletor(view: View) {
  var wordList: List[String] = List()
  var nextWordIndex = 0
  var buffer: Buffer = null
  var textArea: JEditTextArea = null
  var caret = 0
  var prefixLength = 0
  var suggestedWordLength = 0
  var caretLine = 0
  
  def firstInvocation() : Boolean = view.getInputHandler().getLastActionCount == 1

  def setup() = {
    log("setup()")
    nextWordIndex = 0
    buffer = view.getBuffer()
    textArea = view.getTextArea()
    caret = textArea.getCaretPosition()
    caretLine = textArea.getCaretLine()
    val prefix = getPrefix()
    if (prefix != null) {
      log("found prefix: " + prefix)
      prefixLength = prefix.length()
      suggestedWordLength = prefixLength
      buildWordList(prefix, getVisibleBuffers())
    } else {
      log("empty prefix, leaving")
      wordList = List()
    }
  }
  
  def complete() : Unit = {
    log("complete()")
    
    if (firstInvocation()) {
      setup()
    }
    
		if (!buffer.isEditable()) {
			textArea.getToolkit().beep()
			return
		}
    
    if (wordList.isEmpty) {
			textArea.getToolkit().beep()
    } else {
      val nextWord = wordList(nextWordIndex)
      textArea.setSelection(new Selection.Range(caret, caret - prefixLength + suggestedWordLength))
      textArea.replaceSelection(nextWord.substring(prefixLength))
      suggestedWordLength = nextWord.length()
      nextWordIndex = (nextWordIndex + 1) % wordList.size
    }
  }

  def getVisibleBuffers() =	{
    val buffers = new ArrayList[Buffer]()
    jEdit.visit(new JEditVisitorAdapter() {
      override def visit(editPane: EditPane) : Unit = {
        buffers.add(editPane.getBuffer())
      }
    })
    buffers
  }
  
  def buildWordList(prefix: String, buffers: ArrayList[Buffer]) = {
    log("buildWordList()")
    var buffersText = ""
    for (buffer <- buffers) {
      buffersText += " " + buffer.getText(0, buffer.getLength())
    }
    wordList = List.fromArray(buffersText.split("[^\\w]+")).removeDuplicates.filter { word => word.startsWith(prefix) } - prefix
    log("wordList = " + wordList)
  }
  
  def getPrefix() : String = {
    log("getPrefix()")
		val line = buffer.getLineSegment(caretLine)
		val dot = caret - buffer.getLineStartOffset(caretLine)
		if (dot == 0) return null
		val ch = line.charAt(dot-1)
		if (!Character.isLetterOrDigit(ch)) return null
		val wordStartPos = TextUtilities.findWordStart(line, dot-1, "")
		val prefix = line.subSequence(wordStartPos, dot)
		if (prefix.length() == 0) return null
		prefix.toString()
  }

  def log(msg: String) = {
    FinishHimExecutor.log("FinishHimCompletor: " + msg)
  }

}
