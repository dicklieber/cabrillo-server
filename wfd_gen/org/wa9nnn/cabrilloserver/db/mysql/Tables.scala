package org.wa9nnn.cabrilloserver.db.mysql
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
  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Addresses.schema, ArrlSections.schema, Assisted.schema, Bands.schema, Contacts.schema, Entries.schema, Modes.schema, Offtimes.schema, OperatorTypes.schema, Overlays.schema, Powers.schema, QsoModes.schema, Soapboxes.schema, Stations.schema, Times.schema, Transmitters.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Addresses
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param entryId Database column entry_id SqlType(INT)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true) */
  case class AddressesRow(id: Int, entryId: Int, address: String)
  /** GetResult implicit for fetching AddressesRow objects using plain SQL queries */
  implicit def GetResultAddressesRow(implicit e0: GR[Int], e1: GR[String]): GR[AddressesRow] = GR{
    prs => import prs._
    AddressesRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table addresses. Objects of this class serve as prototypes for rows in queries. */
  class Addresses(_tableTag: Tag) extends profile.api.Table[AddressesRow](_tableTag, Some("WFD"), "addresses") {
    def * = (id, entryId, address) <> (AddressesRow.tupled, AddressesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(entryId), Rep.Some(address))).shaped.<>({r=>import r._; _1.map(_=> AddressesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column entry_id SqlType(INT) */
    val entryId: Rep[Int] = column[Int]("entry_id")
    /** Database column address SqlType(VARCHAR), Length(255,true) */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Addresses */
  lazy val Addresses = new TableQuery(tag => new Addresses(tag))

  /** Entity class storing rows of table ArrlSections
   *  @param abbrev Database column abbrev SqlType(VARCHAR), Length(20,true)
   *  @param name Database column name SqlType(VARCHAR), Length(30,true)
   *  @param description Database column description SqlType(VARCHAR), Length(255,true) */
  case class ArrlSectionsRow(abbrev: String, name: String, description: String)
  /** GetResult implicit for fetching ArrlSectionsRow objects using plain SQL queries */
  implicit def GetResultArrlSectionsRow(implicit e0: GR[String]): GR[ArrlSectionsRow] = GR{
    prs => import prs._
    ArrlSectionsRow.tupled((<<[String], <<[String], <<[String]))
  }
  /** Table description of table arrl_sections. Objects of this class serve as prototypes for rows in queries. */
  class ArrlSections(_tableTag: Tag) extends profile.api.Table[ArrlSectionsRow](_tableTag, Some("WFD"), "arrl_sections") {
    def * = (abbrev, name, description) <> (ArrlSectionsRow.tupled, ArrlSectionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(abbrev), Rep.Some(name), Rep.Some(description))).shaped.<>({r=>import r._; _1.map(_=> ArrlSectionsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column abbrev SqlType(VARCHAR), Length(20,true) */
    val abbrev: Rep[String] = column[String]("abbrev", O.Length(20,varying=true))
    /** Database column name SqlType(VARCHAR), Length(30,true) */
    val name: Rep[String] = column[String]("name", O.Length(30,varying=true))
    /** Database column description SqlType(VARCHAR), Length(255,true) */
    val description: Rep[String] = column[String]("description", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table ArrlSections */
  lazy val ArrlSections = new TableQuery(tag => new ArrlSections(tag))

  /** Entity class storing rows of table Assisted
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(12,true) */
  case class AssistedRow(id: Int, name: String)
  /** GetResult implicit for fetching AssistedRow objects using plain SQL queries */
  implicit def GetResultAssistedRow(implicit e0: GR[Int], e1: GR[String]): GR[AssistedRow] = GR{
    prs => import prs._
    AssistedRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table assisted. Objects of this class serve as prototypes for rows in queries. */
  class Assisted(_tableTag: Tag) extends profile.api.Table[AssistedRow](_tableTag, Some("WFD"), "assisted") {
    def * = (id, name) <> (AssistedRow.tupled, AssistedRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> AssistedRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(12,true) */
    val name: Rep[String] = column[String]("name", O.Length(12,varying=true))
  }
  /** Collection-like TableQuery object for table Assisted */
  lazy val Assisted = new TableQuery(tag => new Assisted(tag))

  /** Entity class storing rows of table Bands
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(11,true) */
  case class BandsRow(id: Int, name: String)
  /** GetResult implicit for fetching BandsRow objects using plain SQL queries */
  implicit def GetResultBandsRow(implicit e0: GR[Int], e1: GR[String]): GR[BandsRow] = GR{
    prs => import prs._
    BandsRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table bands. Objects of this class serve as prototypes for rows in queries. */
  class Bands(_tableTag: Tag) extends profile.api.Table[BandsRow](_tableTag, Some("WFD"), "bands") {
    def * = (id, name) <> (BandsRow.tupled, BandsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> BandsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(11,true) */
    val name: Rep[String] = column[String]("name", O.Length(11,varying=true))
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
   *  @param category Database column category SqlType(VARCHAR), Length(25,true)
   *  @param sect Database column sect SqlType(VARCHAR), Length(25,true)
   *  @param transmitter Database column transmitter SqlType(INT) */
  case class ContactsRow(id: Int, entryId: Int, freq: String, qsoMode: Int, contactDate: java.sql.Date, contactTime: java.sql.Time, callsign: String, exch: String, category: String, sect: String, transmitter: Int)
  /** GetResult implicit for fetching ContactsRow objects using plain SQL queries */
  implicit def GetResultContactsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Date], e3: GR[java.sql.Time]): GR[ContactsRow] = GR{
    prs => import prs._
    ContactsRow.tupled((<<[Int], <<[Int], <<[String], <<[Int], <<[java.sql.Date], <<[java.sql.Time], <<[String], <<[String], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table contacts. Objects of this class serve as prototypes for rows in queries. */
  class Contacts(_tableTag: Tag) extends profile.api.Table[ContactsRow](_tableTag, Some("WFD"), "contacts") {
    def * = (id, entryId, freq, qsoMode, contactDate, contactTime, callsign, exch, category, sect, transmitter) <> (ContactsRow.tupled, ContactsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(entryId), Rep.Some(freq), Rep.Some(qsoMode), Rep.Some(contactDate), Rep.Some(contactTime), Rep.Some(callsign), Rep.Some(exch), Rep.Some(category), Rep.Some(sect), Rep.Some(transmitter))).shaped.<>({r=>import r._; _1.map(_=> ContactsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    /** Database column category SqlType(VARCHAR), Length(25,true) */
    val category: Rep[String] = column[String]("category", O.Length(25,varying=true))
    /** Database column sect SqlType(VARCHAR), Length(25,true) */
    val sect: Rep[String] = column[String]("sect", O.Length(25,varying=true))
    /** Database column transmitter SqlType(INT) */
    val transmitter: Rep[Int] = column[Int]("transmitter")
  }
  /** Collection-like TableQuery object for table Contacts */
  lazy val Contacts = new TableQuery(tag => new Contacts(tag))

  /** Entity class storing rows of table Entries
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param logVersion Database column log_version SqlType(INT)
   *  @param callsign Database column callsign SqlType(VARCHAR), Length(20,true)
   *  @param contest Database column contest SqlType(VARCHAR), Length(20,true)
   *  @param assistedId Database column assisted_id SqlType(BIT)
   *  @param bandId Database column band_id SqlType(INT)
   *  @param modeId Database column mode_id SqlType(INT)
   *  @param operators Database column operators SqlType(INT)
   *  @param operatorTypeId Database column operator_type_id SqlType(INT)
   *  @param powerId Database column power_id SqlType(INT)
   *  @param stationId Database column station_id SqlType(INT)
   *  @param timeId Database column time_id SqlType(INT)
   *  @param transmitterId Database column transmitter_id SqlType(INT)
   *  @param overlayId Database column overlay_id SqlType(INT)
   *  @param certificate Database column certificate SqlType(BIT)
   *  @param claimedScore Database column claimed_score SqlType(INT)
   *  @param club Database column club SqlType(VARCHAR), Length(255,true)
   *  @param createdBy Database column created_by SqlType(VARCHAR), Length(255,true)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true)
   *  @param gridLocator Database column grid_locator SqlType(VARCHAR), Length(6,true)
   *  @param location Database column location SqlType(VARCHAR), Length(25,true)
   *  @param name Database column name SqlType(VARCHAR), Length(75,true)
   *  @param address Database column address SqlType(VARCHAR), Length(75,true)
   *  @param city Database column city SqlType(VARCHAR), Length(75,true)
   *  @param stateProvince Database column state_province SqlType(VARCHAR), Length(75,true)
   *  @param postalcode Database column postalcode SqlType(VARCHAR), Length(75,true)
   *  @param country Database column country SqlType(VARCHAR), Length(75,true)
   *  @param arrlSection Database column arrl_section SqlType(VARCHAR), Length(20,true)
   *  @param category Database column category SqlType(VARCHAR), Length(10,true) */
  case class EntriesRow(id: Int, logVersion: Int, callsign: String, contest: String, assistedId: Boolean, bandId: Int, modeId: Int, operators: Int, operatorTypeId: Int, powerId: Int, stationId: Int, timeId: Int, transmitterId: Int, overlayId: Int, certificate: Boolean, claimedScore: Int, club: String, createdBy: String, email: String, gridLocator: String, location: String, name: String, address: String, city: String, stateProvince: String, postalcode: String, country: String, arrlSection: String, category: String)
  /** GetResult implicit for fetching EntriesRow objects using plain SQL queries */
  implicit def GetResultEntriesRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Boolean]): GR[EntriesRow] = GR{
    prs => import prs._
    EntriesRow(<<[Int], <<[Int], <<[String], <<[String], <<[Boolean], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Boolean], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String])
  }
  /** Table description of table entries. Objects of this class serve as prototypes for rows in queries. */
  class Entries(_tableTag: Tag) extends profile.api.Table[EntriesRow](_tableTag, Some("WFD"), "entries") {
    def * = (id :: logVersion :: callsign :: contest :: assistedId :: bandId :: modeId :: operators :: operatorTypeId :: powerId :: stationId :: timeId :: transmitterId :: overlayId :: certificate :: claimedScore :: club :: createdBy :: email :: gridLocator :: location :: name :: address :: city :: stateProvince :: postalcode :: country :: arrlSection :: category :: HNil).mapTo[EntriesRow]
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id) :: Rep.Some(logVersion) :: Rep.Some(callsign) :: Rep.Some(contest) :: Rep.Some(assistedId) :: Rep.Some(bandId) :: Rep.Some(modeId) :: Rep.Some(operators) :: Rep.Some(operatorTypeId) :: Rep.Some(powerId) :: Rep.Some(stationId) :: Rep.Some(timeId) :: Rep.Some(transmitterId) :: Rep.Some(overlayId) :: Rep.Some(certificate) :: Rep.Some(claimedScore) :: Rep.Some(club) :: Rep.Some(createdBy) :: Rep.Some(email) :: Rep.Some(gridLocator) :: Rep.Some(location) :: Rep.Some(name) :: Rep.Some(address) :: Rep.Some(city) :: Rep.Some(stateProvince) :: Rep.Some(postalcode) :: Rep.Some(country) :: Rep.Some(arrlSection) :: Rep.Some(category) :: HNil).shaped.<>(r => EntriesRow(r(0).asInstanceOf[Option[Int]].get, r(1).asInstanceOf[Option[Int]].get, r(2).asInstanceOf[Option[String]].get, r(3).asInstanceOf[Option[String]].get, r(4).asInstanceOf[Option[Boolean]].get, r(5).asInstanceOf[Option[Int]].get, r(6).asInstanceOf[Option[Int]].get, r(7).asInstanceOf[Option[Int]].get, r(8).asInstanceOf[Option[Int]].get, r(9).asInstanceOf[Option[Int]].get, r(10).asInstanceOf[Option[Int]].get, r(11).asInstanceOf[Option[Int]].get, r(12).asInstanceOf[Option[Int]].get, r(13).asInstanceOf[Option[Int]].get, r(14).asInstanceOf[Option[Boolean]].get, r(15).asInstanceOf[Option[Int]].get, r(16).asInstanceOf[Option[String]].get, r(17).asInstanceOf[Option[String]].get, r(18).asInstanceOf[Option[String]].get, r(19).asInstanceOf[Option[String]].get, r(20).asInstanceOf[Option[String]].get, r(21).asInstanceOf[Option[String]].get, r(22).asInstanceOf[Option[String]].get, r(23).asInstanceOf[Option[String]].get, r(24).asInstanceOf[Option[String]].get, r(25).asInstanceOf[Option[String]].get, r(26).asInstanceOf[Option[String]].get, r(27).asInstanceOf[Option[String]].get, r(28).asInstanceOf[Option[String]].get), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column log_version SqlType(INT) */
    val logVersion: Rep[Int] = column[Int]("log_version")
    /** Database column callsign SqlType(VARCHAR), Length(20,true) */
    val callsign: Rep[String] = column[String]("callsign", O.Length(20,varying=true))
    /** Database column contest SqlType(VARCHAR), Length(20,true) */
    val contest: Rep[String] = column[String]("contest", O.Length(20,varying=true))
    /** Database column assisted_id SqlType(BIT) */
    val assistedId: Rep[Boolean] = column[Boolean]("assisted_id")
    /** Database column band_id SqlType(INT) */
    val bandId: Rep[Int] = column[Int]("band_id")
    /** Database column mode_id SqlType(INT) */
    val modeId: Rep[Int] = column[Int]("mode_id")
    /** Database column operators SqlType(INT) */
    val operators: Rep[Int] = column[Int]("operators")
    /** Database column operator_type_id SqlType(INT) */
    val operatorTypeId: Rep[Int] = column[Int]("operator_type_id")
    /** Database column power_id SqlType(INT) */
    val powerId: Rep[Int] = column[Int]("power_id")
    /** Database column station_id SqlType(INT) */
    val stationId: Rep[Int] = column[Int]("station_id")
    /** Database column time_id SqlType(INT) */
    val timeId: Rep[Int] = column[Int]("time_id")
    /** Database column transmitter_id SqlType(INT) */
    val transmitterId: Rep[Int] = column[Int]("transmitter_id")
    /** Database column overlay_id SqlType(INT) */
    val overlayId: Rep[Int] = column[Int]("overlay_id")
    /** Database column certificate SqlType(BIT) */
    val certificate: Rep[Boolean] = column[Boolean]("certificate")
    /** Database column claimed_score SqlType(INT) */
    val claimedScore: Rep[Int] = column[Int]("claimed_score")
    /** Database column club SqlType(VARCHAR), Length(255,true) */
    val club: Rep[String] = column[String]("club", O.Length(255,varying=true))
    /** Database column created_by SqlType(VARCHAR), Length(255,true) */
    val createdBy: Rep[String] = column[String]("created_by", O.Length(255,varying=true))
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column grid_locator SqlType(VARCHAR), Length(6,true) */
    val gridLocator: Rep[String] = column[String]("grid_locator", O.Length(6,varying=true))
    /** Database column location SqlType(VARCHAR), Length(25,true) */
    val location: Rep[String] = column[String]("location", O.Length(25,varying=true))
    /** Database column name SqlType(VARCHAR), Length(75,true) */
    val name: Rep[String] = column[String]("name", O.Length(75,varying=true))
    /** Database column address SqlType(VARCHAR), Length(75,true) */
    val address: Rep[String] = column[String]("address", O.Length(75,varying=true))
    /** Database column city SqlType(VARCHAR), Length(75,true) */
    val city: Rep[String] = column[String]("city", O.Length(75,varying=true))
    /** Database column state_province SqlType(VARCHAR), Length(75,true) */
    val stateProvince: Rep[String] = column[String]("state_province", O.Length(75,varying=true))
    /** Database column postalcode SqlType(VARCHAR), Length(75,true) */
    val postalcode: Rep[String] = column[String]("postalcode", O.Length(75,varying=true))
    /** Database column country SqlType(VARCHAR), Length(75,true) */
    val country: Rep[String] = column[String]("country", O.Length(75,varying=true))
    /** Database column arrl_section SqlType(VARCHAR), Length(20,true) */
    val arrlSection: Rep[String] = column[String]("arrl_section", O.Length(20,varying=true))
    /** Database column category SqlType(VARCHAR), Length(10,true) */
    val category: Rep[String] = column[String]("category", O.Length(10,varying=true))
  }
  /** Collection-like TableQuery object for table Entries */
  lazy val Entries = new TableQuery(tag => new Entries(tag))

  /** Entity class storing rows of table Modes
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(10,true) */
  case class ModesRow(id: Int, name: String)
  /** GetResult implicit for fetching ModesRow objects using plain SQL queries */
  implicit def GetResultModesRow(implicit e0: GR[Int], e1: GR[String]): GR[ModesRow] = GR{
    prs => import prs._
    ModesRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table modes. Objects of this class serve as prototypes for rows in queries. */
  class Modes(_tableTag: Tag) extends profile.api.Table[ModesRow](_tableTag, Some("WFD"), "modes") {
    def * = (id, name) <> (ModesRow.tupled, ModesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> ModesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(10,true) */
    val name: Rep[String] = column[String]("name", O.Length(10,varying=true))
  }
  /** Collection-like TableQuery object for table Modes */
  lazy val Modes = new TableQuery(tag => new Modes(tag))

  /** Entity class storing rows of table Offtimes
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param entryId Database column entry_id SqlType(INT)
   *  @param soapbox Database column soapbox SqlType(VARCHAR), Length(255,true) */
  case class OfftimesRow(id: Int, entryId: Int, soapbox: String)
  /** GetResult implicit for fetching OfftimesRow objects using plain SQL queries */
  implicit def GetResultOfftimesRow(implicit e0: GR[Int], e1: GR[String]): GR[OfftimesRow] = GR{
    prs => import prs._
    OfftimesRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table offtimes. Objects of this class serve as prototypes for rows in queries. */
  class Offtimes(_tableTag: Tag) extends profile.api.Table[OfftimesRow](_tableTag, Some("WFD"), "offtimes") {
    def * = (id, entryId, soapbox) <> (OfftimesRow.tupled, OfftimesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(entryId), Rep.Some(soapbox))).shaped.<>({r=>import r._; _1.map(_=> OfftimesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column entry_id SqlType(INT) */
    val entryId: Rep[Int] = column[Int]("entry_id")
    /** Database column soapbox SqlType(VARCHAR), Length(255,true) */
    val soapbox: Rep[String] = column[String]("soapbox", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Offtimes */
  lazy val Offtimes = new TableQuery(tag => new Offtimes(tag))

  /** Entity class storing rows of table OperatorTypes
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(9,true) */
  case class OperatorTypesRow(id: Int, name: String)
  /** GetResult implicit for fetching OperatorTypesRow objects using plain SQL queries */
  implicit def GetResultOperatorTypesRow(implicit e0: GR[Int], e1: GR[String]): GR[OperatorTypesRow] = GR{
    prs => import prs._
    OperatorTypesRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table operator_types. Objects of this class serve as prototypes for rows in queries. */
  class OperatorTypes(_tableTag: Tag) extends profile.api.Table[OperatorTypesRow](_tableTag, Some("WFD"), "operator_types") {
    def * = (id, name) <> (OperatorTypesRow.tupled, OperatorTypesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> OperatorTypesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(9,true) */
    val name: Rep[String] = column[String]("name", O.Length(9,varying=true))
  }
  /** Collection-like TableQuery object for table OperatorTypes */
  lazy val OperatorTypes = new TableQuery(tag => new OperatorTypes(tag))

  /** Entity class storing rows of table Overlays
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(11,true) */
  case class OverlaysRow(id: Int, name: String)
  /** GetResult implicit for fetching OverlaysRow objects using plain SQL queries */
  implicit def GetResultOverlaysRow(implicit e0: GR[Int], e1: GR[String]): GR[OverlaysRow] = GR{
    prs => import prs._
    OverlaysRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table overlays. Objects of this class serve as prototypes for rows in queries. */
  class Overlays(_tableTag: Tag) extends profile.api.Table[OverlaysRow](_tableTag, Some("WFD"), "overlays") {
    def * = (id, name) <> (OverlaysRow.tupled, OverlaysRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> OverlaysRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(11,true) */
    val name: Rep[String] = column[String]("name", O.Length(11,varying=true))
  }
  /** Collection-like TableQuery object for table Overlays */
  lazy val Overlays = new TableQuery(tag => new Overlays(tag))

  /** Entity class storing rows of table Powers
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(4,true) */
  case class PowersRow(id: Int, name: String)
  /** GetResult implicit for fetching PowersRow objects using plain SQL queries */
  implicit def GetResultPowersRow(implicit e0: GR[Int], e1: GR[String]): GR[PowersRow] = GR{
    prs => import prs._
    PowersRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table powers. Objects of this class serve as prototypes for rows in queries. */
  class Powers(_tableTag: Tag) extends profile.api.Table[PowersRow](_tableTag, Some("WFD"), "powers") {
    def * = (id, name) <> (PowersRow.tupled, PowersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> PowersRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(4,true) */
    val name: Rep[String] = column[String]("name", O.Length(4,varying=true))
  }
  /** Collection-like TableQuery object for table Powers */
  lazy val Powers = new TableQuery(tag => new Powers(tag))

  /** Entity class storing rows of table QsoModes
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(2,true) */
  case class QsoModesRow(id: Int, name: String)
  /** GetResult implicit for fetching QsoModesRow objects using plain SQL queries */
  implicit def GetResultQsoModesRow(implicit e0: GR[Int], e1: GR[String]): GR[QsoModesRow] = GR{
    prs => import prs._
    QsoModesRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table qso_modes. Objects of this class serve as prototypes for rows in queries. */
  class QsoModes(_tableTag: Tag) extends profile.api.Table[QsoModesRow](_tableTag, Some("WFD"), "qso_modes") {
    def * = (id, name) <> (QsoModesRow.tupled, QsoModesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> QsoModesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(2,true) */
    val name: Rep[String] = column[String]("name", O.Length(2,varying=true))
  }
  /** Collection-like TableQuery object for table QsoModes */
  lazy val QsoModes = new TableQuery(tag => new QsoModes(tag))

  /** Entity class storing rows of table Soapboxes
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param entryId Database column entry_id SqlType(INT)
   *  @param soapbox Database column soapbox SqlType(VARCHAR), Length(255,true) */
  case class SoapboxesRow(id: Int, entryId: Int, soapbox: String)
  /** GetResult implicit for fetching SoapboxesRow objects using plain SQL queries */
  implicit def GetResultSoapboxesRow(implicit e0: GR[Int], e1: GR[String]): GR[SoapboxesRow] = GR{
    prs => import prs._
    SoapboxesRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table soapboxes. Objects of this class serve as prototypes for rows in queries. */
  class Soapboxes(_tableTag: Tag) extends profile.api.Table[SoapboxesRow](_tableTag, Some("WFD"), "soapboxes") {
    def * = (id, entryId, soapbox) <> (SoapboxesRow.tupled, SoapboxesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(entryId), Rep.Some(soapbox))).shaped.<>({r=>import r._; _1.map(_=> SoapboxesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column entry_id SqlType(INT) */
    val entryId: Rep[Int] = column[Int]("entry_id")
    /** Database column soapbox SqlType(VARCHAR), Length(255,true) */
    val soapbox: Rep[String] = column[String]("soapbox", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Soapboxes */
  lazy val Soapboxes = new TableQuery(tag => new Soapboxes(tag))

  /** Entity class storing rows of table Stations
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(15,true) */
  case class StationsRow(id: Int, name: String)
  /** GetResult implicit for fetching StationsRow objects using plain SQL queries */
  implicit def GetResultStationsRow(implicit e0: GR[Int], e1: GR[String]): GR[StationsRow] = GR{
    prs => import prs._
    StationsRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table stations. Objects of this class serve as prototypes for rows in queries. */
  class Stations(_tableTag: Tag) extends profile.api.Table[StationsRow](_tableTag, Some("WFD"), "stations") {
    def * = (id, name) <> (StationsRow.tupled, StationsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> StationsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(15,true) */
    val name: Rep[String] = column[String]("name", O.Length(15,varying=true))
  }
  /** Collection-like TableQuery object for table Stations */
  lazy val Stations = new TableQuery(tag => new Stations(tag))

  /** Entity class storing rows of table Times
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(8,true) */
  case class TimesRow(id: Int, name: String)
  /** GetResult implicit for fetching TimesRow objects using plain SQL queries */
  implicit def GetResultTimesRow(implicit e0: GR[Int], e1: GR[String]): GR[TimesRow] = GR{
    prs => import prs._
    TimesRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table times. Objects of this class serve as prototypes for rows in queries. */
  class Times(_tableTag: Tag) extends profile.api.Table[TimesRow](_tableTag, Some("WFD"), "times") {
    def * = (id, name) <> (TimesRow.tupled, TimesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> TimesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(8,true) */
    val name: Rep[String] = column[String]("name", O.Length(8,varying=true))
  }
  /** Collection-like TableQuery object for table Times */
  lazy val Times = new TableQuery(tag => new Times(tag))

  /** Entity class storing rows of table Transmitters
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(9,true) */
  case class TransmittersRow(id: Int, name: String)
  /** GetResult implicit for fetching TransmittersRow objects using plain SQL queries */
  implicit def GetResultTransmittersRow(implicit e0: GR[Int], e1: GR[String]): GR[TransmittersRow] = GR{
    prs => import prs._
    TransmittersRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table transmitters. Objects of this class serve as prototypes for rows in queries. */
  class Transmitters(_tableTag: Tag) extends profile.api.Table[TransmittersRow](_tableTag, Some("WFD"), "transmitters") {
    def * = (id, name) <> (TransmittersRow.tupled, TransmittersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> TransmittersRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(9,true) */
    val name: Rep[String] = column[String]("name", O.Length(9,varying=true))
  }
  /** Collection-like TableQuery object for table Transmitters */
  lazy val Transmitters = new TableQuery(tag => new Transmitters(tag))
}
