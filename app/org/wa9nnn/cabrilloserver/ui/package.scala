package org.wa9nnn.cabrilloserver

/**
 *
  *   * ==Overview==
  UiTable is a data transfwer object between and code that has tabular data that may need to be displayed.
  * There is no UI code in these classes, rather something like a Play Whirl template might be used to
  * render a UiTable as an HTML table.
  *
  * Data is organized into a [[org.wa9nnn.cabrilloserver.ui.UiTable]] which consists of a head and a collection of [[org.wa9nnn.cabrilloserver.ui.UiRow]]s.
  * Each [[org.wa9nnn.cabrilloserver.ui.UiRow]] consists of one or more [[org.wa9nnn.cabrilloserver.ui.TableCell]]s.
  *
  * If rendered as HTML
  * [[org.wa9nnn.cabrilloserver.ui.UiTable.columnHeaders]] produces the <thead>
  *   The header consists of a [[Seq[Seq[[TableCell]]
  *   Producing:
  *   <thead>
  *   <tr>
  *     <th>row0|cell0 with colSpan=2</th>
  *   </tr>
  *   <tr>
  *     <th>row1|cell0</th>
  *     <th>row1|cell1</th>
  *   </tr>
  *   </thead>
  * [[org.wa9nnn.cabrilloserver.ui.UiTable.rows]] produces the <body>
  *   Each [[org.wa9nnn.cabrilloserver.ui.UiRow]] becomes:
  *   <tr>
  *     <td>tablecell0</td>
  *     <td>tablecell1</td>
  *   </tr>
  *
  * [[org.wa9nnn.cabrilloserver.ui.TableCell]]s are generally created with a rich set of apply methods in the [[org.wa9nnn.cabrilloserver.ui.TableCell]] object.
  * {{{
  *   val uiTable = new UiTable(List(
        "A",
        "B"
      ),
        UiRow("row1", "b"),
        UiRow("row2", "bb")
      )

  * }}}
  *
  *
  */
package object ui {}
