package org.wa9nnn.wfdserver.htmlTable

import java.net.URL
import java.text.DecimalFormat
import java.time.{Duration, Instant}

import org.wa9nnn.wfdserver.util.{TimeConverters => tc}


/**
 * One cell in a row of a table
 *
 * @param value    String to display in <td>. If [[rawHtml]] is true then values(0) is used with any embedded markup.
 * @param id       if Some, then this is the id attribute for the <td>
 * @param cssClass becomes space-separated value for the class attribute.
 * @param button   if Some, an html button with text value and class set to the this button val.
 * @param href     if Some, then cell contents will be wrapped in a <a> tag with href set to this url val.
 * @param image    url of image.
 * @param rowSpan  rowSpan for <td> defaults to 1.
 * @param colSpan  colSpan for <td> defaults to 1.
 * @param tooltip  if non empty, this is the title attribute.
 * @param style    if Some, then the style attribute. Most of the the time you should use css classes instead of this.
 *                 But this is handy for age color e.g. s"background-color:$${ageValue.color}"
 * @param rawHtml  if true value will be cell contents with no escaping.
 */
case class Cell(value: String,
                id: Option[String] = None,
                cssClass: Seq[String] = Seq.empty,
                button: Option[String] = None,
                href: Option[Link] = None,
                image: Seq[String] = Seq.empty,
                rowSpan: Int = 1,
                colSpan: Int = 1,
                tooltip: String = "", style:
                Option[String] = None,
                rawHtml: Boolean = false) {
  //  def this(value: String) = this(value, None, Seq.empty, None, None, Seq.empty, 1)

  /**
   *
   * @param id is attribute.
   * @return copy of TableCell with id attribute added.
   */
  def withId(id: String): Cell = copy(id = Some(id))

  def withUrl(url: String): Cell = copy(href = Option(Link(url)))

  def withUrl(url: String, target: String): Cell = copy(href = Option(Link(url, target)))

  def withUrl(url: URL, target: String = ""): Cell = copy(href = Option(Link(url.toExternalForm, target)))

  /**
   *
   * @param image best to use a relative file path. e.g. /assets/images/delete.png
   * @return
   */
  def withImage(image: String): Cell = {
    val ccc = copy(image = Seq(image))
    ccc
  }

  def withImage(imageUrl: URL): Cell = {
    copy(image = Seq(imageUrl.toExternalForm))
  }

  def withImages(imageUrls: Seq[URL]): Cell = {
    copy(image = imageUrls.map(_.toExternalForm))
  }


  /**
   *
   * @param cssClass to be added.
   * @return copy of the TableCell with cssClass appended to any existing cssClasses.
   */
  def withCssClass(cssClass: String): Cell = copy(cssClass = this.cssClass :+ cssClass)

  /**
   *
   * @param cssClasses to be added.
   * @return copy of the TableCell with cssClass appended to any existing cssClasses.
   */
  def withCssClass(cssClasses: Seq[String]): Cell = copy(cssClass = this.cssClass ++ cssClasses)

  def withStyle(style: String): Cell = copy(style = Some(style))

  def withButton(buttonClass: String): Cell = copy(button = Some(buttonClass))

  def withRowSpan(rowSpan: Int): Cell = copy(rowSpan = rowSpan)

  def withColSpan(colSpan: Int): Cell = copy(colSpan = colSpan)

  def withToolTip(toolTip: String): Cell = copy(tooltip = toolTip)

  /**
   *
   * @return all the cssClasses separated by a space.
   */
  def renderedCssClass: String = {
    cssClass.mkString(" ")
  }

  /**
   *
   * @param tc contents of a [[Cell]] to be appended to this [[Cell]] and collected into a [[Row]]
   * @return
   */
  def :+(tc: Any): Row = {
    val cellToAppend = Cell(tc)
    val cells = Seq(this, cellToAppend)
    Row(cells)
  }
}

object Cell {
  /**
   * Create a [[Row]] [[Cell]].
   *
   * @param any A value that will be converted to a [[Cell]]. This can accept a [[Cell]] which is
   *            passed through unchanged.
   *            You can even pass a [[Traversable]] and you will get a [[Cell]] with one string that is
   *            all the values in the [[Traversable]] toString'd and separated with a space.
   * @return
   */
  @scala.annotation.tailrec
  def apply(any: Any): Cell = {
    if (any == null) {
      new Cell("null")
    } else {
      any match {
        case s: String =>
          new Cell(s)
        case duration: Duration =>
          new Cell(tc.durationToString(duration)).withCssClass("number")
        case instant: Instant =>
          (instant match {
            case Instant.MIN =>
              new Cell("\u03B1")
            case Instant.MAX =>
              new Cell("\u03C9")
            case ok =>
              new Cell(tc.instantDisplayUTCCST(ok))
          }).withCssClass("number")
        case i: Int =>
          new Cell(java.text.NumberFormat.getIntegerInstance.format(i), cssClass = Seq("number"))
        case d: Double =>
          number(d, 1)
        case l: Long =>
          new Cell(java.text.NumberFormat.getIntegerInstance.format(l), cssClass = Seq("number"))
        case tableCell: Cell => // already a [[Cell]] use as is.
          tableCell
        case exception: Exception =>
          val renderedStackTrace = ExceptionRenderer(exception)
          new Cell(exception.getMessage).withCssClass("errorCell").withToolTip(renderedStackTrace)
        case None =>
          new Cell("")
        case Some(x) =>
          Cell(x)
        case other =>
          new Cell(other.toString)
      }
    }
  }

  def number(value: Double, decimals: Int = 5): Cell = {
    val decimalFormat: DecimalFormat = new DecimalFormat()
    decimalFormat.setMinimumFractionDigits(decimals)

    new Cell(decimalFormat.format(value), cssClass = Seq("number")).withCssClass("number")
  }

  /**
   *
   * @param html contents of the <td> will be rendered without escaping.
   * @return TableCell that can be modified with the various with... methods.
   */
  def rawHtml(html: String): Cell = {
    new Cell(html, rawHtml = true)
  }
}

/**
 *
 * @param url    as a string
 * @param target @See http://www.w3schools.com/tags/att_a_target.asp, omit to open in same window.
 *               "_blank" new window or some name.
 */
case class Link(url: String, target: String = "")

