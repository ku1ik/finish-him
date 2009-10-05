package net.sickill.finishhim

import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.Buffer
import org.gjt.sp.jedit.textarea.JEditTextArea
import org.gjt.sp.jedit.textarea.Selection
import org.gjt.sp.util.Log
import org.gjt.sp.jedit.TextUtilities

object FinishHimExecutor {
  var completing = false
  var invoked = false
  var wordList: List[String] = List()
  var nextWordIndex = 0
  var buffer: Buffer = null
  var textArea: JEditTextArea = null
  var caret = 0
  var prefixLength = 0
  var suggestedWordLength = 0
  var caretLine = 0
  
  def execute(view: View) = {
    invoked = true
    log("executing...")
    if (completing) {
      log("continuing completion...")
      complete(view)
    } else {
      log("starting completion...")
      if (setup(view)) {
        complete(view)
      }
    }
  }
  
  def setup(view: View) : Boolean = {
    nextWordIndex = 0
    buffer = view.getBuffer()
    textArea = view.getTextArea()
    caret = textArea.getCaretPosition()
    caretLine = textArea.getCaretLine()
    val prefix = getPrefix()
    if (prefix != null) {
      log("FinishHim: found prefix: " + prefix)
      prefixLength = prefix.length()
      suggestedWordLength = prefixLength
      if (buildWordList(prefix, buffer.getText(0, buffer.getLength()))) {
        completing = true
        true
      } else {
        false
      }
    } else {
      log("FinishHim: empty prefix, leaving")
      false
    }
  }
  
  def buildWordList(prefix: String, bufferText: String) = {
    log("FinishHim: buildWordList")
    wordList = List.fromArray(bufferText.split("[^\\w]+")).removeDuplicates.filter { word => word.startsWith(prefix) } - prefix
    log("FinishHim: wordList = " + wordList)
    !wordList.isEmpty
  }
  
  def getPrefix() : String = {
    log("FinishHim: getPrefix")
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
  
  def complete(view: View) = {
    log("FinishHim: complete")
    val nextWord = wordList(nextWordIndex)
    textArea.setSelection(new Selection.Range(caret, caret - prefixLength + suggestedWordLength))
    textArea.replaceSelection(nextWord.substring(prefixLength))
    suggestedWordLength = nextWord.length()
    nextWordIndex += 1
    if (nextWordIndex >= wordList.size) nextWordIndex = 0
    log("done!")
  }
  
  def log(msg: String) = {
    Log.log(Log.DEBUG, this, msg)
  }
}