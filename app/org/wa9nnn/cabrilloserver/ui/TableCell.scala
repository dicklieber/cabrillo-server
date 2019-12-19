package org.wa9nnn.cabrilloserver.ui

import java.net.URL
import java.text.DecimalFormat
import java.time.{Duration, Instant}

import org.wa9nnn.cabrilloserver.util.{TimeConverters => tc}


/**
  * One <TD> or <TH> cell that can be rendered in an HTML <table>
  *
  * This is normally constructed using the apply methods in [[TableCell]] then modified with the with methods
  * using the builder pattern. For example:
  * {{{rowCells += TableCell.number(42).withRowSpan(2).withCssClass("happyCell)}}}
  *
  * @param values   String to display in <td>. values(0) uses cssClass from [[cssClass]]. Subsequent values have
  *                 class "parenthetic". If [[rawHtml]] is true then values(0) is used with any embedded markup.
  * @param id       if Some, then this is the id attribute for the <td>
  * @param cssClass becomes space-seperated value for the class attribute.
  * @param button   if Some, an html button with text values(0) and class set to the this button val.
  * @param href     if Some, then cell contents will be wrapped in a <a> tag with href set to this url val.
  * @param image    url of imagewith.
  * @param rowSpan  rowSpan for <td> defaults to 1.
  * @param colSpan  colSpan for <td> defaults to 1.
  * @param tooltip  if non empty, this is the title attribute.
  * @param style    if Some, then the style attribute. Most of the the time you should use css classes instead of this.
  *                 But this is handy for age color e.g. s"background-color:$${ageValue.color}"
  * @param rawHtml  if true values(0) will be cell contents with no escaping.
  */
case class TableCell(values: Seq[String], id: Option[String] = None, cssClass: Seq[String] = Seq.empty, button:
Option[String] = None, href: Option[Link] = None,
                     image: Seq[String] = Seq.empty, rowSpan: Int = 1, colSpan: Int = 1, tooltip: String = "", style:
                     Option[String] = None,
                     rawHtml: Boolean = false) {
  def this(value: String) = this(Seq(value), None, Seq.empty, None, None, Seq.empty, 1)


  /**
    *
    * @param id is attribute.
    * @return copy of TableCell with id attribute added.
    */
  def withId(id: String): TableCell = copy(id = Some(id))

  def withUrl(url: String): TableCell = copy(href = Option(Link(url, "")))

  def withUrl(url: String, target: String): TableCell = copy(href = Option(Link(url, target)))

  def withUrl(url: URL, target: String = ""): TableCell = copy(href = Option(Link(url.toExternalForm, target)))

  /**
    *
    * @param image best to use a relative file path. e.g. /assets/images/delete.png
    * @return
    */
  def withImage(image: String): TableCell = {
    val ccc = copy(image = Seq(image))
    ccc
  }

  def withImage(imageUrl: URL): TableCell = {
    copy(image = Seq(imageUrl.toExternalForm))
  }

  def withImages(imageUrls: Seq[URL]): TableCell = {
    copy(image = imageUrls.map(_.toExternalForm))
  }


  /**
    *
    * @param cssClass to be added.
    * @return copy of the TableCell with cssClass appended to any existing cssClasses.
    */
  def withCssClass(cssClass: String): TableCell = copy(cssClass = this.cssClass :+ cssClass)

  /**
    *
    * @param cssClasses to be added.
    * @return copy of the TableCell with cssClass appended to any existing cssClasses.
    */
  def withCssClass(cssClasses: Seq[String]): TableCell = copy(cssClass = this.cssClass ++ cssClasses)

  def withStyle(style: String): TableCell = copy(style = Some(style))

  def withButton(buttonClass: String): TableCell = copy(button = Some(buttonClass))

  def withRowSpan(rowSpan: Int): TableCell = copy(rowSpan = rowSpan)

  def withColSpan(colSpan: Int): TableCell = copy(colSpan = colSpan)

  def withToolTip(toolTip: String): TableCell = copy(tooltip = toolTip)

  def addValue(value: String) = copy(values = this.values :+ value)

  /**
    *
    * @return all the cssClases seperated by a space.
    */
  def renderedCssClass: String = {
    cssClass.mkString(" ")
  }

  /**
    *
    * @param tc contents of a [[TableCell]] to be appended to this [[TableCell]] and collected into a [[UiRow]]
    * @return
    */
  def :+(tc: Any): UiRow = {
    val cellToAppend = TableCell(tc)
    val cells = Seq(this, cellToAppend)
    UiRow(cells)
  }
}

object TableCell {
  /**
    * Creat a TableCell.
    *
    * @param any A value that will be converted to a [[TableCell]]. This can accept a [[TableCell]] which is
    *            passed through unchanged.
    *            You can even pass a [[Traversable]] and you will get a [[TableCell]] with one string that is
    *            all the values in the [[Traversable]] toString'd and seperated with a space.
    * @return
    */
  def apply(any: Any): TableCell = {
    if (any == null) {
      new TableCell("null")
    } else {
      any match {
        case s: String =>
          new TableCell(s)
        case duration: Duration =>
          new TableCell(tc.durationToString(duration)).withCssClass("number")
        case instant: Instant =>
          (instant match {
            case Instant.MIN =>
              new TableCell("\u03B1")
            case Instant.MAX =>
              new TableCell("\u03C9")
            case ok =>
              new TableCell(tc.instantToString(ok))
          }).withCssClass("number")

        case i: Int =>
          new TableCell(Seq(java.text.NumberFormat.getIntegerInstance.format(i)), cssClass = Seq("number"))
        case d: Double =>
          number(d, 1)
        case l: Long =>
          new TableCell(Seq(java.text.NumberFormat.getIntegerInstance.format(l)), cssClass = Seq("number"))
        case tableCell: TableCell =>

          tableCell
        case exception: Exception =>
          val renderedStackTrace = ExceptionUi.render(exception)
          new TableCell(exception.getMessage).withCssClass("errorCell").withToolTip(renderedStackTrace)
        case col: Traversable[_] =>
          val cellString = col.map(_.toString).mkString(" ")
          new TableCell(Seq(cellString))
        case other =>
          new TableCell(other.toString)
      }
    }
  }

  def number(value: Double, decimals: Int = 5): TableCell = {
    val decimalFormat: DecimalFormat = new DecimalFormat()
    decimalFormat.setMinimumFractionDigits(decimals)

    new TableCell(Seq(decimalFormat.format(value)), cssClass = Seq("number")).withCssClass("number")
  }

  /**
    *
    * @param html contents of the <td> will be rendered without escaping.
    * @return TableCell that can be modified with the various with... methods.
    */
  def rawHtml(html: String): TableCell = {
    new TableCell(Seq(html), rawHtml = true)
  }
}

/**
  *
  * @param url    stringified
  * @param target @See http://www.w3schools.com/tags/att_a_target.asp, omit to open in same window.
  *               "_blank" new window or some name.
  */
case class Link(url: String, target: String = "")

