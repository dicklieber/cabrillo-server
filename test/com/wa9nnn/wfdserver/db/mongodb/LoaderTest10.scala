
package com.wa9nnn.wfdserver.db.mongodb

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.{MongoClient, MongoDatabase, WriteConcern}
import com.wa9nnn.wfdserver.db.mongodb.Helpers._

import scala.io.Source

class LoaderTest10(connectUri: String = "mongodb://localhost", dbName: String = "wfd-test10") {

  private val mongoClient: MongoClient = MongoClient(connectUri)
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withWriteConcern(WriteConcern.ACKNOWLEDGED)
  private val logCollection = database.getCollection("logs")
  logCollection.drop()

  Source.fromResource("testdb10.json").getLines().foreach{line =>
    val bsonDocument = BsonDocument(line)
    logCollection.insertOne(bsonDocument).results()
  }



}
