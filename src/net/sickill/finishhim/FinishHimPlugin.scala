package net.sickill.finishhim

import org.gjt.sp.jedit.jEdit
import org.gjt.sp.jedit.EBMessage
import org.gjt.sp.jedit.EditPlugin
import org.gjt.sp.util.Log
import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.msg.ViewUpdate

class FinishHimPlugin extends EditPlugin {
  override def start = {
    for(view <- jEdit.getViews()) {
      initView(view);
    }
  }
  
  override def stop = {
    for(view <- jEdit.getViews()) {
      unInitView(view);
    }
  }
  
  def initView(view: View) = {
    Log.log(Log.DEBUG, this, "initView("+view+")");
    val ih = view.getInputHandler()
    if (!ih.isInstanceOf[FinishHimInputHandler]) {
      view.setInputHandler(new FinishHimInputHandler(view))
    }
  }
  
  def unInitView(view: View) = {
    Log.log(Log.DEBUG, this, "unInitView("+view+")");
    val ih = view.getInputHandler()
    if (ih.isInstanceOf[FinishHimInputHandler]) {
      view.setInputHandler(ih.asInstanceOf[FinishHimInputHandler].defaultInputHandler)
    }
  }

	def handleMessage(msg: EBMessage) = {
		if (msg.isInstanceOf[ViewUpdate]) {
			val vu = msg.asInstanceOf[ViewUpdate];
			if (vu.getWhat() == ViewUpdate.CREATED) {
        initView(vu.getView())
			} else if (vu.getWhat() == ViewUpdate.CLOSED) {
        unInitView(vu.getView());
			}
		// } else if (msg.isInstanceOf[PropertiesChanged]) {
			// loadAccents();
		}
	} //}}}

}
