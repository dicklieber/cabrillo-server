package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.htmlTable.RowsSource
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  import slick.collection.heterogeneous._
  import slick.collection.heterogeneous.syntax._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(ArrlSections.schema, Bands.schema, Contacts.schema, Entries.schema, Modes.schema, Offtimes.schema, OperatorTypes.schema, Overlays.schema, Powers.schema, Qso.schema, Soapboxes.schema, Stations.schema, Times.schema, Transmitters.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table ArrlSections
   *  @param abbrev Database column abbrev SqlType(VARCHAR), Length(20,true), Default(None)
   *  @param name Database column name SqlType(VARCHAR), Length(30,true), Default(None)
   *  @param description Database column description SqlType(VARCHAR), Length(255,true), Default(None) */
  case class ArrlSectionsRow(abbrev: Option[String] = None, name: Option[String] = None, description: Option[String] = None)
  /** GetResult implicit for fetching ArrlSectionsRow objects using plain SQL queries */
  implicit def GetResultArrlSectionsRow(implicit e0: GR[Option[String]]): GR[ArrlSectionsRow] = GR{
    prs => import prs._
    ArrlSectionsRow.tupled((<<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table arrl_sections. Objects of this class serve as prototypes for rows in queries. */
  class ArrlSections(_tableTag: Tag) extends profile.api.Table[ArrlSectionsRow](_tableTag, Some("WFD"), "arrl_sections") {
    def * = (abbrev, name, description) <> (ArrlSectionsRow.tupled, ArrlSectionsRow.unapply)

    /** Database column abbrev SqlType(VARCHAR), Length(20,true), Default(None) */
    val abbrev: Rep[Option[String]] = column[Option[String]]("abbrev", O.Length(20,varying=true), O.Default(None))
    /** Database column name SqlType(VARCHAR), Length(30,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(30,varying=true), O.Default(None))
    /** Database column description SqlType(VARCHAR), Length(255,true), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table ArrlSections */
  lazy val ArrlSections = new TableQuery(tag => new ArrlSections(tag))

  /** Entity class storing rows of table Bands
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(11,true), Default(None) */
  case class BandsRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching BandsRow objects using plain SQL queries */
  implicit def GetResultBandsRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[BandsRow] = GR{
    prs => import prs._
    BandsRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table bands. Objects of this class serve as prototypes for rows in queries. */
  class Bands(_tableTag: Tag) extends profile.api.Table[BandsRow](_tableTag, Some("WFD"), "bands") {
    def * = (id, name) <> (BandsRow.tupled, BandsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> BandsRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(11,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(11,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Bands */
  lazy val Bands = new TableQuery(tag => new Bands(tag))

  /** Entity class storing rows of table Contacts
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param entryId Database column entry_id SqlType(INT)
   *  @param freq Database column freq SqlType(VARCHAR), Length(25,true)
   *  @param qsoMode Database column qso_mode SqlType(INT)
   *  @param contactDate Database column contact_date SqlType(DATE)
   *  @param contactTime Database column contact_time SqlType(TIME)
   *  @param callsign Database column callsign SqlType(VARCHAR), Length(25,true)
   *  @param exch Database column exch SqlType(VARCHAR), Length(25,true)
   *  @param transmitter Database column transmitter SqlType(INT) */
  case class ContactsRow(id: Int, entryId: Int, freq: String, qsoMode: Int, contactDate: java.sql.Date, contactTime: java.sql.Time, callsign: String, exch: String, transmitter: Int)
  /** GetResult implicit for fetching ContactsRow objects using plain SQL queries */
  implicit def GetResultContactsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Date], e3: GR[java.sql.Time]): GR[ContactsRow] = GR{
    prs => import prs._
    ContactsRow.tupled((<<[Int], <<[Int], <<[String], <<[Int], <<[java.sql.Date], <<[java.sql.Time], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table contacts. Objects of this class serve as prototypes for rows in queries. */
  class Contacts(_tableTag: Tag) extends profile.api.Table[ContactsRow](_tableTag, Some("WFD"), "contacts") {
    def * = (id, entryId, freq, qsoMode, contactDate, contactTime, callsign, exch, transmitter) <> (ContactsRow.tupled, ContactsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(entryId), Rep.Some(freq), Rep.Some(qsoMode), Rep.Some(contactDate), Rep.Some(contactTime), Rep.Some(callsign), Rep.Some(exch), Rep.Some(transmitter))).shaped.<>({r=>import r._; _1.map(_=> ContactsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column entry_id SqlType(INT) */
    val entryId: Rep[Int] = column[Int]("entry_id")
    /** Database column freq SqlType(VARCHAR), Length(25,true) */
    val freq: Rep[String] = column[String]("freq", O.Length(25,varying=true))
    /** Database column qso_mode SqlType(INT) */
    val qsoMode: Rep[Int] = column[Int]("qso_mode")
    /** Database column contact_date SqlType(DATE) */
    val contactDate: Rep[java.sql.Date] = column[java.sql.Date]("contact_date")
    /** Database column contact_time SqlType(TIME) */
    val contactTime: Rep[java.sql.Time] = column[java.sql.Time]("contact_time")
    /** Database column callsign SqlType(VARCHAR), Length(25,true) */
    val callsign: Rep[String] = column[String]("callsign", O.Length(25,varying=true))
    /** Database column exch SqlType(VARCHAR), Length(25,true) */
    val exch: Rep[String] = column[String]("exch", O.Length(25,varying=true))
    /** Database column transmitter SqlType(INT) */
    val transmitter: Rep[Int] = column[Int]("transmitter")

    /** Index over (entryId) (database name FK_entry_contact) */
    val index1 = index("FK_entry_contact", entryId)
  }
  /** Collection-like TableQuery object for table Contacts */
  lazy val Contacts = new TableQuery(tag => new Contacts(tag))

  /** Entity class storing rows of table Entries
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param logVersion Database column log_version SqlType(INT), Default(None)
   *  @param callsign Database column callsign SqlType(VARCHAR), Length(20,true), Default(None)
   *  @param contest Database column contest SqlType(VARCHAR), Length(20,true), Default(None)
   *  @param assisted Database column assisted SqlType(BIT), Default(None)
   *  @param bandId Database column band_id SqlType(INT), Default(None)
   *  @param modeId Database column mode_id SqlType(INT), Default(None)
   *  @param operators Database column operators SqlType(INT), Default(None)
   *  @param operatorTypeId Database column operator_type_id SqlType(INT), Default(None)
   *  @param powerId Database column power_id SqlType(INT), Default(None)
   *  @param stationId Database column station_id SqlType(INT), Default(None)
   *  @param timeId Database column time_id SqlType(INT), Default(None)
   *  @param transmitterId Database column transmitter_id SqlType(INT), Default(None)
   *  @param overlayId Database column overlay_id SqlType(INT), Default(None)
   *  @param certificate Database column certificate SqlType(BIT), Default(None)
   *  @param claimedScore Database column claimed_score SqlType(INT), Default(None)
   *  @param club Database column club SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param createdBy Database column created_by SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param gridLocator Database column grid_locator SqlType(VARCHAR), Length(6,true), Default(None)
   *  @param location Database column location SqlType(VARCHAR), Length(25,true), Default(None)
   *  @param name Database column name SqlType(VARCHAR), Length(75,true), Default(None)
   *  @param address Database column address SqlType(VARCHAR), Length(75,true), Default(None)
   *  @param city Database column city SqlType(VARCHAR), Length(75,true), Default(None)
   *  @param stateProvince Database column state_province SqlType(VARCHAR), Length(75,true), Default(None)
   *  @param postalcode Database column postalcode SqlType(VARCHAR), Length(75,true), Default(None)
   *  @param country Database column country SqlType(VARCHAR), Length(75,true), Default(None) */
  case class EntriesRow(id: Int, logVersion: Option[Int] = None, callsign: Option[String] = None, contest: Option[String] = None, assisted: Option[Boolean] = None, bandId: Option[Int] = None, modeId: Option[Int] = None, operators: Option[Int] = None, operatorTypeId: Option[Int] = None, powerId: Option[Int] = None, stationId: Option[Int] = None, timeId: Option[Int] = None, transmitterId: Option[Int] = None, overlayId: Option[Int] = None, certificate: Option[Boolean] = None, claimedScore: Option[Int] = None, club: Option[String] = None, createdBy: Option[String] = None, email: Option[String] = None, gridLocator: Option[String] = None, location: Option[String] = None, name: Option[String] = None, address: Option[String] = None, city: Option[String] = None, stateProvince: Option[String] = None, postalcode: Option[String] = None, country: Option[String] = None)
  extends RowsSource
  /** GetResult implicit for fetching EntriesRow objects using plain SQL queries */
  implicit def GetResultEntriesRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]], e3: GR[Option[Boolean]]): GR[EntriesRow] = GR{
    prs => import prs._
    EntriesRow(<<[Int], <<?[Int], <<?[String], <<?[String], <<?[Boolean], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Boolean], <<?[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String])
  }
  /** Table description of table entries. Objects of this class serve as prototypes for rows in queries. */
  class Entries(_tableTag: Tag) extends profile.api.Table[EntriesRow](_tableTag, Some("WFD"), "entries") {
    def * = (id :: logVersion :: callsign :: contest :: assisted :: bandId :: modeId :: operators :: operatorTypeId :: powerId :: stationId :: timeId :: transmitterId :: overlayId :: certificate :: claimedScore :: club :: createdBy :: email :: gridLocator :: location :: name :: address :: city :: stateProvince :: postalcode :: country :: HNil).mapTo[EntriesRow]
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id) :: logVersion :: callsign :: contest :: assisted :: bandId :: modeId :: operators :: operatorTypeId :: powerId :: stationId :: timeId :: transmitterId :: overlayId :: certificate :: claimedScore :: club :: createdBy :: email :: gridLocator :: location :: name :: address :: city :: stateProvince :: postalcode :: country :: HNil).shaped.<>(r => EntriesRow(r(0).asInstanceOf[Option[Int]].get, r(1).asInstanceOf[Option[Int]], r(2).asInstanceOf[Option[String]], r(3).asInstanceOf[Option[String]], r(4).asInstanceOf[Option[Boolean]], r(5).asInstanceOf[Option[Int]], r(6).asInstanceOf[Option[Int]], r(7).asInstanceOf[Option[Int]], r(8).asInstanceOf[Option[Int]], r(9).asInstanceOf[Option[Int]], r(10).asInstanceOf[Option[Int]], r(11).asInstanceOf[Option[Int]], r(12).asInstanceOf[Option[Int]], r(13).asInstanceOf[Option[Int]], r(14).asInstanceOf[Option[Boolean]], r(15).asInstanceOf[Option[Int]], r(16).asInstanceOf[Option[String]], r(17).asInstanceOf[Option[String]], r(18).asInstanceOf[Option[String]], r(19).asInstanceOf[Option[String]], r(20).asInstanceOf[Option[String]], r(21).asInstanceOf[Option[String]], r(22).asInstanceOf[Option[String]], r(23).asInstanceOf[Option[String]], r(24).asInstanceOf[Option[String]], r(25).asInstanceOf[Option[String]], r(26).asInstanceOf[Option[String]]), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column log_version SqlType(INT), Default(None) */
    val logVersion: Rep[Option[Int]] = column[Option[Int]]("log_version", O.Default(None))
    /** Database column callsign SqlType(VARCHAR), Length(20,true), Default(None) */
    val callsign: Rep[Option[String]] = column[Option[String]]("callsign", O.Length(20,varying=true), O.Default(None))
    /** Database column contest SqlType(VARCHAR), Length(20,true), Default(None) */
    val contest: Rep[Option[String]] = column[Option[String]]("contest", O.Length(20,varying=true), O.Default(None))
    /** Database column assisted SqlType(BIT), Default(None) */
    val assisted: Rep[Option[Boolean]] = column[Option[Boolean]]("assisted", O.Default(None))
    /** Database column band_id SqlType(INT), Default(None) */
    val bandId: Rep[Option[Int]] = column[Option[Int]]("band_id", O.Default(None))
    /** Database column mode_id SqlType(INT), Default(None) */
    val modeId: Rep[Option[Int]] = column[Option[Int]]("mode_id", O.Default(None))
    /** Database column operators SqlType(INT), Default(None) */
    val operators: Rep[Option[Int]] = column[Option[Int]]("operators", O.Default(None))
    /** Database column operator_type_id SqlType(INT), Default(None) */
    val operatorTypeId: Rep[Option[Int]] = column[Option[Int]]("operator_type_id", O.Default(None))
    /** Database column power_id SqlType(INT), Default(None) */
    val powerId: Rep[Option[Int]] = column[Option[Int]]("power_id", O.Default(None))
    /** Database column station_id SqlType(INT), Default(None) */
    val stationId: Rep[Option[Int]] = column[Option[Int]]("station_id", O.Default(None))
    /** Database column time_id SqlType(INT), Default(None) */
    val timeId: Rep[Option[Int]] = column[Option[Int]]("time_id", O.Default(None))
    /** Database column transmitter_id SqlType(INT), Default(None) */
    val transmitterId: Rep[Option[Int]] = column[Option[Int]]("transmitter_id", O.Default(None))
    /** Database column overlay_id SqlType(INT), Default(None) */
    val overlayId: Rep[Option[Int]] = column[Option[Int]]("overlay_id", O.Default(None))
    /** Database column certificate SqlType(BIT), Default(None) */
    val certificate: Rep[Option[Boolean]] = column[Option[Boolean]]("certificate", O.Default(None))
    /** Database column claimed_score SqlType(INT), Default(None) */
    val claimedScore: Rep[Option[Int]] = column[Option[Int]]("claimed_score", O.Default(None))
    /** Database column club SqlType(VARCHAR), Length(255,true), Default(None) */
    val club: Rep[Option[String]] = column[Option[String]]("club", O.Length(255,varying=true), O.Default(None))
    /** Database column created_by SqlType(VARCHAR), Length(255,true), Default(None) */
    val createdBy: Rep[Option[String]] = column[Option[String]]("created_by", O.Length(255,varying=true), O.Default(None))
    /** Database column email SqlType(VARCHAR), Length(255,true), Default(None) */
    val email: Rep[Option[String]] = column[Option[String]]("email", O.Length(255,varying=true), O.Default(None))
    /** Database column grid_locator SqlType(VARCHAR), Length(6,true), Default(None) */
    val gridLocator: Rep[Option[String]] = column[Option[String]]("grid_locator", O.Length(6,varying=true), O.Default(None))
    /** Database column location SqlType(VARCHAR), Length(25,true), Default(None) */
    val location: Rep[Option[String]] = column[Option[String]]("location", O.Length(25,varying=true), O.Default(None))
    /** Database column name SqlType(VARCHAR), Length(75,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(75,varying=true), O.Default(None))
    /** Database column address SqlType(VARCHAR), Length(75,true), Default(None) */
    val address: Rep[Option[String]] = column[Option[String]]("address", O.Length(75,varying=true), O.Default(None))
    /** Database column city SqlType(VARCHAR), Length(75,true), Default(None) */
    val city: Rep[Option[String]] = column[Option[String]]("city", O.Length(75,varying=true), O.Default(None))
    /** Database column state_province SqlType(VARCHAR), Length(75,true), Default(None) */
    val stateProvince: Rep[Option[String]] = column[Option[String]]("state_province", O.Length(75,varying=true), O.Default(None))
    /** Database column postalcode SqlType(VARCHAR), Length(75,true), Default(None) */
    val postalcode: Rep[Option[String]] = column[Option[String]]("postalcode", O.Length(75,varying=true), O.Default(None))
    /** Database column country SqlType(VARCHAR), Length(75,true), Default(None) */
    val country: Rep[Option[String]] = column[Option[String]]("country", O.Length(75,varying=true), O.Default(None))

    /** Index over (bandId) (database name FK_band_id) */
    val index1 = index("FK_band_id", bandId :: HNil)
    /** Index over (modeId) (database name FK_mode_id) */
    val index2 = index("FK_mode_id", modeId :: HNil)
    /** Index over (operatorTypeId) (database name FK_operator_type_id) */
    val index3 = index("FK_operator_type_id", operatorTypeId :: HNil)
    /** Index over (overlayId) (database name FK_overlay_id) */
    val index4 = index("FK_overlay_id", overlayId :: HNil)
    /** Index over (powerId) (database name FK_power_id) */
    val index5 = index("FK_power_id", powerId :: HNil)
    /** Index over (stationId) (database name FK_station_id) */
    val index6 = index("FK_station_id", stationId :: HNil)
    /** Index over (timeId) (database name FK_time_id) */
    val index7 = index("FK_time_id", timeId :: HNil)
    /** Index over (transmitterId) (database name FK_transmitter_id) */
    val index8 = index("FK_transmitter_id", transmitterId :: HNil)
  }
  /** Collection-like TableQuery object for table Entries */
  lazy val Entries = new TableQuery(tag => new Entries(tag))

  /** Entity class storing rows of table Modes
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(10,true), Default(None) */
  case class ModesRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching ModesRow objects using plain SQL queries */
  implicit def GetResultModesRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[ModesRow] = GR{
    prs => import prs._
    ModesRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table modes. Objects of this class serve as prototypes for rows in queries. */
  class Modes(_tableTag: Tag) extends profile.api.Table[ModesRow](_tableTag, Some("WFD"), "modes") {
    def * = (id, name) <> (ModesRow.tupled, ModesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> ModesRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(10,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(10,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Modes */
  lazy val Modes = new TableQuery(tag => new Modes(tag))

  /** Entity class storing rows of table Offtimes
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param entryId Database column entry_id SqlType(INT), Default(None)
   *  @param soapbox Database column soapbox SqlType(VARCHAR), Length(255,true), Default(None) */
  case class OfftimesRow(id: Int, entryId: Option[Int] = None, soapbox: Option[String] = None)
  /** GetResult implicit for fetching OfftimesRow objects using plain SQL queries */
  implicit def GetResultOfftimesRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]]): GR[OfftimesRow] = GR{
    prs => import prs._
    OfftimesRow.tupled((<<[Int], <<?[Int], <<?[String]))
  }
  /** Table description of table offtimes. Objects of this class serve as prototypes for rows in queries. */
  class Offtimes(_tableTag: Tag) extends profile.api.Table[OfftimesRow](_tableTag, Some("WFD"), "offtimes") {
    def * = (id, entryId, soapbox) <> (OfftimesRow.tupled, OfftimesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), entryId, soapbox)).shaped.<>({r=>import r._; _1.map(_=> OfftimesRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column entry_id SqlType(INT), Default(None) */
    val entryId: Rep[Option[Int]] = column[Option[Int]]("entry_id", O.Default(None))
    /** Database column soapbox SqlType(VARCHAR), Length(255,true), Default(None) */
    val soapbox: Rep[Option[String]] = column[Option[String]]("soapbox", O.Length(255,varying=true), O.Default(None))

    /** Index over (entryId) (database name FK_entry_offtime) */
    val index1 = index("FK_entry_offtime", entryId)
  }
  /** Collection-like TableQuery object for table Offtimes */
  lazy val Offtimes = new TableQuery(tag => new Offtimes(tag))

  /** Entity class storing rows of table OperatorTypes
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(9,true), Default(None) */
  case class OperatorTypesRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching OperatorTypesRow objects using plain SQL queries */
  implicit def GetResultOperatorTypesRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[OperatorTypesRow] = GR{
    prs => import prs._
    OperatorTypesRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table operator_types. Objects of this class serve as prototypes for rows in queries. */
  class OperatorTypes(_tableTag: Tag) extends profile.api.Table[OperatorTypesRow](_tableTag, Some("WFD"), "operator_types") {
    def * = (id, name) <> (OperatorTypesRow.tupled, OperatorTypesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> OperatorTypesRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(9,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(9,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table OperatorTypes */
  lazy val OperatorTypes = new TableQuery(tag => new OperatorTypes(tag))

  /** Entity class storing rows of table Overlays
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(11,true), Default(None) */
  case class OverlaysRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching OverlaysRow objects using plain SQL queries */
  implicit def GetResultOverlaysRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[OverlaysRow] = GR{
    prs => import prs._
    OverlaysRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table overlays. Objects of this class serve as prototypes for rows in queries. */
  class Overlays(_tableTag: Tag) extends profile.api.Table[OverlaysRow](_tableTag, Some("WFD"), "overlays") {
    def * = (id, name) <> (OverlaysRow.tupled, OverlaysRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> OverlaysRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(11,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(11,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Overlays */
  lazy val Overlays = new TableQuery(tag => new Overlays(tag))

  /** Entity class storing rows of table Powers
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(4,true), Default(None) */
  case class PowersRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching PowersRow objects using plain SQL queries */
  implicit def GetResultPowersRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[PowersRow] = GR{
    prs => import prs._
    PowersRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table powers. Objects of this class serve as prototypes for rows in queries. */
  class Powers(_tableTag: Tag) extends profile.api.Table[PowersRow](_tableTag, Some("WFD"), "powers") {
    def * = (id, name) <> (PowersRow.tupled, PowersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> PowersRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(4,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(4,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Powers */
  lazy val Powers = new TableQuery(tag => new Powers(tag))

  /** Entity class storing rows of table Qso
   *  @param freq Database column FREQ SqlType(VARCHAR), Length(20,true), Default(None)
   *  @param mode Database column MODE SqlType(VARCHAR), Length(4,true), Default(None)
   *  @param stamp Database column STAMP SqlType(DATETIME), Default(None)
   *  @param sentCallsign Database column SENT_CALLSIGN SqlType(VARCHAR), Length(15,true), Default(None)
   *  @param sentCat Database column SENT_CAT SqlType(VARCHAR), Length(4,true), Default(None)
   *  @param sentSection Database column SENT_SECTION SqlType(VARCHAR), Length(3,true), Default(None)
   *  @param recvdCallsign Database column RECVD_CALLSIGN SqlType(VARCHAR), Length(15,true), Default(None)
   *  @param recvdCat Database column RECVD_CAT SqlType(VARCHAR), Length(4,true), Default(None)
   *  @param recvdSection Database column RECVD_SECTION SqlType(VARCHAR), Length(3,true), Default(None) */
  case class QsoRow(freq: Option[String] = None, mode: Option[String] = None, stamp: Option[java.sql.Timestamp] = None, sentCallsign: Option[String] = None, sentCat: Option[String] = None, sentSection: Option[String] = None, recvdCallsign: Option[String] = None, recvdCat: Option[String] = None, recvdSection: Option[String] = None)
  /** GetResult implicit for fetching QsoRow objects using plain SQL queries */
  implicit def GetResultQsoRow(implicit e0: GR[Option[String]], e1: GR[Option[java.sql.Timestamp]]): GR[QsoRow] = GR{
    prs => import prs._
    QsoRow.tupled((<<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table QSO. Objects of this class serve as prototypes for rows in queries. */
  class Qso(_tableTag: Tag) extends profile.api.Table[QsoRow](_tableTag, Some("WFD"), "QSO") {
    def * = (freq, mode, stamp, sentCallsign, sentCat, sentSection, recvdCallsign, recvdCat, recvdSection) <> (QsoRow.tupled, QsoRow.unapply)

    /** Database column FREQ SqlType(VARCHAR), Length(20,true), Default(None) */
    val freq: Rep[Option[String]] = column[Option[String]]("FREQ", O.Length(20,varying=true), O.Default(None))
    /** Database column MODE SqlType(VARCHAR), Length(4,true), Default(None) */
    val mode: Rep[Option[String]] = column[Option[String]]("MODE", O.Length(4,varying=true), O.Default(None))
    /** Database column STAMP SqlType(DATETIME), Default(None) */
    val stamp: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("STAMP", O.Default(None))
    /** Database column SENT_CALLSIGN SqlType(VARCHAR), Length(15,true), Default(None) */
    val sentCallsign: Rep[Option[String]] = column[Option[String]]("SENT_CALLSIGN", O.Length(15,varying=true), O.Default(None))
    /** Database column SENT_CAT SqlType(VARCHAR), Length(4,true), Default(None) */
    val sentCat: Rep[Option[String]] = column[Option[String]]("SENT_CAT", O.Length(4,varying=true), O.Default(None))
    /** Database column SENT_SECTION SqlType(VARCHAR), Length(3,true), Default(None) */
    val sentSection: Rep[Option[String]] = column[Option[String]]("SENT_SECTION", O.Length(3,varying=true), O.Default(None))
    /** Database column RECVD_CALLSIGN SqlType(VARCHAR), Length(15,true), Default(None) */
    val recvdCallsign: Rep[Option[String]] = column[Option[String]]("RECVD_CALLSIGN", O.Length(15,varying=true), O.Default(None))
    /** Database column RECVD_CAT SqlType(VARCHAR), Length(4,true), Default(None) */
    val recvdCat: Rep[Option[String]] = column[Option[String]]("RECVD_CAT", O.Length(4,varying=true), O.Default(None))
    /** Database column RECVD_SECTION SqlType(VARCHAR), Length(3,true), Default(None) */
    val recvdSection: Rep[Option[String]] = column[Option[String]]("RECVD_SECTION", O.Length(3,varying=true), O.Default(None))

    /** Index over (recvdCallsign) (database name idx_QSO_RECVD_CALLSIGN) */
    val index1 = index("idx_QSO_RECVD_CALLSIGN", recvdCallsign)
    /** Index over (sentCallsign) (database name idx_QSO_SENT_CALLSIGN) */
    val index2 = index("idx_QSO_SENT_CALLSIGN", sentCallsign)
  }
  /** Collection-like TableQuery object for table Qso */
  lazy val Qso = new TableQuery(tag => new Qso(tag))

  /** Entity class storing rows of table Soapboxes
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param entryId Database column entry_id SqlType(INT), Default(None)
   *  @param soapbox Database column soapbox SqlType(VARCHAR), Length(255,true), Default(None) */
  case class SoapboxesRow(id: Int, entryId: Option[Int] = None, soapbox: Option[String] = None)
  /** GetResult implicit for fetching SoapboxesRow objects using plain SQL queries */
  implicit def GetResultSoapboxesRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]]): GR[SoapboxesRow] = GR{
    prs => import prs._
    SoapboxesRow.tupled((<<[Int], <<?[Int], <<?[String]))
  }
  /** Table description of table soapboxes. Objects of this class serve as prototypes for rows in queries. */
  class Soapboxes(_tableTag: Tag) extends profile.api.Table[SoapboxesRow](_tableTag, Some("WFD"), "soapboxes") {
    def * = (id, entryId, soapbox) <> (SoapboxesRow.tupled, SoapboxesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), entryId, soapbox)).shaped.<>({r=>import r._; _1.map(_=> SoapboxesRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column entry_id SqlType(INT), Default(None) */
    val entryId: Rep[Option[Int]] = column[Option[Int]]("entry_id", O.Default(None))
    /** Database column soapbox SqlType(VARCHAR), Length(255,true), Default(None) */
    val soapbox: Rep[Option[String]] = column[Option[String]]("soapbox", O.Length(255,varying=true), O.Default(None))

    /** Index over (entryId) (database name FK_entry_soapbox) */
    val index1 = index("FK_entry_soapbox", entryId)
  }
  /** Collection-like TableQuery object for table Soapboxes */
  lazy val Soapboxes = new TableQuery(tag => new Soapboxes(tag))

  /** Entity class storing rows of table Stations
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(15,true), Default(None) */
  case class StationsRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching StationsRow objects using plain SQL queries */
  implicit def GetResultStationsRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[StationsRow] = GR{
    prs => import prs._
    StationsRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table stations. Objects of this class serve as prototypes for rows in queries. */
  class Stations(_tableTag: Tag) extends profile.api.Table[StationsRow](_tableTag, Some("WFD"), "stations") {
    def * = (id, name) <> (StationsRow.tupled, StationsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> StationsRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(15,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(15,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Stations */
  lazy val Stations = new TableQuery(tag => new Stations(tag))

  /** Entity class storing rows of table Times
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(8,true), Default(None) */
  case class TimesRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching TimesRow objects using plain SQL queries */
  implicit def GetResultTimesRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[TimesRow] = GR{
    prs => import prs._
    TimesRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table times. Objects of this class serve as prototypes for rows in queries. */
  class Times(_tableTag: Tag) extends profile.api.Table[TimesRow](_tableTag, Some("WFD"), "times") {
    def * = (id, name) <> (TimesRow.tupled, TimesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> TimesRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(8,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(8,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Times */
  lazy val Times = new TableQuery(tag => new Times(tag))

  /** Entity class storing rows of table Transmitters
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(9,true), Default(None) */
  case class TransmittersRow(id: Int, name: Option[String] = None)
  /** GetResult implicit for fetching TransmittersRow objects using plain SQL queries */
  implicit def GetResultTransmittersRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[TransmittersRow] = GR{
    prs => import prs._
    TransmittersRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table transmitters. Objects of this class serve as prototypes for rows in queries. */
  class Transmitters(_tableTag: Tag) extends profile.api.Table[TransmittersRow](_tableTag, Some("WFD"), "transmitters") {
    def * = (id, name) <> (TransmittersRow.tupled, TransmittersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), name)).shaped.<>({r=>import r._; _1.map(_=> TransmittersRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(9,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(9,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Transmitters */
  lazy val Transmitters = new TableQuery(tag => new Transmitters(tag))
}
