
package com.wa9nnn.wfdserver.model

import com.wa9nnn.wfdserver.model.WfdTypes.CallSign
import com.wa9nnn.wfdserver.paper.EditableData

case class QSoEditor (paperLogQso: PaperLogQso) extends EditableData {
  override val callSign: CallSign = paperLogQso.callSign
}
