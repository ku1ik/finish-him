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
  var prefix: String = null
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
    if (findPrefix()) {
      log("found prefix: " + prefix)
      prefixLength = prefix.length()
      suggestedWordLength = prefixLength
      buildWordList()
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
        val b = editPane.getBuffer()
        if (b != buffer) {
          buffers.add(b)
        }
      }
    })
    buffers
  }
  
  def buildWordList() = {
    log("buildWordList()")
    // add words before caret and reverse their position and words after caret
    wordList = getWordsFromBuffer(buffer, 0, caret).reverse ++ getWordsFromBuffer(buffer, caret, buffer.getLength())
    // add words from other buffers
    for (buffer <- getVisibleBuffers()) {
      wordList = wordList ++ getWordsFromBuffer(buffer, 0, buffer.getLength())
    }
    // remove duplicated words
    wordList = wordList.removeDuplicates
    log("wordList = " + wordList)
  }
  
  def getWordsFromBuffer(buffer: Buffer, start: Int, end: Int) : List[String] = {
    getWordsFromString(buffer.getText(start, end-start))
  }
  
  def getWordsFromString(s: String) : List[String] = {
    List.fromArray(s.split("[^\\w]+")).filter { word => word.startsWith(prefix) } - prefix
  }
  
  def findPrefix() : Boolean = {
    log("getPrefix()")
		val line = buffer.getLineSegment(caretLine)
		val dot = caret - buffer.getLineStartOffset(caretLine)
		if (dot == 0) return false
		val ch = line.charAt(dot-1)
		if (!Character.isLetterOrDigit(ch)) return false
		val wordStartPos = TextUtilities.findWordStart(line, dot-1, "")
		val prfx = line.subSequence(wordStartPos, dot)
		if (prfx.length() == 0) return false
		prefix = prfx.toString()
		true
  }

  def log(msg: String) = {
    FinishHimExecutor.log("FinishHimCompletor: " + msg)
  }

}
